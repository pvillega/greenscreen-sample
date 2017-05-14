#!/usr/bin/env bash
dir="$( cd "$( dirname "$0" )" && pwd )"
cd ${dir}/..

GC_LOG="$dir/../log/gc.log"
DUMP_LOG="$dir/../log/dump-`date +%Y-%m-%d:%H:%M:%S`.hprof"

# See http://blog.sokolenko.me/2014/11/javavm-options-production.html
# See http://www.oracle.com/technetwork/tutorials/tutorials-1876574.html for G1 garbage collector
export JAVA_OPTS="$JAVA_OPTS\
 -server\
 -Dfile.encoding=UTF8\
 -Xms512m\
 -Xmx1024m\
 -XX:MaxMetaspaceSize=256m\
 -XX:+UseG1GC\
 -XX:MaxGCPauseMillis=200\
 -XX:+PrintGCDateStamps\
 -verbose:gc\
 -XX:+PrintGCDetails\
 -Xloggc:"$GC_LOG"\
 -XX:+UseGCLogFileRotation\
 -XX:NumberOfGCLogFiles=10\
 -XX:GCLogFileSize=100M\
 -XX:+HeapDumpOnOutOfMemoryError\
 -XX:HeapDumpPath="$DUMP_LOG"\
 -Djava.awt.headless=true\
 -Dsun.net.inetaddr.ttl=60\
 -XX:+PerfDisableSharedMem\
 -XX:+UseTLAB"

CONFIG_OPT="-v -Dconfig.resource=application.dev.conf"

echo "Running server locally. This requires a local postgres db running, check script 'psql/setupPostgresSocker.sh' in this same folder to run one using docker."
sbt ${CONFIG_OPT} run