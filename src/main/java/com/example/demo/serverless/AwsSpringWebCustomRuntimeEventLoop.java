/*
 * Copyright 2024-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo.serverless;

import com.amazonaws.serverless.proxy.internal.servlet.AwsProxyHttpServletResponseWriter;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.cloud.function.serverless.web.ServerlessMVC;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Event loop and necessary configurations to support AWS Lambda Custom Runtime
 * - https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html.
 *
 * @author Oleg Zhurakousky
 * @author Mark Sailes
 */
public final class AwsSpringWebCustomRuntimeEventLoop implements SmartLifecycle {

    private static Log logger = LogFactory.getLog(AwsSpringWebCustomRuntimeEventLoop.class);

    static final String LAMBDA_VERSION_DATE = "2018-06-01";
    private static final String LAMBDA_ERROR_URL_TEMPLATE = "http://{0}/{1}/runtime/invocation/{2}/error";
    private static final String LAMBDA_RUNTIME_URL_TEMPLATE = "http://{0}/{1}/runtime/invocation/next";
    private static final String LAMBDA_INVOCATION_URL_TEMPLATE = "http://{0}/{1}/runtime/invocation/{2}/response";
    private static final String USER_AGENT_VALUE = String.format("spring-cloud-function/%s-%s",
            System.getProperty("java.runtime.version"), AwsSpringHttpProcessingUtils.extractVersion());

    private final ServletWebServerApplicationContext applicationContext;

