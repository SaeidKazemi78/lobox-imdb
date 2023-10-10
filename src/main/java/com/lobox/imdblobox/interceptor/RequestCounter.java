package com.lobox.imdblobox.interceptor;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class RequestCounter {
    private final AtomicLong COUNTER = new AtomicLong(0);
    public void increment() {
        COUNTER.incrementAndGet();
    }

    public long getTotalRequestCount() {
        return COUNTER.get();
    }
}
