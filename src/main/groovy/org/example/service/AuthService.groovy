package org.example.service

import org.example.configuration.jwt.JwtTokenUtil
import org.example.dtos.AuthRequestDTO
import org.example.model.User
import org.example.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService {

    @Autowired
    AuthenticationManager authenticationManager

    @Autowired
    CustomUserDetailsService userDetailsService

    @Autowired
    JwtTokenUtil jwtTokenUtil

    @Autowired
    UserRepository userRepository

    @Autowired
    PasswordEncoder passwordEncoder

    String createAuthenticationToken(AuthRequestDTO authRequestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.username, authRequestDTO.password)
        )
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequestDTO.username)
        jwtTokenUtil.generateToken(userDetails)
    }

    def registerUser(AuthRequestDTO createUserDTO) {
        def isUserExists = userRepository.findByUsername(createUserDTO.username)
        if(isUserExists.isPresent()){
            throw new IllegalArgumentException("User with username" + createUserDTO.username + "is already exists")
        }

        String encodedPassword = passwordEncoder.encode(createUserDTO.password)
        def user = new User(username: createUserDTO.username, password: encodedPassword, roles: ['USER'])
        return userRepository.save(user)
    }
}
