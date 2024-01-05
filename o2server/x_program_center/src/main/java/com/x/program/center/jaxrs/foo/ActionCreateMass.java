package com.x.program.center.jaxrs.foo;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.validation.Foo;

class ActionCreateMass extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateMass.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, Integer from, Integer count) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		mass(from, count);
		result.setData(wo);
		return result;
	}

	private void mass(int from, int count) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			for (int i = from; i < from + count; i++) {
				Foo foo = new Foo();
				String name = "name_" + pad(i, 10);
				int s1 = i % 10;
				int s2 = i % 100;
				int s3 = i % 1000;
				int s4 = i % 10000;
				foo.setName(name);
				foo.setS1("s1_" + s1);
				foo.setS2("s2_" + s2);
				foo.setS3("s3_" + s3);
				foo.setS4("s4_" + s4);
				emc.persist(foo);
				if (i != 0 && s4 == 0) {
					LOGGER.info("create foo mass:{}.", i);
					emc.beginTransaction(Foo.class);
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
