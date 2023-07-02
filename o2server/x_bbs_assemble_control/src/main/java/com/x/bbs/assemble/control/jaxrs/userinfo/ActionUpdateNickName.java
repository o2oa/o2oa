package com.x.bbs.assemble.control.jaxrs.userinfo;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.bbs.assemble.control.ThisApplication;

/**
 * @author sword
 */
public class ActionUpdateNickName extends BaseAction {

	protected ActionResult<Wo> execute(EffectivePerson effectivePerson, String person) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = new Wo();
		result.setData(wrap);
		if(!effectivePerson.isManager()){
			person = effectivePerson.getDistinguishedName();
		}
		wrap.setValue(true);
		ThisApplication.nickNameConsumeQueue.send(person);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}
}
