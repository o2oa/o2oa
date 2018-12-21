package com.x.base.core.project.tools;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

public class FileTools {

	public static String parent(String path) {
		int idx = StringUtils.lastIndexOfAny(path, new String[] { "\\", "/" });
		if (idx > 0) {
			return StringUtils.substring(path, 0, idx);
		} else {
			return path;
		}
	}

	public static File parent(File file) {
		return new File(parent(file.getAbsolutePath()));
	}

}
