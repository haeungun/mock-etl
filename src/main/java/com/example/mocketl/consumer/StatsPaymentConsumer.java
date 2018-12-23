package com.example.mocketl.consumer;

import com.example.mocketl.ApplicationContext;
import com.example.mocketl.database.PaymentDao;
import com.example.mocketl.model.UserLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class StatsPaymentConsumer implements Consumer {
    private static final Logger logger = LoggerFactory.getLogger(StatsPaymentConsumer.class);

    private final ApplicationContext context;
    private final BlockingQueue<UserLog> queue;

    private final int pollTimeout;
    private final int recordCount;

    protected final PaymentDao dao;
    protected final List<UserLog> userLogs;

    public StatsPaymentConsumer(ApplicationContext context,
                                BlockingQueue<UserLog> queue,
                                PaymentDao dao) {
        this.context = context;
        this.queue = queue;
        this.dao = dao;
        this.userLogs = new ArrayList<>();
        this.pollTimeout = context.getConfig().getPollTimeout();
        this.recordCount = context.getConfig().getRecordCount();
    }

    @Override
    public void run() {
        while (this.context.isConsumerRunning() || !this.queue.isEmpty()) {
            try {
                UserLog userLog = this.queue.poll(pollTimeout, TimeUnit.SECONDS);

                if (userLog != null) {
                    logger.info(userLog.toString());
                    this.userLogs.add(userLog);
                }

                if ((userLog == null) || this.userLogs.size() == this.recordCount) {
                    if (this.savePaymentState()) {
                        this.userLogs.clear();
                    } else {
                        // TODO do something ...
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

        if (this.userLogs.size() > 0) this.savePaymentState();
        logger.info("StatsPaymentConsumer is exit ..");
    }

    protected abstract boolean savePaymentState();
}
