package com.haeungun.mocketl;

import com.haeungun.mocketl.util.PropertyUtil;

import java.util.HashMap;
import java.util.Map;

public class ApplicationConfig {
    private static final String PROGRAM_NAME = "mocketl";

    private final String apiEndpoint;
    private final String apiTokenKey;
    private final String apiTokenValue;
    private final String dbUrl;
    private final String defaultUserCountry;
    private final int consumerTimeout;
    private final int defaultUserAge;
    private final int producerTimeout;
    private final int producerTerm;
    private final int pollTimeout;
    private final int queueCapacity;
    private final int recordCount;
    private final String[] topicNames;


    public ApplicationConfig() {
        PropertyUtil propertyUtil = new PropertyUtil(PROGRAM_NAME);

        this.apiEndpoint = propertyUtil.getStringValue("api.endpoint");
        this.apiTokenKey = propertyUtil.getStringValue("api.token.key");
        this.apiTokenValue = propertyUtil.getStringValue("api.token.value");
        this.dbUrl = propertyUtil.getStringValue("db.url");
        this.defaultUserCountry = propertyUtil.getStringValue("default.user.country");

        this.consumerTimeout = propertyUtil.getIntValue("consumer.shutdown.timeout");
        this.defaultUserAge = propertyUtil.getIntValue("default.user.age");
        this.producerTimeout = propertyUtil.getIntValue("producer.shutdown.timeout");
        this.producerTerm = propertyUtil.getIntValue("producer.term");
        this.pollTimeout = propertyUtil.getIntValue("poll.wait.timeout");
        this.queueCapacity = propertyUtil.getIntValue("queue.capacity");
        this.recordCount = propertyUtil.getIntValue("stats.record.count");

        this.topicNames = propertyUtil.getStringValue("queue.topics").split("\\|");
    }

    public String getDefaultUserCountry() {
        return this.defaultUserCountry;
    }

    public int getDefaultUserAge() {
        return this.defaultUserAge;
    }

    public String getApiEndpoint() {
        return this.apiEndpoint;
    }

    public Map<String, String> getApiToken() {
        Map<String, String> apiToken = new HashMap<>();
        apiToken.put(this.apiTokenKey, this.apiTokenValue);
        return apiToken;
    }

    public String getDbUrl() {
        return this.dbUrl;
    }

    public int getConsumerTimeout() {
        return this.consumerTimeout;
    }

    public int getProducerTimeout() {
        return this.producerTimeout;
    }

    public int getProducerTerm() {
        return this.producerTerm;
    }

    public int getPollTimeout() {
        return this.pollTimeout;
    }

    public int getQueueCapacity() {
        return this.queueCapacity;
    }

    public int getRecordCount() {
        return this.recordCount;
    }

    public String[] getTopicNames() {
        return this.topicNames;
    }
}
