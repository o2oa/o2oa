package com.x.query.core.entity.schema;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.openjpa.enhance.PCEnhancer;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class Enhance {

	private static final String DOT_CLASS = ".class";

	private static Logger logger = LoggerFactory.getLogger(Enhance.class);

	public static void main(String... args) throws Exception {

		Collection<File> files = FileUtils.listFiles(new File(args[0]), FileFilterUtils.suffixFileFilter(DOT_CLASS),
				DirectoryFileFilter.INSTANCE);

		for (File f : files) {
			PCEnhancer.main(new String[] { f.getAbsolutePath() });
		}

	}

}
