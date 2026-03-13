package com.example.demo4.SecurityApp.utils;

import com.example.demo4.SecurityApp.dto.PostDTO;
import com.example.demo4.SecurityApp.entities.PostEntity;
import com.example.demo4.SecurityApp.entities.User;
import com.example.demo4.SecurityApp.entities.enums.Subscription;
import com.example.demo4.SecurityApp.services.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostSecurity {

    private  final PostService postService;


    public boolean isOwnerOfPost(Long postId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostDTO post = postService.getPostById(postId);
        return post.getAuthor().getId().equals(user.getId());
    }
    public boolean isAuthorizedUserForCreatePost() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        User user = (User) authentication.getPrincipal();
        log.info("Authorization check for user: {}", user.getEmail());

        boolean hasPermission =
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(auth -> auth.equals("POST_CREATE"));
        log.info(user.getSubscription());
        boolean hasSubscription =
                user.getSubscription()!=null && (user.getSubscription().equals(Subscription.BASIC.toString()) ||
                        user.getSubscription().equals(Subscription.PREMIUM.toString()));

        return hasPermission || hasSubscription;
    }
}

