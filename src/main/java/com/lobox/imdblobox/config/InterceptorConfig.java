package com.lobox.imdblobox.config;

import com.lobox.imdblobox.interceptor.RequestCounter;
import com.lobox.imdblobox.interceptor.RequestCounterInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class InterceptorConfig  implements WebMvcConfigurer {
    private final RequestCounter requestCounter;
    public InterceptorConfig(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestCounterInterceptor(requestCounter))
                .addPathPatterns("/**");
    }
}
