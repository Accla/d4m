PRGDIR=`dirname "$PRG"`

H=`cd "$PRGDIR/../.." ; pwd`
H=/state/partition1/crossmounts/f-2-1/hadoop
echo "base dir is $H" 
export JAVA_HOME=/home/gridsan/hadoop/java/jdk1.6.0_11
export CLOUDBASE_HOME=$H/cloudbase
export HADOOP_HOME=$H/hadoop
export CLOUDBASE_LOG_DIR=$H/cloudbase/logs
export ZOOKEEPER_HOME=$H/zookeeper
export CLOUDBASE_TSERVER_OPTS="-Xmx6g -Xms6g "
export CLOUDBASE_MASTER_OPTS="-Xmx3g -Xms3g "
export CLOUDBASE_GC_OPTS="-Xmx1g -Xms1g "
export CLOUDBASE_GENERAL_OPTS="-XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75 "

