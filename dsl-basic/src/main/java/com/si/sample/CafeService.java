package com.si.sample;

import com.si.sample.models.Order;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface CafeService {

    @Gateway(requestChannel = "orders.input")
    void placeOrder(Order order);

}