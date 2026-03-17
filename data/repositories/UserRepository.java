package com.shop.shop.data.repositories;

import com.shop.shop.data.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public User save(User user);
    public void delete(User user);
    public Optional<User> findByUsername(String username);
    public List<User> findByShopid(Long shopid);
    public boolean existsByUsername(String username);

}
