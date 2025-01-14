package com.user_service.service;


import com.user_service.dto.LoginDTO;
import com.user_service.dto.UserDTO;
import com.user_service.exception.UserNotFoundException;
import com.user_service.model.User;
import com.user_service.repository.UserRepository;
import com.user_service.security.JwtService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserServiceImpl implements  UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        User user = new User();

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setDob(userDTO.getDob());
        user.setEmail(userDTO.getEmail());
        user.setRegisteredDate(LocalDate.now());
        user.setUpdatedDate(userDTO.getUpdatedDate());
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());

        return mapToUserDTO(userRepository.save(user));
    }

    public UserDTO mapToUserDTO(User user){
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setDob(user.getDob());
        userDTO.setEmail(user.getEmail());
        userDTO.setRegisteredDate(user.getRegisteredDate());
        userDTO.setUpdatedDate(user.getUpdatedDate());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());

        return userDTO;
    }

    @Override
    public String login(LoginDTO loginDTO) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(),loginDTO.getPassword()));

        if(authentication.isAuthenticated()){
            return jwtService.generateToken(loginDTO.getEmail());
        }
        return "Incorrect Email or Password";
    }

    @Override
    public String forgotPassword(String email) {

        User user = userRepository.findByEmail(email);
        if(user == null){
            return "Email Not Found";
        }
        return jwtService.generateToken(email);
    }

    @Override
    public String resetPassword(String token, String newPassword) {
        if(newPassword == null || newPassword.trim().isEmpty()){
            return "Password cannot be null";
        }

        String email = jwtService.extractUserName(token);
        if(email==null ){
            return "Invalid token or email";
        }

        User user = userRepository.findByEmail(email);
        if(user == null){
            return "User not found";
        }
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        return "password successfully reset...";
    }

    @Override
    @CircuitBreaker(name = "userBreaker",fallbackMethod = "userBreakerFallback")
    public UserDTO getUser(String token) {
        String email = jwtService.extractUserName(token);
        User user = userRepository.findByEmail(email);

        if(email==null || user==null){
            return  null;
        }
        return mapToUserDTO(user);
    }

    public UserDTO userBreakerFallback(Exception e){

        UserDTO fallbackUser = new UserDTO();
        fallbackUser.setId(-1L); // Default ID to indicate fallback
        fallbackUser.setFirstName("Default");
        fallbackUser.setLastName("User");
        fallbackUser.setEmail("unknown@example.com");
        fallbackUser.setRole("GUEST");

        return fallbackUser;

    }

    @Override
    public String removeUser(String token){

        String email = jwtService.extractUserName(token);
        User user = userRepository.findByEmail(email);

        if(user == null){
            throw new UserNotFoundException("User Not Fount");
        }
        userRepository.deleteById(user.getId());
        return "Successfully remove user from DB";
    }
}