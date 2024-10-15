package org.example.service

import org.example.dtos.PostDTO
import org.example.exception.ResourceNotFoundException
import org.example.model.Comment
import org.example.model.Post
import org.example.repository.PostRepository
import org.example.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class PostService {

    @Autowired
    PostRepository postRepository

    @Autowired
    UserRepository userRepository

    PostDTO createPost(String content, UserDetails userDetails) {
        def user = userRepository.findByUsername(userDetails.username).orElseThrow{new ResourceNotFoundException("User with username" + userDetails.username + "does not exist")}
        def post = new Post(userId: user.id, content: content)
        postRepository.save(post).toDTO()
    }

    PostDTO updatePost(String postId, String content, UserDetails userDetails) {
        def post = postRepository.findById(postId).orElseThrow{new ResourceNotFoundException("Post with id "+ postId + "not found")}
        def userOnPost = userRepository.findById(post.userId).orElseThrow{new ResourceNotFoundException("User with username" + userDetails.username + "does not exist")}
        if(userDetails.username != userOnPost.username || !userDetails.getAuthorities().contains("ADMIN")){
            throw new IllegalArgumentException("This action cannot be executed with current conditions")
        }
        post.setContent(content)
        postRepository.save(post).toDTO()
    }

    def deletePost(String postId, UserDetails userDetails) {
        def post = postRepository.findById(postId).orElseThrow{new ResourceNotFoundException("Post with id "+ postId + "not found")}
        def userOnPost = userRepository.findById(post.userId).orElseThrow{new ResourceNotFoundException("User with username" + userDetails.username + "does not exist")}
        if(userDetails.username != userOnPost.username || !userDetails.getAuthorities().contains("ADMIN")){
            throw new IllegalArgumentException("This action cannot be executed with current conditions")
        }
        postRepository.delete(post)
    }

    PostDTO likePost(String postId, UserDetails userDetails) {
        def post = postRepository.findById(postId).orElseThrow{new ResourceNotFoundException("Post with id "+ postId + "not found")}
        def user = userRepository.findByUsername(userDetails.username).orElseThrow{new ResourceNotFoundException("User with username" + userDetails.username + "does not exist")}
        if(post.likes.contains(user.id)){
            throw new IllegalStateException("User with id " + user.id + "is already liked post with id" + postId)
        }
        post.likes.add(user.id)
        postRepository.save(post).toDTO()
    }

    PostDTO unlikePost(String postId, UserDetails userDetails) {
        def post = postRepository.findById(postId).orElseThrow{new ResourceNotFoundException("Post with id "+ postId + "not found")}
        def user = userRepository.findByUsername(userDetails.username).orElseThrow{new ResourceNotFoundException("User with username" + userDetails.username + "does not exist")}
        if(!post.likes.contains(user.id)){
            throw new IllegalStateException("User with id " + user.id + "is did not have a like on a post with id" + postId)
        }
        post.likes.remove(user.id)
        postRepository.save(post).toDTO()
    }

    PostDTO commentOnPost(String postId, UserDetails userDetails, String comment) {
        def post = postRepository.findById(postId).orElseThrow{new ResourceNotFoundException("Post with id "+ postId + "not found")}
        def user = userRepository.findByUsername(userDetails.username).orElseThrow{new ResourceNotFoundException("User with userId" + userId + "does not exist")}
        post.comments.add(new Comment(userId: user.id, content: comment))
        postRepository.save(post).toDTO()
    }

    List<Comment> getCommentsOnPost(String postId) {
        def post = postRepository.findById(postId).orElseThrow{new ResourceNotFoundException("Post with id "+ postId + "not found")}
        post.comments
    }

    List<PostDTO> getFeed(UserDetails userDetails) {
        def following = userRepository.findByUsername(userDetails.username).orElseThrow{new ResourceNotFoundException("User with userId" + userId + "does not exist")}
                .following.toList()
        postRepository.findAllByUserIdIn(following).stream().map {
            post -> post.toDTO()}
                .toList()
    }
}
