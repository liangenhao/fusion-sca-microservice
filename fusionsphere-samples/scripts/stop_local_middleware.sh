#!/bin/bash

# nacos
cd ~/Documents/develop/nacos
bin/shutdown.sh
rm -f ~/Documents/develop/nacos/logs/*

# sentinel
sentinel_pid=`ps ax | grep -i 'sentinel-dashboard.jar' | grep -v grep | awk '{print $1}'`
if [ -z "$sentinel_pid" ] ; then
        echo "No sentinel-dashboard running."
        exit -1;
fi
kill ${sentinel_pid}
echo "Send shutdown request to sentinel-dashboard(${sentinel_pid}) OK"

# seata
seata_pid=`ps ax | grep -i 'seata-server.jar' | grep -v grep | awk '{print $1}'`
if [ -z "$seata_pid" ] ; then
        echo "No seata-server running."
        exit -1;
fi
kill ${seata_pid}
echo "Send shutdown request to seata-server(${seata_pid}) OK"