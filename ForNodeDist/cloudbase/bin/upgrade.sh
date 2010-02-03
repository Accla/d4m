#!/bin/sh
#This script upgrades a cloudbase 0.5.X instance to cloudbase 1.0.X.  
#
#To use this script, shutdown cloudbase 0.5.X cleanly.  Then move the
#cloudbase directory in HDFS from /cloudbase to /cloudbase-0.5.  Set
#the num map task below to the number of conncurrent mappers you want
#to run.  Then run this script.
#
#If you do not have enough space in HDFS to hold the 0.5 and 1.0
#data at the same time, you can try setting DELETE_EXISTING to 
#true.  This will cause the 0.5 map files to be deleted as they
#are converted.
#
#Before upgrading, check how much memory the namenode is using.  If
#you have  a lot of files, the upgrade will double the number of files.
#This could cause the namenode to use too much memory and crash.
#

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/cloudbase-config.sh

OLD_DIR=/cloudbase-0.5
NEW_DIR=/cloudbase
SCRATCH_DIR=/scratch
NUM_MAPS=8
CORE_JAR=$bin/../lib/cloudbase-core-$CLOUDBASE_VERSION.jar
THRIFT_JAR=$bin/../lib/thrift-20080411p1.jar
DELETE_EXISTING=false


hadoop fs -test -e $OLD_DIR
if [ $? != 0 ] ; then
	echo "Directory $OLD_DIR does not exist";
	exit 1;
fi

hadoop fs -test -e $NEW_DIR
if [ $? == 0 ] ; then
	echo "Directory $NEW_DIR already exist";
	exit 1;
fi

hadoop fs -test -e $SCRATCH_DIR
if [ $? == 0 ] ; then
	echo "Directory $SCRATCH_DIR already exist";
	exit 1;
fi

echo
echo "*** Creating a blank new cloudbase dir***"
echo
#initialize a blank new cloudbase dir
$bin/cloudbase.sh init

#remove the !METADATA dir created by init, we will import the metadata table from
#the old 0.5 instance
hadoop fs -rmr $NEW_DIR/tables/\!METADATA

echo
echo "*** Starting a map reduce job to translate 0.5 map files***"
echo
#start a mapreduce job to translate all of the 0.5 map files to 1.0 mapfiles
if [ `hadoop version | head -1 | awk -F . '{print $2}'` = 20 ] 
then
	hadoop jar $CORE_JAR cloudbase.core.util.ConvertMapFiles -libjars $THRIFT_JAR $OLD_DIR $NEW_DIR $SCRATCH_DIR $NUM_MAPS $DELETE_EXISTING
else
	hadoop jar -libjars $THRIFT_JAR $CORE_JAR cloudbase.core.util.ConvertMapFiles $OLD_DIR $NEW_DIR $SCRATCH_DIR $NUM_MAPS $DELETE_EXISTING
fi

hadoop fs -rmr $SCRATCH_DIR

echo
echo "*** Translating the 0.5 !METADATA table ***"
echo
#translate the 0.5 metadata table to a 1.0 metadata table
hadoop fs -mv $NEW_DIR/tables/\!METADATA $NEW_DIR/tables/\!METADATA.old
$bin/cloudbase.sh cloudbase.core.util.UpgradeMetadataTable $NEW_DIR/tables/\!METADATA.old $NEW_DIR/tables/\!METADATA
hadoop fs -rmr $NEW_DIR/tables/\!METADATA.old

echo
echo "*** Finished upgrade ***"
echo

