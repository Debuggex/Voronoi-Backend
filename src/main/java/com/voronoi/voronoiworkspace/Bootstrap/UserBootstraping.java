package com.voronoi.voronoiworkspace.Bootstrap;

import com.voronoi.voronoiworkspace.Entities.User;
import com.voronoi.voronoiworkspace.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Configuration
public class UserBootstraping implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        User user = userRepository.findByRole("SUPERADMIN");
        if (user != null) return;
        User user1 = new User();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

        // Format the current date and time using the formatter
        String formattedDateTime = now.format(formatter);
        user1.setRegistrationDate(formattedDateTime);
        user1.setNextPaymentDue(formattedDateTime);

        user1.setEmail("broadwaylamb13@gmail.com");
        user1.setFirstName("Broadway");
        user1.setIsAdmin(true);
        user1.setLastName("Lamb");
        user1.setPlainPassword("DevDev2024");
        user1.setPassword(new BCryptPasswordEncoder().encode("DevDev2024"));
        user1.setUsername("broadwaylamb13");
        user1.setRole("SUPERADMIN");
        user1.setSubscriptionPlan("Premium");
        user1.setStatus("Subscribed");
        user1.setUserType("Employers");
        userRepository.save(user1);
    }
}
