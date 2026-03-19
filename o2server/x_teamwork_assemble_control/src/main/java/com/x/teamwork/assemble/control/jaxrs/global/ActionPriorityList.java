package com.x.teamwork.assemble.control.jaxrs.global;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Priority;

public class ActionPriorityList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPriorityList.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<Priority> prioritys = null;

		try {
			prioritys = priorityQueryService.listPriority();
			if( ListTools.isNotEmpty( prioritys )) {
				wos = Wo.copier.copy( prioritys );
				
				//SortTools.asc( wos, "createTime");
				result.setData(wos);
			}
			result.setData(wos);
		} catch (Exception e) {
			Exception exception = new PriorityQueryException(e, "查询优先级信息列表时发生异常。");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		return result;
	}

	public static class Wo extends Priority {
		
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Priority, Wo> copier = WrapCopierFactory.wo( Priority.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}