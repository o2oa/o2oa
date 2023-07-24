package com.x.program.center.jaxrs.prompterrorlog;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;
import com.x.program.center.core.entity.PromptErrorLog;

class ActionListPrevWithLoggerName extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, String loggerName)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		EqualsTerms equalsTerms = new EqualsTerms();
		equalsTerms.put(PromptErrorLog.loggerName_FIELDNAME, loggerName);
		result = this.standardListPrev(Wo.copier, id, count, PromptErrorLog.sequence_FIELDNAME, equalsTerms, null, null,
				null, null, null, null, null, true, DESC);
		return result;
	}

	public static class Wo extends PromptErrorLog {

		private static final long serialVersionUID = -7592184343034018992L;

		static WrapCopier<PromptErrorLog, Wo> copier = WrapCopierFactory.wo(PromptErrorLog.class, Wo.class, null,
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
