package com.x.program.center.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.base.core.project.gson.XGsonBuilder;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

public class TestClient {

	@Test
	public void test7() throws Exception {
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("javascript");
		String str = "function aaa(){";
		str += StringUtils.LF;
		str += "var list = [{},{}]";
		str += StringUtils.LF;
		str += "return list";
		str += StringUtils.LF;
		str += "}";
		str += StringUtils.LF;
		str += "aaa()";
		Object obj = engine.eval(str);
		System.out.println(obj);
		System.out.println(obj.getClass().getName());
		ScriptObjectMirror som = (ScriptObjectMirror) obj;
		System.out.println(som.isArray());

		Object[] os = new Object[] {};
		ScriptObjectMirror.unwrapArray(os, som);
		System.out.println(os);
		for (Object o : os) {
			System.out.println(o);
		}
	}

	@Test
	public void test3() {

		Pattern REGEX = Pattern.compile("^\\/\\*(\\s|.)*?\\*\\/");

		String COMMENT = "/*" + StringUtils.LF;
		COMMENT += "* resources.getEntityManagerContainer(); // 实体管理容器." + StringUtils.LF;
		COMMENT += "* resources.getContext(); //上下文根." + StringUtils.LF;
		COMMENT += "* resources.getOrganization(); //组织访问接口." + StringUtils.LF;
		COMMENT += "* requestText //请求内容." + StringUtils.LF;
		COMMENT += "* request //请求对象." + StringUtils.LF;
		COMMENT += "*/adfasdfasdfasdf";
		Matcher m = REGEX.matcher(COMMENT);
		System.out.println(m.find());
		System.out.println(m.replaceFirst(""));
	}

	@Test
	public void test4() {

		List<Integer> list = new ArrayList<>();
		list.add(5);
		list.add(4);
		list.add(8);
		list.add(5);
		list.add(5);
		list.add(9);
		list = list.stream()
				.sorted(Comparator.comparing(Integer::intValue, Comparator.nullsLast(Integer::compareTo)).reversed())
				.collect(Collectors.toList());
		System.out.println(XGsonBuilder.toJson(list));

	}

}
