package com.haeungun.mocketl.queue;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MemoryQueue<T> implements TopicQueue<T> {

    private final int capacity;

    private Map<String, BlockingQueue<T>> queues;

    public MemoryQueue(int capacity) {
        this.capacity = capacity;
        this.queues = new ConcurrentHashMap<>();
    }

    @Override
    public boolean produce(String topicName, T t) {
        if (!this.queues.containsKey(topicName))
            this.createTopic(topicName);

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

    private void createTopic(String topicName) {
        BlockingQueue<T> queue = new ArrayBlockingQueue<>(this.capacity);
        this.queues.put(topicName, queue);
    }
}
