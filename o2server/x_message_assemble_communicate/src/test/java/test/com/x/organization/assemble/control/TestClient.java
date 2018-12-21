package test.com.x.organization.assemble.control;

import java.io.File;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.sun.javafx.tk.Toolkit.Task;
import com.x.base.core.project.gson.XGsonBuilder;

public class TestClient {

	@Test
	public void test_d() throws IOException {
		File file = new File("E:/icon_d.png");
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.println(icon);
	}

	@Test
	public void test_m() throws IOException {
		File file = new File("E:/icon_m.png");
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
	public void test_11() throws IOException {
		String str = null;
		System.out.println(XGsonBuilder.instance().fromJson(str, Task.class));
	}
}
