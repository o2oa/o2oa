package com.x.base.core.project.build;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DefaultCharset;

public class PackageO2server {

	private static final Logger LOGGER = LoggerFactory.getLogger(PackageO2server.class);

	public static void main(String... args) throws Exception {
		File base = new File(args[0]).getParentFile();
		createO2Version(base.getAbsolutePath());
		CreateConfigSample.main(base.getAbsolutePath());
		CreateLocalSample.main(base.getAbsolutePath());
		CreateVersion.main(base.getAbsolutePath());
	}

	public static void compress(ZipOutputStream outputStream, String base, String path) throws Exception {
		File file = new File(base, path);
		if (!file.exists()) {
			System.out.println("file or directory not exist:" + path + ".");
			return;
		}
		if (file.isFile()) {
			outputStream.putNextEntry(new ZipEntry(path));
			byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			outputStream.write(bytes, 0, bytes.length);
		} else if (file.isDirectory()) {
			Path b = Paths.get(base);
			Files.walkFileTree(Paths.get(file.getAbsolutePath()), new SimpleFileVisitor<Path>() {
				public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
					try {
						Path p = b.relativize(file);
						outputStream.putNextEntry(new ZipEntry(p.toString()));
						byte[] bytes = Files.readAllBytes(file);
						outputStream.write(bytes, 0, bytes.length);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}

	public static void createO2Version(String base) throws Exception {

		File file = new File(base, "version.o2");

		FileUtils.write(file, DateTools.format(new Date(), DateTools.format_yyyyMMddHHmmss),
				DefaultCharset.charset_utf_8);

	}

}
