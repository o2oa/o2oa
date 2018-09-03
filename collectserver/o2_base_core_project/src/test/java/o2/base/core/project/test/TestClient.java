package o2.base.core.project.test;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.base.core.project.tools.Crypto;

public class TestClient {

	@Test
	public void test() throws Exception {
		System.out.println(Crypto.decrypt("9Zl59bbBnLM", "v4o2servero2collect"));
	}

	@Test
	public void test1() throws Exception {
		String val = "123456789";
		System.out.println(StringUtils.substring(val, 0, 7));
	}

}
