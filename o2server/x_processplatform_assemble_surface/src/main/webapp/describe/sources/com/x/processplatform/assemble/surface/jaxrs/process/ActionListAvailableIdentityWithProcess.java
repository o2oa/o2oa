package com.x.processplatform.assemble.surface.jaxrs.process;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Identity;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Process;

public class ActionListAvailableIdentityWithProcess extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Process process = business.process().pick(flag);
			if (null == process) {
				throw new ExceptionEntityNotExist(flag, Process.class);
			}
			List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
			List<String> dns = new ArrayList<>();
			if ((ListTools.isEmpty(process.getStartableIdentityList()))
					&& (ListTools.isEmpty(process.getStartableUnitList()))) {
				/** 没有设置可启动人员,所有人都可以启动 */
				dns.addAll(identities);
			} else {
				if (business.organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
						OrganizationDefinition.ProcessPlatformManager)) {
					dns.addAll(identities);
				} else {
					for (String str : identities) {
						List<String> units = business.organization().unit().listWithIdentitySupNested(str);
						if (ListTools.containsAny(units, process.getStartableUnitList())
								|| process.getStartableIdentityList().contains(str)) {
							dns.add(str);
						}
					}
				}
			}
			List<Identity> os = business.organization().identity().listObject(dns);
			List<Wo> wos = Wo.copier.copy(os);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Identity {

		static WrapCopier<Identity, Wo> copier = WrapCopierFactory.wo(Identity.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}