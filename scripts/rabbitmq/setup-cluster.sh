#!/bin/bash

# Stop app on rabbit2
sudo rabbitmqctl -n rabbit2 stop_app

# Reset rabbit2
sudo rabbitmqctl -n rabbit2 reset

# Join cluster with rabbit1
sudo rabbitmqctl -n rabbit2 join_cluster rabbit1@$(hostname)

# Start app on rabbit2
sudo rabbitmqctl -n rabbit2 start_app

# Enable HA policy
sudo rabbitmqctl -n rabbit1 set_policy ha-all "^.*" '{"ha-mode":"all"}'

# Verify cluster status
sudo rabbitmqctl -n rabbit1 cluster_status
