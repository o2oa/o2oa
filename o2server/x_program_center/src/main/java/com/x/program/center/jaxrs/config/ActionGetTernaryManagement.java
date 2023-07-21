package com.x.program.center.jaxrs.config;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.TernaryManagement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;

class ActionGetTernaryManagement extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = Wo.copier.copy(Config.ternaryManagement());
		result.setData(wo);
		return result;
	}

	public static class Wo extends TernaryManagement {

		private static final long serialVersionUID = -2727709861590469212L;
		
		static WrapCopier<TernaryManagement, Wo> copier = WrapCopierFactory.wo(TernaryManagement.class, Wo.class, null,
				ListTools.toList("auditManagerPassword", "securityManagerPassword", "systemManagerPassword"));

	}
}