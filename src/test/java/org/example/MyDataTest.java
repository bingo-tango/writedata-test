package org.example;

import org.example.protobuf.MyDataProto;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test case to make sure our methods work before we run benchmarks.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyDataTest {


    static int bins = 20000;
    static int frames = 20000;
    static final Random r = new Random();

    static MyData md = new MyData(bins, frames);
    @BeforeAll
    public static void setup() {
            float[][] data = new float[md.data.length][md.data[0].length];
            for (int i = 0; i < md.data.length; i++) {
                for (int j = 0; j < md.data[0].length; j++) {
                    data[i][j] = r.nextFloat(0, 100);
                }

            }
            md.data = data;
    }


    @AfterAll
    public static void cleanup() throws IOException {
//        Files.deleteIfExists(new File("out.pb").toPath());
    }
    @Test
    @Order(1)
    public void writeProto() {
        // build the protobuf object, transforming the 2D array into a 1D List.
        MyDataProto.MyData mdpb = md.toProtobuf();
        try (FileOutputStream fw = new FileOutputStream("out.pb")) {
            mdpb.writeTo(fw);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    public void readProto() {
        try(FileInputStream fis = new FileInputStream("out.pb")) {
            MyDataProto.MyData mdp = MyDataProto.MyData.parseFrom(fis);
            MyData md = MyData.fromProtobuf(mdp);
            assertEquals(bins, md.bins);
            assertEquals(frames, md.frames);
            assertEquals(bins, md.data.length);
            assertEquals(frames, md.data[0].length);
            assertEquals(this.md.data[3][4], md.data[3][4]);
            assert md.eo.data1 == 512;
            assert md.eo.data2 == 123;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
