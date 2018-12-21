package com.x.base.core.project.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;



public class TestClient {

	private static final int factor = 16;

	@Test
	public void test1() throws Exception {
		System.out.println(Integer.toBinaryString(0xff0000));
	}

	@Test
	public void test() throws Exception {

		File file = new File("d:/a.jpg");
		BufferedImage bufferedImage = ImageIO.read(file);
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		int r = 0;
		int g = 0;
		int b = 0;
		int rr = 0;
		int gg = 0;
		int bb = 0;
		List<String> list = new ArrayList<>();
		out: for (int w = 0; w < width; w++) {
			for (int h = 0; h < height; h++) {
				int pixel = bufferedImage.getRGB(w, h);
				System.out.println(Integer.toBinaryString(pixel));
				System.out.println(Integer.toBinaryString(pixel & 0xff0000));
				System.out.println(Integer.toBinaryString((pixel & 0xff0000) >> 16));
				if (1 == 1) {
					break out;
				}
				r = (pixel & 0xff0000) >> 16;
				// r = pixel >>> 16;
				g = (pixel & 0xff00) >> 8;
				b = (pixel & 0xff);
				rr = (((r + factor) / factor) * factor) - 1;
				gg = (((g + factor) / factor) * factor) - 1;
				bb = (((b + factor) / factor) * factor) - 1;
				list.add(rr + "," + gg + "," + bb);
				System.out.println("[" + r + "," + g + "," + b + "] --> [" + rr + "," + gg + "," + bb + "]");
			}
		}
		Map<String, Long> map = list.stream().collect(Collectors.groupingBy(p -> p, Collectors.counting()));
		Optional<Entry<String, Long>> o = map.entrySet().stream().max(Comparator.comparing(Entry::getValue));
		System.out.println(o.get().getKey() + ":" + o.get().getValue());
		String str = o.get().getKey();
		String[] rgb = str.split(",");
		String value = "#";
		value += Integer.toHexString(Integer.parseInt(rgb[0]));
		value += Integer.toHexString(Integer.parseInt(rgb[1]));
		value += Integer.toHexString(Integer.parseInt(rgb[2]));
		System.out.println(value);
		// ImageIO.write(scaled, "jpg", new File("d:/s.jpg"));

	}

	@Test
	public void test11() throws Exception {
		Date d1 = DateTools.parse("2018-01-01 00:00:00");
		System.out.println(d1.getTime());
		Date d2 = new Date();
		System.out.println(d1.getTime());
		System.out.println(d2.getTime());
		System.out.println(Integer.MAX_VALUE);
	}

	@Test
	public void test5() {
		List<Unit> units = new ArrayList<>();
		Unit u1 = new Unit();
		u1.setId("1111");
		Unit u2 = new Unit();
		u2.setId("2222");
		units.add(u1);
		units.add(u2);
		List<Person> list = new ArrayList<>();
		Person a1 = new Person();
		a1.setName("aaa");
		a1.setUnit("1111");
		Person a2 = new Person();
		a2.setName("bbb");
		a2.setUnit("2222");
		Person a3 = new Person();
		a3.setName("cccc");
		a3.setUnit("2222");
		list.add(a1);
		list.add(a2);
		list.add(a3);
		units = ListTools.groupStick(units, list, "id", "unit", "people");
		System.out.println(XGsonBuilder.toJson(units));
	}

	@Test
	public void test15() {
		List<Unit> units = new ArrayList<>();
		Unit u1 = new Unit();
		u1.setId("1111");
		Unit u2 = new Unit();
		u2.setId("2222");
		units.add(u1);
		units.add(u2);
		List<Person> list = new ArrayList<>();
		Person a1 = new Person();
		a1.setName("aaa");
		a1.setUnit("1111");
		Person a2 = new Person();
		a2.setName("bbb");
		a2.setUnit("2222");
		Person a3 = new Person();
		a3.setName("cccc");
		a3.setUnit("2222");
		Person a4 = new Person();
		a4.setName("bbb");
		a4.setUnit("");
		list.add(a1);
		list.add(a2);
		list.add(a3);
		list.add(a4);
		list = list.stream()
				.sorted(Comparator.comparing(Person::getName, StringTools.emptyLastComparator())
						.thenComparing(Person::getUnit, StringTools.emptyLastComparator()))
				.collect(Collectors.toList());
		System.out.println(XGsonBuilder.toJson(list));
	}

