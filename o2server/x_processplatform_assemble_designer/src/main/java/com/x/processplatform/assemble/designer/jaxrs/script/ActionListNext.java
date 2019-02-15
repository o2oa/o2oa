package com.x.processplatform.assemble.designer.jaxrs.script;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.processplatform.core.entity.element.Script;

class ActionListNext extends BaseAction {
	ActionResult<List<Wo>> execute(String id, Integer count) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		result = this.standardListNext(Wo.copier, id, count,  JpaObject.sequence_FIELDNAME, null, null, null, null, null, null, null, null,
				true, DESC);
		return result;
	}

	public static class Wo extends Script {

		private static final long serialVersionUID = 2475165883507548650L;

		static WrapCopier<Script, Wo> copier = WrapCopierFactory.wo(Script.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("排序号")
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}
