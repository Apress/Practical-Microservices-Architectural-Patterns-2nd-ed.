# Running Multiple RabbitMQ Instances in a Cluster

The example in `ch06-03` demonstrates RabbitMQ clustering with two RabbitMQ instances running on different ports as per the below configuration: 

| Instance | AMQP Port | Management UI Port |
|----------|-----------|-------------------|
| rabbit1  | 5672      | 15672            |
| rabbit2  | 5673      | 15673            |

Follow these steps to set up a two-node cluster:

## Option 1: Windows Setup

### 1. Create Batch Scripts
Create the following scripts in `scripts\rabbitmq`:

#### First Instance (setup-rabbit1.bat)
```batch
@echo off
set RABBITMQ_NODE_PORT=5672
set RABBITMQ_NODENAME=rabbit1
set RABBITMQ_SERVICE_NAME=rabbit1
set RABBITMQ_SERVER_START_ARGS=-rabbitmq_management listener [{port,15672}]

:: Create directories
mkdir "%APPDATA%\RabbitMQ\mnesia-rabbit1"
mkdir "%APPDATA%\RabbitMQ\log\rabbit1"

:: Start RabbitMQ
rabbitmq-server
```

#### Second Instance (setup-rabbit2.bat)
```batch
@echo off
set RABBITMQ_NODE_PORT=5673
set RABBITMQ_NODENAME=rabbit2
set RABBITMQ_SERVICE_NAME=rabbit2
set RABBITMQ_SERVER_START_ARGS=-rabbitmq_management listener [{port,15673}]

:: Create directories
mkdir "%APPDATA%\RabbitMQ\mnesia-rabbit2"
mkdir "%APPDATA%\RabbitMQ\log\rabbit2"

:: Start RabbitMQ
rabbitmq-server
```

#### Cluster Setup (setup-cluster.bat)
```batch
@echo off
:: Stop and reset second node
rabbitmqctl -n rabbit2 stop_app
rabbitmqctl -n rabbit2 reset

:: Join cluster
rabbitmqctl -n rabbit2 join_cluster rabbit1@%COMPUTERNAME%
rabbitmqctl -n rabbit2 start_app

:: Enable HA policy
rabbitmqctl -n rabbit1 set_policy ha-all "^.*" "{""ha-mode"":""all""}"
```

### 2. Running the Cluster

Open three Command Prompt windows as Administrator and execute in order:

#### Command Prompt 1 - Start First Instance:
```batch
cd scripts\rabbitmq
setup-rabbit1.bat
```

#### Command Prompt 2 - Start Second Instance:
```batch
cd scripts\rabbitmq
setup-rabbit2.bat
```

#### Command Prompt 3 - Set Up Clustering:
```batch
cd scripts\rabbitmq
setup-cluster.bat
```

### 3. Verify Setup
```batch
:: Check cluster status
rabbitmqctl -n rabbit1 cluster_status

:: Check individual nodes
rabbitmqctl -n rabbit1 status
rabbitmqctl -n rabbit2 status
```

### Troubleshooting Verification Issues
If you're unable to verify rabbit1 status, try these steps:

#### Windows
```batch
:: Check if RabbitMQ service is running
sc query RabbitMQ

:: Check ERLANG_HOME environment variable
echo %ERLANG_HOME%

:: Check RABBITMQ_BASE environment variable
echo %RABBITMQ_BASE%

:: Verify node name resolution
ping %COMPUTERNAME%

:: Check RabbitMQ logs for errors
type "%APPDATA%\RabbitMQ\log\rabbit1\rabbit@%COMPUTERNAME%.log"

:: Restart RabbitMQ node
rabbitmqctl -n rabbit1 stop_app
rabbitmqctl -n rabbit1 start_app

:: Reset node if needed
rabbitmqctl -n rabbit1 stop_app
rabbitmqctl -n rabbit1 reset
rabbitmqctl -n rabbit1 start_app
```

### 4. Stop Instances
```batch
:: Stop nodes
rabbitmqctl -n rabbit1 stop
rabbitmqctl -n rabbit2 stop
```

### 5. View Logs
```batch
:: View first instance logs
type "%APPDATA%\RabbitMQ\log\rabbit1\rabbit@%COMPUTERNAME%.log"

:: View second instance logs
type "%APPDATA%\RabbitMQ\log\rabbit2\rabbit@%COMPUTERNAME%.log"

:: Monitor logs in real-time (requires PowerShell)
powershell -Command "Get-Content '%APPDATA%\RabbitMQ\log\rabbit1\rabbit@%COMPUTERNAME%.log' -Wait"
powershell -Command "Get-Content '%APPDATA%\RabbitMQ\log\rabbit2\rabbit@%COMPUTERNAME%.log' -Wait"
```

