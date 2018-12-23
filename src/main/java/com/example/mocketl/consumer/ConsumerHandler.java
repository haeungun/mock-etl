package com.example.mocketl.consumer;

import com.example.mocketl.ApplicationContext;
import com.example.mocketl.database.StatsCountryPaymentDao;
import com.example.mocketl.database.StatsGenderPaymentDao;
import com.example.mocketl.model.UserLog;
import com.example.mocketl.queue.MemoryQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConsumerHandler {
	private static final Logger logger = LoggerFactory.getLogger(ConsumerHandler.class);

	private final ApplicationContext context;
	private final MemoryQueue<UserLog> queue;
	private final ExecutorService executor;

	public ConsumerHandler(ApplicationContext context, MemoryQueue<UserLog> queue) {
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
        BlockingQueue<UserLog> countryQueue = this.queue.getQueueByTopicName(topicName);
		Consumer consumer = new StatsCountryConsumer(this.context, countryQueue, dao);
		this.executor.execute(consumer);
	}

	private void registerStatsGenderConsumer() {
		String dbUrl = this.context.getConfig().getDbUrl();
        String topicName = "gender";
		StatsGenderPaymentDao dao = new StatsGenderPaymentDao(dbUrl);
		BlockingQueue<UserLog> genderQueue  = this.queue.getQueueByTopicName(topicName);
		Consumer consumer = new StatsGenderConsumer(this.context, genderQueue, dao);
		this.executor.execute(consumer);
	}
}
