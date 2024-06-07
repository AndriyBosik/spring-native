package com.example.demo.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("data-source")
public class DataSourceProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
