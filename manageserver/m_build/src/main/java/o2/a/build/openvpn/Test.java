package o2.a.build.openvpn;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class Test {
	public static void main(String[] args) throws Exception {
		String name = args[0];
		if (enable()) {
			Process pro = Runtime.getRuntime().exec("ipconfig /all");
			BufferedReader br = new BufferedReader(new InputStreamReader(pro.getInputStream(), "gbk"));
			List<String> lines = new ArrayList<>();
			String temp;
			while ((temp = br.readLine()) != null) {
				lines.add(temp);
			}
			String gateway = "";
			Pattern pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
			for (String string : lines) {
				if (string.indexOf("默认网关") != -1) {
					Matcher mc = pattern.matcher(string);
					if (mc.find()) {
						gateway = mc.group();
						break;
					}
				}
			}
			if (StringUtils.isNotEmpty(gateway)) {
				write_script(name, gateway);
			} else {
				clean_script(name);
			}
		} else {
			clean_script(name);
		}
	}

	private static void write_script(String name, String gateway) throws Exception {
		LinkedHashMap<String, String> map = cn_list();
		List<String> up = new ArrayList<>();
		List<String> down = new ArrayList<>();
		for (Entry<String, String> en : map.entrySet()) {
			up.add("route add " + en.getKey() + " mask " + en.getValue() + " " + gateway);
			down.add("route delete " + en.getKey());
		}
		File upFile = new File(name + "_up.bat");
		FileUtils.writeLines(upFile, up, false);
		File downFile = new File(name + "_down.bat");
		FileUtils.writeLines(downFile, down, false);
	}

	private static void clean_script(String name) throws Exception {
		File upFile = new File(name + "_up.bat");
		if (upFile.exists()){
			FileUtils.deleteQuietly(upFile);
		}
		File downFile = new File(name + "_down.bat");
		if (downFile.exists()){
			FileUtils.deleteQuietly(downFile);
		}
	}

	private static LinkedHashMap<String, String> cn_list() throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		Map<String, String> maskMapping = new HashMap<>();
		maskMapping.put("4194304", "255.192.0.0");
		maskMapping.put("2097152", "255.224.0.0");
		maskMapping.put("1048576", "255.240.0.0");
		maskMapping.put("524288", "255.248.0.0");
		maskMapping.put("262144", "255.252.0.0");
		maskMapping.put("131072", "255.254.0.0");
		maskMapping.put("65536", "255.255.0.0");
		maskMapping.put("32768", "255.255.128.0");
		maskMapping.put("16384", "255.255.192.0");
		maskMapping.put("8192", "255.255.224.0");
		maskMapping.put("4096", "255.255.240.0");
		maskMapping.put("2048", "255.255.248.0");
		maskMapping.put("1024", "255.255.252.0");
		maskMapping.put("512", "255.255.254.0");
		maskMapping.put("256", "255.255.255.0");
		maskMapping.put("128", "255.255.255.128");
		maskMapping.put("64", "255.255.255.192");
		maskMapping.put("32", "255.255.255.224");
		maskMapping.put("16", "255.255.255.240");
		maskMapping.put("8", "255.255.255.248");
		maskMapping.put("4", "255.255.255.252");
		File file = new File("delegated-apnic-latest.txt");
		String txt = FileUtils.readFileToString(file);
		String[] lines = StringUtils.split(txt);
		for (String str : lines) {
			if (StringUtils.contains(str, "apnic|CN|ipv4|")) {
				String[] ps = StringUtils.split(str, "|");
				String host = ps[3];
				String mask = maskMapping.get(ps[4]);
				if (StringUtils.isEmpty(mask)) {
					throw new Exception("error:" + ps[4]);
				}
				map.put(host, mask);
			}
		}
		return map;
	}

	private static Boolean enable() throws Exception {
		File file = new File("route.cfg");
		String str = FileUtils.readFileToString(file, "utf-8");
		return BooleanUtils.isTrue(BooleanUtils.toBoolean(StringUtils.trim(str)));
	}

}