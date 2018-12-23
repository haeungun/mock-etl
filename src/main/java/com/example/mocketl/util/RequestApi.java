package com.example.mocketl.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

enum Method {
    POST,
    GET;
}

public class RequestApi implements IRequest {

    private final String endPoint;

    public RequestApi(String endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public String requestGet(String path, Map<String, String> header) throws IOException {
        if (header == null) {
            header = new HashMap<>();
        }
        String baseUrl = this.endPoint + path;
        return this.connection(baseUrl, Method.GET, header);
    }

    private String connection(String baseUrl, Method method, Map<String, String> header) throws IOException {
        StringBuilder url = new StringBuilder(baseUrl);

        URL apiUrl = new URL(url.toString());
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod(method.name());

        for (Map.Entry<String, String> h : header.entrySet()) {
            connection.setRequestProperty(h.getKey(), h.getValue());
        }

        int responseCode = connection.getResponseCode();

        BufferedReader br;
        if (responseCode == 200) {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();

        return response.toString();
    }

}