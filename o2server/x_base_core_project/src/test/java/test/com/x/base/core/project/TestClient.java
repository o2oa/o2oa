package test.com.x.base.core.project;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleScriptContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import com.x.base.core.project.tools.StringTools;
import com.x.base.core.project.utils.time.ClockStamp;

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

	@Test
	public void test3() throws Exception {
		File file = new File("D:/O2/o2oa/o2server/commons/mooToolsScriptText.js");
		String text = FileUtils.readFileToString(file, "utf-8");
		for (int i = 0; i < 10; i++) {
			ClockStamp.INIT(i, "1");
			ScriptContext scriptContext = new SimpleScriptContext();
			ClockStamp.STAMP("2");
			Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
			ClockStamp.STAMP("3");
			ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
			ClockStamp.STAMP("4");
			//ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
			ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("Nashorn");
			ClockStamp.STAMP("5");
			scriptEngine.eval(text, scriptContext);
			ClockStamp.STAMP("6");
			ClockStamp.TRACE();
		}
	}

	@Test
	public void test4() throws Exception {
		ClockStamp.INIT(-1, "1");
		File file = new File("D:/O2/o2oa/o2server/commons/mooToolsScriptText.js");
		ClockStamp.STAMP("2");
		String text = FileUtils.readFileToString(file, "utf-8");
		ClockStamp.STAMP("3");
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ClockStamp.STAMP("4");
		//		ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
		ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("Nashorn");
		ClockStamp.STAMP("5");
		CompiledScript script = ((Compilable) scriptEngine).compile(text);
		ClockStamp.STAMP("6");
		ClockStamp.TRACE();
		for (int i = 0; i < 10; i++) {
			ClockStamp.INIT(i, "1");
			ScriptContext scriptContext = new SimpleScriptContext();
			ClockStamp.STAMP("2");
			Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
			ClockStamp.STAMP("3");
			//scriptEngine.eval(text, scriptContext);
			script.eval(scriptContext);
			ClockStamp.STAMP("4");
			ClockStamp.TRACE();
		}
	}
}
