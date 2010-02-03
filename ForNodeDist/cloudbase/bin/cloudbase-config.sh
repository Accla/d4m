#copied below from hadoop-config.sh
this="$0"
while [ -h "$this" ]; do
    ls=`ls -ld "$this"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '.*/.*' > /dev/null; then
        this="$link"
    else
        this=`dirname "$this"`/"$link"
    fi
done
bin=`dirname "$this"`
script=`basename "$this"`
bin=`cd "$bin"; pwd`
this="$bin/$script"

CLOUDBASE_HOME=`dirname "$this"`/..
export CLOUDBASE_HOME=`cd $CLOUDBASE_HOME; pwd`

if [ -f $CLOUDBASE_HOME/conf/cloudbase-env.sh ] ; then
. $CLOUDBASE_HOME/conf/cloudbase-env.sh
fi

if [ -z ${CLOUDBASE_LOG_DIR} ]; then
        CLOUDBASE_LOG_DIR=$CLOUDBASE_HOME/logs
fi

mkdir -p $CLOUDBASE_LOG_DIR 2>/dev/null

export CLOUDBASE_LOG_DIR

if [ -z ${CLOUDBASE_VERSION} ]; then
        CLOUDBASE_VERSION=1.0.0-RC2
fi

if [ ! -f "$CLOUDBASE_HOME/conf/masters" ]
then
   echo "Unable to find file $CLOUDBASE_HOME/conf/masters"
   exit 1
fi
MASTER=`head -1 "$CLOUDBASE_HOME/conf/masters"`
SSH='ssh -qf'
