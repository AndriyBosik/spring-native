package com.example.demo.serverless;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.cloud.function.serverless.web.ServerlessHttpServletRequest;

public class CustomServerlessHttpServletRequest extends ServerlessHttpServletRequest {
    public CustomServerlessHttpServletRequest(ServletContext servletContext, String method, String requestURI) {
        super(servletContext, method, requestURI);
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }
}
