package org.example.service

import org.example.dtos.PostDTO
import org.example.exception.ResourceNotFoundException
import org.example.model.Comment
import org.example.model.Post
import org.example.model.User
import org.example.repository.PostRepository
import org.example.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import spock.lang.Specification

class PostServiceSpec extends Specification {

    def postRepository = Mock(PostRepository)
    def userRepository = Mock(UserRepository)
    def postService = new PostService(postRepository: postRepository, userRepository: userRepository)

    def "createPost should create a new post when user exists"() {
        given:
        def content = "This is a post"
        def username = "user1"
        def userDetails = Mock(UserDetails) {
            getUsername() >> username
        }
        def user = new User(id: "123", username: username)
        def post = new Post(userId: user.id, content: content)

        when:
        userRepository.findByUsername(username) >> Optional.of(user)

        def result = postService.createPost(content, userDetails)

        then:
        result.content == content
        1 * postRepository.save(_) >> post
    }

    def "createPost should throw ResourceNotFoundException when user does not exist"() {
        given:
        def content = "This is a post"
        def username = "nonexistentUser"
        def userDetails = Mock(UserDetails) {
            getUsername() >> username
        }

        when:
        userRepository.findByUsername(username) >> Optional.empty()

        postService.createPost(content, userDetails)

        then:
        thrown(ResourceNotFoundException)
    }

    def "updatePost should update post content when user is authorized"() {
        given:
        def postId = "123"
        def newContent = "Updated post content"
        def username = "admin"
        def userDetails = Mock(UserDetails) {
            getUsername() >> username
            getAuthorities() >> ["ADMIN"]
        }
        def user = new User(id: "456", username: username)
        def post = new Post(id: postId, userId: user.id, content: "Old post content")

        when:
        postRepository.findById(postId) >> Optional.of(post)
        userRepository.findById(post.userId) >> Optional.of(user)

        def result = postService.updatePost(postId, newContent, userDetails)

        then:
        result.content == newContent
        1 * postRepository.save(_) >> post
    }

    def "updatePost should throw IllegalArgumentException when user is not authorized"() {
        given:
        def postId = "123"
        def newContent = "Updated post content"
        def username = "nonAdminUser"
        def userDetails = Mock(UserDetails) {
            getUsername() >> username
            getAuthorities() >> []
        }
        def user = new User(id: "456", username: "admin")
        def post = new Post(id: postId, userId: user.id, content: "Old post content")

        when:
        postRepository.findById(postId) >> Optional.of(post)
        userRepository.findById(post.userId) >> Optional.of(user)

        postService.updatePost(postId, newContent, userDetails)

        then:
        thrown(IllegalArgumentException)
    }

    def "deletePost should delete the post when user is authorized"() {
        given:
        def postId = "123"
        def username = "admin"
        def userDetails = Mock(UserDetails) {
            getUsername() >> username
            getAuthorities() >> ["ADMIN"]
        }
        def user = new User(id: "456", username: username)
        def post = new Post(id: postId, userId: user.id, content: "Post content")

        when:
        postRepository.findById(postId) >> Optional.of(post)
        userRepository.findById(post.userId) >> Optional.of(user)

        postService.deletePost(postId, userDetails)

        then:
        1 * postRepository.delete(post)
    }

    def "deletePost should throw IllegalArgumentException when user is not authorized"() {
        given:
        def postId = "123"
        def username = "nonAdminUser"
        def userDetails = Mock(UserDetails) {
            getUsername() >> username
            getAuthorities() >> []
        }
        def user = new User(id: "456", username: "admin")
        def post = new Post(id: postId, userId: user.id, content: "Post content")

        when:
        postRepository.findById(postId) >> Optional.of(post)
        userRepository.findById(post.userId) >> Optional.of(user)

        postService.deletePost(postId, userDetails)

        then:
        thrown(IllegalArgumentException)
    }

    def "likePost should add like when user exists and post not already liked"() {
        given:
        def postId = "123"
        def username = "user1"
        def userDetails = Mock(UserDetails) {
            getUsername() >> username
        }
        def user = new User(id: "456", username: username)
        def post = new Post(id: postId, userId: "789", content: "Post content")
        post.likes = new HashSet<>()

        when:
        postRepository.findById(postId) >> Optional.of(post)
        userRepository.findByUsername(username) >> Optional.of(user)

        def result = postService.likePost(postId, userDetails)

        then:
        post.likes.contains(user.id)
        1 * postRepository.save(_) >> post
    }

