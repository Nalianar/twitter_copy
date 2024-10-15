package org.example.dtos

import groovy.transform.EqualsAndHashCode
import org.example.model.Comment;

@EqualsAndHashCode
class PostDTO {

    String postId
    String userId
    String content
    Long numberOfLikes
    List<Comment> comments
}
