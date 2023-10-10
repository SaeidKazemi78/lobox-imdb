package com.lobox.imdblobox.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * This class is used for counting total  request count
 */

public class RequestCounterInterceptor implements HandlerInterceptor {
    private final RequestCounter requestCounter;
    public RequestCounterInterceptor(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        requestCounter.increment();
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
