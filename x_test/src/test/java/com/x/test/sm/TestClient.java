package com.x.test.sm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

public class TestClient {

	@Test
	public void test() throws Exception {
		HttpURLConnection connection = null;
		String str = "http://ums.zj165.com:8888/sms/Api/Send.do?SpCode=004796&LoginName=domino&Password=domino1234&MessageContent=短信内容&UserNumber=13336173316&SerialNumber=&ScheduleTime=&f=1";
		BufferedReader in = null;
		try {
			URL url = new URL(str);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// connection.setRequestProperty(JaxrsAttribute.x_token_key,
			// this.getKeyValue());
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			connection.setUseCaches(false);
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
			System.out.println(buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != connection) {
				connection.disconnect();
			}
			if (null != in) {
				in.close();
			}
		}

	}
}
