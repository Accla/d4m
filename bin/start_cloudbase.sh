
#!/bin/sh


PRGDIR=`dirname "$PRG"`

HOME=`cd "$PRGDIR/.." ; pwd`


. $HOME/bin/setenv.sh


$CLOUDBASE_HOME/bin/cloudbase.sh init

sleep 5

$CLOUDBASE_HOME/bin/start-all.sh
