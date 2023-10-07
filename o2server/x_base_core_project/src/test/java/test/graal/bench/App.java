/*
 * Copyright (c) 2018, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package test.graal.bench;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.Invocable;
import java.io.IOException;
import org.graalvm.polyglot.Source;

/**
 * Simple benchmark for Graal.js via GraalVM Polyglot Context and ScriptEngine.
 */
public class App {

    public static final int WARMUP = 30;
    public static final int ITERATIONS = 10;
    public static final String BENCHFILE = "src/bench.js";

    public static final String SOURCE = ""
            + "var N = 2000;\n"
            + "var EXPECTED = 17393;\n"
            + "\n"
            + "function Natural() {\n"
            + "    x = 2;\n"
            + "    return {\n"
            + "        'next' : function() { return x++; }\n"
            + "    };\n"
            + "}\n"
            + "\n"
            + "function Filter(number, filter) {\n"
            + "    var self = this;\n"
            + "    this.number = number;\n"
            + "    this.filter = filter;\n"
            + "    this.accept = function(n) {\n"
            + "      var filter = self;\n"
            + "      for (;;) {\n"
            + "          if (n % filter.number === 0) {\n"
            + "              return false;\n"
            + "          }\n"
            + "          filter = filter.filter;\n"
            + "          if (filter === null) {\n"
            + "              break;\n"
            + "          }\n"
            + "      }\n"
            + "      return true;\n"
            + "    };\n"
            + "    return this;\n"
            + "}\n"
            + "\n"
            + "function Primes(natural) {\n"
            + "    var self = this;\n"
            + "    this.natural = natural;\n"
            + "    this.filter = null;\n"
            + "\n"
            + "    this.next = function() {\n"
            + "        for (;;) {\n"
            + "            var n = self.natural.next();\n"
            + "            if (self.filter === null || self.filter.accept(n)) {\n"
            + "                self.filter = new Filter(n, self.filter);\n"
            + "                return n;\n"
            + "            }\n"
            + "        }\n"
            + "    };\n"
            + "}\n"
            + "\n"
            + "function primesMain() {\n"
            + "    var primes = new Primes(Natural());\n"
            + "    var primArray = [];\n"
            + "    for (var i=0;i<=N;i++) { primArray.push(primes.next()); }\n"
            + "    if (primArray[N] != EXPECTED) { throw new Error('wrong prime found: '+primArray[N]); }\n"
            + "}\n";

    public static void main(String[] args) throws Exception {
        benchGraalPolyglotContext();
        benchGraalScriptEngine();
        benchNashornScriptEngine();
    }

    static long benchGraalPolyglotContext() throws IOException {
        System.out.println("=== Graal.js via org.graalvm.polyglot.Context === ");
        long sum = 0;
        try (Context context = Context.create()) {
            context.eval(Source.newBuilder("js", SOURCE, "src.js").build());
            Value primesMain = context.getBindings("js").getMember("primesMain");
            System.out.println("warming up ...");
            for (int i = 0; i < WARMUP; i++) {
                primesMain.execute();
            }
            System.out.println("warmup finished, now measuring");
            for (int i = 0; i < ITERATIONS; i++) {
                long start = System.currentTimeMillis();
                primesMain.execute();
                long took = System.currentTimeMillis() - start;
                sum += took;
                System.out.println("iteration: " + took);
            }
        } // context.close() is automatic
        return sum;
    }

    static long benchNashornScriptEngine() throws IOException {
        System.out.println("=== Nashorn via javax.script.ScriptEngine ===");
        ScriptEngine nashornEngine = new ScriptEngineManager().getEngineByName("nashorn");
        if (nashornEngine == null) {
            System.out.println("*** Nashorn not found ***");
            return 0;
        } else {
            return benchScriptEngineIntl(nashornEngine);
        }
    }

    static long benchGraalScriptEngine() throws IOException {
        System.out.println("=== Graal.js via javax.script.ScriptEngine ===");
        ScriptEngine graaljsEngine = new ScriptEngineManager().getEngineByName("graal.js");
        if (graaljsEngine == null) {
            System.out.println("*** Graal.js not found ***");
            return 0;
        } else {
            return benchScriptEngineIntl(graaljsEngine);
        }
    }

    private static long benchScriptEngineIntl(ScriptEngine eng) throws IOException {
        long sum = 0L;
        try {
            eng.eval(SOURCE);
            Invocable inv = (Invocable) eng;
            System.out.println("warming up ...");
            for (int i = 0; i < WARMUP; i++) {
                inv.invokeFunction("primesMain");
            }
            System.out.println("warmup finished, now measuring");
            for (int i = 0; i < ITERATIONS; i++) {
                long start = System.currentTimeMillis();
                inv.invokeFunction("primesMain");
                long took = System.currentTimeMillis() - start;
                sum += took;
                System.out.println("iteration: " + (took));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return sum;
    }

}