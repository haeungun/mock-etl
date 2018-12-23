package com.example.mocketl.exceptions;

public class MockEtlDatabaseConfigException extends Exception {
    private static final String MSG = "Database config is invalid";

    public MockEtlDatabaseConfigException() {
        super(MSG);
    }
}
