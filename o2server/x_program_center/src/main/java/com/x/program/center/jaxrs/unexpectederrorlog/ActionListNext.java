package com.x.program.center.jaxrs.unexpectederrorlog;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.program.center.core.entity.UnexpectedErrorLog;

class ActionListNext extends BaseAction {
	ActionResult<List<Wo>> execute(String id, Integer count) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		result = this.standardListNext(Wo.copier, id, count,  JpaObject.sequence_FIELDNAME, null, null, null, null, null, null, null, null,
				true, DESC);
		return result;
	}

	public static class Wo extends UnexpectedErrorLog {

		private static final long serialVersionUID = -7592184343034018992L;

		static WrapCopier<UnexpectedErrorLog, Wo> copier = WrapCopierFactory.wo(UnexpectedErrorLog.class, Wo.class,
				null, JpaObject.FieldsInvisible);

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
