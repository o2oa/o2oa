package com.x.base.core.project.build;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

public class CreateLocalSample {

	private static Logger logger = LoggerFactory.getLogger(CreateLocalSample.class);

	public static void main(String... args) throws Exception {
		File base = new File(args[0]);
		File dir = new File(base, "localSample");
		File node_cfg = new File(dir, "node.cfg");
		FileUtils.write(node_cfg, "127.0.0.1", DefaultCharset.charset);
	}
}
