package com.haeungun.mocketl.model;

public class PaymentStats {

    private final String name;
    private final int userCount;
    private final int payment;

    public PaymentStats(String name, int userCount, int payment) {
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
