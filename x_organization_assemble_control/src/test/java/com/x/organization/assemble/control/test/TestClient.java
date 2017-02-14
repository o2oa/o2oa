package com.x.organization.assemble.control.test;

import java.io.BufferedReader;
import java.util.Objects;

import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.base.core.Crypto;

public class TestClient {
	@Test
	public void test() {
		BufferedReader br = null;
		try {
			// String name;
			// br = new BufferedReader(new FileReader("names.txt"));
			// int i = 0;
			// while ((name = br.readLine()) != null) {
			// name = name + i;
			// String body = "{name:\"" + name + "\",password:\"1\",employee:\""
			// + (name + "测试" + i) + "\"}";
			// Request.Post("http://xa01.zoneland.net:9080/x_organization_assemble_control/jaxrs/person").bodyString(body,
			// ContentType.APPLICATION_JSON).execute().returnContent()
			// .asString();
			// System.out.println(name);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Test
	public void test1() throws Exception {
		String str = "1234567890";
		System.out.println(StringUtils.left(str, 8));
		System.out.println(Crypto.decrypt("I5m4vNOW00U", "xplatform111111111111111111"));
		System.out.println(Crypto.encrypt("8", "xplatformo2paltform"));

	}
}
