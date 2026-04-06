package com.servesmart.service;

import com.servesmart.config.TenantContext;
import com.servesmart.config.AppWorkflowProperties;
import com.servesmart.dto.MenuItemRequest;
import com.servesmart.entity.Category;
import com.servesmart.entity.MenuItem;
import com.servesmart.entity.Restaurant;
import com.servesmart.repository.CategoryRepository;
import com.servesmart.repository.MenuItemRepository;
import com.servesmart.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final RestaurantRepository restaurantRepository;
    private final FileStorageService fileStorageService;
    private final AppWorkflowProperties appWorkflowProperties;

    public List<MenuItem> getAllMenuItems() {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        if (restaurantId == null) {
            return java.util.Collections.emptyList();
        }
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    public List<Category> getAllCategories() {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        if (restaurantId == null) {
            return java.util.Collections.emptyList();
        }
        return categoryRepository.findByRestaurantId(restaurantId);
    }

    public Category addCategory(Category requestCategory) {
        Long restaurantId = TenantContext.requireCurrentTenantAsLong();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant context not found"));
        
        Category category = new Category();
        category.setName(requestCategory.getName());
        category.setRestaurant(restaurant);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (restaurantId != null && category.getRestaurant() != null
                && !restaurantId.equals(category.getRestaurant().getId())) {
            throw new RuntimeException("Unauthorized access to delete category");
        }
        categoryRepository.delete(category);
    }

    public MenuItem addItem(MenuItemRequest request, org.springframework.web.multipart.MultipartFile image) {
        Long restaurantId = TenantContext.requireCurrentTenantAsLong();
        if (request.getCategoryId() == null) {
            throw new RuntimeException("Category ID is required to add an item. Please select or create a category first.");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (category.getRestaurant() == null || !restaurantId.equals(category.getRestaurant().getId())) {
            throw new RuntimeException("Selected category does not belong to this restaurant");
        }
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant context not found"));

        MenuItem item = new MenuItem();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setAvailable(request.getAvailable() != null ? request.getAvailable() : true);
        item.setCategory(category);

        if (image != null && !image.isEmpty()) {
            String fileName = fileStorageService.storeFile(image);
            item.setImageUrl(appWorkflowProperties.getUrl().getBackendBase() + "/uploads/" + fileName);
        } else if (request.getImageUrl() != null) {
            item.setImageUrl(request.getImageUrl());
        }

        item.setRestaurant(restaurant);
        return menuItemRepository.save(item);
    }

    public MenuItem updateItem(Long id, MenuItemRequest request, org.springframework.web.multipart.MultipartFile image) {
        Long restaurantId = TenantContext.requireCurrentTenantAsLong();
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        
        if (restaurantId != null && item.getRestaurant() != null
                && !restaurantId.equals(item.getRestaurant().getId())) {
            throw new RuntimeException("Unauthorized access to menu item");
        }

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        if (request.getAvailable() != null) {
            item.setAvailable(request.getAvailable());
        }

        if (request.getCategoryId() != null && !item.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            if (category.getRestaurant() == null || !restaurantId.equals(category.getRestaurant().getId())) {
                throw new RuntimeException("Selected category does not belong to this restaurant");
            }
            item.setCategory(category);
        }

        if (image != null && !image.isEmpty()) {
            String fileName = fileStorageService.storeFile(image);
            item.setImageUrl(appWorkflowProperties.getUrl().getBackendBase() + "/uploads/" + fileName + "?t=" + System.currentTimeMillis());
        } else if (request.getImageUrl() != null) {
            item.setImageUrl(request.getImageUrl());
        }

        return menuItemRepository.save(item);
    }

    public void deleteItem(Long id) {
        Long restaurantId = TenantContext.getCurrentTenantAsLong();
        MenuItem item = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        
        if (restaurantId != null && item.getRestaurant() != null
                && !restaurantId.equals(item.getRestaurant().getId())) {
            throw new RuntimeException("Unauthorized access to delete menu item");
        }
        menuItemRepository.delete(item);
    }
}
