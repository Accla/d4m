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
  exit 1OME
fi
CLASSES=.
CLASSES=/home/wi20909/hadoopdev/cloudbase/cloudbase/conf/cloudbase-default.xml
CLASSES="$CLASSES":/home/wi20909/hadoopdev/cloudbase/cloudbase/conf/cloudbase-site.xml

# CLASSES

CLASSES_DIR="$BASEDIR"/lib
echo "classes dir  = $CLASSES_DIR"

if [ -d "$CLASSES_DIR" ] ; then
    for i in "$CLASSES_DIR"/*.*; do
      CLASSES="$CLASSES":"$i"
      
    done
echo "classes are = $CLASSES"
fi


export CLASSPATH="$CLASSES"


# Set standard command for invoking Java.
  _RUNJAVA=java

