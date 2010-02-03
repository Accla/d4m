#!/bin/bash

# only start if there's not one already running
if [ "$1" = "`hostname`" ]; then
	PID=`ps -ef | egrep ${2} | grep ${3} | grep -v grep | grep -v ssh | grep -v kill_server.sh | awk {'print \$2'} | head -1`
else
	PID=`ssh -q $1 "ps -ef | egrep '${2}' | grep '${3}' | grep -v grep | grep -v ssh | grep -v kill_server.sh" | awk {'print $2'} | head -1`
fi
if [ ! -z $PID ]; then
        echo "stopping ${2} ${3} on $1";
        ssh -q -o 'ConnectTimeout 8' $1 "kill -s ${4} ${PID} 2>/dev/null"
fi;