    def "likePost should throw IllegalStateException when post already liked by user"() {
        given:
        def postId = "123"
        def username = "user1"
        def userDetails = Mock(UserDetails) {
            getUsername() >> username
        }
        def user = new User(id: "456", username: username)
        def post = new Post(id: postId, userId: "789", content: "Post content")
        post.likes = new HashSet<>()
        post.likes.add(user.id)

        when:
        postRepository.findById(postId) >> Optional.of(post)
        userRepository.findByUsername(username) >> Optional.of(user)

        postService.likePost(postId, userDetails)

        then:
        thrown(IllegalStateException)
    }

    def "unlikePost should remove like when user exists and post already liked"() {
        given:
        def postId = "123"
        def username = "user1"
        def userDetails = Mock(UserDetails) {
            getUsername() >> username
        }
        def user = new User(id: "456", username: username)
        def post = new Post(id: postId, userId: "789", content: "Post content")
        post.likes = new HashSet<>()
        post.likes.add(user.id)

        when:
        postRepository.findById(postId) >> Optional.of(post)
        userRepository.findByUsername(username) >> Optional.of(user)

        def result = postService.unlikePost(postId, userDetails)

        then:
        !post.likes.contains(user.id)
        1 * postRepository.save(_) >> post
    }

    def "unlikePost should throw IllegalStateException when post not liked by user"() {
        given:
        def postId = "123"
        def username = "user1"
        def userDetails = Mock(UserDetails) {
            getUsername() >> username
        }
        def user = new User(id: "456", username: username)
        def post = new Post(id: postId, userId: "789", content: "Post content")
        post.likes = new HashSet<>()

        when:
        postRepository.findById(postId) >> Optional.of(post)
        userRepository.findByUsername(username) >> Optional.of(user)

        postService.unlikePost(postId, userDetails)

        then:
        thrown(IllegalStateException)
    }

    def "commentOnPost should add comment when user exists"() {
        given:
        def postId = "123"
        def commentContent = "Great post!"
        def username = "user1"
        def userDetails = Mock(UserDetails) {
            getUsername() >> username
        }
        def user = new User(id: "456", username: username)
        def post = new Post(id: postId, userId: "789", content: "Post content")
        post.comments = new ArrayList<>()

        when:
        postRepository.findById(postId) >> Optional.of(post)
        userRepository.findByUsername(username) >> Optional.of(user)

        def result = postService.commentOnPost(postId, userDetails, commentContent)

        then:
        post.comments.size() == 1
        post.comments.get(0).content == commentContent
        1 * postRepository.save(_)  >> post
    }

    def "getCommentsOnPost should return comments when post exists"() {
        given:
        def postId = "123"
        def post = new Post(id: postId, userId: "789", content: "Post content")
        def comment1 = new Comment(userId: "456", content: "Nice post!")
        def comment2 = new Comment(userId: "789", content: "Great read!")
        post.comments = [comment1, comment2]

        when:
        postRepository.findById(postId) >> Optional.of(post)

        def result = postService.getCommentsOnPost(postId)

        then:
        result.size() == 2
        result[0].content == "Nice post!"
        result[1].content == "Great read!"
    }

    def "getFeed should return posts from followed users"() {
        given:
        def username = "user1"
        def userDetails = Mock(UserDetails) {
            getUsername() >> username
        }
        def user = new User(id: "456", username: username, following: ["789", "101"])
        def post1 = new Post(id: "123", userId: "789", content: "Post from user 789")
        def post2 = new Post(id: "456", userId: "101", content: "Post from user 101")

        when:
        userRepository.findByUsername(username) >> Optional.of(user)
        postRepository.findAllByUserIdIn(["789", "101"]) >> [post1, post2]

        def result = postService.getFeed(userDetails)

        then:
        result.size() == 2
        result[0].content == "Post from user 789"
        result[1].content == "Post from user 101"
    }
}

