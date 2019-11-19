package com.x.server.console.action;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.JarTools;

public class ActionUpdateFile extends ActionUpdate {

	private static Logger logger = LoggerFactory.getLogger(ActionUpdateFile.class);

	private Date start;

	private void init() throws Exception {
		this.start = new Date();
	}

	public boolean execute(String path, boolean backup, String password) {
		try {
			this.init();
			if (!StringUtils.equals(Config.token().getPassword(), password)) {
				logger.print("password not mactch.");
				return false;
			}
			File file = new File(path);
			if (!file.exists() || file.isDirectory()) {
				logger.print("zip file not exist path:{}.", path);
				return false;
			}
			if (backup) {
				this.backup();
			}
			logger.print("update from file:{}.", file.getAbsolutePath());
			this.unzip(file);
			logger.print("update completed in {} seconds, restart server to continue update.",
					((new Date()).getTime() - start.getTime()) / 1000);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void backup() throws Exception {
		File dir = Config.dir_local_backup(true);
		String tag = DateTools.compact(new Date());
		File dest = new File(dir, tag + ".zip");
		logger.print("backup current version to {}.", dest.getAbsolutePath());
		List<File> files = new ArrayList<>();
		files.add(Config.dir_commons());
		files.add(Config.dir_config());
		files.add(Config.dir_configSample());
		files.add(Config.dir_localSample());
		files.add(Config.dir_jvm());
		files.add(Config.dir_servers());
		files.add(Config.dir_store());
		files.add(Config.dir_dynamic());
		files.add(Config.dir_custom());
		files.add(new File(Config.base(), "console.jar"));
		files.add(new File(Config.base(), "index.html"));
		files.add(new File(Config.base(), "version.o2"));
		FileFilter fileFilter = new RegexFileFilter(
				"^(start_|stop_|console_|service_)(aix|windows|linux|macos).(sh|bat)$");
		for (File _f : new File(Config.base()).listFiles(fileFilter)) {
			files.add(_f);
		}
		JarTools.jar(files, dest);
		logger.print("backup current version completed.");
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
	}
}