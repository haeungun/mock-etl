package com.haeungun.mocketl.consumer;

import com.haeungun.mocketl.ApplicationContext;
import com.haeungun.mocketl.database.PaymentDao;
import com.haeungun.mocketl.model.UserLog;
import com.haeungun.mocketl.queue.TopicQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPaymentConsumer implements Consumer {
    private static final Logger logger = LoggerFactory.getLogger(AbstractPaymentConsumer.class);

    private final ApplicationContext context;
    private final TopicQueue<UserLog> queue;

    private final int pollTimeout;
    private final int recordCount;
    private final String topicName;

    protected final PaymentDao dao;
    protected final List<UserLog> userLogs;

    public AbstractPaymentConsumer(ApplicationContext context,
                                   TopicQueue<UserLog> queue,
                                   String topicName,
                                   PaymentDao dao) {
        this.context = context;
        this.queue = queue;
        this.dao = dao;
        this.userLogs = new ArrayList<>();
        this.topicName = topicName;
        this.pollTimeout = context.getConfig().getPollTimeout();
        this.recordCount = context.getConfig().getRecordCount();
    }

    @Override
    public void run() {
        UserLog userLog = null;
        while (this.context.isRunning() || userLog != null) {
            try {
                userLog = this.queue.consume(this.topicName, this.pollTimeout);
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
