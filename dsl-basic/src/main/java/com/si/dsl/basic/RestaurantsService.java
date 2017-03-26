package com.si.dsl.basic;

import com.si.dsl.basic.models.Order;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface RestaurantsService {

    @Gateway(requestChannel = "orders.input")
    void placeOrder(Order order);

}