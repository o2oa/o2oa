package com.x.base.core.project.scripting;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Scripting {

	public static final String ENGINE_NAME = "nashorn";

	private static ScriptEngineManager scriptEngineManager;

	public static ScriptEngine getEngine() {
		if (scriptEngineManager == null) {
			synchronized (Scripting.class) {
				if (scriptEngineManager == null) {
					scriptEngineManager = new ScriptEngineManager();
				}
			}
		}
		return scriptEngineManager.getEngineByName(ENGINE_NAME);
	}
}
