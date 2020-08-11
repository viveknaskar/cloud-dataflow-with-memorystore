# cloud-dataflow-with-memorystore

Java code to create dataflow pipeline that reads file data from a cloud storage, processes and outputs in memory store.


## Command to execute to creating the template:

mvn compile exec:java \
  -Dexec.mainClass=com.viveknaskar.DataFlowPipelineForMemStore \
  -Dexec.args="--project=your-project-id \
  --stagingLocation=gs://cloud-function-gcsbucket/staging/ \
  --dataflowJobFile=gs://dataflow-pipeline-staging/templates/dataflow-memorystore-template \
  --tempLocation=gs://cloud-function-gcsbucket/tmp/ \
  --runner=DataflowRunner"
