package com.nomcci.user.management.controller.internal;

import com.nomcci.user.management.service.InternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/internal")
public class InternalWalletController {

    private final InternalService internalService;

    @Autowired
    public InternalWalletController(InternalService internalService) {
        this.internalService = internalService;
    }

    @GetMapping("/wallet/get-user-by-id/{userId}")
    public ResponseEntity<String> getFirstNameById(@PathVariable Long userId) {
        String firstName = internalService.getFirstNameById(userId);
        return ResponseEntity.ok(firstName);
    }

    @GetMapping("/wallet/get-id-by-email")
    public ResponseEntity<Long> getUserIdByEmail(@RequestParam String email) {
        Long userId = internalService.getUserIdByEmail(email);
        return ResponseEntity.ok(userId);
    }
}

