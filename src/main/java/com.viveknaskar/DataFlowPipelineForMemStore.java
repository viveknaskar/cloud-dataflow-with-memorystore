package com.viveknaskar;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.redis.RedisIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataFlowPipelineForMemStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataFlowPipelineForMemStore.class);

    public static interface WordCountOptions extends PipelineOptions {
        @Description("Path of the file to read from")
        @Default.String("gs://cloud-function-gcsbucket/RedisFile.txt")
        String getInputFile();
        void setInputFile(String value);

        @Description("Path of the file to write to")
        @Default.String("gs://cloud-function-gcsbucket/outputRedisFile.txt")
        String getOutput();
        void setOutput(String value);

    }

    public static void main(String[] args) {
        WordCountOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(WordCountOptions.class);

        Pipeline p = Pipeline.create(options);
        p.apply("Reading Lines", TextIO.read().from(options.getInputFile()))
                .apply("Transforming data",
                        ParDo.of(new DoFn<String, String[]>() {
                            @ProcessElement
                            public void TransformData(@Element String line, OutputReceiver<String[]> out) {
                                LOGGER.info("line content: " + line);
                                String[] fields = line.split("\\|");
                                out.output(fields);
                            }
                        }))
                .apply("Processing data",
                        ParDo.of(new DoFn<String[], KV<String, String>>() {
                            @ProcessElement
                            public void ProcessData(@Element String[] fields, OutputReceiver<KV<String, String>> out) {
                                String guid = null;
                                String firstName = null;
                                String lastName = null;
                                String dob = null;
                                String postalCode = null;

                                for (String field : fields) {
                                    LOGGER.info("field data: " + field.toString() );
                                    String[] fieldKeyValue = field.split(":");
                                    if(fieldKeyValue.length == 2) {
                                        String key = fieldKeyValue[0].trim().toLowerCase();
                                        String value = fieldKeyValue[1].trim().toLowerCase();
                                        if(key.equals("guid")) {
                                            guid = value;
                                            LOGGER.info("found guid: "+guid);
                                        } else if(key.equals("firstname")) {
                                            firstName = value;
                                            LOGGER.info("found firstName: " + firstName);
                                        } else if(key.equals("lastname")) {
                                            lastName = value;
                                            LOGGER.info("found lastName: " + lastName);
                                        } else if(key.equals("dob")) {
                                            dob = value;
                                            LOGGER.info("found dob: " + dob);
                                        } else if(key.equals("postalcode")) {
                                            postalCode = value;
                                            LOGGER.info("found postalCode: " + postalCode);
                                        }
                                    }
                                }

                                if(guid != null) {
                                    out.output(KV.of("firstname:".concat(firstName), guid));
                                    out.output(KV.of("lastname:".concat(lastName), guid));
                                    out.output(KV.of("dob:".concat(dob), guid));
                                    out.output(KV.of("postalcode:".concat(postalCode), guid));
                                }
                            }
                        }))
                .apply(RedisIO.write().withMethod(RedisIO.Write.Method.SADD)
                        .withEndpoint("10.201.96.187", 6379));

        p.run();

    }

}