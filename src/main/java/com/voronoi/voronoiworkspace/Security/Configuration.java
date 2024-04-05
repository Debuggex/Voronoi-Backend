package com.voronoi.voronoiworkspace.Security;

import com.voronoi.voronoiworkspace.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@org.springframework.context.annotation.Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class Configuration {
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;


    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(encoder());
        http.csrf(AbstractHttpConfigurer::disable);
        http.logout(lOut->{
            lOut.logoutUrl("/auth/logout").invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID").logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)));

            // Allow CORS for the logout URL
            lOut.addLogoutHandler((request, response, authentication) -> {
                response.addHeader("Access-Control-Allow-Origin", "*");
                response.addHeader("Access-Control-Allow-Credentials", "true");
            });
        });
        http.apply(new CustomDSL(userRepository));
        http.sessionManagement(session->{
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });
        http.headers().frameOptions().disable();
        http.authorizeHttpRequests(authorize->{
           authorize.antMatchers("/h2-console/**").permitAll();
           authorize.antMatchers("/admin/v1/previewImage/**").permitAll();
            authorize.antMatchers("/admin/v1/addUser/**").permitAll();
        });
        http.authorizeHttpRequests(authorize->{
            authorize.antMatchers("/admin/v1/uploadFile").hasAuthority("INTERNAL");
            authorize.antMatchers("/admin/**").hasAuthority("SUPERADMIN");
        });

        http.authorizeHttpRequests(authorize->{
            authorize.anyRequest().authenticated();
        });

        http.addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    public static class CustomDSL extends AbstractHttpConfigurer<CustomDSL, HttpSecurity> {

        private final UserRepository userRepository;

        public CustomDSL(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public void configure(HttpSecurity http) {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http.addFilter(new AuthenticationFilter(authenticationManager, userRepository));
        }

        public CustomDSL customDsl() {
            return new CustomDSL(userRepository);
        }
    }
}
