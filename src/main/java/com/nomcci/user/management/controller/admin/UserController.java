package com.nomcci.user.management.controller.admin;

import com.nomcci.user.management.model.User;
import com.nomcci.user.management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,      // Página inicial
            @RequestParam(defaultValue = "10") int size,    // Número de elementos por página
            @RequestParam(defaultValue = "") String search  // Parámetro de búsqueda
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<User> users;


        if (!search.isEmpty()) {
            users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(search, search, pageRequest);
        } else {
            users = userRepository.findAll(pageRequest);
        }

        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
