# Llama3.java

Practical [Llama 3](https://github.com/meta-llama/llama3) inference implemented in a single Java file.

<p align="center">
  <img width="700" src="https://github.com/mukel/llama3.java/assets/1896283/7939588c-c0ff-4261-b67f-8a54bad59ab5">
</p>

This project is the successor of [llama2.java](https://github.com/mukel/llama2.java)
based on [llama2.c](https://github.com/karpathy/llama2.c) by [Andrej Karpathy](https://twitter.com/karpathy) and his [excellent educational videos](https://www.youtube.com/c/AndrejKarpathy).

Besides the educational value, this project will be used to test and tune compiler optimizations and features on the JVM, particularly for the [Graal compiler](https://www.graalvm.org/latest/reference-manual/java/compiler).

## Features

 - Single file, no dependencies
 - [GGUF format](https://github.com/ggerganov/ggml/blob/master/docs/gguf.md) parser
 - Llama 3 tokenizer based on [minbpe](https://github.com/karpathy/minbpe)
 - Llama 3 inference with Grouped-Query Attention
 - Support for Q8_0 and Q4_0 quantizations
 - Fast matrix-vector multiplication routines for quantized tensors using Java's [Vector API](https://openjdk.org/jeps/469)
 - Simple CLI with `--chat` and `--instruct` modes.

Here's the interactive `--chat` mode in action: 

<p align="center">
  <img width="700" src="https://github.com/mukel/llama3.java/assets/1896283/2245f59d-6c86-49c3-87d3-8b1a2cb83a91">
</p>

## Setup

Download pure `Q4_0` and (optionally) `Q8_0` quantized .gguf files from:  
https://huggingface.co/mukel/Meta-Llama-3-8B-Instruct-GGUF

The `~4.3GB` pure `Q4_0` quantized model is recommended, please be gentle with [huggingface.co](https://huggingface.co) servers: 
```
curl -L -O https://huggingface.co/mukel/Meta-Llama-3-8B-Instruct-GGUF/resolve/main/Meta-Llama-3-8B-Instruct-Q4_0.gguf

# Optionally download the Q8_0 quantized model ~8GB
# curl -L -O https://huggingface.co/mukel/Meta-Llama-3-8B-Instruct-GGUF/resolve/main/Meta-Llama-3-8B-Instruct-Q8_0.gguf
```

#### Optional: quantize to pure `Q4_0` manually

In the wild, `Q8_0` quantizations are fine, but `Q4_0` quantizations are rarely pure e.g. the `output.weights` tensor is quantized with `Q6_K`, instead of `Q4_0`.  
A **pure** `Q4_0` quantization can be generated from a high precision (F32, F16, BFLOAT16) .gguf source 
with the `quantize` utility from [llama.cpp](https://github.com/ggerganov/llama.cpp) as follows:

```bash
./quantize --pure ./Meta-Llama-3-8B-Instruct-F32.gguf ./Meta-Llama-3-8B-Instruct-Q4_0.gguf Q4_0
```

## Build and run

Java 21+ is required, in particular the [`MemorySegment` mmap-ing feature](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/channels/FileChannel.html#map(java.nio.channels.FileChannel.MapMode,long,long,java.lang.foreign.Arena)).

[`jbang`](https://www.jbang.dev/) is a perfect fit for this use case, just:
```
jbang Llama3.java --help
```
Or execute directly, also via [`jbang`](https://www.jbang.dev/):
```bash 
chmod +x Llama3.java
./Llama3.java --help
```

## Run from source

```bash
java --enable-preview --source 21 --add-modules jdk.incubator.vector LLama3.java -i --model Meta-Llama-3-8B-Instruct-Q4_0.gguf
```

#### Optional: Makefile + manually build and run

A simple [Makefile](./Makefile) is provided, run `make` to produce `llama3.jar` or manually:
```bash
javac -g --enable-preview -source 21 --add-modules jdk.incubator.vector -d target/classes Llama3.java
jar -cvfe llama3.jar Llama3 LICENSE -C target/classes .
```

Run the resulting `llama3.jar` as follows: 
```bash
java --enable-preview --add-modules jdk.incubator.vector -jar llama3.jar --help
```

## Performance

**Important Note**  
On GraalVM, please note that the Graal compiler doesn't support the Vector API yet, run with `-Dllama.VectorAPI=false`, but expect sub-optimal performance.   
Vanilla OpenJDK 21+ is recommended for now, which supports the Vector API.

### llama.cpp

Vanilla `llama.cpp` built with `make -j 20`.
```bash
./main --version
version: 2879 (4f026363)
built with cc (GCC) 13.2.1 20230801 for x86_64-pc-linux-gnu
```

Executed as follows:
```bash
./main -m ../Meta-Llama-3-8B-Instruct-Q4_0.gguf \
  -n 512 \
  -s 42 \
  -p "<|start_of_header_id|>user<|end_of_header_id|>Why is the sky blue?<|eot_id|><|start_of_header_id|>assistant<|end_of_header_id|>\n\n" \
  --interactive-specials
```
Collected the **"eval time"** metric in tokens\s.

### Llama3.java
Running on OpenJDK 21.0.2.

```bash
jbang Llama3.java \
  --model ./Meta-Llama-3-8B-Instruct-Q4_0.gguf \
  --max-tokens 512 \
  --seed 42 \
  --stream false \
  --prompt "Why is the sky blue?"
```

### Results

#### Notebook Intel 13900H 6pC+8eC/20T 64GB (5200) Linux 6.6.26 
| Model                            | tokens/s | Implementation   |  
|----------------------------------|----------|------------------|
| Llama-3-8B-Instruct-Q4_0.gguf    | 7.53     | llama.cpp        |
| Llama-3-8B-Instruct-Q4_0.gguf    | 6.95     | llama3.java      |
| Llama-3-8B-Instruct-Q8_0.gguf    | 5.16     | llama.cpp        |
| Llama-3-8B-Instruct-Q8_0.gguf    | 4.02     | llama3.java      |

#### Workstation AMD 3950X 16C/32T 64GB (3200) Linux 6.6.25

****Notes**  
*Running on a single CCD e.g. `taskset -c 0-15 jbang Llama3.java ...` since inference is constrained by memory bandwidth.* 

| Model                            | tokens/s | Implementation   |  
|----------------------------------|----------|------------------|
| Llama-3-8B-Instruct-Q4_0.gguf    | 9.26     | llama.cpp        |
| Llama-3-8B-Instruct-Q4_0.gguf    | 8.03     | llama3.java      |
| Llama-3-8B-Instruct-Q8_0.gguf    | 5.79     | llama.cpp        |
| Llama-3-8B-Instruct-Q8_0.gguf    | 4.92     | llama3.java      |

## License

MIT
