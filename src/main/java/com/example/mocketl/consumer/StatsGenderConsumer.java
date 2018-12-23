package com.example.mocketl.consumer;

import com.example.mocketl.ApplicationContext;
import com.example.mocketl.database.PaymentDao;
import com.example.mocketl.database.StatsGenderPaymentDao;
import com.example.mocketl.model.StatsPayment;
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
        Map<String, Integer> payments = new HashMap<>();
        Map<String, Integer> userCounts = new HashMap<>();

        for (UserLog userLog : this.userLogs) {
            String gender = userLog.getGender();
            int payment = payments.getOrDefault(gender, 0);
            int userCount = userCounts.getOrDefault(gender, 0);
            payment += userLog.getPayment();
            payments.put(gender, payment);
            userCounts.put(gender, ++userCount);
        }

        int savedCount = 0;
        int numOfCountry = payments.size();
        for (Map.Entry<String, Integer> entry : payments.entrySet()) {
            String gender = entry.getKey();
            int amountOfPayment = entry.getValue();
            int amountOfUserCount = userCounts.get(gender);

            StatsPayment payment = new StatsPayment(gender, amountOfUserCount, amountOfPayment);
            savedCount += this.dao.insertOne(payment);
        }

        return numOfCountry == savedCount;
    }

}
