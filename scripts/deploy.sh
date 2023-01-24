##! /usr/bin/env bash
#
#REPOSITORY=/home/ubuntu/app
#cd $REPOSITORY
#
#APP_NAME=namoldak
#JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep 'SNAPSHOT.jar' | tail -n 1)
#JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME
#
#CURRENT_PID=$( ps -ef | grep "$JAR_NAME" | grep -v 'grep' | awk '{print $2}')
#
#if [ -z $CURRENT_PID ]
#then
#  echo "> 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
#else
#  echo "> kill -9 $CURRENT_PID"
#  kill -15 $CURRENT_PID
#  sleep 5
#fi
#
#echo"> $JAR_PATH 배포"
#nohup java -jar $JAR_PATH --logging.file.path=$REPOSITORY/log --logging.level.org.hibernate.SQL=DEBUG >> $REPOSITORY/log/deploy.log 2>/$REPOSITORY/log/deploy_err.log &