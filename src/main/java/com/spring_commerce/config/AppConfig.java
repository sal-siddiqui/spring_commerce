package com.spring_commerce.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // When copying from src to destination, this makes sure the untouched values in
        // destination are not nulled
        mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        // // Create a custom mapping for ProductDTO to Product
        // TypeMap<ProductDTO, Product> typeMap = mapper.createTypeMap(ProductDTO.class,
        // Product.class);

        // // Add a mapping that calculates specialPrice based on price and discount
        // typeMap.addMappings(mapping -> {
        // mapping.skip(Product::setSpecialPrice); // Skip the default mapping
        // });

        // // After mapping is done, calculate the specialPrice
        // typeMap.setPostConverter(context -> {
        // ProductDTO source = context.getSource();
        // Product destination = context.getDestination();

        // // Calculate special price from price and discount
        // if (source.getPrice() != null && source.getDiscount() != null) {
        // double specialPrice = source.getPrice() * (1 - source.getDiscount() / 100.0);
        // destination.setSpecialPrice(specialPrice);
        // }

        // return destination;
        // });
        return mapper;
    }
}
