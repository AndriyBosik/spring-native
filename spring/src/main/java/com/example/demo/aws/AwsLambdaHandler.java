package com.example.demo.aws;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.example.demo.NativeApplication;
import com.example.demo.serverless.SpringBootLambdaContainerHandler;
import com.example.demo.serverless.SpringBootProxyHandlerBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class AwsLambdaHandler implements RequestStreamHandler {
    private static final SpringBootLambdaContainerHandler<HttpApiV2ProxyRequest, AwsProxyResponse> handler;
    private static final Logger log = LoggerFactory.getLogger(AwsLambdaHandler.class);
    private final static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    static {
        try {
            handler = new SpringBootProxyHandlerBuilder<HttpApiV2ProxyRequest>()
                    .defaultHttpApiV2Proxy()
                    .springBootApplication(NativeApplication.class)
                    .servletApplication()
                    .buildAndInitialize();
        } catch (ContainerInitializationException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        String json = objectMapper.writeValueAsString(context);
        log.info("AWS Context: {}", json);
        handler.proxyStream(input, output, context);
    }
}
