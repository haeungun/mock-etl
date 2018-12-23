package com.example.mocketl.model;

public class StatsPayment {

    private final String name;
    private final int userCount;
    private final int payment;

    public StatsPayment(String name, int userCount, int payment) {
        this.name = name;
        this.userCount = userCount;
        this.payment = payment;
    }

    public String getName() {
        return this.name;
    }

    public int getUserCount() {
        return this.userCount;
    }

    public int getPayment() {
        return this.payment;
    }
}
