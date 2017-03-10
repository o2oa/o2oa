package com.x.file.assemble.control.jaxrs.file;

import java.util.HashMap;
import java.util.Map;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutBoolean;
import com.x.file.assemble.control.ThisApplication;

class ActionDelete extends ActionBase {
	ActionResult<WrapOutBoolean> execute(EffectivePerson effectivePerson, String referenceType, String reference)
			throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = WrapOutBoolean.trueInstance();
		if (effectivePerson.isNotManager()) {
			throw new AccessDeniedException(effectivePerson.getName());
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put(FileRemoveQueue.REFERENCETYPE, referenceType);
		map.put(FileRemoveQueue.REFERENCE, reference);
		ThisApplication.fileRemoveQueue.send(map);
		result.setData(wrap);
		return result;
	}
}
