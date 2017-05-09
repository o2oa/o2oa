package test.com.x.base.connection;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;

public class TestClient {

	@Test
	public void test() throws Exception {
		Map<String, Object> body = new HashMap<>();
		body.put("name", "dev.ray.local");
		body.put("password", "1");
		body.put("unexpectedEorrorLogList", "[]");
		String url = "http://collect.xplatform.tech:20080/o2_collect_assemble/jaxrs/collect/unexpectederrorlog/receive";
		ActionResponse response = ConnectionAction.put(url, null, body);
		System.out.println(response.getData(WrapOutBoolean.class));
	}
	
	@Test
	public void test1(){
		System.out.println(Integer.MAX_VALUE);
	}
	

}