package com.example.mocketl.consumer;

import com.example.mocketl.ApplicationContext;
import com.example.mocketl.database.PaymentDao;
import com.example.mocketl.database.StatsGenderPaymentDao;
import com.example.mocketl.model.UserLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class StatsGenderConsumer extends StatsPaymentConsumer {

    public StatsGenderConsumer(ApplicationContext context,
                               BlockingQueue<UserLog> queue,
                               StatsGenderPaymentDao dao) {
        super(context, queue, dao);
    }

    @Override
    protected boolean savePaymentState() {
        Map<String, Integer> stats = new HashMap<>();

        for (UserLog userLog : this.userLogs) {
            String country = userLog.getGender();
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
