# run new/old version container
#if activity ... if activity2 ...

#function runcontainer {
#    docker ps -a > /tmp/yy_xx$$
#    if ... web1 exists
#    
#    then web2
#    and connect to network
#    
#    else if web2 exists
#    
#    then run web1
#    and connect to network
#}

docker ps -a > /tmp/yy_xx$$

if grep --quiet web1 /tmp/yy_xx$$
  then
  echo "Running container web2"
  docker run -d -P --name web2 $1
  echo "Connecting web2 to network"
  docker network connect ecs189_default web2
  echo "Executing swap2 in the proxy container"
  docker exec ecs189_proxy_1 /bin/bash /bin/swap2.sh
  echo "Killing web1"
  docker rm -f `docker ps -a | grep web1 | sed -e 's: .*$::'`
elif grep --quiet web2 /tmp/yy_xx$$
  then
  echo "Running container web1"
  docker run -d -P --name web1 $1
  echo "Connecting web1 to network"
  docker network connect ecs189_default web1
  echo "Executing swap1 in the proxy container"
  docker exec ecs189_proxy_1 /bin/bash /bin/swap1.sh
  echo "Killing web2"
  docker rm -f `docker ps -a | grep web2 | sed -e 's: .*$::'`
fi

echo "Swap completed"
#runcontainer $1
#docker run -d -P --name web1 $1
#docker run -d -P --name web1 activity
#docker run -d -P --name web2 activity2

#docker network connect ecs189_default web1
#docker network connect ecs189_default web2

#docker exec ecs189_proxy_1 /bin/bash /bin/swap1.sh
#docker exec ecs189_proxy_1 /bin/bash /bin/swap2.sh

#killitif web1
#killitif web2

#echo "Swap completed"