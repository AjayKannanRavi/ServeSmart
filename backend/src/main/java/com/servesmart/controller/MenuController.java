package com.servesmart.controller;

import com.servesmart.dto.MenuItemRequest;
import com.servesmart.entity.Category;
import com.servesmart.entity.MenuItem;
import com.servesmart.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        return ResponseEntity.ok(menuService.getAllMenuItems());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(menuService.getAllCategories());
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        return ResponseEntity.ok(menuService.addCategory(category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        menuService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<MenuItem> addItem(
            @RequestPart("item") MenuItemRequest request,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {
        return ResponseEntity.ok(menuService.addItem(request, image));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<MenuItem> updateItem(
            @PathVariable("id") Long id,
            @RequestPart("item") MenuItemRequest request,
            @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {
        return ResponseEntity.ok(menuService.updateItem(id, request, image));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateItemSimple(
            @PathVariable("id") Long id,
            @RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(menuService.updateItem(id, request, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") Long id) {
        menuService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
