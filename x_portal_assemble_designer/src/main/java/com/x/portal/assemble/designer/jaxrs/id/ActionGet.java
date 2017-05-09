package com.x.portal.assemble.designer.jaxrs.id;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutId;

class ActionGet extends ActionBase {

	ActionResult<List<WrapOutId>> execute(Integer count) throws Exception {
		ActionResult<List<WrapOutId>> result = new ActionResult<>();
		List<WrapOutId> list = new ArrayList<>();
		if (count > 0 && count < 200) {
			for (int i = 0; i < count; i++) {
				WrapOutId wrap = new WrapOutId(JpaObject.createId());
				list.add(wrap);
			}
		}
		result.setData(list);
		return result;
	}

}