package com.example.mocketl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EtlApplication {
    private static final Logger logger = LoggerFactory.getLogger(EtlApplication.class);

    private static final int ERROR_CODE = -1;
    private static final int NORMAL_CODE = 0;

    public int run() {
        ApplicationController controller = null;
        try {
            controller = new ApplicationController();
            controller.start();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ERROR_CODE;
        }

        Runtime.getRuntime().addShutdownHook(new EtlShutdownHook(controller));
        return NORMAL_CODE;
    }

    public static void main(String[] args) {
        EtlApplication application = new EtlApplication();
        application.run();
    }
}
