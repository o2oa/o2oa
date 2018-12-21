package com.x.strategydeploy.assemble.control.measures;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.strategydeploy.assemble.control.Business;

public class ActionIsAdd extends BaseAction {
	private static  Logger logger = LoggerFactory.getLogger(ActionIsAdd.class);

	public static class Wo extends WrapBoolean {
	}

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<Wo>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> writer_g = measuresInfoOperationService.getWriter_groups();
			List<String> writer_persons = business.organization().person().listWithGroup(writer_g);
			String distinguishedName = effectivePerson.getDistinguishedName();
			Wo wo = new Wo();
			if (writer_persons.indexOf(distinguishedName) >= 0) {
				wo.setValue(true);
				result.setData(wo);
			} else {
				wo.setValue(false);
				result.setData(wo);
			}
		} catch (Exception e) {
			result.error(e);
		}

		return result;
	}
}
