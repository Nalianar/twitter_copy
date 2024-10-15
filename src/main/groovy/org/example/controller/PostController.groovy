package org.example.controller

import org.example.dtos.PostDTO
import org.example.model.Comment
import org.example.dtos.CreatePostDTO
import org.example.service.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostController {

    @Autowired
    PostService postService

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    PostDTO createPost(@RequestBody String content, @AuthenticationPrincipal UserDetails userDetails) {
        postService.createPost(content, userDetails)
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PutMapping("/{postId}/update")
    PostDTO updatePost(@PathVariable String postId, @RequestBody String postContent, @AuthenticationPrincipal UserDetails userDetails){
        postService.updatePost(postId, postContent, userDetails)
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{postId}/delete")
    def deletePost(@PathVariable String postId, @AuthenticationPrincipal UserDetails userDetails){
        postService.deletePost(postId, userDetails)
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/{postId}/like")
    PostDTO likePost(@PathVariable String postId, @AuthenticationPrincipal UserDetails userDetails) {
        postService.likePost(postId, userDetails)
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/{postId}/unlike")
    PostDTO unlikePost(@PathVariable String postId, @AuthenticationPrincipal UserDetails userDetails) {
        postService.unlikePost(postId, userDetails)
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{postId}/comment")
    PostDTO commentOnPost(@PathVariable String postId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody String comment) {
        postService.commentOnPost(postId, userDetails, comment)
    }

    @GetMapping("/{postId}/comments")
    List<Comment> getCommentsOnPost(@PathVariable String postId){
        postService.getCommentsOnPost(postId)
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/feed")
    List<PostDTO> getFeed(@AuthenticationPrincipal UserDetails userDetails) {
        postService.getFeed(userDetails)
    }
}
