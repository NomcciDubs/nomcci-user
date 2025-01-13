package com.nomcci.user.management.service;

import com.nomcci.user.management.dto.WalletRequest;
import com.nomcci.user.management.model.User;
import com.nomcci.user.management.model.Role;
import com.nomcci.user.management.repository.UserRepository;
import com.nomcci.user.management.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    @Value("${wallet.service.url}")
    private String walletServiceUrl;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
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

        User savedUser = userRepository.save(user);

        createWalletForUser(savedUser);

        return savedUser;
    }

    private void createWalletForUser(User user) {
        // Crear un objeto con los datos necesarios para crear la wallet
        WalletRequest walletRequest = new WalletRequest(user.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtUtil.generateToken(user.getId()));
        System.out.println(headers);
        // Crear la solicitud
        HttpEntity<WalletRequest> requestEntity = new HttpEntity<>(walletRequest, headers);

        // Enviar la solicitud al microservicio de wallet
        String endpoint = walletServiceUrl + "/user/create";
        restTemplate.postForEntity(endpoint, requestEntity, Void.class);
    }



}
