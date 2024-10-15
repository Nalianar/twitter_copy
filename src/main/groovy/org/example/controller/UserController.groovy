package org.example.controller

import org.example.dtos.PostDTO
import org.example.dtos.UserDTO
import org.example.model.User
import org.example.dtos.CreateUserDTO
import org.example.dtos.UpdateUserDTO
import org.example.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController {

    @Autowired
    UserService userService

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{userId}/update")
    UserDTO updateUser(@PathVariable String userId, @RequestBody UpdateUserDTO updateUserDTO, @AuthenticationPrincipal UserDetails userDetails){
        userService.updateUserById(userId, updateUserDTO, userDetails)
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{userId}/delete")
    def deleteUser(@PathVariable String userId, @AuthenticationPrincipal UserDetails userDetails){
        userService.deleteUserById(userId, userDetails)
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/follow/{targetUserId}")
    UserDTO follow(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String targetUserId) {
        userService.follow(userDetails, targetUserId)
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/unfollow/{targetUserId}")
    UserDTO unfollow(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String targetUserId) {
        userService.unfollow(userDetails, targetUserId)
    }

    @GetMapping("/{userId}/posts")
    List<PostDTO> getUserPosts(@PathVariable String userId){
        userService.getUserPosts(userId)
    }
}
