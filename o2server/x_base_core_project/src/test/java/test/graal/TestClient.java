package test.graal;

import javax.script.CompiledScript;
import javax.script.ScriptException;

public class TestClient {
	public static void main(String[] args) throws ScriptException {
		String text = "var x = 2 + 5; return x;";
		CompiledScript compiledScript = ScriptingFactory2.functionalizationCompile(text);
		Object o = compiledScript.eval();
		System.out.println(o);
	}

}
