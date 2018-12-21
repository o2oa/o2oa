package x_organization_assemble_authentication.test;

import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.x.base.core.project.message.Message;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.assemble.authentication.ThisApplication;

public class TestClient {

	@Test
	public void test3() throws Exception {
		String val = "蔡艳红#10000";
		byte[] bb = val.getBytes("utf8");
		byte[] cb = Crypto.encrypt(bb, "xplatform".getBytes());
		// String data = new String(cb, "utf-8");
		String data = new String(Base64.encodeBase64(cb), "utf-8");
		System.out.println("!!!!!!!!!@" + data);

		byte[] bs = Crypto.decrypt(Base64.decodeBase64(data), "xplatform".getBytes());
		String content = new String(bs, "utf-8");
		System.out.println(content);

	}

	@Test
	public void test4() throws Exception {
		System.out.println(Crypto.decrypt("3L583HhoElKrsx%2BqOLOeiWanrYdDRjwM", "strmgtuat"));
	}

}
