package com.haeungun.mocketl.database;

import com.haeungun.mocketl.model.PaymentStats;

public interface PaymentDao {
    int insertOne(PaymentStats statsPayment);
}
