package com.example.mocketl.util;

import com.example.mocketl.exceptions.MockEtlJsonConvertException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonParserTest {

    private JsonParser parser;

    @Before
    public void setUp() {
        this.parser = new JsonParser();
    }

    @Test
    public void 문자열을_JSON_Array로_변환() throws MockEtlJsonConvertException {
        String str = "[{\"name\":\"procon\"}]";

        JSONArray jsonArray = this.parser.strToJsonArr(str);
        JSONObject json = jsonArray.getJSONObject(0);

        assertEquals(json.getString("name"), "procon");
    }

    @Test(expected=MockEtlJsonConvertException.class)
    public void 빈_문자열을_JSON_Array로_변환() throws MockEtlJsonConvertException {
        String str = "";
        this.parser.strToJsonArr(str);
    }

    @Test(expected=MockEtlJsonConvertException.class)
    public void 잘못된_문자열을_JSON_Array로_변환() throws MockEtlJsonConvertException {
        String str = "[{\"name\"";
        this.parser.strToJsonArr(str);
    }
}