package com.example.mocketl.producer;

import com.example.mocketl.ApplicationContext;
import com.example.mocketl.model.UserLog;
import com.example.mocketl.queue.MemoryQueue;
import com.example.mocketl.util.IRequest;
import com.example.mocketl.util.RequestApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ProducerHandler {
	private static final Logger logger = LoggerFactory.getLogger(ProducerHandler.class);

	private final ApplicationContext context;
	private final MemoryQueue<UserLog> queue;
	private final ScheduledExecutorService executor;
	private final int produceTerm;

	public ProducerHandler(ApplicationContext context, MemoryQueue<UserLog> queue) {
		this.context = context;
		this.queue = queue;
		this.executor = Executors.newSingleThreadScheduledExecutor();
		this.produceTerm = context.getConfig().getProducerTerm();
	}

	public boolean registerProducer() {
		try {
			this.registerApiProducer();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}

		return true;
	}

	public boolean shutdown() {
		int timeout = this.context.getConfig().getProducerTimeout();

		this.executor.shutdown();
		try {
			while (!this.executor.awaitTermination(timeout, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			this.executor.shutdownNow();
		}

		return true;
	}

	private void registerApiProducer() {
		int initialTerm = 0;
		String endPoint = this.context.getConfig().getApiEndpoint();
		IRequest requestApi = new RequestApi(endPoint);
		Producer apiProducer = new UserApiProducer(this.context, this.queue, requestApi);
		this.executor.scheduleWithFixedDelay(apiProducer, initialTerm, this.produceTerm, TimeUnit.SECONDS);
	}
}
