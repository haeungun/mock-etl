package com.haeungun.mocketl.consumer;

import com.haeungun.mocketl.ApplicationContext;
import com.haeungun.mocketl.database.StatsGenderPaymentDao;
import com.haeungun.mocketl.model.PaymentStats;
import com.haeungun.mocketl.model.UserLog;
import com.haeungun.mocketl.queue.TopicQueue;

import java.util.HashMap;
import java.util.Map;

public class StatsGenderConsumer extends AbstractPaymentConsumer {

    public StatsGenderConsumer(ApplicationContext context,
                               TopicQueue<UserLog> queue,
                               String topicName,
                               StatsGenderPaymentDao dao) {
        super(context, queue, topicName, dao);
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

            PaymentStats payment = new PaymentStats(gender, amountOfUserCount, amountOfPayment);
            savedCount += this.dao.insertOne(payment);
        }

        return numOfCountry == savedCount;
    }

}
