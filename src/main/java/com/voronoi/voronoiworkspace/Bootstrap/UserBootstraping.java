package com.voronoi.voronoiworkspace.Bootstrap;

import com.voronoi.voronoiworkspace.Entities.User;
import com.voronoi.voronoiworkspace.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class UserBootstraping implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        for(int i = 0;i<4;i++){
            User user = new User();
            user.setEmail("the@user"+i+".dev");
            user.setFirstName("user"+i);
            user.setPlainPassword("sample1234");
            user.setPassword(new BCryptPasswordEncoder().encode("sample1234"));
            user.setIsAdmin(false);
            user.setIsInternal(i % 2 == 0);
            User u = userRepository.save(user);
        }
        User user = new User();
        user.setEmail("the@raajpatel.dev");
        user.setPassword(new BCryptPasswordEncoder().encode("sample1234"));
        user.setIsAdmin(true);
        User u = userRepository.save(user);
    }
}
