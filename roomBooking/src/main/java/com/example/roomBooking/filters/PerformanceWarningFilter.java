package com.example.roomBooking.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

// @Component
@Order(2)
public class PerformanceWarningFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LoggingAndTracingFilter.class);
    private static final long COMPLETION_THRESHOLD_MS = 20L;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            if (duration > COMPLETION_THRESHOLD_MS) {
                log.warn("Slow request detected:  {} took {}ms",
                        request.getRequestURI(), duration);
            }
        }

    }
}
