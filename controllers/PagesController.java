package com.shop.shop.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

    @GetMapping("/main")
    public String index() {
        return "redirect:/pages/index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "forward:/pages/login.html";
    }

    @GetMapping("/profile")
    public String profile() {
        return "forward:/pages/profile.html";
    }

    @GetMapping("/batches")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SENIOR_STOREKEEPER', 'STOREKEEPER')")
    public String batches() {
        return "forward:/pages/batches.html";
    }

    @GetMapping("/batches/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SENIOR_STOREKEEPER')")
    public String createBatch() {
        return "forward:/pages/createBatch.html";
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SENIOR_STOREKEEPER', 'STOREKEEPER')")
    public String inventory() {
        return "forward:/pages/inventory.html";
    }

    @GetMapping("/movements")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SENIOR_STOREKEEPER', 'STOREKEEPER')")
    public String movements() {
        return "forward:/pages/movements.html";
    }

    @GetMapping("/admin/pannel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String adminPannel() {
        return "forward:/pages/adminPannel.html";
    }

    @GetMapping("/admin/users/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createUser() {
        return "forward:/pages/createUser.html";
    }

    @GetMapping("/warehouses")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String warehouses() {
        return "forward:/pages/warehouses.html";
    }

    @GetMapping("/suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String suppliers() {
        return "forward:/pages/suppliers.html";
    }
}