#!/bin/bash

JAR=target/FM14BFBot-0.5-jar-with-dependencies.jar

if [ -f $JAR ]
then
    java -jar $JAR
else
    echo "couldn't find $JAR"
    echo "please compile the sources"
fi

