package com.example.mocketl;

import com.example.mocketl.enums.ExecuteState;

public class ApplicationContext {

	private final ApplicationConfig config;
	private ExecuteState producerState;
	private ExecuteState consumerState;
	
	public ApplicationContext() throws Exception {
		this.config = new ApplicationConfig();
		this.producerState = ExecuteState.READY;
		this.consumerState = ExecuteState.READY;
	}

	public void setState(ExecuteState state) {
		this.setProducerState(state);
		this.setConsumerState(state);
	}

	public void setProducerState(ExecuteState state) {
		this.producerState = state;
	}
	
	public void setConsumerState(ExecuteState state) {
		this.consumerState = state;
	}
	
	public boolean isProducerRunning() {
		return ExecuteState.RUNNING == this.producerState;
	}
	
	public boolean isConsumerRunning() {
		return ExecuteState.RUNNING == this.consumerState;
	}
	
	public boolean isRunning() {
		return this.isProducerRunning() && this.isConsumerRunning();
	}
	
	public ApplicationConfig getConfig() {
		return this.config;
	}

}
