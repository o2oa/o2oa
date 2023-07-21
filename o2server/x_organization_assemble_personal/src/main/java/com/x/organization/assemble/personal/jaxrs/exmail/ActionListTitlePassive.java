package com.x.organization.assemble.personal.jaxrs.exmail;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonExtend;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 
 * @author ray
 *
 */
class ActionListTitlePassive extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListTitlePassive.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		checkEnable();

		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();

		if ((!effectivePerson.isAnonymous()) && (!effectivePerson.isCipher())) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				wos = this.list(business, effectivePerson);
			}
		}

		result.setData(wos);
		return result;
	}

	private List<Wo> list(Business business, EffectivePerson effectivePerson) throws Exception {
		List<Wo> wos = new ArrayList<>();
		Person person = business.person().pick(effectivePerson.getDistinguishedName());
		PersonExtend personExtend = business.entityManagerContainer().firstEqualAndEqual(PersonExtend.class,
				PersonExtend.TYPE_FIELDNAME, ExtendExmail.TYPE, PersonExtend.PERSON_FIELDNAME, person.getId());
		if (null != personExtend) {
			ExtendExmail extendExmail = gson.fromJson(personExtend.getExtend(), ExtendExmail.class);
			for (String str : extendExmail.getTitleList()) {
				Wo wo = new Wo();
				wo.setValue(str);
				wos.add(wo);
			}
		}
		return wos;
	}

	@Schema(name = "com.x.organization.assemble.personal.jaxrs.exmail.ActionListTitlePassive$Wo")
	public static class Wo extends WrapString {

		private static final long serialVersionUID = -2531632901264624035L;

	}

}