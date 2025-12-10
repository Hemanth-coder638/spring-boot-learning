package com.hemanth.librarymanagment.config;

import com.hemanth.librarymanagment.dto.AuthorDto;
import com.hemanth.librarymanagment.entity.AuthorEntity;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        return mapper;
    }
}

