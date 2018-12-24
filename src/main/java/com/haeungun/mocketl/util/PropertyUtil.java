package com.haeungun.mocketl.util;

import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    private final String propertyName;

    private Properties props;

    public PropertyUtil(String propertyName) {
        this.propertyName = propertyName + ".properties";
        this.readProperties();
    }

    private void readProperties() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        this.props = new Properties();
        try (InputStream input = loader.getResourceAsStream(this.propertyName)) {
            this.props.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getStringValue(String key) {
        return this.props.getProperty(key);
    }

    public int getIntValue(String key) {
        try {
            return Integer.parseInt(this.props.getProperty(key));
        } catch (Exception e) {
            return 0;
        }
    }

}
