# cloud-dataflow-with-memorystore


Command to execute to creating the template:

mvn compile exec:java -e -Dexec.mainClass=com.viveknaskar.DataFlowPipelineForMemStore -Dexec.args="--project=project-id --stagingLocation=gs://cloud-function-gcsbucket"/staging/ --tempLocation=gs://cloud-function-gcsbucket/tmp/ --runner=DataflowRunner --output=gs://cloud-function-gcsbucket"
