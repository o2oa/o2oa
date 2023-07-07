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
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.WoIdentity;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Process;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListAvailableIdentityWithProcess extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListAvailableIdentityWithProcess.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

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
					&& (ListTools.isEmpty(process.getStartableUnitList()))
					&& (ListTools.isEmpty(process.getStartableGroupList()))) {
				/** 没有设置可启动人员,所有人都可以启动 */
				dns.addAll(identities);
			} else {
				if (business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", "")) {
					dns.addAll(identities);
				} else {
					for (String str : identities) {
						if (ListTools.isNotEmpty(process.getStartableIdentityList())
								&& process.getStartableIdentityList().contains(str)) {
							dns.add(str);
						} else {
							if (ListTools.isNotEmpty(process.getStartableUnitList())) {
								List<String> units = business.organization().unit().listWithIdentitySupNested(str);
								if (ListTools.containsAny(units, process.getStartableUnitList())) {
									dns.add(str);
									continue;
								}
							}
							if (ListTools.isNotEmpty(process.getStartableGroupList())) {
								List<String> groups = business.organization().group().listWithIdentity(str);
								if (ListTools.containsAny(groups, process.getStartableGroupList())) {
									dns.add(str);
									continue;
								}
							}
						}
					}
				}
			}
			List<WoIdentity> os = business.organization().identity().listWoObject(dns);
			List<Wo> wos = Wo.copier.copy(os);
			result.setData(wos);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.process.ActionListAvailableIdentityWithProcess$Wo")
	public static class Wo extends WoIdentity {

		private static final long serialVersionUID = -3700611118921654394L;

		static WrapCopier<WoIdentity, Wo> copier = WrapCopierFactory.wo(WoIdentity.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
