package com.user_service.controller;


import com.user_service.dto.LoginDTO;
import com.user_service.dto.UserDTO;
import com.user_service.exception.UnAuthorizedUserException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public UserDTO registerUser(@RequestBody UserDTO userDTO)
    {
        return userService.registerUser(userDTO);
    }

    @GetMapping("/login")
    public String login(@RequestBody LoginDTO loginDTO)
    {
        return userService.login(loginDTO);
    }

    @PostMapping("/forget")
    public String forgetPassword(@RequestParam String email){
        return userService.forgotPassword(email);
    }

    @PatchMapping("/reset")
    public String resetPassword(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String,String> reqPassword){
        String password = reqPassword.get("newPassword");

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            return userService.resetPassword(token,password);
        }else{
            throw new UnAuthorizedUserException("UnAuthorized User");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String authHeader){
        if(authHeader != null && authHeader.startsWith("Bearer")){
            String token = authHeader.substring(7);
            UserDTO userDTO = userService.getUser(token);

            if(userDTO != null){
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(new UserNotFoundException("User Not Found"),HttpStatus.NOT_FOUND);
            }
        }else {
            throw new UnAuthorizedUserException("UnAuthorized User");
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeUser(@RequestHeader("Authorization") String authHeader){
        if(authHeader != null && authHeader.startsWith("Bearer")){
            String token = authHeader.substring(7);
            String isDeleted = userService.removeUser(token);

            return new ResponseEntity<>(isDeleted,HttpStatus.OK);
        }else{
            throw new UnAuthorizedUserException("UnAuthorized User");
        }
    }
}