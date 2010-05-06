#!/bin/sh
# -----------------------------------------------------------------------------
#  Set CLASSPATH
#  Author: Will Smith
# -----------------------------------------------------------------------------
# First clear out the user classpath
#CLASSPATH=



if [ -z "$BASEDIR" ]; then
  echo "The BASEDIR environment variable is not defined"
  echo "This environment variable is needed to run this program"
  exit
fi
CLASSES=.
CLASSES="$CLASSES":$CLOUDBASE_HOME/conf/cloudbase-site.xml
echo adding $CLOUDBASE_HOME/conf/cloudbase-site.xml
CLASSES="$CLASSES":$CLOUDBASE_HOME/conf
echo adding $CLOUDBASE_HOME/conf
# CLASSES

CLASSES_DIR="$BASEDIR"/lib
#echo "classes dir  = $CLASSES_DIR"

if [ -d "$CLASSES_DIR" ] ; then
    for i in "$CLASSES_DIR"/*.jar; do
      CLASSES="$CLASSES":"$i"
		echo adding: $i
    done
echo "classes are = $CLASSES"
fi


export CLASSPATH="$CLASSES"


# Set standard command for invoking Java.
  _RUNJAVA=java

