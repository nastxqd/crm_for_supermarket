package com.shop.shop.controllers;

import com.shop.shop.buiseness.UserService;
import com.shop.shop.data.entities.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/user/profile")
    public User getCurrentUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
    @GetMapping("/api/user/warehouse")
    public WarehouseInfo getUserWarehouse(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        WarehouseInfo info = new WarehouseInfo();
        info.setShopId(user.getShopid());
        info.setRole(user.getRole());
        info.setCanSelectWarehouse(isAdmin(user.getRole()));

        return info;
    }

    private boolean isAdmin(String role) {
        return role != null && role.toUpperCase().contains("ADMIN");
    }

    // Внутренний класс для ответа
    class WarehouseInfo {
        private Long shopId;
        private String role;
        private boolean canSelectWarehouse;

        // геттеры и сеттеры
        public Long getShopId() { return shopId; }
        public void setShopId(Long shopId) { this.shopId = shopId; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public boolean isCanSelectWarehouse() { return canSelectWarehouse; }
        public void setCanSelectWarehouse(boolean canSelectWarehouse) { this.canSelectWarehouse = canSelectWarehouse; }
    }
}