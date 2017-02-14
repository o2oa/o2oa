package com.x.processplatform.core.entity.content.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Test;

import com.x.base.core.bean.NameIdPair;
import com.x.base.core.gson.XGsonBuilder;
import com.x.processplatform.core.entity.element.ExpireType;
import com.x.processplatform.core.entity.query.CalculateEntry;
import com.x.processplatform.core.entity.query.WhereEntry;

public class TestClient {
	@Test
	public void test3() throws Exception {
		System.out.println(Objects.equals(null, ExpireType.never));

	}

	@Test
	public void test() throws Exception {
		ExpireType type = null;
		switch (type) {
		case never:
			System.out.println("n");
			break;

		default:
			System.out.println("nnnnnnnnnnnnn");
			break;
		}

		System.out.println(Objects.equals(null, ExpireType.never));

	}

	@Test
	public void test1() throws Exception {
		String str = "[\"bbb\",\"ccc\"]";
		Object o = XGsonBuilder.instance().fromJson(str, Object.class);
		System.out.println(o.getClass());
	}

	@Test
	public void test2() {
		WhereEntry o = new WhereEntry();
		List<NameIdPair> list = new ArrayList<>();
		NameIdPair p = new NameIdPair("", "");
		list.add(p);
		o.setApplicationList(list);
		System.out.println(o);
	}

	@Test
	public void test4() {
		CalculateEntry o = new CalculateEntry();
		System.out.println(o);
	}
}
