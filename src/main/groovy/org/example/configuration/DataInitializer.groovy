package org.example.configuration

import org.example.model.User
import org.example.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer {

    @Autowired
    UserRepository userRepository

    @Bean
    CommandLineRunner initAdminUser() {
        return (args) -> {
            if (!userRepository.findByUsername("admin").isPresent()) {
                def adminUser = new User(
                        username: "admin",
                        password: new BCryptPasswordEncoder().encode("admin_password"),
                        roles: ["ADMIN"] as Set
                )
                userRepository.save(adminUser)
                println("Admin user created successfully")
            } else {
                println("Admin user already exists")
            }
        }
    }
}
