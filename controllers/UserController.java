package com.shop.shop.controllers;

import com.shop.shop.buiseness.UserService;
import com.shop.shop.data.entities.User;
import com.shop.shop.data.repositories.MyUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {
    private UserService userService;
    public UserController(UserService userService){
        this.userService=userService;
    }
    @PostMapping("/new-user")
    @PreAuthorize("hasRole('ADMIN')")
    public String addUser(@RequestBody User user){
        userService.addUser(user);
        return "User is saved";
    }
    @GetMapping("/all-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity getAllUsers(){
        return ResponseEntity.ok(userService.getAll());
    }
    @GetMapping("/users-for-shop")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity getUsersForManager(@RequestParam Long id){
        return ResponseEntity.ok(userService.getByShopid(id));
    }
    @PostMapping("/delete-user")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@RequestBody User user){
        userService.deleteUser(user);
    }

    @PostMapping("update-user")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity updateUser(@RequestBody User user){
        return ResponseEntity.ok(userService.updateUser(user));
    }
    @GetMapping("/api/user/current")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal MyUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userDetails.getUser());
    }

}
