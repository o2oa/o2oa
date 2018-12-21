package test.com.x.base.connection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

public class TestClient {

	@Test
	public void test() throws Exception {
		String url = "http://dev.o2oa.io:20020/x_organization_assemble_authentication/jaxrs/authentication";
		List<NameValuePair> heads = new ArrayList<>();
		heads.add(new NameValuePair(HttpToken.X_Token, "TGzu9RzlNSKiKmp0wVDcEedUKdS6M0BL46GBeK0mokkCyEgxqBLr4w"));
		for (int i = 0; i < 10000; i++) {
			Date date = new Date();
			ActionResponse response = ConnectionAction.get(url, heads);
			long time = new Date().getTime() - date.getTime();
			if (time > 20000) {
				System.out.println(i + ":" + DateTools.format(date) + "elapsed:" + time);
			}
		}

	}

	@Test
	public void testLocal() throws Exception {
		String url = "http://127.0.0.1:20020/x_organization_assemble_authentication/jaxrs/authentication";
		List<NameValuePair> heads = new ArrayList<>();
		heads.add(new NameValuePair(HttpToken.X_Token, "_As7di3gCwTimjjL55fuZZXmus9kE3XwlL04Q5jXX4nsmj2nSOtafg"));
		for (int i = 0; i < 10000; i++) {
			Date date = new Date();
			ActionResponse response = ConnectionAction.get(url, heads);
			long time = new Date().getTime() - date.getTime();
			if (time > 20000) {
				System.out.println(i + ":" + DateTools.format(date) + "elapsed:" + time);
			}
		}

	}

	@Test
	public void test1() throws Exception {
		String url = "http://127.0.0.1:8080/examples/jsp/test.jsp";
		List<com.x.base.core.project.bean.NameValuePair> heads = new ArrayList<>();
		com.x.base.core.project.bean.NameValuePair pair = new NameValuePair("Content-Type",
				"application/x-www-form-urlencoded");
		heads.add(pair);
		String aaa = HttpConnection.postAsString(url, heads, "op=2&data=444");
		System.out.println(aaa);
	}

	@Test
	public void test2() throws Exception {
		String servercode = "qhI8uDxo";
		String serverpwd = "zRY3juPG";
		String srcnum = "3139";
		String desttype = "0";
		String dest = "18958143015";
		String message = "abc test你好";

		String time = com.x.base.core.project.tools.DateTools.format(new Date(),
				com.x.base.core.project.tools.DateTools.formatCompact_yyyyMMddHHmmss);
		String sign = org.apache.commons.codec.digest.DigestUtils.md5Hex(servercode + serverpwd + time);

		String url = "http://115.29.3.87/servlet/smsapi?method=send";
		java.util.List<com.x.base.core.project.bean.NameValuePair> heads = new java.util.ArrayList<>();
		heads.add(new com.x.base.core.project.bean.NameValuePair("Content-Type", "application/x-www-form-urlencoded"));
		String value = "servercode=" + servercode + "&time=" + time + "&sign=" + sign + "&srcnum=" + srcnum
				+ "&desttype=" + desttype + "&dest=" + dest + "&message=" + message;
		com.x.base.core.project.connection.HttpConnection.postAsString(url, heads, value);
	}

}