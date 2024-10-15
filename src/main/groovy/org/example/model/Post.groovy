package org.example.model

import org.example.dtos.PostDTO
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "posts")
class Post {
    @Id
    String id
    String userId
    String content
    Set<String> likes = new HashSet<>()
    List<Comment> comments = []

    PostDTO toDTO(){
        new PostDTO(postId: id,
                userId: userId,
                content: content,
                numberOfLikes: likes.size(),
                comments: comments)
    }
}
