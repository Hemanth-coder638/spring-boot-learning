package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.dto.PostDTO;

import java.util.List;
import java.util.Map;

public interface PostService {

    List<PostDTO> getAllPosts();

    PostDTO createNewPost(PostDTO inputPost);

    PostDTO getPostById(Long postId);

    PostDTO updatePostById(Long postId, Map<String,Object> map);

    void deleteById(Long postId);
}
