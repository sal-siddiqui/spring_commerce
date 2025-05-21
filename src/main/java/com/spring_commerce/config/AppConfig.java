package com.spring_commerce.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper mapper = new ModelMapper();

        // When copying from src to destination, this makes sure the untouched values in
        // destination are not nulled
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        return mapper;
    }
}
