package com.cisco.wccai.grpc.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class LoadProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadProperties.class);
    private static final String PROPERTY_FILE_NAME = "config.properties";
    //Singleton
    private static Properties properties;

    private LoadProperties() {
    }

    public static synchronized void loadProperties() {
        if (properties == null) {
            properties = new Properties();
            loadFromPropertyFile();
            overrideWithEnvironmentVariables();
            overrideWithSystemProperties();
        }
    }

    private static void loadFromPropertyFile() {
        try (InputStream is = LoadProperties.class.getClassLoader().getResourceAsStream(PROPERTY_FILE_NAME)) {
            if (is == null) {
                throw new FileNotFoundException("Property file " + PROPERTY_FILE_NAME + " not found in classpath");
            }
            properties.load(is);
        } catch (IOException ex) {
            LOGGER.warn("Error loading property file: {}", ex.getMessage());
        }
    }

    private static void overrideWithEnvironmentVariables() {
        Map<String, String> env = System.getenv();
        properties.stringPropertyNames().forEach(propName -> {
            String envValue = env.get(propName);
            if (envValue != null) {
                properties.setProperty(propName, envValue);
                LOGGER.info("Overriding property '{}' with environment variable value '{}'", propName, envValue);
            }
        });
    }

    private static void overrideWithSystemProperties() {
        properties.stringPropertyNames().forEach(propName -> {
            String sysValue = System.getProperty(propName);
            if (sysValue != null) {
                properties.setProperty(propName, sysValue);
                LOGGER.info("Overriding property '{}' with system property value '{}'", propName, sysValue);
            }
        });
    }

    // get singleton properties
    public static Properties getProperties() {
        if (properties == null) {
            loadProperties();
        }
        return properties;
    }
}
