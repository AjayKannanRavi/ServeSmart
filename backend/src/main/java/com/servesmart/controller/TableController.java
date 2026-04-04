package com.servesmart.controller;

import com.servesmart.entity.RestaurantTable;
import com.servesmart.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @GetMapping
    public List<RestaurantTable> getAllTables() {
        return tableService.getAllTables();
    }

    @PostMapping
    public ResponseEntity<?> createTable(@RequestBody RestaurantTable table) {
        try {
            return ResponseEntity.ok(tableService.addTable(table));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantTable> updateTable(@PathVariable("id") Long id, @RequestBody RestaurantTable updated) {
        try {
            return ResponseEntity.ok(tableService.updateTable(id, updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/generate-qr")
    public ResponseEntity<Void> generateQr(@PathVariable("id") Long id) {
        try {
            tableService.generateQr(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable("id") Long id) {
        return ResponseEntity.status(403).build();
    }
}
