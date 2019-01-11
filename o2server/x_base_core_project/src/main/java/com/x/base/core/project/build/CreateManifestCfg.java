package com.x.base.core.project.build;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class CreateManifestCfg {

	private static Logger logger = LoggerFactory.getLogger(CreateManifestCfg.class);

	public static void main(String[] args) throws Exception {
		File base = new File(args[0]);
		CreateManifestCfg creator = new CreateManifestCfg();
		creator.commonsExt(base);
		creator.storeJars(base);
		creator.store(base);
	}

	public void store(File base) throws Exception {
		File dir = new File(base, "store");
		if ((!dir.exists()) || (!dir.isDirectory())) {
			throw new Exception("error");
		}
		List<String> names = new ArrayList<>();
		for (File o : dir.listFiles()) {
			if ((!StringUtils.equals(o.getName(), "manifest.cfg")) && (!StringUtils.equals(o.getName(), "jars"))
					&& (!StringUtils.equals(o.getName(), ".gitignore"))) {
				if (StringUtils.isNotEmpty(o.getName())) {
					if ((!StringUtils.equals("x_report_assemble_control.war", o.getName()))
							&& (!StringUtils.equals("x_strategydeploy_assemble_control.war", o.getName()))) {
						names.add(o.getName());
					}
				}
			}
		}
		names = names.stream().sorted().collect(Collectors.toList());
		File file = new File(dir, "manifest.cfg");
		logger.print("create manifest.cfg, path:{}.", file.getAbsolutePath());
		FileUtils.writeLines(file, names);
	}

	public void storeJars(File base) throws Exception {
		File dir = new File(base, "store/jars");
		if ((!dir.exists()) || (!dir.isDirectory())) {
			throw new Exception("error");
		}
		List<String> names = new ArrayList<>();
		for (File o : dir.listFiles()) {
			if ((!StringUtils.equals(o.getName(), "manifest.cfg"))
					&& (!StringUtils.equals(o.getName(), ".gitignore"))) {
				if (StringUtils.isNotEmpty(o.getName())) {
					if ((!StringUtils.equals("x_report_core_entity.jar", o.getName()))
							&& (!StringUtils.equals("x_strategydeploy_core_entity.jar", o.getName()))) {
						names.add(o.getName());
					}
				}
			}
		}
		names = names.stream().sorted().collect(Collectors.toList());
		File file = new File(dir, "manifest.cfg");
		logger.print("create manifest.cfg, path:{}.", file.getAbsolutePath());
		FileUtils.writeLines(file, names);
	}

	public void commonsExt(File base) throws Exception {
		File dir = new File(base, "commons/ext");
		if ((!dir.exists()) || (!dir.isDirectory())) {
			throw new Exception("error");
		}
		List<String> names = new ArrayList<>();
		for (File o : dir.listFiles()) {
			if ((!StringUtils.equals(o.getName(), "manifest.cfg"))
					&& (!StringUtils.equals(o.getName(), ".gitignore"))) {
				if (StringUtils.isNotEmpty(o.getName())) {
					names.add(o.getName());
				}
			}
		}
		names = names.stream().sorted().collect(Collectors.toList());
		File file = new File(dir, "manifest.cfg");
		logger.print("create manifest.cfg, path:{}.", file.getAbsolutePath());
		FileUtils.writeLines(file, names);
	}
}