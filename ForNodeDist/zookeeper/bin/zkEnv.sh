#!/bin/sh

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# This script should be sourced into other zookeeper
# scripts to setup the env variables

# We use ZOOCFGDIR if defined,
# otherwise we use /etc/zookeeper
# or the conf directory that is
# a sibling of this script's directory
if [ "x$ZOOCFGDIR" = "x" ]
then
    if [ -d "/etc/zookeeper" ]
    then
        ZOOCFGDIR="/etc/zookeeper"
    else
        ZOOCFGDIR="$ZOOBINDIR/../conf"
    fi
fi

if [ -e "$ZOOCFGDIR/java.env" ]
then
    . "$ZOOCFGDIR/java.env"
fi

if [ "x$ZOO_LOG_DIR" = "x" ]
then 
    ZOO_LOG_DIR="."
fi

if [ "x$ZOO_LOG4J_PROP" = "x" ]
then 
    ZOO_LOG4J_PROP="INFO,CONSOLE"
fi

for f in ${ZOOBINDIR}/../zookeeper-*.jar
do 
    CLASSPATH="$CLASSPATH:$f"
done

ZOOLIBDIR=${ZOOLIBDIR:-$ZOOBINDIR/../lib}
for i in "$ZOOLIBDIR"/*.jar
do
    CLASSPATH="$CLASSPATH:$i"
done
#make it work for developers
for d in ${ZOOBINDIR}/../src/java/lib/*.jar
do
   CLASSPATH="$CLASSPATH:$d"
done
#add the zoocfg dir to classpath
CLASSPATH=$ZOOCFGDIR:$CLASSPATH
ZOOCFG="$ZOOCFGDIR/zoo.cfg"
