package com.x.message.assemble.communicate.jaxrs.connector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class TestClient {
	public static void main(String[] args) throws Exception {
		// String path ="/user/beifeng/mapreduce/wordcount/input/wc.input";
		// readFile(path);
		// 把/opt/modules/hadoop-2.5.0/wc.input 内容写入到 /user/beifeng/put-wc.input
		// write file
		FileInputStream fisin = null;
		FSDataOutputStream fsoutstream = null;
		try {
			String filename = "/ray/node.json";// 文件系统目录
			Path inputpath = new Path(filename);
			FileSystem fs = getFileSystem();
			fsoutstream = fs.create(inputpath);
			fisin = new FileInputStream(new File("/data/Temp/node.json"));// 本地系统目录文件
			IOUtils.copyBytes(fisin, fsoutstream, 4000, false);
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			IOUtils.closeStream(fsoutstream);
			IOUtils.closeStream(fisin);
		}
	}

	public static FileSystem getFileSystem() throws IOException {
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://127.0.0.1:9000");
		FileSystem fs = FileSystem.get(conf);
		return fs;
	}
}
