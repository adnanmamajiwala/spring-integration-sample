package com.si.dsl.scattergather;

import com.google.common.util.concurrent.Uninterruptibles;
import com.si.dsl.scattergather.models.Dessert;
import com.si.dsl.scattergather.models.Dish;
import com.si.dsl.scattergather.models.Drink;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class KitchenService {

    private final AtomicInteger drinkCounter = new AtomicInteger();
    private final AtomicInteger dishCounter = new AtomicInteger();
    private final AtomicInteger dessertCounter = new AtomicInteger();

    public Drink prepareDrink(Drink drink){
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        System.out.println(Thread.currentThread().getName()
                + " prepared drink #" + drinkCounter.incrementAndGet()
                + " for order #" + drink.getOrderNumber() + ": " + drink.getDrinkName());
        return drink;
    }

    public Dish prepareDish(Dish dish){
        Uninterruptibles.sleepUninterruptibly(5, TimeUnit.SECONDS);
        System.out.println(Thread.currentThread().getName()
                + " prepared dish #" + dishCounter.incrementAndGet()
                + " for order #" + dish.getOrderNumber() + ": " + dish.getDishName());
        return dish;
    }

    public Dessert prepareDessert(Dessert dessert){
        Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
        System.out.println(Thread.currentThread().getName()
                + " prepared dessert #" + dessertCounter.incrementAndGet()
                + " for order #" + dessert.getOrderNumber() + ": " + dessert.getDessertName());
        return dessert;
    }
}
