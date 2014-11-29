/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.iam.oauth.init;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.WebApplicationInitializer;

/**
 * 
 * @author yandypiedra
 */

public class ApplicationInit implements WebApplicationInitializer {

	private static final Logger logger = LogManager
			.getLogger(ApplicationInit.class.getName());

	@Override
	public void onStartup(ServletContext sc) throws ServletException {
		Config conf = new Config();
		try {
			logger.info("Initilizing configuration");
			conf.initialize();
			logger.info("Configuration initialized");
		} catch (ConfigurationException ex) {
			logger.fatal("Error initializing the configuration");
		}
	}
}
