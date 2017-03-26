package com.si.dsl.basic;

import com.si.dsl.basic.models.Delivery;
import com.si.dsl.basic.models.Dessert;
import com.si.dsl.basic.models.Dish;
import com.si.dsl.basic.models.Drink;
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
        Delivery delivery = new Delivery();
        for (Object o : objects) {
            if (o instanceof Drink) {
                delivery.setDrinkName(((Drink) o).getDrinkName());
                delivery.setOrderNumber(((Drink) o).getOrderNumber());
            } else if (o instanceof Dish){
                delivery.setDishName(((Dish) o).getDishName());
            } else {
                delivery.setDesertName(((Dessert) o).getDessertName());
            }
        }

        return delivery;
    }

    @CorrelationStrategy
    public int correlateBy(Object object) {
        if (object instanceof Drink) {
            return ((Drink) object).getOrderNumber();
        } else if (object instanceof Dish) {
            return ((Dish) object).getOrderNumber();
        } else {
            return ((Dessert) object).getOrderNumber();
        }

    }

    @ReleaseStrategy
    public boolean releaseChecker(List<Message<Object>> messages) {
        return messages.size() == 3;
    }

}