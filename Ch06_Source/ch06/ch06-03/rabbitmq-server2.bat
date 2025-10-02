@echo off
set RABBITMQ_NODE_PORT=5673
set RABBITMQ_NODENAME=rabbit2
set RABBITMQ_MNESIA_BASE=%APPDATA%\RabbitMQ\mnesia-rabbit2
set RABBITMQ_LOG_BASE=%APPDATA%\RabbitMQ\log
set RABBITMQ_CONFIG_FILE=%APPDATA%\RabbitMQ\rabbit2.conf
set RABBITMQ_SERVER_START_ARGS=-rabbitmq_management listener [{port,15673}]

rabbitmq-server
