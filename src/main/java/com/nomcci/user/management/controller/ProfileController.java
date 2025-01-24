package com.nomcci.user.management.controller;

import com.nomcci.user.management.model.UserDTO;
import com.nomcci.user.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    // Obtener perfil del usuario
    @GetMapping
    public ResponseEntity<UserDTO> getProfile() {
        UserDTO userDTO = userService.getAuthenticatedUserProfile();
        return ResponseEntity.ok(userDTO);
    }

    // Editar perfil del usuario
    @PutMapping
    public ResponseEntity<String> updateProfile(@RequestBody UserDTO userDTO) {
        userService.updateAuthenticatedUserProfile(userDTO);
        return ResponseEntity.ok("Perfil actualizado exitosamente.");
    }

    // Cambiar contraseña del usuario
    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody String newPassword) {
        userService.updateAuthenticatedUserPassword(newPassword);
        return ResponseEntity.ok("Contraseña actualizada exitosamente.");
    }
}
