package ch.bfh.iam.oauth.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @author yandypiedra
 *
 */

@Controller
public class HomeController {
	
	 private static final Logger logger = LogManager.getLogger(HomeController.class.getName());

	@RequestMapping(value="/", method = RequestMethod.GET)
	public String home() {
		logger.info("Loading index page");
		return "index";
	}
}