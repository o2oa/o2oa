package com.x.organization.assemble.personal.jaxrs.signature;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Custom;
import com.x.organization.core.entity.Person;
import java.util.List;

class ActionManagerList extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManagerList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String personFlag) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName);
		if(effectivePerson.isNotManager()){
			throw new ExceptionAccessDenied(effectivePerson);
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Person person = business.person().pick(personFlag);
			if (null == person) {
				throw new ExceptionEntityNotExist(personFlag);
			}
			List<Custom> os = emc.listEqualAndEqual(Custom.class, Custom.person_FIELDNAME,
					person.getDistinguishedName(), Custom.name_FIELDNAME, CUSTOM_SIGNATURE_NAME);
			List<Wo> wos = Wo.copier.copy(os);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Custom {

		private static final long serialVersionUID = 4279205128463146835L;

		static WrapCopier<Custom, Wo> copier = WrapCopierFactory.wi(Custom.class, Wo.class, null,
				ListTools.toList(JpaObject.FieldsInvisible, Custom.name_FIELDNAME));

	}

}
