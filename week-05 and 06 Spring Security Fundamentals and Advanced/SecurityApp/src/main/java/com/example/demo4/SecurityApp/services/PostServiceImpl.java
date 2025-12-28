package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.dto.PostDTO;
import com.example.demo4.SecurityApp.entities.PostEntity;
import com.example.demo4.SecurityApp.entities.User;
import com.example.demo4.SecurityApp.exceptions.ResourceNotFoundException;
import com.example.demo4.SecurityApp.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Ref;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service @RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;


    @Override
    public List<PostDTO> getAllPosts() {
        return postRepository
                .findAll()
                .stream()
                .map(postEntity -> modelMapper.map(postEntity, PostDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PostDTO createNewPost(PostDTO inputPost) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostEntity postEntity = modelMapper.map(inputPost, PostEntity.class);
        postEntity.setAuthor(user);
        return modelMapper.map(postRepository.save(postEntity), PostDTO.class);
    }

    @Override
    public PostDTO getPostById(Long postId) {
        PostEntity postEntity = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id "+postId));
        return modelMapper.map(postEntity, PostDTO.class);
    }

    @Override
    public PostDTO updatePostById(Long postId, Map<String,Object> map) {
        isPostExistById(postId);
        PostEntity postEntity=postRepository.getReferenceById(postId);
        map.forEach((field,value)->{
            if(field.contains("id"))throw new ResourceNotFoundException("Id cannot be updated");
            Field field1= ReflectionUtils.findRequiredField(PostEntity.class,field);
            field1.setAccessible(true);
            ReflectionUtils.setField(field1,postEntity,value);
        });
        return modelMapper.map(postRepository.save(postEntity),PostDTO.class);
    }

    @Override
    public void deleteById(Long postId) {
        isPostExistById(postId);
        postRepository.deleteById(postId);
    }

    public void isPostExistById(Long postId){
       boolean exist=postRepository.existsById(postId);
       if(!exist)throw new ResourceNotFoundException("Post not found with id "+postId);
    }


}
