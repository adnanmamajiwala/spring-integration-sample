package com.si.dsl.scattergather;


import com.si.dsl.scattergather.models.Order;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface RestaurantsService {

    @Gateway(requestChannel = "orders.input")
    void placeOrder(Order order);

}