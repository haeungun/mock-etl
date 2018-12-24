package com.haeungun.mocketl.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyUtilTest {

    private PropertyUtil propertyUtil;

    @Before
    public void setUp() {
        this.propertyUtil = new PropertyUtil("test");
    }

    @Test
    public void 프로퍼티파일로부터_문자값읽기() {
        String expected = "barrack";
        String actual = this.propertyUtil.getStringValue("test.string");

        assertEquals(expected, actual);
    }

    @Test
    public void 프로퍼티파일로부터_숫자값읽기() {
        int expected = 12345;
        int actual = this.propertyUtil.getIntValue("test.number");

        assertEquals(expected, actual);
    }
}