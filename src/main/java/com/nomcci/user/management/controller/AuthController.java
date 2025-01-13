package com.nomcci.user.management.controller;

import com.nomcci.user.management.dto.LoginRequest;
import com.nomcci.user.management.model.User;
import com.nomcci.user.management.service.AuthService;
import com.nomcci.user.management.util.JWKSUtil;
import com.nomcci.user.management.util.RsaKeyUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PublicKey publicKey;

    public AuthController(AuthService authService, RsaKeyUtil rsaKeyUtil) {
        this.authService = authService;
        this.publicKey = rsaKeyUtil.loadPublicKey();
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User registeredUser = authService.register(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody LoginRequest loginRequest) {
        String token = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(token);
    }

}
