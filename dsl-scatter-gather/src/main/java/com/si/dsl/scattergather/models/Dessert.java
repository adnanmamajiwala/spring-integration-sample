package com.si.dsl.scattergather.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class Dessert {

    private int orderNumber;
    private String dessertName;

    public int getOrderNumber() {
        return orderNumber;
    }

    public Dessert setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public String getDessertName() {
        return dessertName;
    }

    public Dessert setDessertName(String dessertName) {
        this.dessertName = dessertName;
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
