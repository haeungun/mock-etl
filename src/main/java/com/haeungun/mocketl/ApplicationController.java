package com.haeungun.mocketl;

import com.haeungun.mocketl.consumer.ConsumerHandler;
import com.haeungun.mocketl.enums.ExecuteState;
import com.haeungun.mocketl.model.UserLog;
import com.haeungun.mocketl.producer.ProducerHandler;
import com.haeungun.mocketl.queue.MemoryQueue;
import com.haeungun.mocketl.queue.TopicQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationController {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

	private ApplicationContext context;
	private ProducerHandler producerHandler;
	private ConsumerHandler consumerHandler;
	private TopicQueue<UserLog> queue;
	
	public ApplicationController() {
		this.context = new ApplicationContext();

        int capacity = this.context.getConfig().getQueueCapacity();
        this.queue = new MemoryQueue<>(capacity);

		this.producerHandler = new ProducerHandler(this.context, this.queue);
		this.consumerHandler = new ConsumerHandler(this.context, this.queue);
	}

	/**
	 * Start application producer and consumer
	 *
	 * @return true if start successful
	 */
	public boolean start() {
		logger.info("Application is starting ...");
		this.context.setState(ExecuteState.RUNNING);

		if (!this.producerHandler.registerProducer()) {
			logger.error("Failed to register producer !!");
			return false;
		}

		if (!this.consumerHandler.registerProducer()) {
			logger.error("Failed to register consumer !!");
			return false;
		}

		logger.info("Application is running ...");
		return true;
	}

	/**
	 * Shutdown application producer and consumer
	 *
	 * @return true if stop successful
	 */
	public boolean stop() {
		logger.info("Application will be shutdown ...");
		this.context.setState(ExecuteState.STOP);

		if (!this.producerHandler.shutdown()) {
			logger.error("Failed to shutdown producer !!");
			return false;
		}

		if (!this.consumerHandler.shutdown()) {
			logger.error("Failed to shutdown consumer !!");
			return false;
		}

		logger.info("Application is stopped :D");
		return true;
	}

}
