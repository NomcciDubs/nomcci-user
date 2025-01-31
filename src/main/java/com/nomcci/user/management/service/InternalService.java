package com.nomcci.user.management.service;

import com.nomcci.user.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.nomcci.user.management.model.User;

@Service
public class InternalService {

    private final UserRepository userRepository;

    @Autowired
    public InternalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Obtiene el primer nombre del usuario por su ID.
     *
     * @param userId ID del usuario.
     * @return Primer nombre del usuario.
     * @throws IllegalArgumentException si el usuario no existe.
     */
    public String getFirstNameById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
        return user.getFirstName();
    }

    /**
     * Obtiene el ID del usuario por su correo electrónico.
     *
     * @param email Correo electrónico del usuario.
     * @return ID del usuario.
     * @throws IllegalArgumentException si el usuario no existe.
     */
    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con correo: " + email));
        return user.getId();
    }
}