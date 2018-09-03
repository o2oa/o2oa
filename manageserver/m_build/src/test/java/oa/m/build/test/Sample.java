package oa.m.build.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

public class Sample {

	// App ID: 10035151
	//
	// API Key: 2ke1ZzGFK8LXrvUkvEnoGcs0
	//
	// Secret Key: 2c777c5899de9da039c6c2e008bb0af5

	private static final String serverURL = "http://vop.baidu.com/server_api";
	private static String token = "";
	private static final String testFileName = "upload.wav";
	// put your own params here
	private static final String apiKey = "2ke1ZzGFK8LXrvUkvEnoGcs0";
	private static final String secretKey = "2c777c5899de9da039c6c2e008bb0af5";
	private static final String cuid = "10035151";

	public static void main(String[] args) throws Exception {
		getToken();
		method1();
		method2();
	}

	private static void getToken() throws Exception {
		String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" + "&client_id="
				+ apiKey + "&client_secret=" + secretKey;
		HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();
		token = new JSONObject(printResponse(conn)).getString("access_token");
	}

	private static void method1() throws Exception {
		File pcmFile = new File(testFileName);
		HttpURLConnection conn = (HttpURLConnection) new URL(serverURL).openConnection();

		// construct params
		JSONObject params = new JSONObject();
		params.put("format", "wav");
		params.put("rate", 8000);
		params.put("channel", "1");
		params.put("token", token);
		params.put("cuid", cuid);
		params.put("len", pcmFile.length());
		params.put("speech", DatatypeConverter.printBase64Binary(loadFile(pcmFile)));

		// add request header
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

		conn.setDoInput(true);
		conn.setDoOutput(true);

		// send request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(params.toString());
		wr.flush();
		wr.close();

		printResponse(conn);
	}

	private static void method2() throws Exception {
		File pcmFile = new File(testFileName);
		HttpURLConnection conn = (HttpURLConnection) new URL(serverURL + "?cuid=" + cuid + "&token=" + token)
				.openConnection();

		// add request header
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "audio/pcm; rate=8000");

		conn.setDoInput(true);
		conn.setDoOutput(true);

		// send request
		DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.write(loadFile(pcmFile));
		wr.flush();
		wr.close();

		printResponse(conn);
	}

	private static String printResponse(HttpURLConnection conn) throws Exception {
		if (conn.getResponseCode() != 200) {
			// request error
			return "";
		}
		InputStream is = conn.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer response = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
		System.out.println(new JSONObject(response.toString()).toString(4));
		return response.toString();
	}

	private static byte[] loadFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();
		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			is.close();
			throw new IOException("Could not completely read file " + file.getName());
		}

		is.close();
		return bytes;
	}
}
