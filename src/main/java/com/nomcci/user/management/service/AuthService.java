package com.nomcci.user.management.service;

import com.nomcci.user.management.model.User;
import com.nomcci.user.management.model.Role;
import com.nomcci.user.management.repository.UserRepository;
import com.nomcci.user.management.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String authenticate(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                return jwtUtil.generateToken(user.getId());
            } else {
                throw new RuntimeException("La contraseña es incorrecta");
            }
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }



    public User register(User user) {
        if (user.getEmail() == null || user.getPassword() == null || user.getFirstName() == null ||
                user.getLastName() == null || user.getPhone() == null || user.getCountry() == null ||
                user.getCity() == null) {
            throw new IllegalArgumentException("Todos los campos obligatorios deben ser proporcionados.");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El correo ya está registrado. Por favor, utiliza otro.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRole(Role.USER);

        return userRepository.save(user);
    }



}
