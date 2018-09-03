package o2.collect.assemble.jaxrs.prompterrorlog;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.core.entity.log.PromptErrorLog;

class ActionListPrev extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		result = this.standardListPrev(Wo.copier, id, count, JpaObject.sequence_FIELDNAME, null, null, null, null, null, null, null, true,
				DESC);
		return result;
	}

	public static class Wo extends PromptErrorLog {

		private static final long serialVersionUID = -4855784604415258419L;

		static WrapCopier<PromptErrorLog, Wo> copier = WrapCopierFactory.wo(PromptErrorLog.class, Wo.class,
				JpaObject.singularAttributeField(PromptErrorLog.class, true, true), null);

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}
