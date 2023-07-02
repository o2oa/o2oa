package com.x.organization.assemble.personal.jaxrs.exmail;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapCount;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author ray
 *
 */
class ActionNewCountPassive extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionNewCountPassive.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		checkEnable();

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();

		if ((!effectivePerson.isAnonymous()) && (!effectivePerson.isCipher())) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				wo = this.get(business, effectivePerson);
			}
		}

		result.setData(wo);
		return result;
	}

	private Wo get(Business business, EffectivePerson effectivePerson) throws Exception {
		Wo wo = new Wo();
		wo.setCount(0L);
		Person person = business.person().pick(effectivePerson.getDistinguishedName());
		if (null != person) {
			PersonAttribute attribute = business.entityManagerContainer().firstEqualAndEqual(PersonAttribute.class,
					PersonAttribute.name_FIELDNAME, Config.exmail().getPersonAttributeNewCountName(),
					PersonAttribute.person_FIELDNAME, person.getId());
			if (null != attribute) {
				List<String> list = attribute.getAttributeList();
				if (ListTools.isNotEmpty(list) && (NumberUtils.isCreatable(list.get(0)))) {
					wo.setCount(NumberUtils.toLong(list.get(0)));
				}
			}
		}
		return wo;
	}

	@Schema(name = "com.x.organization.assemble.personal.jaxrs.exmail.ActionNewCountPassive$Wo")
	public static class Wo extends WrapCount {

		private static final long serialVersionUID = -1043884521557551846L;
	}

}