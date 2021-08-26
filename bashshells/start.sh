#!/bin/bash

app='service-sandbox.jar'

pid=`ps -ef | grep $app | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ];then
    echo "kill process $pid"
    kill -9 $pid
fi
sleep 5s
nohup java \
-Xms512m -Xmx1024m -Duser.timezone=GMT+08 -Dfile.encoding=utf-8 \
-jar $app \
--spring.profiles.active=dev \
>> app.log 2>&1 &
tail -1000f app.log
