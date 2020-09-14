package com.x.base.core.project.tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;

public class CompressTools {

	private CompressTools() {
	}

	public static void unzip(Path zip, Path dir) throws IOException {
		try (ZipArchiveInputStream inputStream = new ZipArchiveInputStream(
				new BufferedInputStream(Files.newInputStream(zip)))) {
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}
			ZipArchiveEntry entry = null;
			while ((entry = inputStream.getNextZipEntry()) != null) {
				Path p = dir.resolve(Paths.get(entry.getName()));
				if (entry.isDirectory()) {
					if (!Files.exists(p)) {
						Files.createDirectories(p);
					}
				} else {
					if (!Files.exists(p)) {
						Files.createFile(p);
					}
					try (OutputStream os = Files.newOutputStream(p)) {
						IOUtils.copy(inputStream, os);
					}
				}
			}
		}
	}
}
