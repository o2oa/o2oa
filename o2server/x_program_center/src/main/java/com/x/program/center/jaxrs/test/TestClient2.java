package com.x.program.center.jaxrs.test;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import com.x.base.core.project.gson.XGsonBuilder;

public class TestClient2 {
	public static void main(String[] args) {
		try (Context context = Context.newBuilder("js").allowHostClassLookup(s -> true).allowHostAccess(HostAccess.ALL)
				.build()) {
			// 执行JavaScript代码
			// Value result = context.eval("js", "({name: 'John', age: 30})");
			 Value result = context.eval("js", "var a = []; a.push('aaa'); a.push('bbb'); a.push('ccc'); a;");
//			Value result = context.eval("js",
//					"Java.type('com.x.base.core.project.gson.XGsonBuilder').instance().toJsonTree(\"o\")");
			// Value result = context.eval("js", "'aaaaaaa'");
			System.out.println("类型1: " + result.isHostObject() + "->" + result.getMetaObject());
			if (!result.isHostObject()) {
				context.getBindings("js").putMember("o", result);
				Value v = context.eval("js", "JSON.stringify(o)");
				System.out.println(v);
			} else {
				System.out.println("OOOOOOOOOO" + XGsonBuilder.instance().toJson(result.asHostObject()));
			}
			// 检查返回值中的对象类型
			if (result.hasMembers()) {
				System.out.println("返回值是一个对象");
				for (String memberKey : result.getMemberKeys()) {
					Value memberValue = result.getMember(memberKey);
					System.out.println("属性名: " + memberKey + ", 类型: " + memberValue.getClass().getSimpleName());
				}
			} else {
				System.out.println("返回值不是一个对象");
				System.out.println("类型: " + result.getMetaObject());
			}
		}
	}
}
