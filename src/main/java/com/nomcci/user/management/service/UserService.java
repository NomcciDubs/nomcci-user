package com.nomcci.user.management.service;

import com.nomcci.user.management.model.User;
import com.nomcci.user.management.model.UserDTO;
import com.nomcci.user.management.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Obtener el perfil del usuario autenticado
    public UserDTO getAuthenticatedUserProfile() {
        Long userId = getAuthenticatedUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setPhone(user.getPhone());
        userDTO.setCountry(user.getCountry());
        userDTO.setCity(user.getCity());
        return userDTO;
    }

    // Actualizar el perfil del usuario autenticado
    public void updateAuthenticatedUserProfile(UserDTO userDTO) {
        Long userId = getAuthenticatedUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        user.setCountry(userDTO.getCountry());
        user.setCity(userDTO.getCity());
        userRepository.save(user);
    }

    // Cambiar la contraseña del usuario autenticado
    public void updateAuthenticatedUserPassword(String newPassword) {
        Long userId = getAuthenticatedUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Obtener el ID del usuario autenticado desde el contexto de seguridad
    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado.");
        }

        // Aquí se asume que el `subject` (claim del token) es el `userId`.
        String userId = authentication.getName();
        return Long.parseLong(userId);
    }
}
