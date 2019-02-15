package com.x.base.core.project.scripting;

import javax.script.ScriptEngineManager;

public class Scripting {

	public static final String ENGINE_NAME = "JavaScript";
	private static ScriptEngineManager scriptEngineManager;

	public static ScriptingEngine getEngine() {
		if (scriptEngineManager == null) {
			synchronized (Scripting.class) {
				if (scriptEngineManager == null) {
					scriptEngineManager = new ScriptEngineManager();
				}
			}
		}
		ScriptingEngine engine = new ScriptingEngine(scriptEngineManager.getEngineByName(ENGINE_NAME));
		return engine;
	}

}
