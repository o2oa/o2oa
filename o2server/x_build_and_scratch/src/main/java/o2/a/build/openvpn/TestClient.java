package o2.a.build.openvpn;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class TestClient {
	@Test
	public void test() throws Exception {

		Map<String, String> map = new HashMap<>();
		// map.put("4194302","255.192.0.0");
		// map.put("2097150","255.224.0.0");
		// map.put("1048574","255.240.0.0");
		// map.put("524286","255.248.0.0");
		// map.put("262142","255.252.0.0");
		// map.put("131070","255.254.0.0 ");
		// map.put("65536","255.255.0.0");
		// map.put("32766","255.255.128.0");
		// map.put("16382","255.255.192.0");
		// map.put("8190","255.255.224.0");
		// map.put("4094","255.255.240.0");
		// map.put("2046","255.255.248.0");
		// map.put("1022","255.255.252.0");
		// map.put("510","255.255.254.0");
		// map.put("254","255.255.255.0");
		// map.put("126","255.255.255.128");
		// map.put("62","255.255.255.192");
		// map.put("30","255.255.255.224");
		// map.put("14","255.255.255.240");
		// map.put("6","255.255.255.248");
		// map.put("2","255.255.255.252");
		map.put("4194304", "255.192.0.0");
		map.put("2097152", "255.224.0.0");
		map.put("1048576", "255.240.0.0");
		map.put("524288", "255.248.0.0");
		map.put("262144", "255.252.0.0");
		map.put("131072", "255.254.0.0");
		map.put("65536", "255.255.0.0");
		map.put("32768", "255.255.128.0");
		map.put("16384", "255.255.192.0");
		map.put("8192", "255.255.224.0");
		map.put("4096", "255.255.240.0");
		map.put("2048", "255.255.248.0");
		map.put("1024", "255.255.252.0");
		map.put("512", "255.255.254.0");
		map.put("256", "255.255.255.0");
		map.put("128", "255.255.255.128");
		map.put("64", "255.255.255.192");
		map.put("32", "255.255.255.224");
		map.put("16", "255.255.255.240");
		map.put("8", "255.255.255.248");
		map.put("4", "255.255.255.252");

		// URL url = new
		// URL("http://ftp.apnic.net/stats/apnic/delegated-apnic-latest");
		File file = new File("src/main/resources/delegated-apnic-latest.txt");
		System.out.println(file.getAbsolutePath());
		String txt = FileUtils.readFileToString(file);
		// System.out.println(txt);
		String[] lines = StringUtils.split(txt);
		for (String str : lines) {
			if (StringUtils.contains(str, "apnic|CN|ipv4|")) {
				String[] ps = StringUtils.split(str, "|");
				String host = ps[3];
				String mask = map.get(ps[4]);
				if (StringUtils.isEmpty(mask)) {
					throw new Exception("error:" + ps[4]);
				}
				// System.out.println("push \"route " + host + " " + mask + "
				// net_gateway\"");
				System.out.println("route " + host + " " + mask + " net_gateway");
			}
		}
	}

	@Test
	public void getName() {
		try {
			File file = new File("aaaaaa");
			System.out.println(file.getName());
			System.out.println(file.getParent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
