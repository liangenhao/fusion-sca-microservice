#!/bin/bash

# elasticsearch
cd ~/Documents/develop/elastic-stack/elasticsearch-8.2.2
pkill -F pid

# kibana
cd ~/Documents/develop/elastic-stack/kibana-8.2.2
pkill -F pid
rm pid