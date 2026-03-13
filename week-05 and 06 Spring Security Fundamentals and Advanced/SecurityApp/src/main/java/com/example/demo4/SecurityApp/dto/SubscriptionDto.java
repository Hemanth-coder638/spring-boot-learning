package com.example.demo4.SecurityApp.dto;

import com.example.demo4.SecurityApp.entities.enums.Subscription;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class SubscriptionDto {

    @Enumerated(EnumType.STRING)
    private Subscription subscription;
}
