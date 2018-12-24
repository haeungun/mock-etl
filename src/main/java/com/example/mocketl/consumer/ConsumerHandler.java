package com.example.mocketl.consumer;

import com.example.mocketl.ApplicationContext;
import com.example.mocketl.database.StatsCountryPaymentDao;
import com.example.mocketl.database.StatsGenderPaymentDao;
import com.example.mocketl.model.UserLog;
import com.example.mocketl.queue.TopicQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConsumerHandler {
	private static final Logger logger = LoggerFactory.getLogger(ConsumerHandler.class);

	private final ApplicationContext context;
	private final TopicQueue<UserLog> queue;
	private final ExecutorService executor;

	public ConsumerHandler(ApplicationContext context, TopicQueue<UserLog> queue) {
		this.context = context;
		this.queue = queue;
		this.executor = Executors.newCachedThreadPool();
	}

	public boolean registerProducer() {
		try {
			this.registerStatsCountryConsumer();
			this.registerStatsGenderConsumer();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}

		return true;
	}

	public boolean shutdown() {
		int timeout = this.context.getConfig().getConsumerTimeout();

		this.executor.shutdown();
		try {
			while (!this.executor.awaitTermination(timeout, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			this.executor.shutdownNow();
		}

		return true;
	}

	// TODO(haeungun): Refactoring registerStatsXX
	private void registerStatsCountryConsumer() {
        String dbUrl = this.context.getConfig().getDbUrl();
        String topicName = "country";
        StatsCountryPaymentDao dao = new StatsCountryPaymentDao(dbUrl);
        Consumer consumer = new StatsCountryConsumer(this.context,this.queue, topicName, dao);
        this.executor.execute(consumer);
	}

	private void registerStatsGenderConsumer() {
        String dbUrl = this.context.getConfig().getDbUrl();
        String topicName = "gender";
        StatsGenderPaymentDao dao = new StatsGenderPaymentDao(dbUrl);
        Consumer consumer = new StatsGenderConsumer(this.context, this.queue, topicName, dao);
        this.executor.execute(consumer);
	}
}