    private volatile boolean running;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AwsSpringWebCustomRuntimeEventLoop(ServletWebServerApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void run() {
        this.running = true;
        this.executor.execute(() -> {
            eventLoop(this.applicationContext);
        });
    }

    @Override
    public void start() {
        this.run();
    }

    @Override
    public void stop() {
        this.executor.shutdownNow();
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    private void eventLoop(ServletWebServerApplicationContext context) {
        ServerlessMVC mvc = ServerlessMVC.INSTANCE(context);

        Environment environment = context.getEnvironment();
        logger.info("Starting AWSWebRuntimeEventLoop");
        if (logger.isDebugEnabled()) {
            logger.debug("AWS LAMBDA ENVIRONMENT: " + System.getenv());
        }

        String runtimeApi = environment.getProperty("AWS_LAMBDA_RUNTIME_API");
        String eventUri = MessageFormat.format(LAMBDA_RUNTIME_URL_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE);
        if (logger.isDebugEnabled()) {
            logger.debug("Event URI: " + eventUri);
        }

        RequestEntity<Void> requestEntity = RequestEntity.get(URI.create(eventUri))
                .header("User-Agent", USER_AGENT_VALUE).build();
        RestTemplate rest = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();//.getBean(ObjectMapper.class);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        AwsProxyHttpServletResponseWriter responseWriter = new AwsProxyHttpServletResponseWriter();

        logger.info("Entering event loop");
//        while (this.isRunning()) {
            logger.debug("Attempting to get new event");
//            ResponseEntity<String> incomingEvent = rest.exchange(requestEntity, String.class);
            ResponseEntity<String> incomingEvent = mockCall();

            if (incomingEvent != null && incomingEvent.hasBody()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("New Event received from AWS Gateway: " + incomingEvent.getBody());
                }
                String requestId = incomingEvent.getHeaders().getFirst("Lambda-Runtime-Aws-Request-Id");

                try {
                    logger.debug("Submitting request to the user's web application");

                    HttpServletRequest request = AwsSpringHttpProcessingUtils.generateHttpServletRequest(incomingEvent.getBody(),
                            null, mvc.getServletContext(), mapper);
                    AwsProxyResponse awsResponse = AwsSpringHttpProcessingUtils.processRequest(
                            request, mvc, responseWriter);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Received response - body: " + awsResponse.getBody() +
                                "; status: " + awsResponse.getStatusCode() + "; headers: " + awsResponse.getHeaders());
                    }
                    System.out.println(awsResponse.getBody());

//                    String invocationUrl = MessageFormat.format(LAMBDA_INVOCATION_URL_TEMPLATE, runtimeApi,
//                            LAMBDA_VERSION_DATE, requestId);
//
//                    ResponseEntity<byte[]> result = rest.exchange(RequestEntity.post(URI.create(invocationUrl))
//                            .header("User-Agent", USER_AGENT_VALUE).body(awsResponse), byte[].class);
//                    if (logger.isDebugEnabled()) {
//                        logger.debug("Response sent: body: " + result.getBody() +
//                                "; status: " + result.getStatusCode() + "; headers: " + result.getHeaders());
//                    }
//                    if (logger.isInfoEnabled()) {
//                        logger.info("Result POST status: " + result);
//                    }
                } catch (Exception e) {
                    logger.error(e);
                    this.propagateAwsError(requestId, e, mapper, runtimeApi, rest);
                }
            }
//        }
    }

    private void propagateAwsError(String requestId, Exception e, ObjectMapper mapper, String runtimeApi, RestTemplate rest) {
        String errorMessage = e.getMessage();
        String errorType = e.getClass().getSimpleName();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        Map<String, String> em = new HashMap<>();
        em.put("errorMessage", errorMessage);
        em.put("errorType", errorType);
        em.put("stackTrace", stackTrace);
        try {
            byte[] outputBody = mapper.writeValueAsBytes(em);
            String errorUrl = MessageFormat.format(LAMBDA_ERROR_URL_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE, requestId);
            ResponseEntity<Object> result = rest.exchange(RequestEntity.post(URI.create(errorUrl))
                    .header("User-Agent", USER_AGENT_VALUE)
                    .body(outputBody), Object.class);
            if (logger.isInfoEnabled()) {
                logger.info("Result ERROR status: " + result.getStatusCode());
            }
        } catch (Exception e2) {
            throw new IllegalArgumentException("Failed to report error", e2);
        }
    }

    private ResponseEntity<String> mockCall() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Lambda-Runtime-Aws-Request-Id", UUID.randomUUID().toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body("{\"version\":\"2.0\",\"routeKey\":\"ANY /{proxy+}\",\"rawPath\":\"/users\",\"rawQueryString\":\"\",\"headers\":{\"accept\":\"*/*\",\"authorization\":\"Basic YWRtaW46YWRtaW4=\",\"accept-encoding\":\"br, deflate, gzip, x-gzip\",\"content-length\":\"0\",\"host\":\"y3jrpavq7h.execute-api.eu-west-1.amazonaws.com\",\"user-agent\":\"IntelliJ HTTP Client/IntelliJ IDEA 2024.1\",\"x-amzn-trace-id\":\"Root=1-66620525-2f9f49ff5ad163201b793f16\",\"x-forwarded-for\":\"91.211.134.126\",\"x-forwarded-port\":\"443\",\"x-forwarded-proto\":\"https\"},\"requestContext\":{\"accountId\":\"058264158658\",\"apiId\":\"y3jrpavq7h\",\"domainName\":\"y3jrpavq7h.execute-api.eu-west-1.amazonaws.com\",\"domainPrefix\":\"y3jrpavq7h\",\"identity\":{\"cognitoIdentityPoolId\":null,\"accountId\":\"\",\"cognitoIdentityId\":null,\"caller\":null,\"accessKey\":null,\"sourceIp\":\"127.0.0.1\",\"cognitoAuthenticationType\":null,\"cognitoAuthenticationProvider\":null,\"userArn\":null,\"userAgent\":\"Custom User Agent String\",\"user\":null},\"http\":{\"method\":\"GET\",\"path\":\"/users\",\"protocol\":\"HTTP/1.1\",\"sourceIp\":\"91.211.134.126\",\"userAgent\":\"IntelliJ HTTP Client/IntelliJ IDEA 2024.1\"},\"requestId\":\"Y9W9-idxDoEEPDQ=\",\"routeKey\":\"ANY /{proxy+}\",\"stage\":\"$default\",\"time\":\"06/Jun/2024:18:51:17 +0000\",\"timeEpoch\":1717699877925},\"isBase64Encoded\":false}");
    }
}
