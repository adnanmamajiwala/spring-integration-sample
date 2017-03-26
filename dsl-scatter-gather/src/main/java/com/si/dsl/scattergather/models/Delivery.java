package com.si.dsl.scattergather.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Delivery {

    private int orderNumber;
    private String drinkName;
    private String dishName;
    private String desertName;

    public int getOrderNumber() {
        return orderNumber;
    }

    public Delivery setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public Delivery setDrinkName(String drinkName) {
        this.drinkName = drinkName;
        return this;
    }

    public String getDishName() {
        return dishName;
    }

    public Delivery setDishName(String dishName) {
        this.dishName = dishName;
        return this;
    }

    public String getDesertName() {
        return desertName;
    }

    public Delivery setDesertName(String desertName) {
        this.desertName = desertName;
        return this;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this);
    }

    @Override
    public String toString() {
        return "-------------------------------------- \n" +
                "Delivery : " + Thread.currentThread().getName() + "\n" +
                "Order number :" + orderNumber + "\n" +
                "DishName : " + dishName + "\n" +
                "DrinkName :" + drinkName + "\n" +
                "DesertName :" + desertName + "\n" +
                "------------------------------------- \n";
    }
}
