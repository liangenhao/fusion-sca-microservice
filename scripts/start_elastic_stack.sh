#!/bin/bash

# elasticsearch
cd ~/Documents/develop/elastic-stack/elasticsearch-8.2.2
./bin/elasticsearch -d -p pid

sleep 20

# kibana
cd ~/Documents/develop/elastic-stack/kibana-8.2.2
nohup ./bin/kibana --allow-root >> kibana.log 2>&1 &
echo $! > pid
echo 'go to http://localhost:5601'
