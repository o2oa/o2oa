package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Identity;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

class ActionGetCreatableIdentity extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetCreatableIdentity.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String processFlag)
			throws Exception {
		LOGGER.debug("execute:{}, processFlag:{}.", effectivePerson::getDistinguishedName, () -> processFlag);
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Identity> identityList = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Process process = business.process().pick(processFlag);
			if (null == process) {
				throw new ExceptionProcessNotExist(processFlag);
			}
			if (StringUtils.isNotEmpty(process.getEdition())
					&& BooleanUtils.isFalse(process.getEditionEnable())) {
				process = business.process().pickEnabled(process.getApplication(), process.getEdition());
			}
			Application application = business.application().pick(process.getApplication());
			List<String> roles = business.organization().role().listWithPerson(effectivePerson);
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
			if (!business.application().allowRead(effectivePerson, roles, identities, units, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(), application.getId());
			}

			for (String identity : identities){
				units = business.organization().unit().listWithIdentitySupNested(identity);
				List<String> groups = business.organization().group().listWithIdentity(identities);
				if (business.process().startable(effectivePerson, List.of(identity), units, groups, process)) {
					identityList.add(business.organization().identity().getObject(identity));
				}
			}
		}

		List<Wo> wos = Wo.copier.copy(identityList);
		result.setData(wos);
		return result;
	}

	@Schema(description = "com.x.processplatform.assemble.surface.jaxrs.work.ActionGetCreatableIdentity$Wo")
	public static class Wo extends Identity {

		private static final long serialVersionUID = 2349788468804225969L;

		static WrapCopier<Identity, Wo> copier = WrapCopierFactory.wo(Identity.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
