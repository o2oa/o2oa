package com.x.processplatform.core.entity.content.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.util.WorkLogTree;

public class TestClient {

	@Test
	public void test1() throws Exception {
		List<WorkLog> list = new ArrayList<>();

		WorkLog o_root = new WorkLog();
		o_root.setFromActivityToken("random");
		o_root.setArrivedActivityToken("root");

		WorkLog o_1 = new WorkLog();
		o_1.setFromActivityToken("root");
		o_1.setArrivedActivityToken("o_1");

		WorkLog o_1_1 = new WorkLog();
		o_1_1.setFromActivityToken("o_1");
		o_1_1.setArrivedActivityToken("o_1_1");

		WorkLog o_1_2 = new WorkLog();
		o_1_2.setFromActivityToken("o_1");
		o_1_2.setArrivedActivityToken("o_1_2");

		WorkLog o_2 = new WorkLog();
		o_2.setFromActivityToken("root");
		o_2.setArrivedActivityToken("o_2");

		WorkLog o_2_1 = new WorkLog();
		o_2_1.setFromActivityToken("o_2");
		o_2_1.setArrivedActivityToken("o_2_1");

		WorkLog o_2_2 = new WorkLog();
		o_2_2.setFromActivityToken("o_2");
		o_2_2.setArrivedActivityToken("o_2_2");

		WorkLog o_3 = new WorkLog();
		o_3.setFromActivityToken("root");
		o_3.setArrivedActivityToken("o_3");

		list.add(o_root);
		list.add(o_1);
		list.add(o_1_1);
		list.add(o_1_2);
		list.add(o_2_1);
		list.add(o_2_2);
		list.add(o_3);
		list.add(o_2);

		WorkLogTree tree = new WorkLogTree(list);

		System.out.println("root:" + tree.root());
		System.out.println("children:" + tree.children(o_2));
		System.out.println("parents:" + tree.parents(o_2_2));
		System.out.println("find:" + tree.find(o_2));
		// System.out.println("nodes:" + XGsonBuilder.toJson(tree.nodes()));

	}
}
