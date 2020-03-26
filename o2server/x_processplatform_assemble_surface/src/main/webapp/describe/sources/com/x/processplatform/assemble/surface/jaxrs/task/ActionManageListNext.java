package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.processplatform.core.entity.content.Task;

import java.util.ArrayList;
import java.util.List;

class ActionManageListNext extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		result.setData(new ArrayList<Wo>());
		if (effectivePerson.isManager()) {
			EqualsTerms equals = new EqualsTerms();
			result = this.standardListNext(Wo.copier, id, count,  JpaObject.sequence_FIELDNAME, equals, null, null, null, null,
					null, null, null, true, DESC);
		}
		return result;
	}

	public static class Wo extends Task {

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo(Task.class, Wo.class, null,
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
