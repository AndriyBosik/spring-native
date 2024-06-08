package com.example.demo.config;

import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequestContext;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.aot.hint.ExecutableMode;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class MyRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        try {
            hints.reflection()
                    .registerConstructor(ParameterNamesModule.class.getDeclaredConstructor(), ExecutableMode.INVOKE)
                    .registerConstructor(HttpApiV2ProxyRequest.class.getDeclaredConstructor(), ExecutableMode.INVOKE)
                    .registerConstructor(AwsProxyResponse.class.getDeclaredConstructor(), ExecutableMode.INVOKE)
                    .registerType(
                            HttpApiV2ProxyRequest.class,
                            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                            MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS,
                            MemberCategory.INVOKE_PUBLIC_METHODS,
                            MemberCategory.INTROSPECT_PUBLIC_METHODS)
                    .registerType(
                            HttpApiV2ProxyRequestContext.class,
                            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                            MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS,
                            MemberCategory.INVOKE_PUBLIC_METHODS,
                            MemberCategory.INTROSPECT_PUBLIC_METHODS)
                    .registerType(
                            AwsProxyResponse.class,
                            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                            MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS,
                            MemberCategory.INVOKE_PUBLIC_METHODS,
                            MemberCategory.INTROSPECT_PUBLIC_METHODS);
        } catch (NoSuchMethodException exception) {
            throw new RuntimeException(exception);
        }
    }
}
