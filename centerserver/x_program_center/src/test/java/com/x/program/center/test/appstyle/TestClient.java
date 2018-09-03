package com.x.program.center.test.appstyle;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class TestClient {

	@Test
	public void test_bas64_launch_logo() throws IOException {
		URL url = this.getClass().getClassLoader().getResource("launch_logo.png");
		File file = FileUtils.toFile(url);
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.print(icon);
	}

	@Test
	public void test_bas64_login_avatar() throws IOException {
		URL url = this.getClass().getClassLoader().getResource("login_avatar.png");
		File file = FileUtils.toFile(url);
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.print(icon);
	}

	@Test
	public void test_bas64_index_bottom_menu_logo_blur() throws IOException {
		URL url = this.getClass().getClassLoader().getResource("index_bottom_menu_logo_blur.png");
		File file = FileUtils.toFile(url);
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.print(icon);
	}

	@Test
	public void test_bas64_index_bottom_menu_logo_focus() throws IOException {
		URL url = this.getClass().getClassLoader().getResource("index_bottom_menu_logo_focus.png");
		File file = FileUtils.toFile(url);
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.print(icon);
	}

	@Test
	public void test_bas64_people_avatar_default() throws IOException {
		URL url = this.getClass().getClassLoader().getResource("people_avatar_default.png");
		File file = FileUtils.toFile(url);
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.print(icon);
	}
	
	@Test
	public void test_bas64_process_default() throws IOException {
		URL url = this.getClass().getClassLoader().getResource("process_default.png");
		File file = FileUtils.toFile(url);
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.print(icon);
	}

	@Test
	public void test_bas64_index_setup_about_logo() throws IOException {
		URL url = this.getClass().getClassLoader().getResource("setup_about_logo.png");
		File file = FileUtils.toFile(url);
		String icon = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
		System.out.print(icon);
	}

}
