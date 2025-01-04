package com.nomcci.user.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping("/keepalive")
    public ResponseEntity<String> keepAlive() {
        return ResponseEntity.ok("Token valido y funcionando");
    }
}
