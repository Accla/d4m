#!/bin/sh

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/cloudbase-config.sh

if [ -z "$START_JAR" ]
then
    START_JAR=$CLOUDBASE_HOME/cloudbase-start/target/cloudbase-start-$CLOUDBASE_VERSION.jar
    if test ! -f $START_JAR
    then
       START_JAR=$CLOUDBASE_HOME/lib/cloudbase-start-$CLOUDBASE_VERSION.jar
    fi
fi

locationByProgram() 
{
   # find the program
   RESULT=`which $1`
   # strip the program name from the path
   RESULT=`dirname ${RESULT}`
   # strip /bin from the path
   RESULT=`dirname ${RESULT}`
   echo ${RESULT}
}

test -n "$JAVA_HOME"      || export JAVA_HOME=`locationByProgram java`
test -n "$HADOOP_HOME"    || export HADOOP_HOME=`locationByProgram hadoop`
test -n "$ZOOKEEPER_HOME" || export ZOOKEEPER_HOME=`locationByProgram zkCli.sh`

DEFAULT_GENERAL_JAVA_OPTS=""
BIG=6g
MED=3g
SMALL=1g
if ! "$JAVA_HOME/bin/java" -Xms10g -Xmx10g >/dev/null 2>&1 ; then
   BIG=3g
   MED=1g
   SMALL=500m
fi

test -n "$CLOUDBASE_TSERVER_OPTS" || export CLOUDBASE_TSERVER_OPTS="-Xmx$BIG -Xms$BIG "
test -n "$CLOUDBASE_MASTER_OPTS"  || export CLOUDBASE_MASTER_OPTS="-Xmx$MED -Xms$MED "
test -n "$CLOUDBASE_GC_OPTS"      || export CLOUDBASE_GC_OPTS="-Xmx$SMALL -Xms$SMALL "
test -n "$CLOUDBASE_GENERAL_OPTS" || export CLOUDBASE_GENERAL_OPTS="$DEFAULT_GENERAL_JAVA_OPTS"

# CLOUDBASE_XTRAJARS is where all of the commandline -add items go into for reading by CB.
if [ "$1" = "-add" ] ; then
	export CLOUDBASE_XTRAJARS=$2
	shift 2
else
	export CLOUDBASE_XTRAJARS=""
fi

if [ "$1" = "master" ] ; then
	export CLOUDBASE_OPTS="${CLOUDBASE_GENERAL_OPTS} ${CLOUDBASE_MASTER_OPTS}"
elif [ "$1" = "gc" ] ; then
	export CLOUDBASE_OPTS="${CLOUDBASE_GENERAL_OPTS} ${CLOUDBASE_GC_OPTS}"
elif [ "$1" = "tserver" ] ; then
	export CLOUDBASE_OPTS="${CLOUDBASE_GENERAL_OPTS} ${CLOUDBASE_TSERVER_OPTS}"
fi

XML_FILES=${CLOUDBASE_HOME}/conf
CLASSPATH=${XML_FILES}:${START_JAR}

if [ -z $JAVA_HOME ] ; then
    echo "JAVA_HOME is not set.  Please make sure it's set globally or in conf/cloudbase-env.sh"
elif [ -z $HADOOP_HOME ] ; then
    echo "HADOOP_HOME is not set.  Please make sure it's set globally or in conf/cloudbase-env.sh"
elif [ -z $ZOOKEEPER_HOME ] ; then
    echo "ZOOKEEPER_HOME is not set.  Please make sure it's set globally or in conf/cloudbase-env.sh"
else
    exec $JAVA_HOME/bin/java -classpath $CLASSPATH $CLOUDBASE_OPTS cloudbase.start.Main $@
fi
exit 1
