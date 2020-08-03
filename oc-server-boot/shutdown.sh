#!/bin/sh
APP_NAME=oc-server-boot-0.0.1-SNAPSHOT.jar
APP_HOME=`pwd`

echo 'APP_HOME: '$APP_HOME

tpid=`ps -ef|grep $APP_NAME|grep -v grep|grep -v kill|awk '{print $2}'`
if [ ${tpid} ]; then
echo 'Kill -15 Process...'
kill -15 $tpid
echo 'Wait 3s to check status...'
sleep 3
fi

tpid=`ps -ef|grep $APP_NAME|grep -v grep|grep -v kill|awk '{print $2}'`
if [ ${tpid} ]; then
echo 'Kill -9 Process!'
kill -9 $tpid
else
echo 'Shutdown Success!'
fi