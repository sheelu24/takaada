package com.takaada.integration.controller;

import com.takaada.integration.service.SyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/external")
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    // On-demand sync: fetches from external source (dummy data for now), stores raw + normalized
    // Note: This can be extended to scheduled jobs syncing data
    @PostMapping("/sync")
    public ResponseEntity<Map<String, String>> sync() {
        syncService.runSync();
        return ResponseEntity.ok(Map.of("status", "ok", "message", "Sync completed"));
    }
}
