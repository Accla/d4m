#!/bin/sh


#HADOOP_HDFS=/tmp/hadoop-wi20909
#CLOUDBASE_HOME=/home/wi20909/cloudbase/cloudbase-1.0
#ZOOKEEPER_HOME=/home/wi20909/zookeeper
#HADOOP_HOME=/home/wi20909/hadoopdev/hadoop-0.19.0
#HADOOP_HOSTNAME=wi20909-desktop
#HADOOP_USER=wi20909


HADOOP_HDFS=/state/partition1/hadoop-hadoop
CLOUDBASE_HOME=/home/gridsan/hadoop/cloudbase
ZOOKEEPER_HOME=/home/gridsan/hadoop/zookeeper
HADOOP_HOME=/home/gridsan/hadoop/hadoop
HADOOP_HOSTNAME=f-2-1
HADOOP_USER=hadoop
JAVA_HOME=/home/gridsan/hadoop/java/jdk1.6.0_11


export CLOUDBASE_HOME
export ZOOKEEPER_HOME
export JAVA_HOME
export HADOOP_HOME
export HADOOP_HOSTNAME
export HADOOP_USER



echo "----------------------------- Env Set "
