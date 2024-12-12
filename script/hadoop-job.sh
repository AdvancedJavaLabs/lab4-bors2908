#!/bin/bash

./run.sh &
./entrypoint.sh &

echo "Waiting for Init."
sleep 10

hdfs dfs -mkdir -p /user/root
hdfs dfs -mkdir -p /user/root/input

for i in {0..7}; do
    input_file="/input/${i}.csv"
    hdfs_input="/user/root/input"
    hdfs_output="/user/root/output"
    local_output="/output/${i}"

    echo "Cleaning up existing results: ${local_output}"

    rm -rf ${local_output}

    mkdir ${local_output}

    echo "Processing file: ${input_file}"

    hdfs dfs -put ${input_file} ${hdfs_input}

    hadoop jar /jar/SalesMapReduce.jar ${hdfs_input} ${hdfs_output}

    hdfs dfs -get ${hdfs_output}/* ${local_output}

    hdfs dfs -rm -r ${hdfs_output}

    echo "Finished processing file: ${input_file}"
done

echo "All files processed."
