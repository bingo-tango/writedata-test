package org.example;

import com.google.gson.Gson;
import org.example.protobuf.MyDataProto;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * JMH Benchmarks for reading. Prepend with 'X" so it runs after the 'write' benchmarks; JMH executes classes
 * in alphabetical order.
 */
public class XReadDataTest {

    static final Gson gson = new Gson().newBuilder().create();

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void readProtobuf(Blackhole bh) {
        try(FileInputStream fis = new FileInputStream("out.pb")) {
            MyDataProto.MyData mdp = MyDataProto.MyData.parseFrom(fis);
            MyData md = MyData.fromProtobuf(mdp);
            assert md.eo.data1 == 512;
            assert md.eo.data2 == 123;
            bh.consume(md.data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void readJava(Blackhole bh) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("out.obj"))) {
                MyData md = (MyData) ois.readObject();
            assert md.eo.data1 == 512;
            assert md.eo.data2 == 123;
                bh.consume(md.data);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void readGson(Blackhole bh) {

        try (FileReader fr = new FileReader("out.json")) {
            MyData md = gson.fromJson(fr, MyData.class);
            assert md.eo.data1 == 512;
            assert md.eo.data2 == 123;
            bh.consume(md.data);
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
