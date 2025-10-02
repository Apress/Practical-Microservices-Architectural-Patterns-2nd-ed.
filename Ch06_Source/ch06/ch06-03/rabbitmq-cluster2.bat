@echo off
REM Stop the application on rabbit2
rabbitmqctl -n rabbit2 stop_app

REM Reset rabbit2
rabbitmqctl -n rabbit2 reset

REM Join rabbit2 to rabbit1's cluster
rabbitmqctl -n rabbit2 join_cluster rabbit1@%COMPUTERNAME%

REM Start the application on rabbit2
rabbitmqctl -n rabbit2 start_app

REM Enable HA policy for all queues
rabbitmqctl -n rabbit1 set_policy ha-all "^.*" "{""ha-mode"":""all""}"

REM Display cluster status
rabbitmqctl -n rabbit1 cluster_status
