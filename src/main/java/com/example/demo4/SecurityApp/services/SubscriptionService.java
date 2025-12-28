package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.dto.SubscriptionDto;
import com.example.demo4.SecurityApp.dto.UserDto;
import com.example.demo4.SecurityApp.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {
    private final UserService userService;
    private final ModelMapper modelMapper;
    public UserDto addSubscription(SubscriptionDto subscriptionDto) {
        log.info("subscription service");
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("securitycontextholder issue");
        user.setSubscription(subscriptionDto.getSubscription().toString());
        log.info(subscriptionDto.getSubscription().toString());
        return modelMapper.map(userService.save(user),UserDto.class);
    }
}
