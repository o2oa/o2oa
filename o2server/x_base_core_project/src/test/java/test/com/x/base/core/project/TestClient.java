package test.com.x.base.core.project;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import com.x.base.core.project.tools.StringTools;

import test.com.x.base.connection.Foo;

public class TestClient {

	@Test
	public void test() throws Exception {
		Foo foo = new Foo();
		Field l = FieldUtils.getField(Foo.class, "list", true);
		Field s = FieldUtils.getField(Foo.class, "str", true);
		StringTools.replaceFieldValue(foo, l, "c", "o");
		StringTools.replaceFieldValue(foo, s, "s", "z");
		System.out.println(FieldUtils.readField(l, foo, true));
		System.out.println(FieldUtils.readField(s, foo, true));
	}

	@Test
	public void test1() throws Exception {
		String aaa = "d:\\a\\b\\c.d";
		System.out.println(FilenameUtils.getName(aaa));
	}

	@Test
	public void test2() {

		ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

	}
}
