package com.example.mocketl.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequestApiTest {

    private final String endPoint = "http://demo9611721.mockable.io";
    private final String path = "/procon";
    private RequestApi api;
    private JsonParser parser;

    @Before
    public void setUp() {
        this.api = new RequestApi(this.endPoint);
        this.parser = new JsonParser();
    }

    @Test
    public void Mockable_API_테스트() throws Exception {
        String res = this.api.requestGet(this.path, null);
        JSONArray jsonArray = this.parser.strToJsonArr(res);
        JSONObject json = jsonArray.getJSONObject(0);

        String expected = "hello procon";
        String actual = json.getString("msg");

        assertEquals(expected, actual);
    }
}