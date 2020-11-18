package com.x.processplatform.service.processing.jaxrs.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.processplatform.core.entity.content.Work;

class ActionTest5 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			for (int i = 0; i < 10000; i++) {
				final String j = i + "";
				CompletableFuture<List<String>> f1 = CompletableFuture.supplyAsync(() -> {
					List<String> list = new ArrayList<>();
					try {
						for (Work work : emc.listNotEqual(Work.class, Work.activityToken_FIELDNAME, j)) {
							list.add(work.getProcess());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return list;
				});

				CompletableFuture<List<String>> f2 = CompletableFuture.supplyAsync(() -> {
					List<String> list = new ArrayList<>();
					try {
						emc.beginTransaction(Work.class);
						for (Work work : emc.listNotEqual(Work.class, Work.process_FIELDNAME, j)) {
							list.add(work.getProcess());
							work.setScratchString(j);
						}
						emc.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return list;
				});

				CompletableFuture<List<String>> f3 = CompletableFuture.supplyAsync(() -> {
					List<String> list = new ArrayList<>();
					try {
						emc.beginTransaction(Work.class);
						for (Work work : emc.listNotEqual(Work.class, Work.activity_FIELDNAME, j)) {
							list.add(work.getProcess());
							work.setScratchString(j);
						}
						emc.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return list;
				});

				CompletableFuture<List<String>> f4 = CompletableFuture.supplyAsync(() -> {
					List<String> list = new ArrayList<>();
					try {
						for (Work work : emc.listNotEqual(Work.class, Work.id_FIELDNAME, j)) {
							list.add(work.getProcess());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return list;
				});

				List<String> list1 = f1.get();
				List<String> list2 = f2.get();
				List<String> list3 = f3.get();
				List<String> list4 = f4.get();
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				System.out.println("loop:" + i + "list1:" + list1.size());
				System.out.println("loop:" + i + "list2:" + list2.size());
				System.out.println("loop:" + i + "list3:" + list3.size());
				System.out.println("loop:" + i + "list4:" + list4.size());
				System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			}

		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}
}