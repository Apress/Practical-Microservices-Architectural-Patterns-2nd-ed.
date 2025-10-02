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
