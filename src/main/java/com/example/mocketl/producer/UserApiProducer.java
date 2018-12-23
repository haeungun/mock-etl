package com.example.mocketl.producer;

import com.example.mocketl.ApplicationConfig;
import com.example.mocketl.ApplicationContext;
import com.example.mocketl.exceptions.MockEtlJsonConvertException;
import com.example.mocketl.model.UserLog;
import com.example.mocketl.queue.MemoryQueue;
import com.example.mocketl.util.IRequest;
import com.example.mocketl.util.JsonParser;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserApiProducer implements Producer {
    private static final Logger logger = LoggerFactory.getLogger(UserApiProducer.class);

    private final ApplicationContext context;
    private final MemoryQueue<UserLog> queue;

    private final IRequest request;
    private final JsonParser parser;

    private final String path;
    private final Map<String, String> token;

    public UserApiProducer(ApplicationContext context, MemoryQueue<UserLog> queue, IRequest request) {
        this.context = context;
        this.queue = queue;

        this.request = request;
        this.parser = new JsonParser();

        this.path = "/user_log.json";
        this.token = context.getConfig().getApiToken();
    }

    @Override
    public void run() {
        try {
            String response = this.request.requestGet(this.path, this.token);
            logger.info(response);
            List<UserLog> documents = this.convertStrToUserLogs(response);
            List<String> topics = this.queue.getTopics();

            for (UserLog document : documents) {
                for (String topic : topics) {
                    this.queue.produce(topic, document);
                }
            }

            logger.info("The number of created documents is " + documents.size());
        } catch (IOException e) {
            // TODO handling error
            logger.error(e.getMessage());
        } catch (MockEtlJsonConvertException e) {
            // TODO handling error
            logger.error(e.getMessage());
        } catch (Exception e) {
            // TODO handling error
            e.printStackTrace();
        }
    }

    private List<UserLog> convertStrToUserLogs(String str) throws MockEtlJsonConvertException {
        List<UserLog> userLogs = new ArrayList<>();

        JSONArray jsonArray = this.parser.strToJsonArr(str);

        ApplicationConfig config = this.context.getConfig();
        for (int i = 0; i < jsonArray.length(); i++) {
            userLogs.add(new UserLog(jsonArray.getJSONObject(i), config));
        }

        return userLogs;
    }
}
