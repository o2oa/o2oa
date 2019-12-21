package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionUpdate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, WrapInCollect wrapIn) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (!Config.nodes().centerServers().first().getValue().getConfigApiEnable()) {
			throw new ExceptionModifyConfig();
		}
		if (BooleanUtils.isTrue(wrapIn.getEnable())) {
			if (!this.connect()) {
				throw new ExceptionUnableConnect();
			}
			if (StringUtils.isEmpty(wrapIn.getName())) {
				throw new ExceptionNameEmpty();
			}
			if (!this.validate(wrapIn.getName(), wrapIn.getPassword())) {
				throw new ExceptionInvalidCredential();
			}
			Config.collect().setEnable(true);
		} else {
			Config.collect().setEnable(false);
		}
		Config.collect().setName(wrapIn.getName());
		Config.collect().setPassword(wrapIn.getPassword());
		Config.collect().save();
		Config.flush();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}
}
