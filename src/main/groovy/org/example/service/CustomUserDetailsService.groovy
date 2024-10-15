package org.example.service

import org.example.configuration.CustomUserDetails
import org.example.exception.ResourceNotFoundException
import org.example.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository

    @Override
    UserDetails loadUserByUsername(String username) {
        def user = userRepository.findByUsername(username).orElseThrow{new ResourceNotFoundException("User with username" + username + "does not exist")}
        return new CustomUserDetails(user)
    }
}