	public static class Unit {
		private List<Person> people;
		private String id;

		public List<Person> getPeople() {
			return people;
		}

		public void setPeople(List<Person> people) {
			this.people = people;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	public static class Person {
		private String unit;
		private String name;

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWcVZIS57VeOUzi8c01WKvwJK9uRe6hrGTUYmF6J/pI6/UvCbdBWCoErbzsBZOElOH8Sqal3vsNMVLjPYClfoDyYDaUlakP3ldfnXJzAFJVVubF53KadG+fwnh9ZMvxdh7VXVqRL3IQBDwGgzX4rmSK+qkUJjc3OkrNJPB7LLD8QIDAQAB";
	public static final String DEFAULT_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJZxVkhLntV45TOLxzTVYq/Akr25F7qGsZNRiYXon+kjr9S8Jt0FYKgStvOwFk4SU4fxKpqXe+w0xUuM9gKV+gPJgNpSVqQ/eV1+dcnMAUlVW5sXncpp0b5/CeH1ky/F2HtVdWpEvchAEPAaDNfiuZIr6qRQmNzc6Ss0k8HsssPxAgMBAAECgYAWtRy05NUgm5Lc6Og0jVDL/mEnydxPBy2ectwzHh2k7wIHNi8XhUxFki2TMqzrM9Dv3/LySpMl4AE3mhs34LNPy6F+MwyF5X7j+2Y6MflJyeb9HNyT++viysQneoOEiOk3ghxF2/GPjpiEF79wSp+1YKTxRAyq7ypV3t35fGOOEQJBANLDPWl8b5c3lrcz/dTamMjHbVamEyX43yzQOphzkhYsz4pruATzTxU+z8/zPdEqHcWWV39CP3xu3EYNcAhxJW8CQQC2u7PF5Xb1xYRCsmIPssFxil64vvdUadSxl7GLAgjQ9ULyYWB24KObCEzLnPcT8Pf2Q0YQOixxa/78FuzmgbyfAkA7ZFFV/H7lugB6t+f7p24OhkRFep9CwBMD6dnZRBgSr6X8d8ZvfrD2Z7DgBMeSva+OEoOtlNmXExZ3lynO9zN5AkAVczEmIMp3DSl6XtAuAZC9kD2QODJ2QToLYsAfjiyUwsWKCC43piTuVOoW2KUUPSwOR1VZIEsJQWEcHGDQqhgHAkAeZ7a6dVRZFdBwKA0ADjYCufAW2cIYiVDQBJpgB+kiLQflusNOCBK0FT3lg8BdUSy2D253Ih6l3lbaM/4M7DFQ";

	@Test
	public void test1111() throws Exception {
		String str = "a你好a";
		System.out.println(StringTools.utf8SubString(str, 2));
		System.out.println(StringTools.utf8SubString(str, 3));
		System.out.println(StringTools.utf8SubString(str, 4));
		System.out.println(StringTools.utf8SubString(str, 5));
		System.out.println(StringTools.utf8SubString(str, 6));
		System.out.println(StringTools.utf8SubString(str, 7));
		System.out.println(StringTools.utf8SubString(str, 8));
		System.out.println(StringTools.utf8SubString(str, 9));

	}

	@Test
	public void test3() {
		String sss = "{\"total\":{\"publishedCount\":24,\"errorCount\":15,\"安庆市分公司@27385a8e-87ac-4716-b20e-792d79d3d255@U\":{\"publishedCount\":3,\"桐城市分公司@a19025c0-ab78-4a7c-8029-1aa0d508dee7@U\":{\"publishedCount\":3}},\"合肥市分公司@bd773aea-76c3-47e0-9442-cda";
		System.out.println(StringTools.utf8Length(sss));
	}

	@Test
	public void test9() {
		TreeSet<String> list = new TreeSet<>();
		list.add("ccc");
		list.add("ccc");
		list.add("acc");
		list.add("dcc");
		list.add("bcc");
		list.add("ecc");
		for (String str : list) {
			System.out.println(str);
		}
	}

}
