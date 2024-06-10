package com.example.demo.aws;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.example.demo.NativeApplication;
import com.example.demo.serverless.SpringBootLambdaContainerHandler;
import com.example.demo.serverless.SpringBootProxyHandlerBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AwsLambdaHandler implements RequestStreamHandler {
    private static final SpringBootLambdaContainerHandler<HttpApiV2ProxyRequest, AwsProxyResponse> handler;

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
        handler.proxyStream(input, output, context);
    }
}
