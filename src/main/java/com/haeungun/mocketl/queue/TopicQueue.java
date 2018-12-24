package com.haeungun.mocketl.queue;

public interface TopicQueue<T> {

    /**
     * produce {{@code t}} into a given topic name
     *
     * @param topicName to produced the {{@code t}} into
     * @param t to inserted value
     * @return true if produce successful
     */
    boolean produce(String topicName, T t);

    /**
     * consume a item from a given {{@code topicName}}
     *
     * @param topicName to consumed topic
     * @param timeout if topic is empty, wait for given seconds
     * @return consumed item nullable
     */
    T consume(String topicName, int timeout);

}
