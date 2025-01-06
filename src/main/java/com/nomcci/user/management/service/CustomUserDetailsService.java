package com.nomcci.user.management.service;

import com.nomcci.user.management.model.User;
import com.nomcci.user.management.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Método personalizado para cargar un usuario por su ID.
     *
     * @param userId ID del usuario.
     * @return Detalles del usuario para Spring Security.
     * @throws UsernameNotFoundException Si el usuario no se encuentra.
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(user.getId()))
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    /**
     * Sobreescribe el método de la interfaz UserDetailsService
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return loadUserById(Long.valueOf(userId));
    }
}
