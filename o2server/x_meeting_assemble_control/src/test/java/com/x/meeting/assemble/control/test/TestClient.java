package com.x.meeting.assemble.control.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.x.base.core.project.tools.ListTools;
import com.x.meeting.core.entity.Meeting;

public class TestClient {
	@Test
	public void test1() throws Exception {
		Meeting m1 = new Meeting();
		m1.setId("111");
		Meeting m2 = new Meeting();
		m1.setId("222");
		Meeting m3 = new Meeting();
		m1.setId("333");
		List<Meeting> list = new ArrayList<>();
		list.add(m1);
		list.add(m2);
		list.add(m3);
		System.out.println(ListTools.findWithProperty(list, "id", "111"));
	//	System.out.println(ListTools.findWithProperty(list, "id", "1111"));
	}
}
