package com.example.mocketl;

public class EtlShutdownHook extends Thread {

    private ApplicationController controller;

    public EtlShutdownHook(ApplicationController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        this.controller.stop();
    }
}
