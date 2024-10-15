package org.example.service

import org.example.dtos.PostDTO
import org.example.dtos.UserDTO
import org.example.exception.ResourceNotFoundException
import org.example.dtos.UpdateUserDTO
import org.example.repository.PostRepository
import org.example.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class UserService {

    @Autowired
    UserRepository userRepository

    @Autowired
    PostRepository postRepository

    UserDTO updateUserById(String userId, UpdateUserDTO updateUserDTO, UserDetails userDetails) {
        def userToUpdate = userRepository.findById(userId).orElseThrow{new ResourceNotFoundException("User with userId " + userId + " does not exist")}
        if(userDetails.username != userToUpdate.username || !userDetails.getAuthorities().contains("ADMIN")){
            throw new IllegalArgumentException("This action cannot be executed with current conditions")
        }
        userToUpdate.setUsername(updateUserDTO.username)
        userToUpdate.setPassword(updateUserDTO.password)
        userRepository.save(userToUpdate).toDTO()
    }

    def deleteUserById(String userId, UserDetails userDetails) {
        def userToDelete = userRepository.findById(userId).orElseThrow{new ResourceNotFoundException("User with userId " + userId + " does not exist")}
        if(userDetails.username != userToDelete.username || !userDetails.getAuthorities().contains("ADMIN")){
            throw new IllegalArgumentException("This action cannot be executed with current conditions")
        }
        userRepository.delete(userToDelete)
    }

    UserDTO follow(UserDetails userDetails, String targetUserId) {
        def user = userRepository.findByUsername(userDetails.username).orElseThrow { new ResourceNotFoundException("User, that wants to follow, with user username" + userDetails.username + "is not exists")}
        userRepository.findById(targetUserId).orElseThrow { new ResourceNotFoundException("Followed user with user id " + targetUserId + " is not exists")}
        if(user.following.contains(targetUserId)){
            throw new IllegalStateException("User with id " + user.id + " is already subscribed to user with id" + targetUserId)
        }
        user.following.add(targetUserId)
        userRepository.save(user).toDTO()
    }

    UserDTO unfollow(UserDetails userDetails, String targetUserId) {
        def user = userRepository.findByUsername(userDetails.username).orElseThrow{new ResourceNotFoundException("User with username" + userDetails.username + "does not exist")}
        userRepository.findById(targetUserId).orElseThrow { new ResourceNotFoundException("Followed user with user id " + targetUserId + " is not exists")}
        if(!user.following.contains(targetUserId)){
            throw new IllegalStateException("User with id " + user.id + " is not subscribed to user with id" + targetUserId)
        }
        user.following.remove(targetUserId)
        userRepository.save(user).toDTO()
    }

    List<PostDTO> getUserPosts(String userId) {
        userRepository.findById(userId).orElseThrow{new ResourceNotFoundException("User with userId " + userId + " does not exist")}
        postRepository.findAllByUserId(userId).collect { post -> post.toDTO() }
    }
}

