package test;

import org.junit.Test;

import com.x.base.core.Crypto;

public class TestClient {

	@Test
	public void test1() throws Exception {

		System.out.println(Crypto.encrypt("1", "o2platform11"));
		System.out.println(Crypto.encrypt("1", "o2platf1"));
	}

}
