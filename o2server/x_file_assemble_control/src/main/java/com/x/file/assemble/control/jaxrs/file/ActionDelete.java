package com.x.file.assemble.control.jaxrs.file;

import java.util.HashMap;
import java.util.Map;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.file.assemble.control.ThisApplication;

class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String referenceType, String reference) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (effectivePerson.isNotManager()) {
			throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put(FileRemoveQueue.REFERENCETYPE, referenceType);
		map.put(FileRemoveQueue.REFERENCE, reference);
		ThisApplication.fileRemoveQueue.send(map);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}
}
