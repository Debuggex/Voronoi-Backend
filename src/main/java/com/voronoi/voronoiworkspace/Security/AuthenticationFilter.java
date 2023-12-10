package com.voronoi.voronoiworkspace.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voronoi.voronoiworkspace.Entities.User;
import com.voronoi.voronoiworkspace.Repositories.UserRepository;
import com.voronoi.voronoiworkspace.RequestDTO.Login;
import com.voronoi.voronoiworkspace.ResponseDTO.BaseResponse;
import com.voronoi.voronoiworkspace.ResponseDTO.LoginResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        super.setFilterProcessesUrl("/auth/login");

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        Login loginDTO;
        try {

            loginDTO = new ObjectMapper().readValue(request.getInputStream(), Login.class);
            log.info(loginDTO.getEmail(), loginDTO.getPassword());



            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(), loginDTO.getPassword());
            try {
                return authenticationManager.authenticate(authenticationToken);
            } catch (RuntimeException e) {

                BaseResponse baseResponse = new BaseResponse();
                baseResponse.setResponseCode(0);
                baseResponse.setResponseBody(null);
                baseResponse.setResponseMessage("Invalid Credentials");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                response.setStatus(500);
                new ObjectMapper().writeValue(response.getOutputStream(),baseResponse);

                return null;
            }
        } catch (IOException e) {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setResponseCode(0);
            baseResponse.setResponseBody(null);
            baseResponse.setResponseMessage("Invalid Credentials");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(500);
            try {
                new ObjectMapper().writeValue(response.getOutputStream(),baseResponse);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return null;
        }


    }


        @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
            Date expirationTime = new Date(System.currentTimeMillis() + 60 * 60 * 1000);
            String accessToken = JWT.create().withSubject(user.getUsername()).withIssuer(request.getRequestURI()).withExpiresAt(expirationTime).withClaim("roles",user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refreshToken = JWT.create().withSubject(user.getUsername()).withIssuer(request.getRequestURI()).withExpiresAt(expirationTime).withClaim("roles",user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
//        response.setHeader("accessToken",accessToken);
//        response.setHeader("refreshToken",refreshToken);
        Map<String, String> tokens = new HashMap<>();

        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(accessToken);
        String username = decodedJWT.getSubject();
        User isUserActive = userRepository.findAll().stream().filter(
                user1 -> user1.getEmail().equals(username)
        ).findFirst().get();
        LoginResponse logInResponse = new LoginResponse();

        {
            BaseResponse<LoginResponse> response1 = new BaseResponse<>();
            logInResponse.setId(isUserActive.getId());
            logInResponse.setEmail(isUserActive.getEmail());
            logInResponse.setUsername(isUserActive.getUsername());
            logInResponse.setFirstName(isUserActive.getFirstName());
            logInResponse.setLastName(isUserActive.getLastName());
            logInResponse.setIsAdmin(isUserActive.getIsAdmin());
            logInResponse.setAccessToken(accessToken);
            logInResponse.setRefreshToken(refreshToken);

            response1.setResponseCode(1);
            response1.setResponseMessage("LogIn Successfully");
            response1.setResponseBody(logInResponse);
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getOutputStream(), response1);
        }


//        tokens.put("accessToken",accessToken);
//        tokens.put("refreshToken",refreshToken);


    }
}
