package com.x.base.core.project.scripting;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class TestClient {

	public static void main(String[] args) throws ScriptException {
		ScriptEngine engine = ScriptingFactory.newScriptEngine();
		Object so = engine.eval("'aaaa';");
		Object ao = engine.eval("[];");
		Object oo = engine.eval("{};");
		System.out.println(so.getClass() + ":" + so.toString());
		System.out.println(ao.getClass() + ":" + ao.toString());
		System.out.println(oo.getClass() + ":" + oo.toString());
	}

}
