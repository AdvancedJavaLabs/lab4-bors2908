FROM bde2020/hadoop-namenode:2.0.0-hadoop3.2.1-java8

COPY build/libs/SalesMapReduce-all.jar /jar/SalesMapReduce.jar

COPY script/hadoop-job.sh /hadoop-job.sh

RUN chmod +x /hadoop-job.sh

RUN mkdir -p /input /output

ENTRYPOINT ./hadoop-job.sh
