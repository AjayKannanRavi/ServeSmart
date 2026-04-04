package com.servesmart.controller;

import com.servesmart.dto.DailyUsageRequest;
import com.servesmart.entity.DailyUsageLog;
import com.servesmart.service.ClosingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/closing")
@RequiredArgsConstructor
public class ClosingController {

    private final ClosingService closingService;

    @PostMapping
    public ResponseEntity<List<DailyUsageLog>> performClosing(@RequestBody DailyUsageRequest request) {
        return ResponseEntity.ok(closingService.performDayClosing(request));
    }
}
