# Cloud Dataflow with Memorystore 

Code to create dataflow pipeline that reads file data from a cloud storage, processes and transforms it and outputs the transformed data in Google's own in-memory datastore which is their Redis implemenation called memorystore. The pipeline code is written in Java and have been worked upon Apache Beam's SDK.

## About Cloud dataflow
Dataflow is a fully-managed service to execute pipelines within the Google Cloud Platform ecosystem. It is a service which is fully dedicated towards transforming and enriching data in stream (real time) and batch (historical) modes. It is a serverless approach where users can focus on programming instead of managing server clusters, can be integrated with Stackdriver, which lets you monitor and troubleshoot pipelines as they are running. It acts as a convenient integration point where Tensorflow machine learning models can be added to process data pipelines.

## About Memorystore
Memorystore for Redis provides a fully-managed service that is powered by the Redis in-memory data store to build application caches that provide sub-millisecond data access.
With Memorystore for Redis, you can easily achieve your latency and throughput targets by scaling up your Redis instances with minimal impact to your application's availability.

## Command to execute for creating the template:

```
mvn compile exec:java \
  -Dexec.mainClass=com.viveknaskar.DataFlowPipelineForMemStore \
  -Dexec.args="--project=your-project-id \
  --jobName=dataflow-memstore-job \
  --stagingLocation=gs://cloud-dataflow-pipeline-bucket/staging/ \
  --dataflowJobFile=gs://cloud-dataflow-pipeline-bucket/templates/dataflow-custom-redis-template \
  --gcpTempLocation=gs://cloud-dataflow-pipeline-bucket/tmp/ \
  --runner=DataflowRunner"
```

## Check the data inserted in Memorystore (Redis) datastore
For checking whether the processed data is stored in the Redis instance after the dataflow pipeline is executed successfully, you must first connect to the Redis instance from any Compute Engine VM instance located within the same project, region and network as the Redis instance.

1) Create a VM instance and SSH to it

2) Install telnet from apt-get in the VM instance
```
  sudo apt-get install telnet
```
3) From the VM instance, connect to the ip-address of the redis instance
```
  telnet instance-ip-address 6379
```
4) Once you are in the redis, check the keys inserted
```
  keys *
```
5) Check whether the data is inserted using the intersection command to get the guid
```
  sinter firstname:<firstname> lastname:<lastname> dob:<dob> postalcode:<post-code>
```
6) Check with individual entry using the below command
```
  smembers firstname:<firstname>
```
7) Command to clear the redis data store
```
  flushall
```

### References

https://redis.io/topics/data-types-intro 

https://beam.apache.org/documentation/programming-guide/

https://thedeveloperstory.com/2020/07/24/cloud-dataflow-a-unified-model-for-batch-and-streaming-data-processing/ 
