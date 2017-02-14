package x_common_core_application.test;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.gson.XGsonBuilder;
import com.x.common.core.application.component.x_program_center;
import com.x.common.core.application.configuration.application.Application;
import com.x.common.core.application.configuration.application.Applications;
import com.x.common.core.application.definition.SsoDefinition;

public class TestClient {
	@Test
	public void test() {
		Gson gson = XGsonBuilder.instance();
		String str = null;
		JsonElement o = gson.fromJson(str, JsonElement.class);
		System.out.println(o);
	}

	@Test
	public void test1() throws Exception {
		Class clz = Class.forName("com.x.common.core.application.war." + "x_organization_assemble_custom");
		System.out.println(clz.getDeclaredField("type").get(null));
	}

	@Test
	public void test2() throws Exception {
		Applications as = new Applications();
		as.add(x_program_center.class, new Application());
		System.out.println(XGsonBuilder.toJson(as));
	}

	@Test
	public void test3() throws Exception {
		SsoDefinition o = new SsoDefinition();
		o.setKey("ddddddd");
		System.out.println(XGsonBuilder.toJson(o));
	}

	@Test
	public void test4() throws Exception {
		Foo foo = new Foo();
		foo.setName("!!!!!!!!!!!!!!!!!!!!!!");
		synchronized (foo.name) {
			foo.name = "@@@@@@@@@@@2";
		}
		System.out.println(foo.name);
	}
}
