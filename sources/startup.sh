#!/bin/sh

#config oc-server

echo "starting server"

# Xmx 最大堆大小 Xms 初始堆大小 -Xmn 年轻代大小(建议总*3/8) -Xss每个线程堆栈大小
# +DisableExplicitGC 禁用调用System.gc() 除jvm
# +UseConcMarkSweepGC 对老年代采用并发标记交换算法进行GC  
# +CMSParallelRemarkEnabled 降低停顿
# +UseCMSCompactAtFullCollection full gc 对年老代进行压缩
# -XX:LargePageSizeInBytes 内存页的大小不可设置过大，会影响perm的大小 default 128
# +UseFastAccessorMethods 原始类型快速优化
# +UseCMSInitiatingOccupancyOnly 使用手动定义初始化定义开始CMS收集
# CMSInitiatingOccupancyFraction=70 使用CMS作为垃圾回收使用70%后开始CMS收集
JAVA_VM_OPTS="-server -Xmx512m -Xms512m -Xmn256m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:CMSInitiatingOccupancyFraction=70 "

echo $JAVA_VM_OPTS

java $JAVA_VM_OPTS -jar ./oc-server.jar --spring.profiles.active=prd > ./logs/console.log &

echo "started"

