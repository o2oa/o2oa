package o2.collect.assemble.sms.code;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DateTools;

import o2.collect.assemble.sms.SmsMessage;
import open189.sign.ParamsSign;

public class SmsCodeSenderOpen189 extends SmsCodeSender {
	private static final String APP_ID = "403283410000251806";// 应用ID------登录平台在应用设置可以找到
	private static final String APP_SECRET = "92eefdd90901c8061109d7756d3ce95b";// 应用secret-----登录平台在应用设置可以找到
	private static final String address_token = "http://api.189.cn/v2/dm/randcode/token";
	private static final String address_access_token = "https://oauth.api.189.cn/emp/oauth2/v3/access_token";
	private static final String address_send = "http://api.189.cn/v2/dm/randcode/sendSms";

	private TypeToken<Map<String, String>> resultTypeToken = new TypeToken<Map<String, String>>() {
	};

	public String send(SmsMessage message) throws Exception {
		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		paramsMap.put("app_id", APP_ID);
		paramsMap.put("access_token", this.getAccessToken());
		paramsMap.put("timestamp", DateTools.format(new Date()));
		paramsMap.put("token", this.getToken(paramsMap));
		paramsMap.put("randcode", message.getMessage());
		paramsMap.put("phone", message.getMobile());
		paramsMap.put("exp_time", "30");
		return this.send(paramsMap);
	}

	public String getToken(TreeMap<String, String> paramsMap) throws Exception {
		String address = address_token + "?app_id=" + APP_ID + "&access_token=" + paramsMap.get("access_token")
				+ "&timestamp=" + URLEncoder.encode(paramsMap.get("timestamp"), "utf-8") + "&sign="
				+ ParamsSign.value(paramsMap, APP_SECRET);
		URL url = new URL(address);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setUseCaches(false);
		connection.setDoOutput(false);
		connection.setDoInput(true);
		connection.connect();
		try (InputStream input = connection.getInputStream(); InputStreamReader reader = new InputStreamReader(input)) {
			Map<String, String> map = XGsonBuilder.instance().fromJson(reader, resultTypeToken.getType());
			return map.get("token");
		}
	}

	public String getAccessToken() throws Exception {
		String data = "grant_type=client_credentials&app_id=" + APP_ID + "&app_secret=" + APP_SECRET;
		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, new TrustManager[] { new MyTrustManager() }, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
		HttpsURLConnection conn = (HttpsURLConnection) new URL(address_access_token).openConnection();
		conn.setUseCaches(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.connect();
		try (OutputStream output = conn.getOutputStream()) {
			IOUtils.write(data, output, "utf-8");
		}
		try (InputStream input = conn.getInputStream(); InputStreamReader reader = new InputStreamReader(input)) {
			Map<String, String> map = XGsonBuilder.instance().fromJson(reader, resultTypeToken.getType());
			return map.get("access_token");
		}
	}

	public String send(TreeMap<String, String> paramsMap) throws Exception {
		String data = "app_id=" + paramsMap.get("app_id") + "&access_token=" + paramsMap.get("access_token") + "&token="
				+ paramsMap.get("token") + "&phone=" + paramsMap.get("phone") + "&randcode=" + paramsMap.get("randcode")
				+ "&exp_time=" + paramsMap.get("exp_time") + "&timestamp=" + paramsMap.get("timestamp") + "&sign="
				+ ParamsSign.value(paramsMap, APP_SECRET);
		URL url = new URL(address_send);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setUseCaches(false);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.connect();
		try (OutputStream output = conn.getOutputStream()) {
			IOUtils.write(data, output, "utf-8");
		}
		try (InputStream input = conn.getInputStream(); InputStreamReader reader = new InputStreamReader(input)) {
			Map<String, String> map = XGsonBuilder.instance().fromJson(reader, resultTypeToken.getType());
			return map.get("identifier");
		}
	}

	private class MyHostnameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	private class MyTrustManager implements X509TrustManager {
		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
