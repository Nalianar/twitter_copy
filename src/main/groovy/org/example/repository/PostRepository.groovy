package org.example.repository

import org.example.dtos.PostDTO
import org.example.model.Post
import org.springframework.data.mongodb.repository.MongoRepository

interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findAllByUserIdIn(List<String> userIds)

    List<Post> findAllByUserId(String userId)
}

