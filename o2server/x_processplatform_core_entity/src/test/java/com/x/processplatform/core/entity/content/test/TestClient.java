package com.x.processplatform.core.entity.content.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
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

	@Test
	public void test3() throws Exception {
		List<TaskCompleted> tcs = new ArrayList<>();
		TaskCompleted tc1 = new TaskCompleted();
		tc1.setId("tc1");
		tc1.setTask("t11");
		tcs.add(tc1);
		TaskCompleted tc2 = new TaskCompleted();
		tc2.setId("tc2");
		tc2.setTask("t2");
		tcs.add(tc2);
		TaskCompleted tc3 = new TaskCompleted();
		tc3.setId("tc3");
		tc3.setTask("t3");
		tcs.add(tc3);
		List<Task> ts = new ArrayList<>();
		Task t1 = new Task();
		t1.setId("t1");
		ts.add(t1);
		Task t2 = new Task();
		t2.setId("t2");
		ts.add(t2);
		Task t3 = new Task();
		t3.setId("t3");
		ts.add(t3);

		Map<TaskCompleted, Task> map = ListTools.pairWithProperty(tcs, "task", ts, "id");
		for (Entry<TaskCompleted, Task> en : map.entrySet()) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println(en.getKey());
			System.out.println(en.getValue());
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		}
	}
}
