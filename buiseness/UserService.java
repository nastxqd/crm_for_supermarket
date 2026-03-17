package com.shop.shop.buiseness;

import com.shop.shop.data.entities.User;
import com.shop.shop.data.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    // Добавление нового пользователя
    public void addUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    // Получить пользователя по имени
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public List<User> getAll(){
        return userRepository.findAll();
    }
    public List<User> getByShopid(Long shopid){
        return userRepository.findByShopid(shopid);
    }
    public void deleteUser(User user){
        userRepository.delete(user);
    }
    @Transactional
    public User updateUser(User user) {
        // Проверяем, существует ли пользователь с таким ID
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + user.getId()));

        // Проверяем уникальность username (если он изменился)
        if (!existingUser.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new RuntimeException("Username already exists: " + user.getUsername());
            }
        }

        // Обновляем поля
        existingUser.setId(existingUser.getId());
        System.out.println(existingUser.getId());
        existingUser.setUsername(user.getUsername());
        existingUser.setFullName(user.getFullName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        existingUser.setRole(user.getRole());
        existingUser.setShopid(user.getShopid());
        existingUser.setIsActive(user.getIsActive());

        // Обновляем пароль только если он передан
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(encoder.encode(user.getPassword()));
        }

        // Сохраняем существующего пользователя с обновленными полями
        return userRepository.save(existingUser);
    }



}