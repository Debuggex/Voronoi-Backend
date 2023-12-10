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

        int j = 1;
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setEmail("the@user" + i + ".dev");
            user.setFirstName("user" + i);
            user.setPlainPassword("sample1234");
            user.setPassword(new BCryptPasswordEncoder().encode("sample1234"));
            user.setIsAdmin(false);
            if (j==1){
                user.setSubscriptionPlan("Free");
                user.setStatus("Subscribed");
            }
            if (j==2){
                user.setSubscriptionPlan("Premium");
                user.setStatus("UnSubscribed");
            }
            if (j==3){
                user.setSubscriptionPlan("Platinum");
                user.setStatus("Paused");
            }
            if (j%2==0){
                user.setUserType("External Users");
            }else{
                user.setUserType("Employers");
            }
            j++;
            if (j==4){
                j=1;
            }
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

            // Format the current date and time using the formatter
            String formattedDateTime = now.format(formatter);
            user.setRegistrationDate(formattedDateTime);
            user.setNextPaymentDue(formattedDateTime);
            User u = userRepository.save(user);
        }
        User user = new User();
        user.setEmail("the@raajpatel.dev");
        user.setPassword(new BCryptPasswordEncoder().encode("sample1234"));
        user.setIsAdmin(true);
        User u = userRepository.save(user);
    }
}
