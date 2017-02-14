package test.x.collaboration.service.message;

import org.junit.Test;

import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.connection.HttpConnection;

import x.collaboration.service.message.PushMessage;

public class TestClient {
	@Test
	public void test() throws Exception {
		PushMessage push = new PushMessage();
		HttpConnection.postAsString("http://collect.xplatform.tech/o2_collect/jaxrs/collect/pushmessage/transfer", null,
				XGsonBuilder.instance().toJson(push));

	}
}