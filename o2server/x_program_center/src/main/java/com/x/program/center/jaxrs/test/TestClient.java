package com.x.program.center.jaxrs.test;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

public class TestClient {

	public static void main(String[] args) {
		try (Context context = Context.create()) {
			Value function = context.eval("js", "x => x+1");
			assert function.canExecute();
			int x = function.execute(41).asInt();
			System.out.println(x);
		}
	}

}
