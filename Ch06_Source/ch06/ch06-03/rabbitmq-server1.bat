@echo off
set RABBITMQ_NODE_PORT=5672
set RABBITMQ_NODENAME=rabbit1
set RABBITMQ_MNESIA_BASE=%APPDATA%\RabbitMQ\mnesia-rabbit1
set RABBITMQ_LOG_BASE=%APPDATA%\RabbitMQ\log
set RABBITMQ_CONFIG_FILE=%APPDATA%\RabbitMQ\rabbit1.conf
set RABBITMQ_SERVER_START_ARGS=-rabbitmq_management listener [{port,15672}]

rabbitmq-server
