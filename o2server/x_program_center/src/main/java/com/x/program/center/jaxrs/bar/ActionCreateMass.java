package com.x.program.center.jaxrs.bar;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
		List<String> sl1 = IntStream.range(0, 100).boxed().map(o -> "sl1_" + Integer.toString(o))
				.collect(Collectors.toList());
		List<String> sl2 = IntStream.range(0, 1000).boxed().map(o -> "sl2_" + Integer.toString(o))
				.collect(Collectors.toList());
		List<String> sl3 = IntStream.range(0, 10000).boxed().map(o -> "sl3_" + Integer.toString(o))
				.collect(Collectors.toList());
		mass(count, sl1, sl2, sl3);
		result.setData(wo);
		return result;
	}

	private void mass(int count, List<String> sl1, List<String> sl2, List<String> sl3) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			for (int i = 0; i < count; i++) {
				Bar bar = new Bar();
				String name = "name_" + pad(i, 10);
				bar.setName(name);
				bar.setSl1(sl1);
				bar.setSl2(sl2);
				bar.setSl3(sl3);
				emc.persist(bar);
				if (i != 0 && ((i % 1000) == 0)) {
					LOGGER.info("create bar mass:{}.", i);
					emc.beginTransaction(Bar.class);
					emc.commit();
				}
			}
		}
	}

	public static String pad(int number, int paddedLength) {
		String numberStr = String.valueOf(number);
		int diff = paddedLength - numberStr.length();

		if (diff <= 0) {
			return numberStr; // Number length is greater or equal to padded length
		} else {
			StringBuilder paddedNumber = new StringBuilder();
			for (int i = 0; i < diff; i++) {
				paddedNumber.append("0"); // Add zeros to the beginning
			}
			paddedNumber.append(numberStr); // Append the number
			return paddedNumber.toString();
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 1636014466988591350L;

	}

}
