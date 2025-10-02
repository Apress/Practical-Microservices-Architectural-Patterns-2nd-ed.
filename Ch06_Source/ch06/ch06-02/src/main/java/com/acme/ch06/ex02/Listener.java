
package com.acme.ch06.ex02;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Listener implements MessageListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

	/**
	 * Handles the incoming AMQP message.
	 *
	 * @param message the AMQP message received
	 */
	public void onMessage(Message message) {

		LOGGER.info("Start");
		String messageBody = new String(message.getBody(), java.nio.charset.StandardCharsets.UTF_8);
		LOGGER.debug("Listener received message----->{}", messageBody);
		LOGGER.info("End");
	}
}
