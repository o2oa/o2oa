package test.graal;

import java.io.File;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

public class GraalJSEngineExample {

	public static void main(String[] args) {
		// 创建GraalVM上下文

		Predicate<String> predicate = new Predicate<>() {

			@Override
			public boolean test(String t) {
				System.out.println("!!!!!!!!!!!!!!!");
				System.out.println(t);
				System.out.println("!!!!!!!!!!!!!!!");
				if (StringUtils.equalsIgnoreCase(t, "java.io.file")) {
					return true;
				}
				return true;
			}
		};

		Context context = Context.newBuilder().allowHostClassLookup(predicate).allowAllAccess(true).build();

		// 编写JavaScript代码
		String jsCode = "var File = Java.type('java.io.File'); var x = new File('/data/Temp/aaa.txt'); print(x.getName());";

		// 解析JavaScript代码
		Source source = Source.create("js", jsCode);
		System.out.println("!!!!!!!!!!!!!!!11");
		System.out.println(source.getPath());
		System.out.println("!!!!!!!!!!!!!!!11");

		// 在GraalVM上下文中执行JavaScript代码
		Value result = context.eval(source);

		// 从JavaScript代码中获取结果
		int zValue = result.asInt();

		System.out.println("结果：" + zValue);
	}
}
