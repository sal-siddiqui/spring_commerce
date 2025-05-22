package com.spring_commerce.config;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.spring_commerce.model.Order;
import com.spring_commerce.model.OrderItem;
import com.spring_commerce.payload.OrderDTO;
import com.spring_commerce.payload.OrderItemDTO;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper modelMapper = new ModelMapper();

        // Skip null values during mapping
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());

        // Map Order -> OrderDTO, converting Payment to PaymentDTO
        final TypeMap<Order, OrderDTO> orderTypeMap = modelMapper.createTypeMap(Order.class, OrderDTO.class);
        orderTypeMap.addMappings(mapper -> mapper.map(Order::getPayment, OrderDTO::setPaymentDTO));

        // Map OrderItem -> OrderItemDTO, converting Product to ProductDTO
        final TypeMap<OrderItem, OrderItemDTO> itemTypeMap = modelMapper.createTypeMap(OrderItem.class,
                OrderItemDTO.class);
        itemTypeMap.addMappings(mapper -> mapper.map(OrderItem::getProduct, OrderItemDTO::setProductDTO));

        return modelMapper;
    }
}
