
# Data model read/write benchmark

This is a test to compare various methods of reading/writing a serialized data model object.

At present, it just compares Protobuf and plain Java serialization.

The data object is fairly simple, but the main feature is a 2D float array. Protobuf does not support multidimensional
arrays, and I was concerned about the overhead of creating the Protobuf object. Extra methods were added to transform the array, and this code was taken into account in the benchmark.

To run the benchmarks, use `.\gradlew.bat jmh` or `gradlew jmh` (Windows and MacOS).

The size of the test data can be changed in `org.example.WriteDataTest`.

The Protobuf source was generated using `protoc.exe --java_out=.\src\main\java data.proto`. If you want to regenerate it, you will need the [protoc compiler](https://github.com/protocolbuffers/protobuf) installed. 

# Results

The results listed below were on Windows, 12th Gen Intel(R) Core(TM) i9-12900K @ 3.20 GHz, 32 GB RAM, with an NVMe PC801 SK hynix 1TB SSD.

Protobuf object files are smaller, but by an insignificant amount (~ 2KB smaller regardless of array dimensions).

 - Warmup: 1 iterations, 1 s each, 2 calls per op
 - Measurement: 5 iterations, 10 s each
 - Timeout: 10 min per iteration
 - Threads: 1 thread, will synchronize iterations
 - Benchmark mode: Average time, time/op

### Array size [200]x[200] (~158KB file size)

| Benchmark                   | Mode | Cnt | Score    | Error   | Units | 
|:----------------------------|:-----|:----|:---------|:--------|:------|
| WriteDataTest.writeJava     | avgt | 10  | 4.274    | 0.039   | ms/op |
| WriteDataTest.writeProtobuf | avgt | 10  | 2.082    | 0.036   | ms/op |
| XReadDataTest.readJava      | avgt | 10  | 0.978    | 0.007   | ms/op |
| XReadDataTest.readProtobuf  | avgt | 10  | 0.242    | 0.004   | ms/op |

For small arrays, reading Protobuf is ~ 4x faster, and writing Protobuf is ~2x faster.

### Array size [2000]x[2000] (~15.6 MB file size)

| Benchmark                   | Mode | Cnt | Score   | Error | Units | 
|:----------------------------|:-----|:----|:--------|:------|:------|
| WriteDataTest.writeJava     | avgt | 10  | 266.322 | 5.331 | ms/op |
| WriteDataTest.writeProtobuf | avgt | 10  | 72.624  | 2.078 | ms/op |
| XReadDataTest.readJava      | avgt | 10  | 25.947  | 0.349 | ms/op |
| XReadDataTest.readProtobuf  | avgt | 10  | 22.227  | 0.119 | ms/op |

For medium arrays, reading Protobuf is about the same, and writing is 

### Array Size [20000]x[20000] (~1.56 GB file size)

| Benchmark                   | Mode | Cnt | Score     | Error   | Units | 
|:----------------------------|:-----|:----|:----------|:--------|:------|
| WriteDataTest.writeJava     | avgt | 10  | 26191.311 | 257.958 | ms/op |
| WriteDataTest.writeProtobuf | avgt | 10  | 7074.767  | 270.726 | ms/op |
| XReadDataTest.readJava      | avgt | 10  | 2162.306  | 99.673  | ms/op |
| XReadDataTest.readProtobuf  | avgt | 10  | 2852.764  | 214.737 | ms/op |


For large arrays, reading is faster in Java by ~1.3x, writing Protobuf is faster by ~3.7x

