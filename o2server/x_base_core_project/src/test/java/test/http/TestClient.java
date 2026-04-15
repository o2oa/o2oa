package test.http;

import com.x.base.core.project.connection.HttpConnection;

public class TestClient {

//	const "; // 这个是专门的一个应用的 secret 
//    const tokenUrl = " " + app_o2oa_check_secret;
//    print("tokenUrl " + tokenUrl);
//    const tokenResult = com.x.base.core.project.connection.HttpConnection.getAsString(tokenUrl, null);
//    print("=====> token 返回： " + tokenResult);
	public static void main(String[] args) throws Exception {
		String key = "jn03Ub1QLq2rpBDaKzd16vPPonBNVVQjufGk_d74PcM";
		String url = " https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=wxe4338503a5176e16&corpsecret=" + key;
		String body = HttpConnection.getAsString(url, null);
		System.out.println("111" + body);
	}

}
