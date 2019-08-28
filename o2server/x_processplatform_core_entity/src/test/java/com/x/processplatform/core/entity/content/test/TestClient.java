package com.x.processplatform.core.entity.content.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Projection;
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
	public void test2() throws Exception {
		List<Projection> os = new ArrayList<>();
		Projection p1 = new Projection();
		p1.setApplication("a111");
		p1.setType("work");
		p1.setProcess("p111");
		Projection p2 = new Projection();
		p2.setApplication("a111");
		p2.setType("work");
		p2.setProcess("");
		Projection p3 = new Projection();
		p2.setApplication("a111");
		p2.setType("task");
		p2.setProcess("");
		os.add(p1);
		os.add(p2);
		os.add(p3);
		final List<Projection> list = new ArrayList<>();
		os.stream().collect(Collectors.groupingBy(o -> {
			return o.getApplication() + o.getType();
		})).forEach((k, v) -> {
			list.add(v.stream().filter(i -> StringUtils.isNotEmpty(i.getProcess())).findFirst().orElse(v.get(0)));
		});
		System.out.println(XGsonBuilder.toJson(list));
	}
}
