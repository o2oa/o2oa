package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.QueryView;

class ActionListWithApplication extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String appId )
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wraps = new ArrayList<>();
			Business business = new Business(emc);
			List<String> ids = business.queryViewFactory().listWithAppId( appId );
			List<QueryView> os = emc.list(QueryView.class, ids);
			wraps = Wo.copier.copy(os);
			SortTools.asc(wraps, "name");
			result.setData(wraps);
			return result;
		}
	}
	
	public static class Wo extends QueryView {

		private static final long serialVersionUID = 2886873983211744188L;
		
		
		public static WrapCopier<QueryView, Wo> copier = WrapCopierFactory.wo(QueryView.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}
