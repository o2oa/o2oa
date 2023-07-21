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
		CreateConfigSample.main(base.getAbsolutePath());
		CreateLocalSample.main(base.getAbsolutePath());
		CreateVersion.main(base.getAbsolutePath());
	}

}
