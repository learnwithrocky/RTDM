# RTDM


####### NIFI Setup  #####################

#################################################

#################################################



####### Install JDK8 #####################


sudo apt-get update
sudo apt-get install openjdk-8-jdk


####### Update the Bash RC file  #####################


vi ~/.bashrc

Add the path in the end :



export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64


source ~/.bashrc


####### Create Setup Directory #####################

mkdir Setup

cd Setup

####### Download the nifi tar #####################

wget https://archive.apache.org/dist/nifi/1.15.3/nifi-1.15.3-bin.tar.gz

####### Extract nifi tar #####################

tar -zxvf nifi-1.15.3-bin.tar.gz


cd nifi-1.15.3/

####### Edit the nifi env.sh to add JDK #####################

vi bin/nifi-env.sh

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64

####### Edit the nifi env.sh to add JDK #####################


Edit nifi.properties(line number 150)

vi conf/nifi.properties


nifi.web.https.host=0.0.0.0
nifi.web.https.port=8883


Edit

sudio vi /etc/hosts

20.81.191.133 rtdm-1

#####Start the service #####################

sh bin/nifi.sh start

##### check the status  #####################

sh bin/nifi.sh status



#####################open the inbound firewall port 8883 #####################



https://74.249.50.101:8883/

./bin/nifi.sh set-single-user-credentials admin  Admin@1234567

sh bin/nifi.sh restart



####### Kafka Setup  #####################

#################################################

#################################################


cd ~/Setup

####### Download the Kafka tar #####################

wget https://downloads.apache.org/kafka/2.8.2/kafka_2.13-2.8.2.tgz

####### Extract the tar #####################


tar -zxvf kafka_2.13-2.8.2.tgz

cd kafka_2.13-2.8.2/

####### Start the zookeeper Service #####################


sh bin/zookeeper-server-start.sh config/zookeeper.properties &


Edit server.properties

vi config/server.properties

port = 9092
advertised.host.name = localhost


####### Start the kafka Service #####################

sh bin/kafka-server-start.sh config/server.properties  &


####### Topic create #####################



  bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic test --partitions 1 --replication-factor 1


  ######   Run the Console producer  #####################




sh bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test

######   Run the Console producer  #####################


  bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning




  ####### Elastic Search Installation #####################

  #################################################

  #################################################


cd ~/Setup


################################################# Use the wget command to pull the public key: #################################################



wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add -


#####################################add the repository #####################################

echo "deb https://artifacts.elastic.co/packages/7.x/apt stable main" | sudo tee -a /etc/apt/sources.list.d/elastic-7.x.list


#####################################Update the package index one more time before proceeding. #####################################

sudo apt update

sudo apt install elasticsearch


##################################### Start Elasticsearch Service #####################################



sudo systemctl daemon-reload

sudo systemctl enable elasticsearch.service
service elasticsearch status


##################################### Edit the network configuration #####################################



sudo vim /etc/elasticsearch/elasticsearch.yml

transport.host: localhost

transport.tcp.port: 9300

http.port: 9200
network.host: 0.0.0.0


sudo systemctl start elasticsearch

curl localhost:9200


####### Kibana Installation #####################

#################################################

#################################################


sudo apt install --assume-yes kibana
sudo ufw allow 5601
sudo vi /etc/kibana/kibana.yml

server.port: 5601
server.host: "0.0.0.0"

elasticsearch.hosts: ["http://localhost:9200"]

sudo systemctl enable kibana
sudo systemctl restart kibana
sudo systemctl status kibana
curl -L 127.0.0.1:5601




####### Spark Installation #####################

#################################################

#################################################

cd ~/Setup


################################################# Download Tar #################################################

wget https://archive.apache.org/dist/spark/spark-2.4.6/spark-2.4.6-bin-hadoop2.6.tgz

################################################# Extract Tar #################################################

tar -zxvf spark-2.4.6-bin-hadoop2.6.tgz

################################################# Change the Configuration #################################################



cd spark-2.4.6-bin-hadoop2.6/conf
cp spark-env.sh.template spark-env.sh

edit spark-env.sh

export SPARK_LOCAL_IP=127.0.0.1

cd ../


################################################# Test the Spark #################################################



./bin/./spark-submit --class org.apache.spark.examples.SparkPi examples/jars/spark-examples_2.11-2.4.6.jar 10



################################################# Creating a polling directory and data_generator  #################################################


cd ~/
mkdir Poll
mkdir data_generator

cd data_generator

vi data_gen.sh

rm -r demo/*
java -cp jsonGenerator-1.0-SNAPSHOT.jar com.adobe.ids.Main user 1000
java -cp jsonGenerator-1.0-SNAPSHOT.jar com.adobe.ids.Main event 1000
java -cp jsonGenerator-1.0-SNAPSHOT.jar com.adobe.ids.Main visit 1000
mv *.txt ../Poll



cp ~/jsonGenerator-1.0-SNAPSHOT.jar .

################################################# Create Demo Directory  #################################################



mkdir Demo
cd Demo

mkdir index_v2

mkdir es_logs


./../Setup/spark-2.4.6-bin-hadoop2.6/bin/spark-submit  --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.4  --class org.example.rtdm --master "local[*]" rtdmtest-4.0-jar-with-dependencies.jar  "test" "index_v2" "./es_logs" "true"

./../Setup/spark-2.4.6-bin-hadoop2.6/bin/spark-submit  --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.4  --class org.example.rtdm --master "local[*]" rtdmtest-4.0-jar-with-dependencies.jar  "test" "index_v2" "./es_logs" "true"



