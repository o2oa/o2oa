package test.graalvm;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;

public class TestClient {
	private static final String LANGUAGE_ID_JS = "js";

	public static void main(String[] args) {
		try (Context context = Context.newBuilder().engine(getEngine()).allowHostClassLoading(true).build()) {
			context.eval("js", "let x=5; x= x+6;");
		}
	}

	private static Engine getEngine() {
		return Engine.newBuilder(LANGUAGE_ID_JS).allowExperimentalOptions(true).option(LANGUAGE_ID_JS, LANGUAGE_ID_JS).build();
	}
}
