package com.x.base.core.project.utils.time;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.connection.FilePart;
import com.x.base.core.project.connection.FormField;

import org.apache.commons.io.FileUtils;

public class TestClient {
	public static void main(String... args) throws Exception {
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// c-token=jlMh2guoFias7Q1qQBSV6SdJ1SIVwfXgyzmmtJsPH9k
		// jsonData=4f7089f7-865a-4fa5-9107-069527a0650c
		List<FormField> fl = new ArrayList<>();
		FormField f = new FormField("jsonData", "4f7089f7-865a-4fa5-9107-069527a0650c");
		fl.add(f);
		List<FilePart> pl = new ArrayList<>();
		FilePart p = new FilePart("1.txt", FileUtils.readFileToByteArray(new File("d:/1.txt")), "text/plain", "file");
		pl.add(p);
		List<NameValuePair> nl = new ArrayList<>();
		nl.add(new NameValuePair("c-token", "jlMh2guoFias7Q1qQBSV6SdJ1SIVwfXgyzmmtJsPH9k"));
		byte[] bytes = ConnectionAction.postBinary(
				"http://114.116.108.117:20080/o2_collect_assemble/jaxrs/attachment/download/4f7089f7-865a-4fa5-9107-069527a0650c/post",
				nl, "{'ddd':'ddd'}");
		FileUtils.writeByteArrayToFile(new File("d:/2.jpg"), bytes);
	}
}