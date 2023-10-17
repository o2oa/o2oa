package com.x.general.assemble.control.jaxrs.securityclearance;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.general.assemble.control.Business;

/**
 * 客体不高于主体
 */
public class ActionObject extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionObject.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Integer limit = Config.ternaryManagement().getSystemSecurityClearance();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Person person = business.organization().person().getObject(effectivePerson.getDistinguishedName());
			if (null != person) {
				Integer subjectSecurityClearance = person.getSubjectSecurityClearance();
				if (null == subjectSecurityClearance) {
					subjectSecurityClearance = Config.ternaryManagement().getDefaultSubjectSecurityClearance();
				}
				limit = Math.min(subjectSecurityClearance, limit);
			}
		}
		final Predicate<Map.Entry<String, Integer>> predicate = (null != limit)
				? new PredicateWithLimitSecurityClearance(limit)
				: new PredicateWithoutLimitSecurityClearance();
		Config.ternaryManagement().getObjectSecurityClearance().entrySet().stream().filter(predicate::test)
				.sorted((o1, o2) -> Integer.compare(o1.getValue(), o2.getValue()))
				.forEach(o -> wo.put(o.getKey(), o.getValue()));
		result.setData(wo);
		return result;
	}

	public class Wo extends LinkedHashMap<String, Integer> {

		private static final long serialVersionUID = 1713071186691612472L;

	}

}
