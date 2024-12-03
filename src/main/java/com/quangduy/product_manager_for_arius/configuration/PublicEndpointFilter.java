package com.quangduy.product_manager_for_arius.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import com.quangduy.product_manager_for_arius.util.annotation.PublicEndpoint;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class PublicEndpointFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Object handler = request.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler");

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.getMethodAnnotation(PublicEndpoint.class) != null) {
                filterChain.doFilter(request, response); // Bỏ qua bảo mật
                return;
            }
        }

        // Tiếp tục các bộ lọc khác
        filterChain.doFilter(request, response);
    }
}
