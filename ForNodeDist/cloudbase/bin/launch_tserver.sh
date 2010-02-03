#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/cloudbase-config.sh

PID=`ssh -q $1 "ps -ef | egrep \"$CLOUDBASE_HOME/.*/cloudbase.*.jar\" | grep tserver | grep -v grep | grep -v ssh" | awk {'print $2'} | head -1`;

if [ `uname -s` = "Darwin" ]; then
	START_COMMAND="ssh -qf $1 \"nohup $CLOUDBASE_HOME/bin/cloudbase.sh tserver >$CLOUDBASE_LOG_DIR/tserver_\\\`hostname\\\`.out \" "
else
	START_COMMAND="ssh -qf $1 \"nohup $CLOUDBASE_HOME/bin/cloudbase.sh tserver >$CLOUDBASE_LOG_DIR/tserver_\\\`hostname\\\`.out 2>$CLOUDBASE_LOG_DIR/tserver_\\\`hostname\\\`.err \" "
fi

if [ -z $PID ]; then
        echo $START_COMMAND
        sh -c "$START_COMMAND"
else
        echo "$1 : tserver already running (${PID})"
fi
