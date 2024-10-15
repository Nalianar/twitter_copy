package org.example.controller

import org.example.dtos.AuthRequestDTO
import org.example.service.AuthService
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users/auth")
class AuthController {

    @Autowired
    AuthService authService

    @PostMapping("/authenticate")
    String createAuthenticationToken(@RequestBody AuthRequestDTO authRequest) {
        return authService.createAuthenticationToken(authRequest)
    }

    @PostMapping("/register")
    def register(@RequestBody AuthRequestDTO createUserDTO) {
        return authService.registerUser(createUserDTO)
    }

}
