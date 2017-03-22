package com.si.sample.models;

public class Drink {

    private int orderNumber;
    private String drinkName;

    public Drink(int orderNumber, String drinkName) {
        this.orderNumber = orderNumber;
        this.drinkName = drinkName;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }
}
