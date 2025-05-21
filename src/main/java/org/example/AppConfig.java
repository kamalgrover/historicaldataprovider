package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final String PROPS_FILE = "config.properties";
    private final Properties props = new Properties();

    public AppConfig() {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream(PROPS_FILE)) {
            if (in == null) {
                throw new IllegalStateException(PROPS_FILE + " not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + PROPS_FILE, e);
        }
    }

    public String getApiKey() {
        return props.getProperty("kite.api.key");
    }

    public String getApiSecret() {
        return props.getProperty("kite.api.secret");
    }

    public int getCallbackPort() {
        return Integer.parseInt(props.getProperty("callback.port"));
    }

    public String getCallbackPath() {
        return props.getProperty("callback.path");
    }

    public String getCacheFilePath() {
        return props.getProperty("cache.file");
    }

    public String getUserId() {
        return props.getProperty("kite.user.id");
    }
}
