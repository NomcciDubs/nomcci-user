package com.nomcci.user.management.controller;
import com.nomcci.user.management.util.JWKSUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class JwksController {
    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJWKS() {
        return JWKSUtil.generateJWKS();
    }
}
