package com.si.sample;

import com.si.sample.models.Delivery;
import com.si.sample.models.Dish;
import com.si.sample.models.Drink;

import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.annotation.ReleaseStrategy;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderAggregator {

    @Aggregator
    public Delivery output(List<Object> objects) {
        System.out.println("inside Aggregator");
        Delivery delivery = new Delivery();
        for (Object o : objects) {
            if (o instanceof Drink) {
                delivery.setOrderNumber(((Drink) o).getOrderNumber());
                delivery.setDrinkName(((Drink) o).getDrinkName());
            } else {
                delivery.setDishName(((Dish) o).getDishName());
            }
        }

        return delivery;
    }

    @CorrelationStrategy
    public int correlateBy(Object object) {
        if (object instanceof Drink) {
            int orderNumber = ((Drink) object).getOrderNumber();
            System.out.println("inside correlation strategy - instanceOf Drink - order# : " + orderNumber);
            return orderNumber;
        } else {
            int orderNumber = ((Dish) object).getOrderNumber();
            System.out.println("inside correlation strategy - instanceOf Dish - order# : " + orderNumber);
            return orderNumber;
        }

    }

    @ReleaseStrategy
    public boolean releaseChecker(List<Message<Object>> messages) {
        System.out.println(" inside release checker - size : " + messages.size());
        return messages.size() == 2;
    }

}