package com.user_service.security;

import com.user_service.model.User;
import com.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    public  CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User getUserByEmail = userRepository.findByEmail(email);

        if(getUserByEmail==null) {

            throw new UsernameNotFoundException("EMAIL NOT FOUND!!");
        }

        return  new CustomUserDetails(getUserByEmail);
    }

}