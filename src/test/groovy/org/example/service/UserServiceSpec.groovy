package org.example.service

import org.example.dtos.PostDTO
import org.example.dtos.UpdateUserDTO
import org.example.model.Post
import org.example.model.User
import org.example.repository.PostRepository
import org.example.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification

class UserServiceSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def postRepository = Mock(PostRepository)
    def userService = new UserService(userRepository: userRepository, postRepository: postRepository)

    def "updateUserById should update user when userDetails match and user is admin"() {
        given:
        def userId = "123"
        def updateUserDTO = new UpdateUserDTO(username: "newUsername", password: "newPassword")
        def userDetails = Mock(UserDetails) {
            getUsername() >> "admin"
            getAuthorities() >> ["ADMIN"]
        }
        def user = new User(id: userId, username: "admin", password: "oldPassword")

        when:
        userRepository.findById(userId) >> Optional.of(user)

        def result = userService.updateUserById(userId, updateUserDTO, userDetails)

        then:
        result.username == "newUsername"
        1 * userRepository.save(_) >> user
    }

    def "updateUserById should throw IllegalArgumentException when userDetails do not match"() {
        given:
        def userId = "123"
        def updateUserDTO = new UpdateUserDTO(username: "newUsername", password: "newPassword")
        def userDetails = Mock(UserDetails) {
            getUsername() >> "otherUser"
            getAuthorities() >> ["USER"]
        }
        def user = new User(id: userId, username: "admin", password: "oldPassword")

        when:
        userRepository.findById(userId) >> Optional.of(user)
        userService.updateUserById(userId, updateUserDTO, userDetails)

        then:
        thrown(IllegalArgumentException)
    }

    def "deleteUserById should delete user when userDetails match and user is admin"() {
        given:
        def userId = "123"
        def userDetails = Mock(UserDetails) {
            getUsername() >> "admin"
            getAuthorities() >> ["ADMIN"]
        }
        def user = new User(id: userId, username: "admin", password: "password")

        when:
        userRepository.findById(userId) >> Optional.of(user)

        userService.deleteUserById(userId, userDetails)

        then:
        1 * userRepository.delete(user)
    }

    def "follow should add targetUserId to user's following list when not already followed"() {
        given:
        def userId = "123"
        def targetUserId = "456"
        def userDetails = Mock(UserDetails) {
            getUsername() >> "user1"
        }
        def user = new User(id: userId, username: "user1", password: "password")
        user.following.add("anotherUserId")

        when:
        userRepository.findByUsername(userDetails.getUsername()) >> Optional.of(user)
        userRepository.findById(targetUserId) >> Optional.of(new User(id: targetUserId, username: "targetUser", password: "password"))

        userService.follow(userDetails, targetUserId)

        then:
        user.following.contains(targetUserId)
        1 * userRepository.save(user) >> user
    }

    def "unfollow should remove targetUserId from user's following list when already followed"() {
        given:
        def userId = "123"
        def targetUserId = "456"
        def userDetails = Mock(UserDetails) {
            getUsername() >> "user1"
        }
        def user = new User(id: userId, username: "user1", password: "password")
        user.following.add(targetUserId)

        when:
        userRepository.findByUsername(userDetails.getUsername()) >> Optional.of(user)
        userRepository.findById(targetUserId) >> Optional.of(new User(id: targetUserId, username: "targetUser", password: "password"))

        userService.unfollow(userDetails, targetUserId)

        then:
        !user.following.contains(targetUserId)
        1 * userRepository.save(user) >> user
    }

    def "getUserPosts should return posts for a given userId"() {
        given:
        def userId = "123"
        def posts = List.of( new Post(id: "1"), new Post(id: "2"))
        def postsDTO = posts.collect { post -> post.toDTO() }

        when:
        userRepository.findById(userId) >> Optional.of(new User(id: userId, username: "user1", password: "password"))
        postRepository.findAllByUserId(userId) >> posts

        def result = userService.getUserPosts(userId)

        then:
        result == postsDTO
    }

    def "follow should throw an exception when user is already following"() {
        given:
        def userId = "123"
        def targetUserId = "456"
        def userDetails = Mock(UserDetails) {
            getUsername() >> "user1"
        }
        def user = new User(id: userId, username: "user1", password: "password")
        user.following.add(targetUserId)

        when:
        userRepository.findByUsername(userDetails.getUsername()) >> Optional.of(user)
        userRepository.findById(targetUserId) >> Optional.of(new User(id: targetUserId, username: "targetUser", password: "password"))

        userService.follow(userDetails, targetUserId)

        then:
        thrown(IllegalStateException)
    }

    def "unfollow should throw an exception when user is not following"() {
        given:
        def userId = "123"
        def targetUserId = "456"
        def userDetails = Mock(UserDetails) {
            getUsername() >> "user1"
        }
        def user = new User(id: userId, username: "user1", password: "password")

        when:
        userRepository.findByUsername(userDetails.getUsername()) >> Optional.of(user)
        userRepository.findById(targetUserId) >> Optional.of(new User(id: targetUserId, username: "targetUser", password: "password"))

        userService.unfollow(userDetails, targetUserId)

        then:
        thrown(IllegalStateException)
    }
}