## Option 2: Linux/macOS Setup

### 1. Create Directory Structure
```bash
mkdir -p scripts/rabbitmq
cd scripts/rabbitmq
```

### 2. Create Shell Scripts
Create the following scripts in the `scripts/rabbitmq` directory:

#### First Instance (`setup-rabbit1.sh`)
```bash
#!/bin/bash
export RABBITMQ_NODE_PORT=5672
export RABBITMQ_NODENAME=rabbit1
export RABBITMQ_SERVICE_NAME=rabbit1
export RABBITMQ_SERVER_START_ARGS="-rabbitmq_management listener [{port,15672}]"

# Create required directories
sudo mkdir -p /var/lib/rabbitmq/mnesia-rabbit1
sudo mkdir -p /var/log/rabbitmq/rabbit1
sudo chown -R rabbitmq:rabbitmq /var/lib/rabbitmq/mnesia-rabbit1
sudo chown -R rabbitmq:rabbitmq /var/log/rabbitmq/rabbit1

# Start RabbitMQ
sudo rabbitmq-server -detached
```

#### Second Instance (`setup-rabbit2.sh`)
```bash
#!/bin/bash
export RABBITMQ_NODE_PORT=5673
export RABBITMQ_NODENAME=rabbit2
export RABBITMQ_SERVICE_NAME=rabbit2
export RABBITMQ_SERVER_START_ARGS="-rabbitmq_management listener [{port,15673}]"

# Create required directories
sudo mkdir -p /var/lib/rabbitmq/mnesia-rabbit2
sudo mkdir -p /var/log/rabbitmq/rabbit2
sudo chown -R rabbitmq:rabbitmq /var/lib/rabbitmq/mnesia-rabbit2
sudo chown -R rabbitmq:rabbitmq /var/log/rabbitmq/rabbit2

# Start RabbitMQ
sudo rabbitmq-server -detached
```

#### Cluster Setup (`setup-cluster.sh`)
```bash
#!/bin/bash
# Stop app on rabbit2
sudo rabbitmqctl -n rabbit2 stop_app

# Reset rabbit2
sudo rabbitmqctl -n rabbit2 reset

# Join cluster with rabbit1
sudo rabbitmqctl -n rabbit2 join_cluster rabbit1@$(hostname)

# Start app on rabbit2
sudo rabbitmqctl -n rabbit2 start_app

# Verify cluster status
sudo rabbitmqctl -n rabbit1 cluster_status

# Enable HA policy
sudo rabbitmqctl -n rabbit1 set_policy ha-all "^.*" '{"ha-mode":"all"}'
```

### 3. Make Scripts Executable
```bash
chmod +x setup-rabbit*.sh
```

### 4. Running the Cluster

Open three terminal windows and execute in order:

#### Terminal 1 - Start First Instance:
```bash
cd /workspaces/practical-microservices-architectural-patterns/scripts/rabbitmq
chmod +x setup-rabbit1.sh
./setup-rabbit1.sh

# Verify it's running
sudo rabbitmqctl -n rabbit1 status
```

#### Terminal 2 - Start Second Instance:
```bash
cd /workspaces/practical-microservices-architectural-patterns/scripts/rabbitmq
chmod +x setup-rabbit2.sh
./setup-rabbit2.sh

# Verify it's running
sudo rabbitmqctl -n rabbit2 status
```

#### Terminal 3 - Set Up Clustering:
```bash
cd /workspaces/practical-microservices-architectural-patterns/scripts/rabbitmq
chmod +x setup-cluster.sh
./setup-cluster.sh

# Verify cluster status
sudo rabbitmqctl cluster_status
```

### 5. Verify Setup

#### Check Cluster Status
```bash
sudo rabbitmqctl -n rabbit1 cluster_status
```

#### Access Management UIs
- First Node: http://localhost:15672 (guest/guest)
- Second Node: http://localhost:15673 (guest/guest)

#### Test with Sample Application
```bash
# Test first node
mvn test -Plisten -Drabbitmq.port=5672
mvn test -Psend -Drabbitmq.port=5672

# Test second node
mvn test -Plisten -Drabbitmq.port=5673
mvn test -Psend -Drabbitmq.port=5673
```

### Troubleshooting Verification Issues
If you're unable to verify rabbit1 status, try these steps:

