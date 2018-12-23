package com.example.mocketl.database;

import com.example.mocketl.model.StatsPayment;

public interface PaymentDao {
    int insertOne(StatsPayment statsPayment);
}
