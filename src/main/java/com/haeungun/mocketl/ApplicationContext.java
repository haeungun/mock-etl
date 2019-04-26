package com.haeungun.mocketl;

import com.haeungun.mocketl.enums.ExecuteState;

public class ApplicationContext {

    private final ApplicationConfig config;
    private volatile ExecuteState executeState;

    public ApplicationContext() {
        this.config = new ApplicationConfig();
        this.executeState = ExecuteState.READY;
    }

    public void setState(ExecuteState state) {
        this.setConsumerState(state);
    }

    public void setConsumerState(ExecuteState state) {
        this.executeState = state;
    }

    public boolean isRunning() {
        return ExecuteState.RUNNING == this.executeState;
    }

    public ApplicationConfig getConfig() {
        return this.config;
    }

}
