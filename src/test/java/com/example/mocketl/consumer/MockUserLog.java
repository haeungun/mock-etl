package com.example.mocketl.consumer;

import com.example.mocketl.ApplicationConfig;
import com.example.mocketl.exceptions.MockEtlJsonConvertException;
import com.example.mocketl.model.UserLog;
import com.example.mocketl.util.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MockUserLog {

    private List<UserLog> mock;

    public MockUserLog() throws MockEtlJsonConvertException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        StringBuilder builder = new StringBuilder();
        String file = loader.getResource("user_log.json").getFile();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String s;
            while ((s = br.readLine()) != null)
                builder.append(s);
        } catch (Exception e) {
            // TODO handling error
            e.printStackTrace();
        }

        JsonParser parser = new JsonParser();
        String dummy = builder.toString();

        JSONArray jsonArr = parser.strToJsonArr(dummy);

        this.mock = new ArrayList<>();
        ApplicationConfig config = new ApplicationConfig();
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject json = jsonArr.getJSONObject(i);
            this.mock.add(new UserLog(json, config));
        }
    }

    public List<UserLog> getMock() {
        return this.mock;
    }
}