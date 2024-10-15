package org.example.model

import org.example.dtos.UserDTO
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
class User {
    @Id
    String id
    String username
    String password
    Set<String> following = new HashSet<>()
    Set<String> roles = new HashSet<>()

    UserDTO toDTO(){
        new UserDTO(id:id,
                    username: username)
    }
}
