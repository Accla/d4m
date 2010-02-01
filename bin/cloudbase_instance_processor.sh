#!/bin/sh


PRGDIR=`dirname "$PRG"`
H=`cd "$PRGDIR/.." ; pwd`
#. $H/bin/setenv.sh

base_dir=$H

THIS_SCRIPT=cloudbase_instance_processor.sh

echo "The base directory is $base_dir"

if [ ! -f "$H/bin/instance_hosts" ]
then
   echo "Unable to find file $H/bin/instance_hosts"
   exit 1
fi



argval=$1
if [ $# -ne 1 ]
then
   echo "Usage; $THIS_SCRIPT [ start|stop|install|remove ]"
   exit 1
fi



counter=1
HOSTS=$H/bin/instance_hosts

cat $HOSTS | while read HOST ;
do


if [ $argval == 'install' ] ;
then
echo "Installing to $HOST."
#$SSH $HOST.llgrid.ll.mit.edu "( nohup ${H}/bin/install_cb_hd_local.sh $HOST $base_dir $counter )"
fi

if [ $argval == 'start' ] ;
then
echo "Starting $HOST." 
#$SSH $HOST.llgrid.ll.mit.edu "( ${H}/bin/start_cb_hd_local.sh $HOST $base_dir )"
fi

if [ $argval == 'stop' ] ;
then
echo "Stopping $HOST." 
#$SSH $HOST.llgrid.ll.mit.edu "( nohup ${H}/bin/stop_cb_hd_local.sh $HOST $base_dir )"
fi

if [ $argval == 'remove' ] ;
then
echo "Uninstalling $HOST." 
#$SSH $HOST.llgrid.ll.mit.edu "( nohup ${H}/bin/remove_cb_hd_local.sh $HOST $base_dir )"
fi

counter=$(($counter + 1))
done





