package com.x.program.center.jaxrs.test;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.google.gson.JsonElement;

public class TestClient2 {
	public static void main(String[] args) {
		try (Context context = Context.create()) {
			// 执行JavaScript代码
			Value result = context.eval("js", "({name: 'John', age: 30})");
			// Value result = context.eval("js", "var a = []; a.push('aaa'); a.push('bbb');
			// a.push('ccc'); a;");
			// Value result = context.eval("js", "'aaaaaaa'");
			System.out.println("类型1: " + result.isMetaObject() + "->" + result.getMetaObject());
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
