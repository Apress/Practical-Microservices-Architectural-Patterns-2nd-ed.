package com.acme.ch06.ex02;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenerContainer {

	private static final Logger LOGGER = LoggerFactory.getLogger(ListenerContainer.class);
	private static final String LISTENER_CONTEXT = "rabbit-listener-context.xml";

	public static void main(String[] args) {

		LOGGER.debug("Start");
		ApplicationContext context = new ClassPathXmlApplicationContext(LISTENER_CONTEXT);
		LOGGER.debug("Context successfully created from: {}", LISTENER_CONTEXT);
		LOGGER.debug("End");
		LOGGER.info("End");
	}
}
