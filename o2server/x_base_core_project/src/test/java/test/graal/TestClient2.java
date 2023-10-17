package test.graal;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class TestClient2 {
	public static void main(String[] args) throws ScriptException {
		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		for (ScriptEngineFactory o : scriptEngineManager.getEngineFactories()) {
			System.out.println(o.getEngineName() + "-->" + o.getClass());
		}
//		com.oracle.truffle.js.scriptengine.GraalJSEngineFactory();
//		jdk.nashorn.api.scripting.NashornScriptEngineFactory();
	}

}
