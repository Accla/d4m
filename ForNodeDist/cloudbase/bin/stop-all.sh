#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/cloudbase-config.sh

${bin}/cloudbase.sh admin stopAll

sleep 5

#look for master and gc processes not killed by 'admin stopAll'
${bin}/kill_server.sh $MASTER "$CLOUDBASE_HOME/.*/cloudbase-start.*.jar" master TERM
${bin}/kill_server.sh $MASTER "$CLOUDBASE_HOME/.*/cloudbase-start.*.jar" gc TERM
${bin}/kill_server.sh $MASTER "$CLOUDBASE_HOME/.*/cloudbase-start.*.jar" monitor TERM

${bin}/kill_server.sh $MASTER "$CLOUDBASE_HOME/.*/cloudbase-start.*.jar" master KILL
${bin}/kill_server.sh $MASTER "$CLOUDBASE_HOME/.*/cloudbase-start.*.jar" gc KILL
${bin}/kill_server.sh $MASTER "$CLOUDBASE_HOME/.*/cloudbase-start.*.jar" monitor KILL
