package test;


import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.tools.Crypto;
import com.x.processplatform.core.entity.element.Form;

public class TestClient {

	@Test
	public void test1() throws Exception {

		System.out.println(Crypto.encrypt("1", "o2platform11"));
		System.out.println(Crypto.encrypt("1", "o2platf1"));
	}

	@Test
	public void test5() {
		// String sss =
		// "{\"total\":{\"publishedCount\":24,\"errorCount\":15,\"安庆市分公司@27385a8e-87ac-4716-b20e-792d79d3d255@U\":{\"publishedCount\":3,\"桐城市分公司@a19025c0-ab78-4a7c-8029-1aa0d508dee7@U\":{\"publishedCount\":3}},\"合肥市分公司@bd773aea-76c3-47e0-9442-cda";
		// System.out.println(StringTools.utf8Length(sss));

		//System.out.println(FieldUtils.get(Form.class, JpaObject.FIELDRESTRICTFLA  GS, true));
	}

}
