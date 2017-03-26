package com.si.dsl.scattergather;

import com.si.dsl.scattergather.models.Delivery;
import com.si.dsl.scattergather.models.Dessert;
import com.si.dsl.scattergather.models.Dish;
import com.si.dsl.scattergather.models.Drink;
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
                delivery.setOrderNumber(((Drink) o).getOrderNumber());
                delivery.setDrinkName(((Drink) o).getDrinkName());
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
        if (object instanceof Dish) {
            Dish dish = (Dish) object;
            return dish.getOrderNumber();
        } else if (object instanceof Drink) {
            return ((Drink) object).getOrderNumber();
        } else {
            return ((Dessert) object).getOrderNumber();
        }

    }

    @ReleaseStrategy
    public boolean releaseChecker(List<Message<Object>> messages) {
        return messages.size() == 3;
    }

}