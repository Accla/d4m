#!/bin/sh

# test to see if we have thrift installed
thrift >/dev/null 2>&1
if [ "$?" -ne 1 ] ; then 
   # Nope: bail
   echo "thrift is not available"
   exit 0
fi
mkdir -p target
for f in src/main/thrift/*.thrift
do
	thrift -o target --gen java $f
	thrift -o target --gen py $f
	thrift -o target --gen rb $f
done
# copy only files that have changed
for d in master monitor tabletserver client/proxy security
do
   mkdir -p src/main/java/cloudbase/core/$d/thrift >& /dev/null
   for f in target/gen-java/cloudbase/core/$d/thrift/* 
   do
      DEST="src/main/java/cloudbase/core/$d/thrift/`basename $f`"
      if ! cmp -s ${f} ${DEST} ; then
	echo cp ${f} ${DEST} 
	cp ${f} ${DEST} 
      fi
   done
done
