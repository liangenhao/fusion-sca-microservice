#!/bin/bash

# nacos
cd ~/Documents/develop/nacos
bin/startup.sh -m standalone

# sentinel
sleep 1
cd ~/Documents/projects/personal/Sentinel/sentinel-dashboard/target
nohup java -Dserver.port=8080 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=sentinel-dashboard -Dserver.servlet.session.timeout=1800000 -Ddatasource.provider=NACOS -jar sentinel-dashboard.jar > sentinel.log 2>&1 &
echo 'sentienl-dashboard is starting, you can check the log in sentinel.log'

# seata
# 需等待 nacos 启动完成
sleep 20

get_ip_addr() {
  # 获取所有网络接口的名称
  interfaces=$(ifconfig -l)
  local ip_addresses=""


  # 遍历所有接口，过滤出符合条件的IP地址
  for interface in $interfaces; do
      # 获取该接口的IP地址
      ip_address=$(ifconfig $interface | grep 'inet ' | awk '{print $2}')

      # 过滤掉127.0.0.1的地址
      if [[ ! -z $ip_address && $ip_address != "127.0.0.1" ]]; then
          # 如果是en开头的接口，打印出IP地址
          if [[ $interface == en* ]]; then
              ip_addresses="$ip_address"
          fi
      fi
  done

  echo "$ip_addresses"
}

ipaddr=`get_ip_addr`
echo "local ip addr is $ipaddr"
cd ~/Documents/develop/seata-1.6.1
bin/seata-server.sh -h "$ipaddr"
