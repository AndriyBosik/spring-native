package com.example.demo;

import com.example.demo.config.MyRuntimeHints;
import com.example.demo.config.properties.DataSourceProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportRuntimeHints;

@SpringBootApplication
@EnableConfigurationProperties(DataSourceProperties.class)
@ImportRuntimeHints(MyRuntimeHints.class)
public class NativeApplication {
	private static final Log log = LogFactory.getLog(NativeApplication.class);

	static {
		log.info("Started (Static Block)");
	}

	public static void main(String[] args) {
		log.info("Started (Main Method)");
		SpringApplication.run(NativeApplication.class, args);
	}

}
