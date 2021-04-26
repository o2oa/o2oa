package com.x.server.console.action;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.SystemUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.JarTools;

/**
 * @author Zhou Rui
 */
public class UpdateFile extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(UpdateFile.class);

	private Date start;

	private void init() throws Exception {
		this.start = new Date();
	}

	public boolean execute(String path) {
		try {
			this.init();
			File file = new File(path);
			if (!file.exists() || file.isDirectory()) {
				logger.print("zip file not exist, path:{}.", path);
				return false;
			}
			logger.print("update from file:{}.", file.getAbsolutePath());
			this.unzip(file);
			this.updateShell();
			logger.print("update completed in {} seconds, restart server to continue update.",
					((new Date()).getTime() - start.getTime()) / 1000);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void updateShell() throws Exception {
		IOFileFilter filter = FileFilterUtils.or(new WildcardFileFilter("start_*.sh"),
				new WildcardFileFilter("start_*.bat"), new WildcardFileFilter("stop_*.sh"),
				new WildcardFileFilter("stop_*.bat"), new WildcardFileFilter("console_*.sh"),
				new WildcardFileFilter("console_*.bat"), new WildcardFileFilter("service_*.bat"));
		try (Stream<Path> stream = Files.list(Config.dir_local_update().toPath().resolve(Paths.get("o2server")))) {
			stream.filter(o -> filter.accept(o.toFile())).forEach(o -> {
				try {
					copy(o);
					Files.delete(o);
				} catch (Exception e) {
					logger.error(e);
				}
			});
		}
	}

	private void copy(Path source) throws Exception {
		Path target = Paths.get(Config.base(), source.getFileName().toString());
		if (Files.exists(target)) {
			Files.write(target, Files.readAllLines(source));
		} else {
			Files.copy(source, target);
		}
	}

	private void unzip(File file) throws Exception {
		File dir = Config.dir_local_update(true);
		FileUtils.cleanDirectory(dir);
		JarTools.unjar(file, "", dir, true);
		File dir_local = new File(dir, "local");
		if (dir_local.exists()) {
			FileUtils.forceDelete(dir_local);
		}
		File dir_config = new File(dir, "config");
		if (dir_config.exists()) {
			FileUtils.forceDelete(dir_config);
		}
		// 非windows设置解压文件权限
		if (!SystemUtils.IS_OS_WINDOWS) {
			Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
			Files.walk(dir.toPath()).forEach(p -> {
				try {
					Files.setPosixFilePermissions(p, permissions);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

	}
}