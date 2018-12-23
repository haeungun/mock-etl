package com.example.mocketl.producer;

import com.example.mocketl.ApplicationConfig;
import com.example.mocketl.ApplicationContext;
import com.example.mocketl.exceptions.MockEtlJsonConvertException;
import com.example.mocketl.model.UserLog;
import com.example.mocketl.queue.MemoryQueue;
import com.example.mocketl.util.IRequest;
import com.example.mocketl.util.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.assertEquals;

class DummyApiRequest implements IRequest {

    private String dummy;
    private List<UserLog> expected;
    private ApplicationConfig config;

    public DummyApiRequest() throws MockEtlJsonConvertException {
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
        this.dummy = builder.toString();

        JSONArray jsonArr = parser.strToJsonArr(this.dummy);

        this.expected = new ArrayList<>();
        this.config = new ApplicationConfig();
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject json = jsonArr.getJSONObject(i);
            this.expected.add(new UserLog(json, this.config));
        }
    }

    @Override
    public String requestGet(String path, Map<String, String> header) throws IOException {
        return this.dummy;
    }

    public List<UserLog> getExpectedBaseDocuments() {
        return this.expected;
    }

}

public class UserApiProducerTest {
    private final String topicName = "test";

    private ApplicationContext context;
    private MemoryQueue<UserLog> queue;
    private DummyApiRequest dummyApi;

    @Before
    public void setUp() throws Exception {
        int capacity = 10;

        this.context = new ApplicationContext();
        this.queue = new MemoryQueue<>(capacity);
        this.queue.createTopic(this.topicName);
        this.dummyApi = new DummyApiRequest();
    }

    @Test
    public void ApiProducer_실행테스트() throws InterruptedException {
        Producer producer = new UserApiProducer(this.context, this.queue, this.dummyApi);

        Thread thread = new Thread(producer);
        thread.start();
        thread.join();

        List<UserLog> expectedBaseDocuments = this.dummyApi.getExpectedBaseDocuments();
        int expectedSize = expectedBaseDocuments.size();

        assertEquals(expectedSize, this.queue.topicSize(this.topicName));

        for (int i = 0; i < expectedSize; i++) {
            UserLog document = this.queue.consume(this.topicName, 1);
            assertEquals(expectedBaseDocuments.get(i), document);
        }
    }

}