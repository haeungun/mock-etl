package com.example.mocketl.consumer;

import com.example.mocketl.ApplicationContext;
import com.example.mocketl.database.StatsCountryPaymentDao;
import com.example.mocketl.model.StatsPayment;
import com.example.mocketl.model.UserLog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class StatsCountryConsumer extends StatsPaymentConsumer {

    public StatsCountryConsumer(ApplicationContext context,
                                BlockingQueue<UserLog> queue,
                                StatsCountryPaymentDao dao) {
        super(context, queue, dao);
    }

    @Override
    protected boolean savePaymentState() {
        Map<String, Integer> payments = new HashMap<>();
        Map<String, Integer> userCounts = new HashMap<>();

        for (UserLog userLog : this.userLogs) {
            String country = userLog.getCountry();
            int payment = payments.getOrDefault(country, 0);
            int userCount = userCounts.getOrDefault(country, 0);
            payment += userLog.getPayment();
            payments.put(country, payment);
            userCounts.put(country, ++userCount);
        }

        int savedCount = 0;
        int numOfCountry = payments.size();
        for (Map.Entry<String, Integer> entry : payments.entrySet()) {
            String country = entry.getKey();
            int amountOfPayment = entry.getValue();
            int amountOfUserCount = userCounts.get(country);

            StatsPayment payment = new StatsPayment(country, amountOfUserCount, amountOfPayment);
            savedCount += this.dao.insertOne(payment);
        }

        return numOfCountry == savedCount;
    }

}
