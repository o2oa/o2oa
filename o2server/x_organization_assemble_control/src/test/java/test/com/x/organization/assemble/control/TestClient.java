package test.com.x.organization.assemble.control;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class TestClient {

	@Test
	public void test_d() throws IOException {
		File file = new File("E:/icon_d.png");
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.println(icon);
	}

	@Test
	public void test_m() throws IOException {
		File file = new File("d:/icon48_appwx_logo.png");
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.println(icon);
	}

	@Test
	public void test_male() throws IOException {
		File file = new File("E:/icon_male.png");
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.println(icon);
	}

	@Test
	public void test_female() throws IOException {
		File file = new File("E:/icon_female.png");
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.println(icon);
	}

	@Test
	public void test_startImage() throws IOException {
		File file = new File("e:/startImage.png");
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.println(icon);
	}

	@Test
	public void test1() {
		List<String> list = new ArrayList<>();
		list.add("神州易桥信息服务股份有限公司");
		list.add("浙江兰德纵横");
		list.add("中國移動香港公司");
		list.add("演示-市文广新局");
		list.add("中国联通安徽省分公司");
		System.out.println(
				list.stream().sorted(Comparator.comparing(String::toString).reversed()).collect(Collectors.toList()));
	}

	@Test
	public void test2() {
		List<String> list = new ArrayList<>();
		list.add("aaa");
		list.add("bbb");
		list.add("ccc");
		list.add("ddd");
		list.add(2, "eee");
		System.out.println(list);
	}
	
	@Test
	public void test3() {
		String str = "12345678";
		System.out.println(str.substring(3));
	}

}
