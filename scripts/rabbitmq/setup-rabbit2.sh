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
