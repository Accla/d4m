
#!/bin/sh


PRGDIR=`dirname "$PRG"`

H=`cd "$PRGDIR/.." ; pwd`

. $H/hadoop/hadoopdev/cloud-sks-tools-1.0/bin/setenv.sh
#. $H/bin/setenv.sh

node_name=$1

echo " CLOUDBASE_HOME = $CLOUDBASE_HOME"
echo " getting node name = $node_name"

rm -r /state/partition1/crossmounts/$node_name/hadoop/dfs
rm -r /state/partition1/crossmounts/$node_name/hadoop/mapred
rm -r /state/partition1/crossmounts/$node_name/hadoop/hadoop
rm -r /state/partition1/crossmounts/$node_name/hadoop/cloudbase
rm -r /state/partition1/crossmounts/$node_name/hadoop/zookeeper

