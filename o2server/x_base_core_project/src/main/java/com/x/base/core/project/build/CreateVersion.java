package com.x.base.core.project.build;

import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.LinkedHashMap;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.DefaultCharset;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

public class CreateVersion {
	public static void main(String... args) throws Exception {
		File base = new File(args[0]);
		File file = new File(base, "version.o2");
		File pom = new File(base, "pom.xml");

		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = reader.read(new FileReader(pom));

		LinkedHashMap<String, String> map = new LinkedHashMap<>();

		map.put("version", model.getVersion() + "");
		map.put("date", DateTools.format(new Date()));

		FileUtils.write(file, XGsonBuilder.toJson(map), DefaultCharset.charset_utf_8);

	}

}
