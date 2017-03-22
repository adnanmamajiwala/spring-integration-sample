package com.si.sample;

import com.google.common.collect.ImmutableList;
import com.si.sample.models.Dish;
import com.si.sample.models.Drink;
import com.si.sample.models.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;

import java.util.List;

@SpringBootApplication
@IntegrationComponentScan
public class SpringIntegrationSampleApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(SpringIntegrationSampleApplication.class, args);
        CafeService cafe = ctx.getBean(CafeService.class);
        for (int i = 1; i <= 10; i++) {
            Order order = new Order(i, new Drink(i, generateDrinkName()), new Dish(i, generateDishName()));
            cafe.placeOrder(order);
        }

        System.out.println("Hit 'Enter' to terminate");
        System.in.read();
        ctx.close();
    }

    private static String generateDrinkName() {
        List<String> drinkName = ImmutableList.of("Black Coffee", "Lemon Tea", "Cappuccino", "Mocha", "Iced Tea", "Pepsi", "Coke", "Mountain Dew", "Vodka", "Whiskey");
        int randomNum = (int) (Math.random() * 9);
        System.out.println("randomNum : " + randomNum);
        return drinkName.get(randomNum);
    }

    private static String generateDishName() {
        List<String> drinkName = ImmutableList.of("Grilled Salmon", "Burger", "Pizza", "Biryani", "Filet Mignon", "Fish and chips", "Chicken tikka masala", "Bruschetta", "Fried eggplant", "Lasagne");
        int randomNum = (int) (Math.random() * 9);
        System.out.println("randomNum : " + randomNum);
        return drinkName.get(randomNum);
    }
}
