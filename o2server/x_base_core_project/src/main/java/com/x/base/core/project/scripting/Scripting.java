package com.x.base.core.project.scripting;

import javax.script.ScriptEngineManager;

import com.x.base.core.project.config.Config;

public class Scripting {

	private static ScriptEngineManager scriptEngineManager;

	public static ScriptingEngine getEngine() {
		if (scriptEngineManager == null) {
			synchronized (Scripting.class) {
				if (scriptEngineManager == null) {
					scriptEngineManager = new ScriptEngineManager();
				}
			}
		}
		ScriptingEngine engine = new ScriptingEngine(scriptEngineManager.getEngineByName(Config.SCRIPTING_ENGINE_NAME));
		return engine;
	}

}
