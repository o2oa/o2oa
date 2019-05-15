package com.x.organization.service.express.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.x.organization.core.entity.accredit.Trust;

public class TestClient {

	@Test
	public void test() throws Exception {
		List<Trust> list = new ArrayList<>();
		Trust t1 = new Trust();
		t1.setApplication("applicationa");
		Trust t2 = new Trust();
		t2.setApplication("applicationb");
		t2.setProcess("processb");
		Trust t3 = new Trust();
		t3.setApplication("applicationc");
		t3.setProcess("processc");
		t3.setWhole(true);
		list.add(t1);
		list.add(t2);
		list.add(t3);
//		list.stream().sorted((o1, o2) -> {
//			if (StringUtils.isNotEmpty(o1.getProcess()) && StringUtils.isNotEmpty(o2.getProcess())) {
//				return 0;
//			} else if (StringUtils.isNotEmpty(o1.getProcess()) && StringUtils.isEmpty(o2.getProcess())) {
//				return -1;
//			} else if (StringUtils.isEmpty(o1.getProcess()) && StringUtils.isNotEmpty(o2.getProcess())) {
//				return 1;
//			} else if (StringUtils.isNotEmpty(o1.getApplication()) && StringUtils.isNotEmpty(o2.getApplication())) {
//				return 0;
//			} else if (StringUtils.isNotEmpty(o1.getApplication()) && StringUtils.isEmpty(o2.getApplication())) {
//				return -1;
//			} else if (StringUtils.isEmpty(o1.getApplication()) && StringUtils.isNotEmpty(o2.getApplication())) {
//				return 1;
//			} else {
//				return 1;
//			}
//		}).forEach(o -> {
//			System.out.println(o);
//		});
		Trust trust = null;
		out: for (;;) {
			for (Trust t : list) {
				if ((BooleanUtils.isNotTrue(t.getWhole())) && StringUtils.isNotEmpty(t.getProcess())) {
					trust = t;
					break out;
				}
			}
			for (Trust t : list) {
				if ((BooleanUtils.isNotTrue(t.getWhole())) && StringUtils.isNotEmpty(t.getApplication())) {
					trust = t;
					break out;
				}
			}
			for (Trust t : list) {
				if (BooleanUtils.isTrue(t.getWhole())) {
					trust = t;
					break out;
				}
			}
			break;
		}
		System.out.println(trust);
	}
}
