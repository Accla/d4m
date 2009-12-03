#!/bin/sh

HADOOP_HDFS=/tmp/hadoop-wi20909
CLOUDBASE_HOME=/home/wi20909/cloudbase/cloudbase-1.0
ZOOKEEPER_HOME=/home/wi20909/zookeeper
HADOOP_HOME=/home/wi20909/hadoopdev/hadoop-0.19.0
HBASE_HOME=/home/wi20909/hadoopdev/hbase-0.19.0
HJOBS_HOME=/home/wi20909/cloud-sks-tools
HADOOP_HOSTNAME=wi20909-desktop
HADOOP_USER=wi20909
DATA_HOME=/home/wi20909/hadoopdev/cloud_data
DATA_NODE_BASE=/home/wi20909/hadoopdev/cloud_data
TABLENAME_ROOT=test




#HADOOP_HDFS=/state/partition1
#CLOUDBASE_HOME=/home/gridsan/hadoop/cloudbase
#HADOOP_HOME=/home/gridsan/hadoop/hadoop
#HBASE_HOME=/home/gridsan/hadoop/hbase
#HJOBS_HOME=/home/gridsan/hadoop/hadoopdev/hjobs_cloud-2.0
#HADOOP_HOSTNAME=f-2-0
#HADOOP_USER=hadoop
#DATA_HOME=/home/gridsan/hadoop/hadoopdev/cloudbase_data
#JAVA_HOME=/home/gridsan/hadoop/java/jdk1.6.0_11
#DATA_NODE_BASE=/state/partition1/crossmounts
#TABLENAME_ROOT=test



D4M_HOME=/home/wi20909/hadoopdev/d4m_api_v2




#HADOOP_HDFS=/state/partition1
#CLOUDBASE_HOME=/home/gridsan/hadoop/cloudbase
#HADOOP_HOME=/home/gridsan/hadoop/hadoop
#HBASE_HOME=/home/gridsan/hadoop/hbase
#HJOBS_HOME=/home/gridsan/hadoop/hadoopdev/hjobs_cloud-2.0
#HADOOP_HOSTNAME=f-2-0/home/wi20909/hadoopdev/
#HADOOP_USER=hadoop
#DATA_HOME=/home/gridsan/hadoop/hadoopdev/cloudbase_data
#JAVA_HOME=/home/gridsan/hadoop/java/jdk1.6.0_11
#DATA_NODE_BASE=/state/partition1/crossmounts
#TABLENAME_ROOT=test


export D4M_HOME
export CLOUDBASE_HOME
#export JAVA_HOME
export HADOOP_HOME
export HBASE_HOME
export HJOBS_HOME
export HADOOP_HOSTNAME
export HADOOP_USER
export DATA_HOME
export DATA_NODE_BASE
export TABLENAME_ROOT
export ZOOKEEPER_HOME


echo "----------------------------- Env Set "