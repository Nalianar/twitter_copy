package org.example.service

import org.example.configuration.jwt.JwtTokenUtil
import org.example.dtos.AuthRequestDTO
import org.example.model.User
import org.example.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification


class AuthServiceSpec extends Specification {

    def authenticationManager = Mock(AuthenticationManager)
    def userDetailsService = Mock(CustomUserDetailsService)
    def jwtTokenUtil = Mock(JwtTokenUtil)
    def userRepository = Mock(UserRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def authService = new AuthService(authenticationManager: authenticationManager,
            userDetailsService: userDetailsService,
            jwtTokenUtil: jwtTokenUtil,
            userRepository: userRepository,
            passwordEncoder: passwordEncoder)

    def "createAuthenticationToken should authenticate and generate token for valid user"() {
        given:
        def authRequestDTO = new AuthRequestDTO(username: "user1", password: "password")
        def userDetails = Mock(UserDetails)

        when:

        def token = authService.createAuthenticationToken(authRequestDTO)

        then:
        token == "mockJwtToken"
        1 * authenticationManager.authenticate(_) >> null
        1 * userDetailsService.loadUserByUsername(authRequestDTO.username)  >> userDetails
        1 * jwtTokenUtil.generateToken(userDetails) >> "mockJwtToken"
    }

    def "createAuthenticationToken should throw exception when authentication fails"() {
        given:
        def authRequestDTO = new AuthRequestDTO(username: "user1", password: "invalidPassword")

        when:

        authService.createAuthenticationToken(authRequestDTO)

        then:
        thrown(RuntimeException)
        1 * authenticationManager.authenticate(_) >> { throw new RuntimeException("Authentication failed") }
        0 * userDetailsService.loadUserByUsername(_)
        0 * jwtTokenUtil.generateToken(_)
    }

    def "registerUser should register new user and return token"() {
        given:
        def createUserDTO = new AuthRequestDTO(username: "newUser", password: "password")
        def user = new User(id: "123", username: createUserDTO.username, password: createUserDTO.password)

        when:

        def result = authService.registerUser(createUserDTO)

        then:
        result.username == createUserDTO.username
        1 * passwordEncoder.encode(createUserDTO.password) >> createUserDTO.password
        1 * userRepository.findByUsername(createUserDTO.username) >> Optional.empty()
        1 * userRepository.save(_)  >> user
    }

    def "registerUser should throw exception when user already exists"() {
        given:
        def createUserDTO = new AuthRequestDTO(username: "existingUser", password: "password")
        def existingUser = new User(id: "456", username: createUserDTO.username, password: createUserDTO.password)

        when:

        authService.registerUser(createUserDTO)

        then:
        thrown(IllegalArgumentException)
        1 * userRepository.findByUsername(createUserDTO.username) >> Optional.of(existingUser)
        0 * userRepository.save(_)
        0 * authenticationManager.authenticate(_)
        0 * userDetailsService.loadUserByUsername(_)
        0 * jwtTokenUtil.generateToken(_)
    }
}
