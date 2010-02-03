#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/cloudbase-config.sh

HADOOP_CMD=$HADOOP_LOCATION/bin/hadoop

SLAVES=$CLOUDBASE_HOME/conf/slaves

echo 'stopping unresponsive tablet servers ...'
for server in `cat $SLAVES | grep -v '^#' `; do
        # only start if there's not one already running
        $CLOUDBASE_HOME/bin/kill_server.sh $server "$CLOUDBASE_HOME/.*/cloudbase-start.*.jar" tserver TERM & 
done

sleep 10

echo 'stopping unresponsive tablet servers hard ...'
for server in `cat $SLAVES | grep -v '^#' `; do
        # only start if there's not one already running
        $CLOUDBASE_HOME/bin/kill_server.sh $server "$CLOUDBASE_HOME/.*/cloudbase-start.*.jar" tserver KILL & 
done

path=`grep -A 2 "cloudbase.directory" $CLOUDBASE_HOME/conf/cloudbase-site.xml | grep \<value\>.*\<\/value\> | cut -d'>' -f2 | cut -d'<' -f1`

if [ -z "$path" ]
then
	path=`grep -A 2 "cloudbase.directory" $CLOUDBASE_HOME/conf/cloudbase-default.xml | grep \<value\>.*\<\/value\> |  cut -d'>' -f2 | cut -d'<' -f1`
fi

echo 'deleting server directory entries'
#$HADOOP_CMD dfs -rmr $path/tservers/*
echo 'done'
