package org.example;

import org.example.protobuf.MyDataProto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import com.google.gson.*;


/**
 * JMH benchmark for writing serialized data.
 */
@State(Scope.Benchmark)
public class WriteDataTest {

    static final MyData md = new MyData(200,200);
    static final Gson gson = new Gson().newBuilder().create();

    // use a constant seed so the values should be the same from run to run in the benchmark.
    private final transient Random r = new Random(0xDEADBEEF);

    @Setup
    public void fillArray() {
        float[][] data = new float[md.data.length][md.data[0].length];
        for (int i = 0; i < md.data.length; i++) {
            for (int j = 0; j < md.data[0].length; j++) {
                data[i][j] = r.nextFloat(0, 100);
            }

        }
        md.data = data;
    }
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void writeJava(Blackhole bh) {
        // write java object
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("out.obj"))) {
            oos.writeObject(md);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void writeProtobuf(Blackhole bh) {
        // build the protobuf object, transforming the 2D array into a 1D List.
        MyDataProto.MyData mdpb = md.toProtobuf();
        try (FileOutputStream fw = new FileOutputStream("out.pb")) {
            mdpb.writeTo(fw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void writeGson(Blackhole bh) {
        String out = gson.toJson(md);
        try (FileOutputStream fos = new FileOutputStream("out.json")) {
            fos.write(out.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(WriteDataTest.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}