#### Linux/macOS
```bash
# Check if RabbitMQ process is running
ps aux | grep rabbit

# Check system logs
sudo journalctl -u rabbitmq-server

# Check RabbitMQ environment
sudo rabbitmqctl environment

# Verify permissions
ls -l /var/lib/rabbitmq/mnesia-rabbit1
ls -l /var/log/rabbitmq/rabbit1

# Check node name resolution
hostname
ping $(hostname)

# Restart RabbitMQ node
sudo rabbitmqctl -n rabbit1 stop_app
sudo rabbitmqctl -n rabbit1 start_app

# Reset node if needed
sudo rabbitmqctl -n rabbit1 stop_app
sudo rabbitmqctl -n rabbit1 reset
sudo rabbitmqctl -n rabbit1 start_app

# Check status with debug info
sudo rabbitmqctl -n rabbit1 status --formatter=pretty
```

### 6. Troubleshooting

#### View Logs
```bash
# First instance logs
sudo tail -f /var/log/rabbitmq/rabbit1/rabbit@hostname.log

# Second instance logs
sudo tail -f /var/log/rabbitmq/rabbit2/rabbit@hostname.log
```

#### Check Node Status
```bash
# Check individual nodes
sudo rabbitmqctl -n rabbit1 status
sudo rabbitmqctl -n rabbit2 status

# Check cluster status
sudo rabbitmqctl -n rabbit1 cluster_status
```

#### Reset Nodes if Needed
```bash
sudo rabbitmqctl -n rabbit2 stop_app
sudo rabbitmqctl -n rabbit2 reset
sudo rabbitmqctl -n rabbit2 start_app
```

#### 7. Stop Instances
```bash
# Stop individual instances
sudo rabbitmqctl stop -n rabbit1
sudo rabbitmqctl stop -n rabbit2
```

### 8. Configuration in Dev Container
The dev container is pre-configured to support multiple instances:

```jsonc
{
    "forwardPorts": [
        5672,   // First instance AMQP
        15672,  // First instance UI
        5673,   // Second instance AMQP
        15673   // Second instance UI
    ]
}
```

## Expected Output
```
Cluster status of node rabbit1@hostname
[{nodes,[{disc,[rabbit1@hostname,rabbit2@hostname]}]},
 {running_nodes,[rabbit1@hostname,rabbit2@hostname]},
 {cluster_name,<<"rabbit@hostname">>},
 {partitions,[]}]
```

## Access Management UI
- First Node: http://localhost:15672
- Second Node: http://localhost:15673
- Credentials: guest/guest

## High Availability Policy
The setup automatically configures HA policy for all queues:
- Policy Name: ha-all
- Pattern: ^.* (all queues)
- Definition: {"ha-mode":"all"} (mirror to all nodes)

## Testing Cluster
```bash
# Test with first node
mvn test -Plisten -Drabbitmq.port=5672

# Test with second node
mvn test -Psend -Drabbitmq.port=5673
```

## Setting up Nginx Load Balancer for RabbitMQ Cluster

### 1. Nginx Configuration File
Create or update `/etc/nginx/nginx.conf`:

```nginx
# filepath: /etc/nginx/nginx.conf
user nginx;
worker_processes 1;
events {
    worker_connections 1024;
}

# TCP Load Balancing for AMQP
stream {
    upstream rabbitmq_cluster {
        server localhost:5672;  # First RabbitMQ instance
        server localhost:5673;  # Second RabbitMQ instance
    }    

    server {
        listen 5671;  # Load balancer port
        proxy_pass rabbitmq_cluster;
    }
}

# HTTP Load Balancing for Management UI
http {
    include mime.types;
    default_type application/octet-stream;
    sendfile on;
    keepalive_timeout 15;

    upstream management_ui {
        server localhost:15672;  # First management UI
        server localhost:15673;  # Second management UI
    }

    server {
        listen 15671;  # Management load balancer port
        
        location / {
            proxy_pass http://management_ui;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
        }
    }
}
```

### 2. Install and Configure Nginx

```bash
# Install Nginx
sudo apt update
sudo apt install nginx -y

# Copy configuration
sudo cp nginx.conf /etc/nginx/nginx.conf

# Test configuration
sudo nginx -t

# Start/Restart Nginx
sudo systemctl restart nginx

# Enable Nginx on boot
sudo systemctl enable nginx
```

### 3. Verify Setup

```bash
# Check Nginx status
sudo systemctl status nginx

# Verify ports are listening
sudo netstat -tulpn | grep -E '5671|15671'

# Test cluster connection
curl -i http://localhost:15671/api/overview -u guest:guest
```

### 4. Update Application Configuration

Update connection settings in your application:

```xml
<!-- filepath: src/main/resources/rabbit-sender-context.xml -->
<rabbit:connection-factory 
    id="connectionFactory"
    host="localhost"
    port="5671"
    username="guest"
    password="guest"/>
```

### 5. Test Load Balancing

```bash
# Start multiple producers/consumers
mvn test -Plisten -Drabbitmq.port=5671
mvn test -Psend -Drabbitmq.port=5671
```

