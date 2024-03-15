package com.x.program.center.jaxrs.bar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.validation.Bar;

class ActionCreateMass extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateMass.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, Integer count) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		mass(count);
		result.setData(wo);
		return result;
	}

	private void mass(Integer count) throws Exception {
		AtomicInteger totalSeed = new AtomicInteger();
		AtomicInteger sl1Seed = new AtomicInteger();
		AtomicInteger sl2Seed = new AtomicInteger();
		AtomicInteger sl3Seed = new AtomicInteger();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			for (int i = 1; i <= count; i++) {
				List<String> sl1 = new ArrayList<>();
				for (int j = 0; j < 10; j++) {
					sl1.add("sl1_" + sl1Seed.incrementAndGet());
				}
				List<String> sl2 = new ArrayList<>();
				for (int j = 0; j < 100; j++) {
					sl2.add("sl2_" + sl2Seed.incrementAndGet());
				}
				List<String> sl3 = new ArrayList<>();
				for (int j = 0; j < 1000; j++) {
					sl3.add("sl3_" + sl2Seed.incrementAndGet());
				}
				Bar bar = new Bar();
				String name = "name_" + totalSeed.incrementAndGet();
				bar.setName(name);
				bar.setSl1(sl1);
				bar.setSl2(sl2);
				bar.setSl3(sl3);
				emc.persist(bar);
				if ((i % 100) == 0) {
					sl1Seed.set(0);
					sl2Seed.set(0);
					sl3Seed.set(0);
					LOGGER.info("create bar mass:{}.", i);
					emc.beginTransaction(Bar.class);
					emc.commit();
				}
			}
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 1636014466988591350L;

	}

}
