package o2.a.build.publish;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DateTools;

public class Publish {
	public static void main(String... args) throws Exception {
		String day = args[0];
		String dir = args[1];
		Date now = new Date();
		File file_x86 = new File(dir, "o2server_" + day + "_x86.zip");
		File file_macos = new File(dir, "o2server_" + day + "_macos.zip");
		File file_aix = new File(dir, "o2server_" + day + "_aix.zip");
		LinkedHashMap<String, Object> map_windows_linux = new LinkedHashMap<>();
		LinkedHashMap<String, Object> map_macos = new LinkedHashMap<>();
		LinkedHashMap<String, Object> map_aix = new LinkedHashMap<>();
		map_windows_linux.put("system", "x86");
		map_windows_linux.put("name", "o2server_" + day + "_windows/linux");
		map_windows_linux.put("fileName", FilenameUtils.getName(file_x86.getName()));
		map_windows_linux.put("fileSize", file_x86.length() / 1024 / 1024 + "MB");
		map_windows_linux.put("updateTime", DateTools.format(now));
		map_macos.put("system", "macos");
		map_macos.put("name", FilenameUtils.getBaseName(file_macos.getName()));
		map_macos.put("fileName", FilenameUtils.getName(file_macos.getName()));
		map_macos.put("fileSize", file_macos.length() / 1024 / 1024 + "MB");
		map_macos.put("updateTime", DateTools.format(now));
		map_aix.put("system", "aix");
		map_aix.put("name", FilenameUtils.getBaseName(file_aix.getName()));
		map_aix.put("fileName", FilenameUtils.getName(file_aix.getName()));
		map_aix.put("fileSize", file_aix.length() / 1024 / 1024 + "MB");
		map_aix.put("updateTime", DateTools.format(now));
		List<LinkedHashMap<String, Object>> list = new ArrayList<>();
		list.add(map_windows_linux);
		list.add(map_macos);
		list.add(map_aix);
		File file = new File(dir, "servers.json");
		FileUtils.writeStringToFile(file, XGsonBuilder.toJson(list));
	}
}
