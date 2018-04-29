package utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Prop {
    
    public static String getValue(final String key) {
        final Properties prop = new Properties();
        InputStream inStream = null;
        String value = "";
        try {
            inStream = new BufferedInputStream(
                    new FileInputStream("system.properties"));
            prop.load(inStream);
            value = prop.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}