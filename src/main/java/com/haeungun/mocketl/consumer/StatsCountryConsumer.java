package com.haeungun.mocketl.consumer;

import com.haeungun.mocketl.ApplicationContext;
import com.haeungun.mocketl.database.StatsCountryPaymentDao;
import com.haeungun.mocketl.model.PaymentStats;
import com.haeungun.mocketl.model.UserLog;
import com.haeungun.mocketl.queue.TopicQueue;

import java.util.HashMap;
import java.util.Map;

public class StatsCountryConsumer extends AbstractPaymentConsumer {

    public StatsCountryConsumer(ApplicationContext context,
                                TopicQueue<UserLog> queue,
                                String topicName,
                                StatsCountryPaymentDao dao) {
        super(context, queue, topicName, dao);
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

            PaymentStats payment = new PaymentStats(country, amountOfUserCount, amountOfPayment);
            savedCount += this.dao.insertOne(payment);
        }

        return numOfCountry == savedCount;
    }

}
