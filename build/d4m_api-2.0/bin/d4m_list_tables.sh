
#!/bin/sh

PRGDIR=`dirname "$PRG"`
H=`cd "$PRGDIR/.." ; pwd`
. $H/bin/setenv.sh

export BASEDIR=$H
echo "base dir = $HOME"
. $H/bin/set-classpath.sh


echo ""
echo ""
echo ""
echo ""
echo ""
echo ""
echo ""


#echo "----------------------------- java_home=$JAVA_HOME"
#echo "----------------------------- HADOOP_HOME=$HADOOP_HOME"
#echo "----------------------------- HJOBS_HOME=$HJOBS_HOME"
#echo "----------------------------- HADOOP_USER=$HADOOP_USER"
#echo "----------------------------- HADOOP_HOSTNAME=$HADOOP_HOSTNAME"

#cd "$HADOOP_HOME"/bin


echo "classpath = $CLASSPATH"

cd $D4M_HOME/build/classes
echo "----------------------------- Loading D4mDbInfo"
#./hadoop jar $D4M_HOME/build/d4m_api-2.0.jar ll.mit.edu.d4m.db.cloud.D4mDbInfo localhost
java -classpath .:$CLASSPATH ll.mit.edu.d4m.db.cloud.D4mDbInfo localhost
echo "----------------------------- Job complete"
#-Xms2000m
