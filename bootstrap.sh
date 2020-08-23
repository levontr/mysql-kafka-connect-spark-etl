#!/usr/bin/env bash

echo "build the spark job"
cd etl
mvn clean package
cd ..
cp etl/target/etl-0.1-SNAPSHOT-jar-with-dependencies.jar job/EtlJob.java

echo "creating containers"
docker-compose up -d

######### Start - Setting up KafkaConnect Mysql connector and Kafka metrics configurations ###########
echo "copy jmx2graphite jar to Kafka Connect Service"
docker cp jars/jmx2graphite-1.4.4-javaagent.jar connect:/home/jmx2graphite.jar

echo "copy JOLOKIA jar to Kafka Connect Service"
docker cp jars/jolokia.jar connect:/home/jolokia.jar

echo "copy mysql jdbc driver to Kafka Connect Service"
docker cp jars/mysql-connector-java-8.0.21.jar connect:/usr/share/java/kafka-connect-jdbc

echo "copy .bashrc to Kafka Connect Service"
docker cp .bashrc connect:/root/

echo "restart connect"
docker restart connect

echo "check healthcheck of Kafka Connect Service"
status=$(docker inspect -f {{.State.Health.Status}} connect)
while [ $status != "healthy" ]
do
    sleep 10s;
    status=$(docker inspect -f {{.State.Health.Status}} connect)
    echo $status
done;

echo "create mysql connector"
docker exec connect curl -X POST http://localhost:8083/connectors -H "Content-Type: application/json" -d '{"name": "jdbc_source_mysql_20",
                        "config": {
                                "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
                                "connection.url": "jdbc:mysql://db:3306/test",
                                "connection.user": "root",
                                "connection.password": "root",
                                "topic.prefix": "mysql-20",
                                "mode": "timestamp",
                                "timestamp.column.name": "date",
                                "validate.non.null": "false",
                                "key.converter": "io.confluent.connect.avro.AvroConverter",
                                "key.converter.schema.registry.url": "http://schema-registry:8081",
                                "value.converter": "io.confluent.connect.avro.AvroConverter",
                                "value.converter.schema.registry.url": "http://schema-registry:8081",
                                "query": "select * from (select v.date, v.volume_type_id, v.volume, t.description from volume v join volume_type t on v.volume_type_id=t.id) a"
                                }
                        }'
######### End - Setting up KafkaConnect Mysql connector and Kafka metrics configurations ###########


######### Start - Setting up Spark job/dependencies #########
echo "copy required jars to the spark Service"
docker cp jars/commons-pool2-2.8.1.jar spark:/opt/bitnami/spark/jars/
docker cp jars/kafka-clients-2.6.0.jar spark:/opt/bitnami/spark/jars/
docker cp jars/spark-sql-kafka-0-10_2.12-3.0.0.jar spark:/opt/bitnami/spark/jars/
docker cp jars/spark-token-provider-kafka-0-10_2.12-3.0.0.jar spark:/opt/bitnami/spark/jars/

echo "copy the spark job to the spark Service"
docker cp job/EtlJob.java spark:/home/EtlJob.java

echo "copy the metric.properties to the spark Service"
docker cp job/metrics.properties spark:/home
######### End - Setting up Spark job/dependencies #########


######### Start - Submit the spark ETL job #########
echo "submit the spark job"
docker exec spark spark-submit --deploy-mode client --conf spark.yarn.submit.waitAppCompletion=false \
--files=/home/metrics.properties \
--conf spark.metrics.conf=/home/metrics.properties \
--class com.etl.job.EtlJob  /home/EtlJob.java > /tmp/log.log 2>&1 &
######### End - Submit the spark ETL job #########


######### Start - Create Grafana datasource and dashboard #########
echo "create grafana datasource"
sh grafana/create_datasource.sh

echo "create grafana dashboard"
sh grafana/create_dashboard.sh
######### End - Create Grafana datasource and dashboard #########
