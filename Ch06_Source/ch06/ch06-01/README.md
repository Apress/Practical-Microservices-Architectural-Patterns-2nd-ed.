# AMQP Hello Project

A simple Spring RabbitMQ AMQP example demonstrating asynchronous message exchange using a producer-consumer pattern.

## Quick Start
```bash
# 1. Start RabbitMQ
sudo systemctl start rabbitmq-server

# 2. Start the receiver (Terminal 1)
mvn test -Plisten

# 3. Send a message (Terminal 2)
mvn test -Psend
```

## Prerequisites
- Java 17
- Maven

## Architecture
```mermaid
graph LR
    S[Send.java] -->|publishes to| Q[Queue 'hello']
    Q -->|consumes from| R[Receive.java]
    R -->|logs using| L[SLF4J/Log4j2]
```

## Important Concepts

### 1. AMQP Messaging
- Uses RabbitMQ as an AMQP-compliant message broker
- Demonstrates asynchronous messaging between sender and receiver
- Uses default exchange with direct routing to queues
- Messages are UTF-8 encoded strings

### 2. Message Flow
- Sender declares queue and publishes messages
- Receiver subscribes to queue using `DefaultConsumer`
- Asynchronous message processing through callbacks
- Proper resource cleanup and error handling

## Setup and Configuration

### 1. Dev Container Setup
```jsonc
{
    "features": {
        "ghcr.io/itsmechlark/features/rabbitmq-server:1": {
            "version": "latest",
            "plugins": "rabbitmq_management"
        }
    },
    "forwardPorts": [
        5672,   // AMQP protocol
        15672   // Management UI
    ]
}
```

### 2. RabbitMQ Configuration
- Default port: 5672 (AMQP)
- Management interface: http://localhost:15672
- Default credentials: guest/guest

Verify setup:
```bash
# Check status
sudo systemctl status rabbitmq-server

# Verify plugins
sudo rabbitmqctl list_enabled_plugins
```

## Building and Running

### 1. Build the Project
You have the following options for building the project:

```bash
# Full build with tests
mvn clean install

# Build without running tests
mvn clean install -DskipTests=true

# Build without compiling tests
mvn clean install -Dmaven.test.skip=true
```

Choose the appropriate build command based on your needs:
- Use `-DskipTests=true` to compile but not run tests
- Use `-Dmaven.test.skip=true` to skip both test compilation and execution
- Default build (no flags) will compile and run all tests

### 2. Start the Message Receiver
```bash
mvn test -Plisten
```
Expected output:
```
[INFO] Start
[DEBUG] [!] Waiting for messages. To exit press CTRL+C
```

### 3. Send Test Messages
```bash
mvn test -Psend
```
Expected output:
```
[INFO] Start
[DEBUG] [!] Sent 'Hello World!'
[INFO] End
```

## Implementation Details

### Dependencies
#### Messaging
- RabbitMQ Client (5.25.0): `com.rabbitmq:amqp-client`

#### Logging
- SLF4J (2.0.17): `org.slf4j:slf4j-api`
- Log4j2 (2.24.3):
  - Core: `log4j-api`, `log4j-core`
  - SLF4J Bridge: `log4j-slf4j2-impl`

### Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/acme/ch06/ex01/
│   │       ├── Send.java     # Message producer
│   │       └── Receive.java  # Message consumer
│   └── resources/
│       └── log4j2.xml       # Logging configuration
```

## Logging Configuration

### Overview
The project uses SLF4J with Log4j2 implementation for logging. Configuration is defined in `src/main/resources/log4j2.xml`.

### Log Levels
- Application code (`com.acme.ch06.ex01`): `DEBUG`
- Root logger (all other packages): `ERROR`
- Log4j2 internal logging: `WARN`

### Appenders
1. **Console Appender**
   ```xml
   <Console name="Console" target="SYSTEM_OUT">
       <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %-5p %C.%M:%L - %m%n"/>
   </Console>
   ```
   - Outputs to standard out
   - Used for development and debugging

2. **Rolling File Appender**
   ```xml
   <RollingFile name="RollingFile" 
                fileName="logs/application.log"
                filePattern="logs/application-%d{yyyy-MM-dd}-%i.log">
   ```
   - Persistent logging to files
   - Rotation policies:
     - Size-based: 10 MB per file
     - Time-based: Daily rollover
   - Keeps maximum 5 backup files

### Log Pattern Format
```
%d{yyyy-MM-dd HH:mm:ss} %-5p %C.%M:%L - %m%n
```
- `%d`: Timestamp (yyyy-MM-dd HH:mm:ss)
- `%-5p`: Log level (padded)
- `%C.%M:%L`: Class name, method, line number
- `%m`: Message
- `%n`: New line

### Dependencies
```xml
<!-- SLF4J API -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>${slf4jVersion}</version>
</dependency>

<!-- Log4j2 Implementation -->
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>${log4j2Version}</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>${log4j2Version}</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j2-impl</artifactId>
    <version>${log4j2Version}</version>
</dependency>
```

### Log File Location
- Active log file: `logs/application.log`
- Archived logs: `logs/application-{date}-{sequence}.log`

### Usage Example
```java
private static final Logger LOGGER = LoggerFactory.getLogger(YourClass.class);

