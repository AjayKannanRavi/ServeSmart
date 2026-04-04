package com.servesmart.service;

import com.servesmart.config.TenantContext;
import com.servesmart.dto.OrderItemRequest;
import com.servesmart.dto.OrderRequest;
import com.servesmart.entity.*;
import com.servesmart.repository.MenuItemRepository;
import com.servesmart.repository.OrderItemRepository;
import com.servesmart.repository.OrderRepository;
import com.servesmart.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantTableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final com.servesmart.repository.PaymentRepository paymentRepository;
    private final com.servesmart.repository.RestaurantRepository restaurantRepository;

    @Transactional
    public RestaurantOrder createOrder(OrderRequest request) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        if (restaurantId == null && request.getTableId() != null) {
            // Internal fallback: try to find the restaurant associated with the table
            Optional<RestaurantTable> tempTable = tableRepository.findById(request.getTableId());
            if (tempTable.isPresent() && tempTable.get().getRestaurant() != null) {
                restaurantId = tempTable.get().getRestaurant().getId();
                TenantContext.setCurrentTenant(restaurantId != null ? restaurantId.toString() : null);
            }
        }

        if (restaurantId == null) {
            throw new RuntimeException("Restaurant context not found");
        }

        RestaurantTable table = tableRepository.findByTableNumberAndRestaurantId(request.getTableId().intValue(), restaurantId)
                .orElseThrow(() -> new RuntimeException("Table " + request.getTableId() + " not found for this restaurant"));
        
        Restaurant restaurant = table.getRestaurant();
        if (restaurant == null) {
            restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant data not found"));
        }

        String sessionId = table.getCurrentSessionId();
        if (sessionId == null) {
            sessionId = java.util.UUID.randomUUID().toString();
            table.setCurrentSessionId(sessionId);
            table.setStatus("OCCUPIED");
            tableRepository.save(table);
        }

        RestaurantOrder order = new RestaurantOrder();
        order.setRestaurantTable(table);
        order.setSessionId(sessionId);
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setIsActive(true);
        order.setTotalAmount(0.0);
        order.setRestaurant(restaurant);
        order = orderRepository.save(order);

        double totalAmount = 0.0;
        for (OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            OrderItem orderItem = new OrderItem();
            orderItem.setRestaurantOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(menuItem.getPrice());
            orderItem.setRestaurant(restaurant);
            orderItemRepository.save(orderItem);
            order.getItems().add(orderItem);

            totalAmount += (menuItem.getPrice() * itemRequest.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        RestaurantOrder savedOrder = orderRepository.save(order);
        
        // Broadcast
        messagingTemplate.convertAndSend("/topic/" + restaurantId + "/kitchen", savedOrder);
        messagingTemplate.convertAndSend("/topic/" + restaurantId + "/admin", savedOrder);
        messagingTemplate.convertAndSend("/topic/customer/" + restaurantId + "/" + order.getRestaurantTable().getTableNumber(), savedOrder);
        
        return savedOrder;
    }

    @Transactional
    public RestaurantOrder addItemsToOrder(Long tableId, List<OrderItemRequest> items) {
        OrderRequest request = new OrderRequest();
        request.setTableId(tableId);
        request.setItems(items);
        return createOrder(request);
    }

    public List<RestaurantOrder> getAllOrders() {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        return restaurantId != null ? orderRepository.findAllByRestaurantId(restaurantId) : orderRepository.findAll();
    }

    public RestaurantOrder getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public RestaurantOrder updateOrderStatus(Long id, OrderStatus newStatus) {
        RestaurantOrder order = getOrder(id);
        OrderStatus currentStatus = order.getStatus();
        
        System.out.println("DEBUG: Updating status for Order #" + id + ": " + currentStatus + " -> " + newStatus);
        
        if (currentStatus == OrderStatus.REJECTED) {
            throw new RuntimeException("Order #" + id + " is already rejected and cannot be updated.");
        }
        if (currentStatus == OrderStatus.COMPLETED) {
            throw new RuntimeException("Order #" + id + " is already received/completed.");
        }

        if (currentStatus == OrderStatus.SERVED && newStatus != OrderStatus.COMPLETED) {
            throw new RuntimeException("Served order #" + id + " can only be moved to COMPLETED.");
        }
        
        if (newStatus.ordinal() < currentStatus.ordinal() && newStatus != OrderStatus.REJECTED) {
             throw new RuntimeException("Cannot move Order #" + id + " from " + currentStatus + " back to " + newStatus);
        }

        order.setStatus(newStatus);
        
        if (newStatus == OrderStatus.COMPLETED) {
            order.setIsActive(false);
            System.out.println("DEBUG: Order #" + id + " marked as INACTIVE (COMPLETED)");
        }
        
        try {
            RestaurantOrder updatedOrder = orderRepository.save(order);
            System.out.println("DEBUG: Successfully saved Order #" + id + " with status " + newStatus);
            
            // Broadcast
            messagingTemplate.convertAndSend("/topic/" + updatedOrder.getRestaurant().getId() + "/kitchen", updatedOrder);
            messagingTemplate.convertAndSend("/topic/" + updatedOrder.getRestaurant().getId() + "/admin", updatedOrder);
            messagingTemplate.convertAndSend("/topic/customer/" + updatedOrder.getRestaurant().getId() + "/" + order.getRestaurantTable().getTableNumber(), updatedOrder);
            
            return updatedOrder;
        } catch (Exception e) {
            System.err.println("ERROR: Failed to save status update for Order #" + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to update database for Order #" + id);
        }
    }

    @Transactional
    public RestaurantOrder rejectOrder(Long id, String reason) {
        RestaurantOrder order = getOrder(id);
        
        if (order.getStatus() == OrderStatus.SERVED || order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Cannot reject completed or paid orders");
        }
        
        order.setStatus(OrderStatus.REJECTED);
        order.setRejectionReason(reason);
        RestaurantOrder updatedOrder = orderRepository.save(order);
        
        messagingTemplate.convertAndSend("/topic/" + updatedOrder.getRestaurant().getId() + "/kitchen", updatedOrder);
        messagingTemplate.convertAndSend("/topic/" + updatedOrder.getRestaurant().getId() + "/admin", updatedOrder);
        messagingTemplate.convertAndSend("/topic/customer/" + updatedOrder.getRestaurant().getId() + "/" + order.getRestaurantTable().getTableNumber(), updatedOrder);
        
        return updatedOrder;
    }

    @Transactional
    public RestaurantOrder updatePaymentStatus(Long id, PaymentStatus status, String paymentMethod) {
        RestaurantOrder order = getOrder(id);
        String sessionId = order.getSessionId();
        
        System.out.println("DEBUG: Updating payment status for Order #" + id + " to " + status + " (Session: " + sessionId + ")");
        
        if (sessionId != null && status == PaymentStatus.PAID) {
            Restaurant restaurant = order.getRestaurant();
            Long restaurantId = (restaurant != null) ? restaurant.getId() : null;
            
            List<RestaurantOrder> sessionOrders = orderRepository.findAllBySessionIdAndRestaurantId(sessionId, restaurantId);
            double subtotal = 0.0;
            
            System.out.println("DEBUG: Marking " + sessionOrders.size() + " orders as PAID for session " + sessionId);
            
            for (RestaurantOrder o : sessionOrders) {
                o.setPaymentStatus(PaymentStatus.PAID);
                o.setStatus(OrderStatus.COMPLETED);
                o.setPaymentMethod(paymentMethod != null ? paymentMethod.toUpperCase() : "CASH");
                o.setIsActive(false);
                orderRepository.save(o);
                subtotal += o.getTotalAmount();
                
                messagingTemplate.convertAndSend("/topic/" + restaurantId + "/kitchen", o);
                messagingTemplate.convertAndSend("/topic/" + restaurantId + "/admin", o);
                messagingTemplate.convertAndSend("/topic/customer/" + restaurantId + "/" + o.getRestaurantTable().getTableNumber(), o);
            }
            
            try {
                double taxRate = (restaurant != null) ? restaurant.getTaxPercentageSafe() : 5.0;
                double serviceRate = (restaurant != null) ? restaurant.getServiceChargeSafe() : 0.0;
                
                double taxAmount = subtotal * (taxRate / 100);
                double serviceCharge = subtotal * (serviceRate / 100);
                double total = subtotal + taxAmount + serviceCharge;
                
                Payment payment = new Payment();
                payment.setSessionId(sessionId);
                payment.setSubtotal(subtotal);
                payment.setTaxAmount(taxAmount);
                payment.setServiceCharge(serviceCharge);
                payment.setTotalAmount(total);
                payment.setPaymentDate(java.time.LocalDateTime.now());
                payment.setPaymentMethod(paymentMethod != null ? paymentMethod.toUpperCase() : "CASH");
                payment.setRestaurant(restaurant);
                paymentRepository.save(payment);
                
                RestaurantTable table = order.getRestaurantTable();
                if (table != null) {
                    table.setCurrentSessionId(null);
                    table.setStatus("AVAILABLE");
                    tableRepository.save(table);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to record payment details: " + e.getMessage());
            }
            
            return order;
        } else {
            order.setPaymentStatus(status);
            if (status == PaymentStatus.PAID) {
                order.setStatus(OrderStatus.COMPLETED);
                order.setPaymentMethod(paymentMethod != null ? paymentMethod.toUpperCase() : "CASH");
                order.setIsActive(false);
            }
            RestaurantOrder updatedOrder = orderRepository.save(order);
            
            messagingTemplate.convertAndSend("/topic/" + (order.getRestaurant() != null ? order.getRestaurant().getId() : "0") + "/kitchen", updatedOrder);
            messagingTemplate.convertAndSend("/topic/" + (order.getRestaurant() != null ? order.getRestaurant().getId() : "0") + "/admin", updatedOrder);
            messagingTemplate.convertAndSend("/topic/customer/" + (order.getRestaurant() != null ? order.getRestaurant().getId() : "0") + "/" + order.getRestaurantTable().getTableNumber(), updatedOrder);
            
            return updatedOrder;
        }
    }

    @Transactional(readOnly = true)
    public List<RestaurantOrder> getSessionOrders(Long tableId) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        RestaurantTable table = tableRepository.findByTableNumberAndRestaurantId(tableId.intValue(), restaurantId)
                .orElseThrow(() -> new RuntimeException("Table " + tableId + " not found"));
        
        String sessionId = table.getCurrentSessionId();
        if (sessionId == null) {
            return List.of();
        }
        
        return orderRepository.findAllBySessionIdAndRestaurantId(sessionId, restaurantId);
    }
}
