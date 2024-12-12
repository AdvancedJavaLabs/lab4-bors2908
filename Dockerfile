FROM bde2020/hadoop-namenode:2.0.0-hadoop3.2.1-java8

COPY build/libs/SalesMapReduce-all.jar /jar/SalesMapReduce.jar

COPY ./input /input

RUN hdfs dfs -mkdir -p /user/root

RUN mkdir -p /data/input /data/output
RUN hdfs dfs -mkdir /user/root/output

RUN hdfs dfs -put /input /user/root

ENTRYPOINT hadoop jar "/jar/SalesMapReduce.jar" "org.itmo.SalesMapReduce" input output
