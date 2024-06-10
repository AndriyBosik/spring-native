package com.example.demo.config;

import com.amazonaws.serverless.proxy.AwsProxySecurityContextWriter;
import com.amazonaws.serverless.proxy.internal.jaxrs.AwsHttpApiV2SecurityContext;
import com.amazonaws.serverless.proxy.internal.jaxrs.AwsProxySecurityContext;
import com.amazonaws.serverless.proxy.internal.servlet.filters.UrlPathValidator;
import com.amazonaws.serverless.proxy.model.AlbContext;
import com.amazonaws.serverless.proxy.model.ApiGatewayAuthorizerContext;
import com.amazonaws.serverless.proxy.model.ApiGatewayRequestIdentity;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyRequestContext;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.CognitoAuthorizerClaims;
import com.amazonaws.serverless.proxy.model.ContainerConfig;
import com.amazonaws.serverless.proxy.model.ErrorModel;
import com.amazonaws.serverless.proxy.model.Headers;
import com.amazonaws.serverless.proxy.model.HttpApiV2AuthorizerMap;
import com.amazonaws.serverless.proxy.model.HttpApiV2HttpContext;
import com.amazonaws.serverless.proxy.model.HttpApiV2JwtAuthorizer;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequestContext;
import com.amazonaws.serverless.proxy.model.MultiValuedTreeMap;
import com.amazonaws.serverless.proxy.model.RequestSource;
import com.amazonaws.serverless.proxy.model.SingleValueHeaders;
import com.example.demo.dto.PageDto;
import com.example.demo.model.PageRequest;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import java.util.stream.Stream;

@Slf4j
public class MyRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        log.info("Registering runtime hints");

        try {
            hints.reflection()
                    .registerConstructor(ParameterNamesModule.class.getDeclaredConstructor(), ExecutableMode.INVOKE)
                    .registerConstructor(HttpApiV2ProxyRequest.class.getDeclaredConstructor(), ExecutableMode.INVOKE)
                    .registerConstructor(AwsProxyResponse.class.getDeclaredConstructor(), ExecutableMode.INVOKE);

            Stream.of(
                            HttpApiV2ProxyRequest.class,
                            HttpApiV2ProxyRequestContext.class,
                            AwsProxyResponse.class,
                            HttpApiV2AuthorizerMap.HttpApiV2AuthorizerSerializer.class,
                            HttpApiV2AuthorizerMap.HttpApiV2AuthorizerDeserializer.class,
                            AwsProxySecurityContextWriter.class,
                            AwsHttpApiV2SecurityContext.class,
                            AwsProxySecurityContext.class,
                            AwsProxySecurityContext.CognitoUserPoolPrincipal.class,
                            UrlPathValidator.class,
                            AlbContext.class,
                            ApiGatewayAuthorizerContext.class,
                            ApiGatewayRequestIdentity.class,
                            AwsProxyRequest.class,
                            AwsProxyRequestContext.class,
                            CognitoAuthorizerClaims.class,
                            ContainerConfig.class,
                            ErrorModel.class,
                            Headers.class,
                            HttpApiV2AuthorizerMap.class,
                            HttpApiV2HttpContext.class,
                            HttpApiV2JwtAuthorizer.class,
                            MultiValuedTreeMap.class,
                            RequestSource.class,
                            SingleValueHeaders.class,
                            PageDto.class,
                            PageRequest.class)
                    .forEach(type -> hints.reflection().registerType(
                            type,
                            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                            MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS,
                            MemberCategory.INVOKE_PUBLIC_METHODS,
                            MemberCategory.INTROSPECT_PUBLIC_METHODS));

            Stream.of(
                            org.apache.ibatis.logging.nologging.NoLoggingImpl.class,
                            org.apache.ibatis.logging.stdout.StdOutImpl.class,
                            org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl.class,
                            org.apache.ibatis.logging.log4j2.Log4j2Impl.class,
                            org.apache.ibatis.logging.log4j.Log4jImpl.class,
                            org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl.class,
                            org.apache.ibatis.logging.slf4j.Slf4jImpl.class)
                    .forEach(type -> hints.reflection().registerType(
                            type,
                            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                            MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS,
                            MemberCategory.INVOKE_PUBLIC_METHODS,
                            MemberCategory.INTROSPECT_PUBLIC_METHODS));

        } catch (NoSuchMethodException exception) {
            throw new RuntimeException(exception);
        }
    }
}
