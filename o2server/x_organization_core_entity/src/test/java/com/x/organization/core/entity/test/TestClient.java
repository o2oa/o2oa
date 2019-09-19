package com.x.organization.core.entity.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.enhance.PCEnhancer;
import org.apache.openjpa.lib.util.Options;
import org.junit.Test;

import com.x.organization.core.entity.accredit.Empower;

public class TestClient {

	@Test
	public void test() throws Exception {

		try {
			Options opts = new Options();
			opts.setFromCmdLine(new String[] { "-p",
					"D:/O2/o2oa/o2server/x_organization_core_entity/target/classes/META-INF/persistence.xml" });

			String cls = "D:/O2/o2oa/o2server/x_organization_core_entity/src/main/java/com/x/organization/core/entity/Group.java";
			PCEnhancer.run(new String[] { cls }, opts);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Test
	public void test1() throws Exception {
		Double d = 111.11;
		Long l = d.longValue();
		System.out.println(Double.parseDouble(l.toString()));
		System.out.println(l.doubleValue() == d);

	}

	@Test
	public void test3() {

		List<Empower> list = new ArrayList<>();

		Empower o1 = new Empower();
		Empower o2 = new Empower();
		Empower o3 = new Empower();
		Empower o4 = new Empower();
		o1.setType(Empower.TYPE_ALL);
		o2.setType(Empower.TYPE_APPLICATION);
		o3.setType(Empower.TYPE_PROCESS);
		o4.setType(Empower.TYPE_PROCESS);
		list.add(o1);
		list.add(o2);
		list.add(o3);
		list.add(o4);
		list.sort(new Comparator<Empower>() {
			public int compare(Empower o1, Empower o2) {
				if (StringUtils.equals(Empower.TYPE_PROCESS, o1.getType())) {
					return -1;
				} else if (StringUtils.equals(Empower.TYPE_PROCESS, o2.getType())) {
					return 1;
				} else if (StringUtils.equals(Empower.TYPE_APPLICATION, o1.getType())) {
					return -1;
				} else if (StringUtils.equals(Empower.TYPE_APPLICATION, o2.getType())) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		for (Empower o : list) {
			System.out.println(o.getType());
		}
	}

}
