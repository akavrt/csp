package com.akavrt.csp.core;

public class MultiCut {
    private final Order order;
    private int quantity;

    public MultiCut(Order order) {
        this(order, 0);
    }

    public MultiCut(Order order, int quantity) {
        this.order = order;
        this.quantity = quantity;
    }

    public Order getOrder() {
        return order;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    public double getWidth() {
        return order.getWidth() * quantity;
    }
}