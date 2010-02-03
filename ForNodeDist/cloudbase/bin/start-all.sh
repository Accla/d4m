#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/cloudbase-config.sh

${bin}/tup.sh

if [ -z `$SSH $MASTER ps -ef | egrep ${CLOUDBASE_HOME}/.*/cloudbase.*.jar | grep master | grep -v grep | awk {'print $2'} | head -1` ]; then
	$SSH $MASTER "( nohup ${bin}/cloudbase.sh master $1 >$CLOUDBASE_LOG_DIR/master_\`hostname\`.out ) >& $CLOUDBASE_LOG_DIR/master_\`hostname\`.err"
else
	echo "Master already running"
fi

if [ -z `$SSH $MASTER ps -ef | egrep ${CLOUDBASE_HOME}/.*/cloudbase.*.jar | grep gc | grep -v grep | awk {'print $2'} | head -1` ]; then
	$SSH $MASTER "( nohup ${bin}/cloudbase.sh gc .9 $1 >$CLOUDBASE_LOG_DIR/gc_\`hostname\`.out ) >&$CLOUDBASE_LOG_DIR/gc_\`hostname\`.err"
else
	echo "Garbage Collector already running"
fi

if [ -z `$SSH $MASTER ps -ef | egrep ${CLOUDBASE_HOME}/.*/cloudbase.*.jar | grep monitor | grep -v grep | awk {'print $2'} | head -1` ]; then
	$SSH $MASTER "( nohup ${bin}/cloudbase.sh monitor >$CLOUDBASE_LOG_DIR/monitor_\`hostname\`.out ) >&$CLOUDBASE_LOG_DIR/monitor_\`hostname\`.err"
else
	echo "Monitor already running"
fi
