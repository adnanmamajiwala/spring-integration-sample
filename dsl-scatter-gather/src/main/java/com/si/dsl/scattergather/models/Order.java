package com.si.dsl.scattergather.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class Order {

    private int orderNumber;
    private Drink drink;
    private Dish dish;
    private Dessert dessert;

    public int getOrderNumber() {
        return orderNumber;
    }

    public Order setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public Drink getDrink() {
        return drink;
    }

    public Order setDrink(Drink drink) {
        this.drink = drink;
        return this;
    }

    public Dish getDish() {
        return dish;
    }

    public Order setDish(Dish dish) {
        this.dish = dish;
        return this;
    }

    public Dessert getDessert() {
        return dessert;
    }

    public Order setDessert(Dessert dessert) {
        this.dessert = dessert;
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
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
