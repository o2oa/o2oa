package com.x.base.core.project.build;

import java.io.File;
import java.io.FileOutputStream;
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

	private static Logger logger = LoggerFactory.getLogger(PackageO2server.class);

	public static void main(String... args) throws Exception {
		File base = new File(args[0]).getParentFile();
		createO2Version(base.getAbsolutePath());
		CreateConfigSample.main(base.getAbsolutePath());
		CreateLocalSample.main(base.getAbsolutePath());
		File file = new File(base, "o2server.zip");
		FileUtils.forceMkdir(new File(base.getAbsolutePath(), "servers"));
		try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(file))) {
			compress(outputStream, base.getAbsolutePath(), "configSample");
			compress(outputStream, base.getAbsolutePath(), "jvm");
			compress(outputStream, base.getAbsolutePath(), "commons");
			compress(outputStream, base.getAbsolutePath(), "store");
			compress(outputStream, base.getAbsolutePath(), "localSample");
			compress(outputStream, base.getAbsolutePath(), "servers");
			compress(outputStream, base.getAbsolutePath(), "console.jar");
			compress(outputStream, base.getAbsolutePath(), "index.html");
			compress(outputStream, base.getAbsolutePath(), "service_windows.bat");
			compress(outputStream, base.getAbsolutePath(), "start_windows.bat");
			compress(outputStream, base.getAbsolutePath(), "start_windows_debug.bat");
			compress(outputStream, base.getAbsolutePath(), "stop_windows.bat");
			compress(outputStream, base.getAbsolutePath(), "console_windows.bat");
			compress(outputStream, base.getAbsolutePath(), "start_linux.sh");
			compress(outputStream, base.getAbsolutePath(), "start_linux_debug.sh");
			compress(outputStream, base.getAbsolutePath(), "stop_linux.sh");
			compress(outputStream, base.getAbsolutePath(), "console_linux.sh");
			compress(outputStream, base.getAbsolutePath(), "start_macos.sh");
			compress(outputStream, base.getAbsolutePath(), "start_macos_debug.sh");
			compress(outputStream, base.getAbsolutePath(), "stop_macos.sh");
			compress(outputStream, base.getAbsolutePath(), "console_macos.sh");
			compress(outputStream, base.getAbsolutePath(), "start_aix.sh");
			compress(outputStream, base.getAbsolutePath(), "start_aix_debug.sh");
			compress(outputStream, base.getAbsolutePath(), "stop_aix.sh");
			compress(outputStream, base.getAbsolutePath(), "console_aix.sh");
			compress(outputStream, base.getAbsolutePath(), "start_raspberrypi.sh");
			compress(outputStream, base.getAbsolutePath(), "start_raspberrypi_debug.sh");
			compress(outputStream, base.getAbsolutePath(), "stop_raspberrypi.sh");
			compress(outputStream, base.getAbsolutePath(), "console_raspberrypi.sh");
			compress(outputStream, base.getAbsolutePath(), "start_neokylin_loongson.sh");
			compress(outputStream, base.getAbsolutePath(), "start_neokylin_loongson_debug.sh");
			compress(outputStream, base.getAbsolutePath(), "stop_neokylin_loongson.sh");
			compress(outputStream, base.getAbsolutePath(), "console_neokylin_loongson.sh");
			compress(outputStream, base.getAbsolutePath(), "start_kylinos_phytium.sh");
			compress(outputStream, base.getAbsolutePath(), "start_kylinos_phytium_debug.sh");
			compress(outputStream, base.getAbsolutePath(), "stop_kylinos_phytium.sh");
			compress(outputStream, base.getAbsolutePath(), "console_kylinos_phytium.sh");
			compress(outputStream, base.getAbsolutePath(), "version.o2");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
