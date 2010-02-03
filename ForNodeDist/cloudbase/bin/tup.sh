#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/cloudbase-config.sh

SLAVES=$CLOUDBASE_HOME/conf/slaves

echo "starting tablet servers ..."

for server in `cat $SLAVES | grep -v '^#' `; do 
	${bin}/launch_tserver.sh $server &
done

echo "done"