### 6. Monitor Load Balancing

```bash
# View Nginx access logs
sudo tail -f /var/log/nginx/access.log

# View Nginx error logs
sudo tail -f /var/log/nginx/error.log

# Monitor RabbitMQ connections
sudo rabbitmqctl -n rabbit1 list_connections
sudo rabbitmqctl -n rabbit2 list_connections
```

### 7. Common Issues and Solutions

#### Connection Refused
```bash
# Check if Nginx is running
sudo systemctl status nginx

# Verify RabbitMQ instances are up
sudo rabbitmqctl -n rabbit1 status
sudo rabbitmqctl -n rabbit2 status
```

#### Permission Issues
```bash
# Fix SELinux issues (if applicable)
sudo setsebool -P httpd_can_network_connect 1

# Fix log permissions
sudo chmod 755 /var/log/nginx
sudo chown -R nginx:nginx /var/log/nginx
```

#### Port Conflicts
```bash
# Check for port conflicts
sudo lsof -i :5671
sudo lsof -i :15671
```

### 8. Performance Tuning

Update `/etc/nginx/nginx.conf` with optimized settings:

```nginx
worker_processes auto;
worker_rlimit_nofile 65535;

events {
    worker_connections 65535;
    use epoll;
    multi_accept on;
}

stream {
    upstream rabbitmq_cluster {
        least_conn;  # Load balancing algorithm
        server localhost:5672 max_fails=3 fail_timeout=30s;
        server localhost:5673 max_fails=3 fail_timeout=30s;
    }
}
```

## Checking RabbitMQ Server Status

### Basic Status Check
```bash
# Check overall status
sudo rabbitmqctl status

# Check status for specific nodes
sudo rabbitmqctl -n rabbit1 status
sudo rabbitmqctl -n rabbit2 status
```

### Detailed Status Information
```bash
# Check cluster status
sudo rabbitmqctl cluster_status

# List queues
sudo rabbitmqctl list_queues

# List exchanges
sudo rabbitmqctl list_exchanges

# List bindings
sudo rabbitmqctl list_bindings

# Check node health
sudo rabbitmqctl node_health_check

# List connections
sudo rabbitmqctl list_connections

# List channels
sudo rabbitmqctl list_channels

# List consumers
sudo rabbitmqctl list_consumers
```

### Monitoring Memory and Disk Space
```bash
# Check memory usage
sudo rabbitmqctl status | grep memory

# Check disk space
sudo rabbitmqctl status | grep disk_free

# List memory usage by queue
sudo rabbitmqctl list_queues name memory
```

## Running in Codespace

### Prerequisites Check
```bash
# Check RabbitMQ installation
which rabbitmq-server
rabbitmq-server --version

# Check if Docker is available (alternative method)
docker --version
```

### Option A: Native Installation
```bash
# Install RabbitMQ if needed
sudo apt-get update
sudo apt-get install -y rabbitmq-server

# Enable management plugin
sudo rabbitmq-plugins enable rabbitmq_management

# Configure permissions
sudo chown -R rabbitmq:rabbitmq /var/lib/rabbitmq/
sudo chmod -R 777 /var/lib/rabbitmq/

# Start RabbitMQ service
sudo service rabbitmq-server start
sudo rabbitmqctl status
```

### Option B: Docker Setup (Recommended for Codespaces)
```bash
# Create Docker network
docker network create rabbitmq-cluster

# Start first node
docker run -d --name rabbit1 --network rabbitmq-cluster \
    -p 5672:5672 -p 15672:15672 \
    -e RABBITMQ_ERLANG_COOKIE='secret-cookie' \
    rabbitmq:management

# Start second node
docker run -d --name rabbit2 --network rabbitmq-cluster \
    -p 5673:5672 -p 15673:15672 \
    -e RABBITMQ_ERLANG_COOKIE='secret-cookie' \
    rabbitmq:management

# Setup cluster
docker exec rabbit2 rabbitmqctl stop_app
docker exec rabbit2 rabbitmqctl reset
docker exec rabbit2 rabbitmqctl join_cluster rabbit@rabbit1
docker exec rabbit2 rabbitmqctl start_app

# Verify cluster status
docker exec rabbit1 rabbitmqctl cluster_status
```

### Troubleshooting Codespace Issues
```bash
# Check system resources
free -h
df -h

# Check RabbitMQ logs
sudo tail -f /var/log/rabbitmq/rabbit@*.log

# Check Docker logs
docker logs rabbit1
docker logs rabbit2

# Reset environment
sudo service rabbitmq-server stop
sudo rm -rf /var/lib/rabbitmq/mnesia/
sudo service rabbitmq-server start
```
