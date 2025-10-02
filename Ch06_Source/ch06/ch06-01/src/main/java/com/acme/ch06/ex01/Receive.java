package com.acme.ch06.ex01;

// Import RabbitMQ client libraries for AMQP communication
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP;

// Import logging libraries
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * RabbitMQ message consumer that listens for messages on a specified queue.
 * Uses the AMQP protocol through the RabbitMQ client library.
 * 
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 * @author <a href="mailto:tarun.telang@gmail.com">Tarun Telang</a>
 */
public class Receive {

    // Initialize SLF4J logger for this class
    private static final Logger LOGGER = LoggerFactory.getLogger(Receive.class);
    
    // Define the queue name that will be used for receiving messages
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {

        LOGGER.info("Start");
        
        // Create a connection factory to establish connection to RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        // Set the host where RabbitMQ server is running (default port 5672 is used)
        factory.setHost("localhost");
        // Create a connection to the RabbitMQ server (uses default credentials: guest/guest)
        Connection connection = factory.newConnection();
        // Create a channel for communicating with RabbitMQ
        Channel channel = connection.createChannel();

        // Declare a queue (creates if doesn't exist, idempotent operation)
        // Parameters: queue name, durable, exclusive, auto-delete, arguments
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        LOGGER.debug(" [!] Waiting for messages. To exit press CTRL+C");

        // Create a consumer that will handle incoming messages
        Consumer consumer = new DefaultConsumer(channel) {
            
            // Override the handleDelivery method to process incoming messages
            @Override
            public void handleDelivery(String consumerTag,    // Identifies the consumer
                                     Envelope envelope,        // Contains message metadata
                                     AMQP.BasicProperties properties,  // Message properties
                                     byte[] body)             // Message content
                throws IOException {
                // Convert message body from bytes to String using UTF-8 encoding
                String message = new String(body, "UTF-8");
                // Log the received message
                LOGGER.debug(" [x] Received '" + message + "'");
            }
        };

        // Start consuming messages from the queue
        // Parameters: queue name, auto-acknowledge messages, consumer callback
        channel.basicConsume(QUEUE_NAME, true, consumer);
        
        // Keep the consumer running indefinitely
        // This prevents the program from exiting and allows continuous message reception
        while (true) {
            Thread.sleep(100);  // Sleep to prevent CPU intensive loop
        }
    }
}