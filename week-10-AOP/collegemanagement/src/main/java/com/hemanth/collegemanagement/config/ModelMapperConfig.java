package com.hemanth.collegemanagement.config;

import lombok.Builder;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Builder
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper=new ModelMapper();
        modelMapper.getConfiguration().
                setPropertyCondition(context->context.getSource()!=null);
        return  modelMapper;
    }
}
