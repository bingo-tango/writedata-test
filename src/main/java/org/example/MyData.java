package org.example;

import org.example.protobuf.MyDataProto;

import java.io.Serializable;
import java.util.Random;

/**
 * <p>
 *     Data class to store 2D float data, and some other miscellaneous values.
 * </p>
 * <p>
 *     This is a POJO version of {@link org.example.protobuf.MyDataProto}, intended to be an easier to use version
 *     it, with methods to convert to and from Protobut. It can and should be improved for application used, but
 *     for benchmarking purposes it is OK.
 * </p>
 */
public class MyData implements Serializable {

    public float[][] data;

    public ExtraObj eo;

    public int frames;
    public int bins;


    /**
     * Create a new instance with data array dimensions [bins][frames]. This will allocate the array but not populate it.
     * @param bins number of bins
     * @param frames number of frames
     */
    public MyData(int bins, int frames) {
        float[][] array = new float[bins][frames];
        ExtraObj o = new ExtraObj();

        this.data = array;
        this.eo = o;
        this.bins = array.length;
        this.frames = array[0].length;
    }

    public MyDataProto.MyData toProtobuf() {
        MyDataProto.ExtraObject newEo = MyDataProto.ExtraObject.newBuilder().setData1(eo.data1).setData2(eo.data2).setValue(eo.value).build();
        MyDataProto.MyData.Builder builder = MyDataProto.MyData.newBuilder().setBins(bins).setFrames(frames).setExtra(newEo);

        for (int i = 0; i < bins; i++) {
            for (int j = 0; j < frames; j++) {
                builder.addData(data[i][j]);
            }
        }

        return builder.build();
    }

    public static MyData fromProtobuf(MyDataProto.MyData proto) {
        MyData md = new MyData(proto.getBins(), proto.getFrames());
        md.eo.data1 = proto.getExtra().getData1();
        md.eo.data2 = proto.getExtra().getData2();
        md.eo.value = proto.getExtra().getValue();
        for (int i = 0; i < md.bins; i++) {
            for (int j = 0; j < md.frames; j++) {
                md.data[i][j] = proto.getData(i * proto.getFrames() + j);
            }
        }
        return md;
    }
}
