package com.example.mocketl.queue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MemoryQueue<T> implements TopicQueue<T> {

    private final int capacity;

    private Map<String, BlockingQueue<T>> queues;

    public MemoryQueue(int capacity) {
        this.capacity = capacity;
        this.queues = new ConcurrentHashMap<>();
    }

    public void createTopic(String topicName) {
        BlockingQueue<T> queue = new ArrayBlockingQueue<>(this.capacity);
        this.queues.put(topicName, queue);
    }

    @Override
    public boolean produce(String topicName, T t) {
        if (!this.queues.containsKey(topicName)) return false;

        BlockingQueue<T> queue = this.queues.get(topicName);
        try {
            queue.put(t);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }

    @Override
    public T consume(String topicName, int timeout) {
        if (!this.queues.containsKey(topicName)) return null;

        T item = null;
        BlockingQueue<T> queue = this.queues.get(topicName);
        try {
            item = queue.poll(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        return item;
    }

    @Override
    public List<String> getTopics() {
        return this.queues.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public int topicSize(String topicName) {
        return this.queues.get(topicName).size();
    }

    public BlockingQueue<T> getQueueByTopicName(String topic) {
        return this.queues.get(topic);
    }

    public boolean hasTopic(String topic) {
        return this.queues.containsKey(topic);
    }
}
