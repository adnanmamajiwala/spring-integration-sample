package com.si.dsl.scattergather;

import com.google.common.collect.ImmutableList;
import com.si.dsl.scattergather.models.Dessert;
import com.si.dsl.scattergather.models.Dish;
import com.si.dsl.scattergather.models.Drink;
import com.si.dsl.scattergather.models.Order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;

import java.util.List;

@SpringBootApplication
@IntegrationComponentScan
public class ScatterGatherApplication {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(ScatterGatherApplication.class, args);
        RestaurantsService cafe = ctx.getBean(RestaurantsService.class);
        for (int i = 1; i <= 10; i++) {
            Order order = new Order()
                            .setOrderNumber(i)
                            .setDish(new Dish()
                                    .setOrderNumber(i)
                                    .setDishName(generateDishName()))
                            .setDrink(new Drink()
                                    .setOrderNumber(i)
                                    .setDrinkName(generateDrinkName()))
                            .setDessert(new Dessert()
                                    .setOrderNumber(i)
                                    .setDessertName(generateDessertName()));
            cafe.placeOrder(order);
        }

        System.out.println("Hit 'Enter' to terminate");
        System.in.read();
        ctx.close();
    }

    private static String generateDrinkName() {
        List<String> drinkName = ImmutableList.of("Black Coffee", "Lemon Tea", "Cappuccino", "Mocha", "Iced Tea", "Pepsi", "Coke", "Mountain Dew", "Vodka", "Whiskey");
        int randomNum = (int) (Math.random() * 9);
        return drinkName.get(randomNum);
    }

    private static String generateDishName() {
        List<String> drinkName = ImmutableList.of("Grilled Salmon", "Burger", "Pizza", "Biryani", "Filet Mignon", "Fish and chips", "Chicken tikka masala", "Bruschetta", "Fried eggplant", "Lasagne");
        int randomNum = (int) (Math.random() * 9);
        return drinkName.get(randomNum);
    }

    private static String generateDessertName() {
        List<String> drinkName = ImmutableList.of("Chocolate Mouse", "Cheese cake", "Gulab Jamun", "Custard", "Sorbet", "Apple Pie", "Creme Brulee", "Sundae", "Baklava", "Flan");
        int randomNum = (int) (Math.random() * 9);
        return drinkName.get(randomNum);
    }
}
