package com.example.mocketl.database;

import com.example.mocketl.model.PaymentStats;

public interface PaymentDao {
    int insertOne(PaymentStats statsPayment);
}
