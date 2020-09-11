package com.x.processplatform.assemble.surface.jaxrs.read;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.Task;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

class ActionManageListWithPerson extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String credential)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (BooleanUtils.isTrue(business.canManageApplication(effectivePerson, null))) {
				String person = business.organization().person().get(credential);
				if (StringUtils.isNotEmpty(person)) {
					List<Read> list = business.read().listWithPersonObject(person);
					List<Wo> wos = Wo.copier.copy(list);
					result.setData(wos);
					result.setCount((long)wos.size());
				}
			}
			return result;
		}
	}

	public static class Wo extends Read {

		static WrapCopier<Read, Wo> copier = WrapCopierFactory.wo(Read.class, Wo.class,
				JpaObject.singularAttributeField(Read.class, true, true), null);

	}

}