LOGGER.debug("Debug message");
LOGGER.info("Info message");
LOGGER.warn("Warning message");
LOGGER.error("Error message");
```

## Dependency Management

### Dependabot Configuration
The project uses GitHub's Dependabot for automated dependency updates. Configuration is in `.github/dependabot.yml`:

```yaml
version: 2
updates:
  # Dev Container Updates
  - package-ecosystem: "devcontainers"
    directory: "/"
    schedule:
      interval: weekly

  # Maven Dependencies
  - package-ecosystem: "maven"
    directory: "/Ch06_Source/ch06/ch06-01"
    schedule:
      interval: weekly
    ignore:
      # Ignore patch updates for stable dependencies
      - dependency-name: "org.slf4j:slf4j-api"
        update-types: ["version-update:semver-patch"]
      - dependency-name: "org.apache.logging.log4j:*"
        update-types: ["version-update:semver-patch"]

  # GitHub Actions
  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: weekly
```

### Key Features
1. **Dev Container Updates**
   - Weekly checks for dev container feature updates
   - Includes RabbitMQ and Java tooling updates

2. **Maven Dependencies**
   - Monitors `pom.xml` for dependency updates
   - Ignores patch updates for logging libraries
   - Weekly automated checks

3. **GitHub Actions**
   - Maintains workflow dependencies
   - Weekly update schedule

### Benefits
- Automated security updates
- Regular dependency maintenance
- Reduced technical debt
- Consistent development environment

### Configuration
1. File Location: `.github/dependabot.yml`
2. Enable in Repository Settings:
   - Settings → Code Security & Analysis
   - Enable Dependabot version updates
3. Monitor Pull Requests for updates

## Development Guidelines
- Use standard Java conventions for code formatting
- Implement logging using SLF4J facade
- Handle exceptions properly with appropriate logging
- Follow proper resource cleanup practices

## Troubleshooting

### Common Issues

#### 1. No SLF4J Provider Found
Add to `pom.xml`:
```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j2-impl</artifactId>
    <version>${log4j2Version}</version>
</dependency>
```

#### 2. RabbitMQ Connection Issues
```bash
# Check RabbitMQ status
sudo systemctl status rabbitmq-server

# View logs
sudo tail -f /var/log/rabbitmq/rabbit@localhost.log

# List queues
sudo rabbitmqctl list_queues

# Check connections
sudo rabbitmqctl list_connections
```

#### 3. Port Conflicts
Verify ports 5672 and 15672 are available:
```bash
sudo netstat -tulpn | grep -E '5672|15672'
```

## Code Details

### Send.java - Message Producer
```java
public class Send {
    private final static String QUEUE_NAME = "hello";
    
    public static void main(String[] argv) throws Exception {
        // 1. Setup Connection
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 2. Declare Queue
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        
        // 3. Send Message
        String message = "Hello World!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));

        // 4. Cleanup
        channel.close();
        connection.close();
    }
}
```

Key features:
- Creates connection to RabbitMQ on localhost:5672
- Declares a non-durable, non-exclusive queue
- Publishes message using default exchange
- Properly closes resources after sending

### Receive.java - Message Consumer
```java
public class Receive {
    private final static String QUEUE_NAME = "hello";
    
    public static void main(String[] argv) throws Exception {
        // 1. Setup Connection
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 2. Declare Queue
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 3. Create Consumer
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                     AMQP.BasicProperties properties, byte[] body)
                throws IOException {
                String message = new String(body, "UTF-8");
                LOGGER.debug(" [x] Received '" + message + "'");
            }
        };

        // 4. Start Consuming
        channel.basicConsume(QUEUE_NAME, true, consumer);

        // 5. Keep Running
        while (true) {
            Thread.sleep(100);
        }
    }
}
```

Key features:
- Asynchronous message consumption using `DefaultConsumer`
- Auto-acknowledgment of messages
- Continuous operation with sleep to prevent CPU overuse
- UTF-8 message decoding

### Queue Parameters
The queue is declared with these parameters:
```java
channel.queueDeclare(QUEUE_NAME, false, false, false, null);
```
- `QUEUE_NAME`: "hello"
- `durable`: false (queue not persisted)
- `exclusive`: false (multiple connections allowed)
- `autoDelete`: false (queue remains after consumer disconnects)
- `arguments`: null (no special arguments)

### Message Publishing
```java
channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
```
- Empty exchange: Uses default exchange
- Routing key: Same as queue name
- No message properties
- Message body: UTF-8 encoded string

### Message Consumption
```java
channel.basicConsume(QUEUE_NAME, true, consumer);
```
- Queue name: "hello"
- Auto-ack: true (automatic message acknowledgment)
- Consumer: Implementation of `DefaultConsumer`

### Resource Management
- Both sender and receiver create these resources:
  - `ConnectionFactory`
  - `Connection`
  - `Channel`
- Sender explicitly closes resources
- Receiver keeps connection open for continuous operation

### Error Handling
- All operations properly wrapped in try-catch blocks
- Errors logged using SLF4J
- Resources cleaned up in finally blocks
- Graceful shutdown support

### Logging Integration
- Uses SLF4J facade with Log4j2 implementation
- Configured via log4j2.xml
- Different log levels for application and framework
- Console and file appenders available

## License
This project is licensed under the MIT License.

---

For more detailed information about the code implementation, refer to the individual source files and their comments.

