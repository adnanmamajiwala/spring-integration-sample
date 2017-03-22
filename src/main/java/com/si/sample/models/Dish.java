package com.si.sample.models;

public class Dish {

    private int orderNumber;
    private String dishName;

    public Dish(int orderNumber, String dishName) {
        this.orderNumber = orderNumber;
        this.dishName = dishName;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }
}
