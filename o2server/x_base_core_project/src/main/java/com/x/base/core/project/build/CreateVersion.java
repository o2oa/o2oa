package com.x.base.core.project.build;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.eclipse.jgit.api.DescribeCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DateTools;

public class CreateVersion {
	public static void main(String... args) throws Exception {
		File base = new File(args[0]);
		File file = new File(base, "version.o2");
		Map<String, String> map = fromGit(base);
		if (map.isEmpty()) {
			map = fromMaven(base);
		}
		FileUtils.write(file, XGsonBuilder.toJson(map), StandardCharsets.UTF_8);
	}

	private static Map<String, String> fromGit(File base) {
		Map<String, String> map = new LinkedHashMap<>();
		File dir = base.getParentFile().getAbsoluteFile();
		try (Git git = Git.init().setDirectory(dir).call()) {
			DescribeCommand describeCommand = git.describe().setTags(true).setAlways(true);
			String version = describeCommand.call();
			map.put("version", version);
			map.put("date", DateTools.format(new Date()));
			return map;
		} catch (GitAPIException e) {
			System.err.print("can not fetch git version, use maven version as tag.");
		}
		return new LinkedHashMap<>();
	}

	private static Map<String, String> fromMaven(File base) {
		Map<String, String> map = new LinkedHashMap<>();
		try {
			File pom = new File(base, "pom.xml");
			MavenXpp3Reader reader = new MavenXpp3Reader();
			Model model = reader.read(new FileReader(pom));
			map.put("version", model.getVersion() + "");
			map.put("date", DateTools.format(new Date()));
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new LinkedHashMap<>();
	}

}
