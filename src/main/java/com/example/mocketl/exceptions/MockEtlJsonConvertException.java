package com.example.mocketl.exceptions;

public class MockEtlJsonConvertException extends Exception {
    private static final String MSG = "cannot be converted to json: ";

    public MockEtlJsonConvertException(String str) {
        super(MSG + str);
    }
}
