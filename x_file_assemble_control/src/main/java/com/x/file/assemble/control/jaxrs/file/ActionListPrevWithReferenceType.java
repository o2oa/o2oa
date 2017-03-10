package com.x.file.assemble.control.jaxrs.file;

import java.util.List;

import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.file.assemble.control.wrapout.WrapOutFile;
import com.x.file.core.entity.open.ReferenceType;

class ActionListPrevWithReferenceType extends ActionBase {
	ActionResult<List<WrapOutFile>> execute(EffectivePerson effectivePerson, String id, Integer count,
			ReferenceType referenceType) throws Exception {
		ActionResult<List<WrapOutFile>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		equals.put("person", effectivePerson.getName());
		equals.put("referenceType", referenceType);
		result = this.standardListPrev(copier, id, count, "sequence", equals, null, null, null, null, null, null, true,
				DESC);
		return result;
	}
}