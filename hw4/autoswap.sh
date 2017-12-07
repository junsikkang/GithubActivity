docker ps -a > /tmp/yy_xx$$

# if there's a container web1 then run web2 and kill web1
# else run web1 and kill web2
if grep --quiet web1 /tmp/yy_xx$$
  then
  echo "Running container web2"
  docker run -d -P --name web2 $1 && sleep 3
  echo "Connecting web2 to network"
  docker network connect ecs189_default web2 && sleep 3
  echo "Executing swap2 in the proxy container"
  docker exec ecs189_proxy_1 /bin/bash /bin/swap2.sh && sleep 3
  echo "Killing web1"
  docker rm -f `docker ps -a | grep web1 | sed -e 's: .*$::'`
elif grep --quiet web2 /tmp/yy_xx$$
  then
  echo "Running container web1"
  docker run -d -P --name web1 $1 && sleep 3
  echo "Connecting web1 to network"
  docker network connect ecs189_default web1 && sleep 3
  echo "Executing swap1 in the proxy container"
  docker exec ecs189_proxy_1 /bin/bash /bin/swap1.sh && sleep 3
  echo "Killing web2"
  docker rm -f `docker ps -a | grep web2 | sed -e 's: .*$::'`
fi

echo "Swap completed"