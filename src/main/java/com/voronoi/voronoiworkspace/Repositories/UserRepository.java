package com.voronoi.voronoiworkspace.Repositories;

import com.voronoi.voronoiworkspace.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    User findByRole(String role);

    @Query(value = "SELECT DISTINCT u FROM User u WHERE u.role IN ('ADMIN', 'SUBSCRIBED', 'INTERNAL')")
    List<User> findUser();
}
