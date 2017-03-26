package com.si.dsl.scattergather.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class Drink {

    private int orderNumber;
    private String drinkName;

    public int getOrderNumber() {
        return orderNumber;
    }

    public Drink setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public Drink setDrinkName(String drinkName) {
        this.drinkName = drinkName;
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
