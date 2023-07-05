package com.x.processplatform.assemble.surface.jaxrs.read;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Read;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionManageListWithPerson extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManageListWithPerson.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String credential) throws Exception {

		LOGGER.debug("execute:{}, credential:{}.", effectivePerson::getDistinguishedName, () -> credential);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			if (BooleanUtils.isTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", ""))) {
				String person = business.organization().person().get(credential);
				if (StringUtils.isNotEmpty(person)) {
					List<Read> list = business.read().listWithPersonObject(person);
					List<Wo> wos = Wo.copier.copy(list);
					result.setData(wos);
					result.setCount((long) wos.size());
				}
			}
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.read.ActionManageListWithPerson$Wo")
	public static class Wo extends Read {

		private static final long serialVersionUID = -5642624566077026662L;

		static WrapCopier<Read, Wo> copier = WrapCopierFactory.wo(Read.class, Wo.class,
				JpaObject.singularAttributeField(Read.class, true, true), null);

	}

}
