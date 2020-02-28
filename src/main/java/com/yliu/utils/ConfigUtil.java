package com.trinity.utils;

import java.io.InputStream;
import java.util.Properties;


/**
 * 配置文件读取类
 *
 * @author buhuaqi
 * @qq 287510038
 *
 */
public class ConfigUtil {

    public static Properties getProps(String confFileName) {
        Properties result = new Properties();
        try (InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream(confFileName);) {
            result.load(in);
            return result;
        } catch (final Exception e) {
            System.err.println("Config file "+confFileName+" does not exist!");
            throw new RuntimeException();
        }
    }

}
