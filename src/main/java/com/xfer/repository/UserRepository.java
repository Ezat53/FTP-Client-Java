package com.xfer.repository;

import com.xfer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.role = 'admin'")
    List<User> findAdminUsers();
    
    @Query("SELECT u FROM User u WHERE u.role = 'user'")
    List<User> findRegularUsers();
}
