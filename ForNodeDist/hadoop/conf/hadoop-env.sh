# Set Hadoop-specific environment variables here.

# The only required environment variable is JAVA_HOME.  All others are
# optional.  When running a distributed configuration it is best to
# set JAVA_HOME in this file, so that it is correctly defined on
# remote nodes.

# The java implementation to use.  Required.
export JAVA_HOME=/home/gridsan/hadoop/java/latest

# Extra Java CLASSPATH elements.  Optional.
# export HADOOP_CLASSPATH=
#export HBASE_HOME=$HOME/hbase
#export CLOUDBASE_HOME=$HOME/cloudbase
#export HJOBS_CLOUDBASE_HOME=$HOME/hadoopdev/hjobs_cloud-1.0
#export HJOBS_HOME=$HOME/hadoopdev/hjobs_cloud-1.0
#export CLOUDBASE_TOOLS_HOME=/home/gridsan/hadoop/hadoopdev/cloudbase1.0_tool_api-1.0

##### FOR HBASE ONLY $HBASE_HOME/conf:$HBASE_HOME/hbase-0.19.0.jar:$HBASE_HOME/hbase-0.19.0-test.jar:$HJOBS_HOME/hjobs-0.19.1-dev.jar

#export HADOOP_CLASSPATH=$CLOUDBASE_HOME/lib/cloudbase-core-1.0.0-RC2.jar
#HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$CLOUDBASE_TOOLS_HOME/cloudbase1.0_tool_api-1.0.jar
#HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$CLOUDBASE_TOOLS_HOME/lib/thrift-20080411p1.jar
#HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$ZOOKEEPER_HOME/zookeeper-3.2.0.jar
#HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$CLOUDBASE_TOOLS_HOME/lib/lucene-core-2.4.1.jar
#HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$CLOUDBASE_TOOLS_HOME/lib/lucene-demos-2.4.1.jar
#HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$CLOUDBASE_TOOLS_HOME/lib/poi-3.2-FINAL-20081019.jar
#HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$CLOUDBASE_TOOLS_HOME/lib/poi-contrib-3.2-FINAL-20081019.jar
#HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$CLOUDBASE_TOOLS_HOME/lib/poi-scratchpad-3.2-FINAL-20081019.jar



# The maximum amount of heap to use, in MB. Default is 1000.
export HADOOP_HEAPSIZE=10000

# Extra Java runtime options.  Empty by default.
# export HADOOP_OPTS=-server

# Command specific options appended to HADOOP_OPTS when specified
export HADOOP_NAMENODE_OPTS="-Dcom.sun.management.jmxremote $HADOOP_NAMENODE_OPTS"
export HADOOP_SECONDARYNAMENODE_OPTS="-Dcom.sun.management.jmxremote $HADOOP_SECONDARYNAMENODE_OPTS"
export HADOOP_DATANODE_OPTS="-Dcom.sun.management.jmxremote $HADOOP_DATANODE_OPTS"
export HADOOP_BALANCER_OPTS="-Dcom.sun.management.jmxremote $HADOOP_BALANCER_OPTS"
export HADOOP_JOBTRACKER_OPTS="-Dcom.sun.management.jmxremote $HADOOP_JOBTRACKER_OPTS"
# export HADOOP_TASKTRACKER_OPTS=
# The following applies to multiple commands (fs, dfs, fsck, distcp etc)
# export HADOOP_CLIENT_OPTS

# Extra ssh options.  Empty by default.
# export HADOOP_SSH_OPTS="-o ConnectTimeout=1 -o SendEnv=HADOOP_CONF_DIR"

# Where log files are stored.  $HADOOP_HOME/logs by default.
# export HADOOP_LOG_DIR=${HADOOP_HOME}/logs

# File naming remote slave hosts.  $HADOOP_HOME/conf/slaves by default.
# export HADOOP_SLAVES=${HADOOP_HOME}/conf/slaves

# host:path where hadoop code should be rsync'd from.  Unset by default.
# export HADOOP_MASTER=master:/home/$USER/src/hadoop

# Seconds to sleep between slave commands.  Unset by default.  This
# can be useful in large clusters, where, e.g., slave rsyncs can
# otherwise arrive faster than the master can service them.
# export HADOOP_SLAVE_SLEEP=0.1

# The directory where pid files are stored. /tmp by default.
# export HADOOP_PID_DIR=/var/hadoop/pids

# A string representing this instance of hadoop. $USER by default.
# export HADOOP_IDENT_STRING=$USER

# The scheduling priority for daemon processes.  See 'man nice'.
# export HADOOP_NICENESS=10
