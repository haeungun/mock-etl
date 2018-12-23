package com.example.mocketl.consumer;

import com.example.mocketl.ApplicationContext;
import com.example.mocketl.database.StatsCountryPaymentDao;
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
        Map<String, Integer> stats = new HashMap<>();

        for (UserLog userLog : this.userLogs) {
            String country = userLog.getCountry();
            int payment = stats.getOrDefault(country, 0);
            payment += userLog.getPayment();
            stats.put(country, payment);
        }

        int savedCount = 0;
        int numOfCountry = stats.size();
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            String country = entry.getKey();
            int amountOfPayment = entry.getValue();
            savedCount += this.dao.insertOne(country, amountOfPayment);
        }

        return numOfCountry == savedCount;
    }

}
