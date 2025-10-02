package com.acme.ch06.ex01;

// Import RabbitMQ client libraries for AMQP communication
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

// Import logging libraries
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 * @author <a href="mailto:tarun.telang@gmail.com">Tarun Telang</a>
 */
public class Send {

    // Initialize SLF4J logger for this class
    private static final Logger LOGGER = LoggerFactory.getLogger(Send.class);
    
    // Define the queue name that will be used for sending messages
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
        
        // Message to be sent
        String message = "Hello World!";
        // Publish message to the queue
        // Parameters: exchange (empty string means default), routing key (queue name), 
        // message properties (null), message body (as bytes)
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        LOGGER.debug(" [!] Sent '" + message + "'");

        // Clean up resources
        channel.close();
        connection.close();
        LOGGER.info("End");
    }
}