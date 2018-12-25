package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.cms.core.entity.element.QueryView;

class ActionListPrev extends BaseAction {
	ActionResult<List<Wo>> execute(String id, Integer count) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		result = this.standardListPrev(Wo.copier, id, count, "sequence", null, null, null, null, null, null, null, null,
				true, DESC);
		return result;
	}

	public static class Wo extends QueryView {

		private static final long serialVersionUID = 2886873983211744188L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		public static WrapCopier<QueryView, Wo> copier = WrapCopierFactory.wo(QueryView.class, Wo.class, null,
				Wo.Excludes);

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}
