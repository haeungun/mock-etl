package com.haeungun.mocketl.producer;

import com.haeungun.mocketl.ApplicationConfig;
import com.haeungun.mocketl.ApplicationContext;
import com.haeungun.mocketl.exceptions.MockEtlJsonConvertException;
import com.haeungun.mocketl.model.UserLog;
import com.haeungun.mocketl.queue.TopicQueue;
import com.haeungun.mocketl.util.IRequest;
import com.haeungun.mocketl.util.JsonParser;
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
    private final TopicQueue<UserLog> queue;

    private final IRequest request;
    private final JsonParser parser;

    private final String path;
    private final Map<String, String> token;

    public UserApiProducer(ApplicationContext context, TopicQueue<UserLog> queue, IRequest request) {
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
            List<UserLog> documents = this.convertStrToUserLogs(response);
            String[] topics = this.context.getConfig().getTopicNames();

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
            logger.error(e.getMessage());
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
