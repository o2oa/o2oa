package o2.collect.assemble.jaxrs.applog;

import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.EqualsTerms;

import o2.collect.core.entity.log.AppLog;

class ActionListPrevWithUnitName extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String id, Integer count, String loggerName)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		EqualsTerms equalsTerms = new EqualsTerms();
		equalsTerms.put(AppLog.unitName_FIELDNAME, loggerName);
		result = this.standardListPrev(Wo.copier, id, count, AppLog.sequence_FIELDNAME, equalsTerms, null, null, null,
				null, null, null, null, true, DESC);
		return result;
	}

	public static class Wo extends AppLog {

		private static final long serialVersionUID = -7592184343034018992L;

		static WrapCopier<AppLog, Wo> copier = WrapCopierFactory.wo(AppLog.class, Wo.class, null,
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
