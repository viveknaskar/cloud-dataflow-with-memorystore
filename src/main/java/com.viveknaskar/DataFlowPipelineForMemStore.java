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

public class DataFlowPipelineForMemStore {

    public static interface StorageToRedisOptions extends PipelineOptions {
        /**
         * Bucket where the text files are taken as input file
         */
        @Description("Path of the file to read from")
        @Default.String("DEFAULT")
        String getInputFile();
        void setInputFile(String value);

        /**
         * Memorystore/Redis instance host. Update with a running memorystore instance in the command-line to execute the pipeline
         */
        @Description("Redis host")
        @Default.String("DEFAULT")
        String getRedisHost();
        void setRedisHost(String value);

        /**
         * Memorystore/Redis instance port. The default port for Redis is 6379
         */
        @Description("Redis port")
        @Default.Integer(6379)
        Integer getRedisPort();
        void setRedisPort(Integer value);

    }

    public static void main(String[] args) {
        /**
         * Constructed StorageToRedisOptions object using the method PipelineOptionsFactory.fromArgs to read options from command-line
         */
        StorageToRedisOptions options = PipelineOptionsFactory.fromArgs(args)
                .withValidation()
                .as(StorageToRedisOptions.class);

        Pipeline p = Pipeline.create(options);
        p.apply("Reading Lines...", TextIO.read().from(options.getInputFile()))
                .apply("Transforming data...",
                        ParDo.of(new DoFn<String, String[]>() {
                            @ProcessElement
                            public void TransformData(@Element String line, OutputReceiver<String[]> out) {
                                String[] fields = line.split("\\|");
                                out.output(fields);
                            }
                        }))
                .apply("Processing data...",
                        ParDo.of(new DoFn<String[], KV<String, String>>() {
                            @ProcessElement
                            public void ProcessData(@Element String[] fields, OutputReceiver<KV<String, String>> out) {
                                if (fields[RedisIndex.GUID.getValue()] != null) {

                                    out.output(KV.of("firstname:"
                                            .concat(fields[RedisIndex.FIRSTNAME.getValue()]), fields[RedisIndex.GUID.getValue()]));

                                    out.output(KV.of("lastname:"
                                            .concat(fields[RedisIndex.LASTNAME.getValue()]), fields[RedisIndex.GUID.getValue()]));

                                    out.output(KV.of("dob:"
                                            .concat(fields[RedisIndex.DOB.getValue()]), fields[RedisIndex.GUID.getValue()]));

                                    out.output(KV.of("postalcode:"
                                            .concat(fields[RedisIndex.POSTAL_CODE.getValue()]), fields[RedisIndex.GUID.getValue()]));

                                }
                            }
                        }))
                .apply("Writing field indexes into redis",
                RedisIO.write().withMethod(RedisIO.Write.Method.SADD)
                        .withEndpoint(options.getRedisHost(), options.getRedisPort()));
        p.run();

    }

}