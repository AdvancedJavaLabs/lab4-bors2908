FROM bde2020/hadoop-namenode:2.0.0-hadoop3.2.1-java8

COPY build/libs/SalesMapReduce-all.jar /jar/SalesMapReduce.jar

COPY script/hadoop-job.sh /hadoop-job.sh

RUN chmod +x /hadoop-job.sh

HEALTHCHECK CMD curl -f http://localhost:9870/ || exit 1

ENV HDFS_CONF_dfs_namenode_name_dir=file:///hadoop/dfs/name
RUN mkdir -p /hadoop/dfs/name
VOLUME /hadoop/dfs/name

ADD script/run.sh /run.sh
RUN chmod a+x /run.sh

EXPOSE 9870

#TODO In case of problems, check if hadoop-job.sh is LF instead of CRLF
ENTRYPOINT ["bash", "./hadoop-job.sh"]
