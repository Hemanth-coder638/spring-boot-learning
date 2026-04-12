package com.codingshuttle.ecommerce.order_service.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Configuration
@RefreshScope
@Data
public class FeaturesEnableConfig {

    @Value("${features.user-tracking-enabled:true}")
    private boolean isUserTrackingEnabled;

}
