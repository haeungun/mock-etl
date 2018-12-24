package com.haeungun.mocketl.queue;

import com.haeungun.mocketl.ApplicationConfig;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MemoryQueueTest {

    private ApplicationConfig config;
    private int capacity;

    @Before
    public void setUp() {
        this.capacity = 50;
    }

    @Test
    public void 토픽별_생산_소비_테스트() {
        MemoryQueue<String> queue = new MemoryQueue<>(this.capacity);

        String topic1 = "topic1";
        String topic2 = "topic2";

        String str1 = "abcde";
        String str2 = "fghij";

        queue.produce(topic1, str1);
        queue.produce(topic2, str2);
        queue.produce(topic1, str2);
        queue.produce(topic2, str1);

        assertEquals(str1, queue.consume(topic1, 1));
        assertEquals(str2, queue.consume(topic1, 1));
        assertEquals(str2, queue.consume(topic2, 1));
        assertEquals(str1, queue.consume(topic2, 1));

        assertNull(queue.consume(topic1, 1));
        assertNull(queue.consume(topic2, 1));
    }
}