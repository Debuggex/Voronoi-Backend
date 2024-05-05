package com.voronoi.voronoiworkspace.Services;

import com.voronoi.voronoiworkspace.Entities.User;
import com.voronoi.voronoiworkspace.Repositories.UserRepository;
import com.voronoi.voronoiworkspace.RequestDTO.EditUser;
import com.voronoi.voronoiworkspace.RequestDTO.GetUser;
import com.voronoi.voronoiworkspace.ResponseDTO.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()){
            throw new UsernameNotFoundException("User with this email does not exist");
        }else{
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.get().getRole()));

            return new org.springframework.security.core.userdetails.User(user.get().getEmail(), user.get().getPassword(), authorities);
        }
    }

    @Transactional
    public List<User> getAllUsers(){
        List<User> users = userRepository.findUser();
        return users;
    }

    public BaseResponse editUser(EditUser editUser){
        User user = userRepository.findById(Long.valueOf(editUser.getUser().getId())).get();
        user.setEmail(editUser.getUser().getEmail());
        user.setFirstName(editUser.getUser().getFirstName());
        user.setPassword(passwordEncoder.encode(editUser.getUser().getPassword()));
        user.setIsAdmin(editUser.getUser().getIsAdmin());
        user.setPlainPassword(editUser.getUser().getPassword());
        user.setRole(editUser.getUser().getRole().toUpperCase());
            if (user.getRole().equalsIgnoreCase("internal") || user.getRole().equalsIgnoreCase("superadmin") ){
                user.setUserType("Employers");
            }else{
                user.setUserType("External Users");
            }
        userRepository.save(user);
        BaseResponse response = new BaseResponse();
        response.setResponseMessage("Request Proceed Successfully");
        response.setResponseCode(1);
        return response;
    }

    public BaseResponse<User> getUser(GetUser getUser){
        BaseResponse<User> response = new BaseResponse<>();
        response.setResponseCode(1);
        response.setResponseMessage("Request Proceed Successfully");
        response.setResponseBody(userRepository.findById(Long.valueOf(getUser.getId())).get());
        return response;
    }

    @Transactional
    public BaseResponse addUser(EditUser user){
        BaseResponse<User> response = new BaseResponse<>();
        Optional<User> fUser = userRepository.findByEmail(user.getUser().getEmail());
        if (fUser.isPresent()){
            response.setResponseCode(0);
            response.setResponseMessage("User with this email already Exists");
            return response;
        }
        if (user.getUser().getRole().equals("ADMIN") || user.getUser().getRole().equals("INTERNAL") ){
            user.getUser().setUserType("Employers");
            user.getUser().setSubscriptionPlan("Free");
        }else{
            user.getUser().setSubscriptionPlan("Premium");
            user.getUser().setStatus("Subscribed");
            user.getUser().setRole("EXTERNAL");
            user.getUser().setUserType("External Users");
        }
        user.getUser().setStatus("UnSubscribed");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

        // Format the current date and time using the formatter
        String formattedDateTime = now.format(formatter);
        user.getUser().setRegistrationDate(formattedDateTime);
        user.getUser().setNextPaymentDue(formattedDateTime);
        user.getUser().setPassword(passwordEncoder.encode(user.getUser().getPassword()));
        User saved = userRepository.save(user.getUser());
        response.setResponseCode(1);
        response.setResponseMessage("Request Proceed Successfully");
        return response;
    }

    public List<User> deleteUser(GetUser getUser){
        userRepository.deleteById(Long.valueOf(getUser.getId()));
        List<User> users = userRepository.findUser();
        return users;
    }
}
