package com.x.query.core.entity.schema;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.openjpa.enhance.PCEnhancer;

import com.x.base.core.project.config.Config;

public class Enhance {

	private static final String DOT_CLASS = ".class";

	public static void main(String... args) throws Exception {

		Collection<File> files = FileUtils.listFiles(Config.dir_local_temp_dynamic_target(),
				FileFilterUtils.suffixFileFilter(DOT_CLASS), DirectoryFileFilter.INSTANCE);

		for (File f : files) {
			PCEnhancer.main(new String[] { f.getAbsolutePath() });
		}

	}

}
