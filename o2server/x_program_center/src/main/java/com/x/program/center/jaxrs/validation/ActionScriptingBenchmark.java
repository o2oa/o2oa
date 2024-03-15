package com.x.program.center.jaxrs.validation;

import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionScriptingBenchmark extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionScriptingBenchmark.class);

	private static final int WARMUP = 30;
	private static final int ITERATIONS = 10;

	private static final String PRIMESMAIN = "primesMain";

	public static final String SOURCE = "" + "var N = 2000;\n" + "var EXPECTED = 17393;\n" + "\n"
			+ "function Natural() {\n" + "    x = 2;\n" + "    return {\n"
			+ "        'next' : function() { return x++; }\n" + "};\n" + "}\n" + "\n"
			+ "function Filter(number, filter) {\n" + "    var self = this;\n" + "    this.number = number;\n"
			+ "    this.filter = filter;\n" + "    this.accept = function(n) {\n" + "      var filter = self;\n"
			+ "      for (;;) {\n" + "          if (n % filter.number === 0) {\n" + "              return false;\n"
			+ "          }\n" + "          filter = filter.filter;\n" + "          if (filter === null) {\n"
			+ "              break;\n" + "          }\n" + "      }\n" + "      return true;\n" + "    };\n"
			+ "    return this;\n" + "}\n" + "\n" + "function Primes(natural) {\n" + "    var self = this;\n"
			+ "    this.natural = natural;\n" + "    this.filter = null;\n" + "\n" + "    this.next = function() {\n"
			+ "        for (;;) {\n" + "            var n = self.natural.next();\n"
			+ "            if (self.filter === null || self.filter.accept(n)) {\n"
			+ "                self.filter = new Filter(n, self.filter);\n" + "                return n;\n"
			+ "            }\n" + "        }\n" + "    };\n" + "}\n" + "\n" + "function primesMain() {\n"
			+ "    var primes = new Primes(Natural());\n" + "    var primArray = [];\n"
			+ "    for (var i=0;i<=N;i++) { primArray.push(primes.next()); }\n"
			+ "    if (primArray[N] != EXPECTED) { throw new Error('wrong prime found: '+primArray[N]); }\n" + "}\n";

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws IOException, IllegalAccessException {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();

		Wo wo = new Wo();
		wo.setGraalPolyglot(benchGraalPolyglotContext());
		wo.setGraalScriptEngine(benchGraalScriptEngine());
		wo.setNashornScriptEngine(benchNashornScriptEngine());
		result.setData(wo);
		return result;
	}

	static long benchGraalPolyglotContext() throws IOException {
		LOGGER.print("Graal.js via org.graalvm.polyglot.Context");
		long sum = 0;
		try (Context context = Context.create()) {
			context.eval(Source.newBuilder("js", SOURCE, "src.js").build());
			Value primesMain = context.getBindings("js").getMember(PRIMESMAIN);
			LOGGER.print("warming up ...");
			for (int i = 0; i < WARMUP; i++) {
				primesMain.execute();
			}
			LOGGER.print("warmup finished, now measuring");
			for (int i = 0; i < ITERATIONS; i++) {
				long start = System.currentTimeMillis();
				primesMain.execute();
				long took = System.currentTimeMillis() - start;
				sum += took;
				LOGGER.print("iteration: " + took);
			}
		}
		return sum;
	}

	static long benchNashornScriptEngine() throws IllegalAccessException {
		LOGGER.print("=== Nashorn via javax.script.ScriptEngine ===");
		ScriptEngine nashornEngine = new ScriptEngineManager().getEngineByName("nashorn");
		if (nashornEngine == null) {
			throw new IllegalAccessException("Nashorn not found.");
		} else {
			return benchScriptEngineIntl(nashornEngine);
		}
	}

	static long benchGraalScriptEngine() throws IllegalAccessException {
		LOGGER.print("Graal.js via javax.script.ScriptEngine");
		ScriptEngine graaljsEngine = new ScriptEngineManager().getEngineByName("graal.js");
		if (graaljsEngine == null) {
			throw new IllegalAccessException("Graal.js not found.");
		} else {
			return benchScriptEngineIntl(graaljsEngine);
		}
	}

	private static long benchScriptEngineIntl(ScriptEngine eng) {
		long sum = 0L;
		try {
			eng.eval(SOURCE);
			Invocable inv = (Invocable) eng;
			LOGGER.print("warming up ...");
			for (int i = 0; i < WARMUP; i++) {
				inv.invokeFunction(PRIMESMAIN);
			}
			LOGGER.print("warmup finished, now measuring");
			for (int i = 0; i < ITERATIONS; i++) {
				long start = System.currentTimeMillis();
				inv.invokeFunction(PRIMESMAIN);
				long took = System.currentTimeMillis() - start;
				sum += took;
				LOGGER.print("iteration: " + (took));
			}
		} catch (Exception ex) {
			LOGGER.error(ex);
		}
		return sum;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = -1189545887919434403L;

		@FieldDescribe("graalPolyglot")
		private Long graalPolyglot;
		@FieldDescribe("graalScriptEngine")
		private Long graalScriptEngine;
		@FieldDescribe("nashornScriptEngine")
		private Long nashornScriptEngine;

		public Long getGraalPolyglot() {
			return graalPolyglot;
		}

		public void setGraalPolyglot(Long graalPolyglot) {
			this.graalPolyglot = graalPolyglot;
		}

		public Long getGraalScriptEngine() {
			return graalScriptEngine;
		}

		public void setGraalScriptEngine(Long graalScriptEngine) {
			this.graalScriptEngine = graalScriptEngine;
		}

		public Long getNashornScriptEngine() {
			return nashornScriptEngine;
		}

		public void setNashornScriptEngine(Long nashornScriptEngine) {
			this.nashornScriptEngine = nashornScriptEngine;
		}

	}

}
