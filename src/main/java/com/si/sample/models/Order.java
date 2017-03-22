package com.si.sample.models;

public class Order {

    private int orderNumber;
    private Drink drink;
    private Dish dish;

    public Order(int orderNumber, Drink drink, Dish dish) {
        this.orderNumber = orderNumber;
        this.drink = drink;
        this.dish = dish;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Drink getDrink() {
        return drink;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }
}
