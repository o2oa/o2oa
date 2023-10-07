package test.graal;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class TestClient2 {
	public static void main(String[] args) throws ScriptException {
		String text = "var x = 2 + 5; return x;";
		ScriptEngine scriptEngine = ScriptingFactory2.scriptEngine();
		
		scriptEngine.eval(new FileReader)
	}

}
