/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.iam.oauth.init;

import java.net.URL;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author yandypiedra
 */
public class Config {

    private static Configuration config;
    private static boolean isInitilized;

    protected void initialize() throws ConfigurationException {
        if (!isInitilized) {
            URL url = Config.class.getResource("/properties/authorization-server.properties");
            config = new PropertiesConfiguration(url.getFile());
            isInitilized = true;
        }
    }

    public static synchronized Configuration getConfig() {
        return config;
    }
}
