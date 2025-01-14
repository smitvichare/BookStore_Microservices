package com.user_service.service;

import com.user_service.dto.LoginDTO;
import com.user_service.dto.UserDTO;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);

    String login(LoginDTO loginDTO);

    UserDTO getUser(String token);

    String removeUser(String token);

    String forgotPassword(String email);

    String resetPassword(String token, String password);
}
