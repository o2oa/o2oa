package com.x.meeting.assemble.control.jaxrs.meeting;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.meeting.core.entity.Meeting;

class ActionListPrev extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		if (effectivePerson.isNotManager()) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		result = this.standardListPrev(Wo.copier, id, count,  JpaObject.sequence_FIELDNAME, null, null, null, null, null, null, null, null,
				true, DESC);
		return result;
	}

	public static class Wo extends Meeting {

		private static final long serialVersionUID = -7495725325510376323L;

		public static WrapCopier<Meeting, Wo> copier = WrapCopierFactory.wo(Meeting.class, Wo.class, null,
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
