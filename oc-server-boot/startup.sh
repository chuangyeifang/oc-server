#!/bin/sh
APP_NAME=oc-server-boot-0.0.1-SNAPSHOT.jar
APP_HOME=`pwd`
APP_ENV=prd

echo 'APP_HOME: '$APP_HOME
echo 'APP_ENV: '$APP_ENV
# Xmx 最大堆大小 Xms 初始堆大小 -Xmn 年轻代大小(建议总*3/8) -Xss每个线程堆栈大小
# +DisableExplicitGC 禁用调用System.gc() 除jvm
# +UseConcMarkSweepGC 对老年代采用并发标记交换算法进行GC
# +CMSParallelRemarkEnabled 降低停顿
# -XX:LargePageSizeInBytes 内存页的大小不可设置过大，会影响perm的大小 default 128
# +UseFastAccessorMethods 原始类型快速优化
# +UseCMSInitiatingOccupancyOnly 使用手动定义初始化定义开始CMS收集
# CMSInitiatingOccupancyFraction=70 使用CMS作为垃圾回收使用70%后开始CMS收集
JAVA_VM_OPTS="-server -Xmx8g -Xms2g -Xmn256m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:CMSInitiatingOccupancyFraction=70 "

tpid=`ps -ef|grep $APP_NAME|grep -v grep|grep -v kill|awk '{print $2}'`
if [ ${tpid} ]; then
echo 'Stop Process...'
kill -15 $tpid
echo 'Wait 30s to start...'
sleep 30
fi

tpid=`ps -ef|grep $APP_NAME|grep -v grep|grep -v kill|awk '{print $2}'`
if [ ${tpid} ]; then
echo 'Kill Process!'
kill -9 $tpid
echo 'Wait 30s to start...'
sleep 30
else
echo 'Stop Success!'
fi

tpid=`ps -ef|grep $APP_NAME|grep -v grep|grep -v kill|awk '{print $2}'`
if [ ${tpid} ]; then
    echo 'oc-server is running.'
else
    echo 'oc-server is NOT running.'
fi

java "$JAVA_VM_OPTS" -jar "$APP_HOME"/$APP_NAME --spring.profiles.active=$APP_ENV > "$APP_HOME"/logs/console.log &
echo Start Success!