#!/usr/bin/env bash

# search for hadoop
HADOOP_HOME="/usr/local/opt/hadoop-2.7.3"
HADOOP_SH=`which hadoop`

if [ -z "${HADOOP_SH}" ]
    then
        if [ -e "${HADOOP_HOME}/bin/hadoop" ]
            then
                HADOOP_SH="${HADOOP_HOME}/bin/hadoop"
                echo "Using default Hadoop script."
            else
                echo "No hadoop script found."
                exit 1
        fi
    else
        echo "Using Hadoop script found in path."
fi

# search for jar
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
LIB=$DIR/target/bdelab2-0.0.1-SNAPSHOT-jar-with-dependencies.jar
if [ -z "${LIB}" ]
    then
        echo"Project jar not found. Build it first with 'mvn package'"
        exit 2
fi

$HADOOP_SH jar $LIB de.hska.iwi.bdelab.batchstore.BatchLoader $1 $2 $3 $4 $5 $6 $7 $8
