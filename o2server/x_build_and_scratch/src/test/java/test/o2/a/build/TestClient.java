package test.o2.a.build;

import org.junit.Test;

import com.x.base.core.project.tools.Crypto;

public class TestClient {

	@Test
	public void test() throws Exception {
		String key = "12366dev";
		String value = "YHf4fX6-AP2HGgNijGHDWmsY6EbeJJhQjSZFfQrWh1HzSoe6Nt0X7YqABjb2f-GI";

		System.out.println(Crypto.decrypt(value, key));
	}
}
