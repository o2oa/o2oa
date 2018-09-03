package o2.collect.assemble.jaxrs.prompterrorlog;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;

import o2.collect.core.entity.log.PromptErrorLog;

class ActionListNextWithUnitName extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, String unitName)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		EqualsTerms equalsTerms = new EqualsTerms();
		equalsTerms.put(PromptErrorLog.unitName_FIELDNAME, unitName);
		result = this.standardListNext(Wo.copier, id, count, PromptErrorLog.sequence_FIELDNAME, equalsTerms, null, null,
				null, null, null, null, true, DESC);
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
