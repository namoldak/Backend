#! /usr/bin/env bash

REPOSITORY=/home/ubuntu/namoldak
cd $REPOSITORY

APP_NAME=namoldak
JAR_NAME=$(ls $REPOSITORY/ | grep '*.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/$JAR_NAME

CURRENT_PID=$( ps -ef | grep "$JAR_NAME" | grep -v 'grep' | awk '{print $1}')

if [ -z $CURRENT_PID ]
then
  echo "> 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -9 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo"> $JAR_PATH 배포"
nohup java -jar $JAR_PATH