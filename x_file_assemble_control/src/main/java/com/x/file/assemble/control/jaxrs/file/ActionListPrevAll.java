package com.x.file.assemble.control.jaxrs.file;

import java.util.List;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.file.assemble.control.wrapout.WrapOutFile;

class ActionListPrevAll extends ActionBase {
	ActionResult<List<WrapOutFile>> execute(EffectivePerson effectivePerson, String id, Integer count)
			throws Exception {
		if (effectivePerson.isNotManager()) {
			throw new AccessDeniedException(effectivePerson.getName());
		}
		ActionResult<List<WrapOutFile>> result = new ActionResult<>();
		result = this.standardListPrev(copier, id, count, "sequence", null, null, null, null, null, null, null, true,
				DESC);
		return result;
	}
}