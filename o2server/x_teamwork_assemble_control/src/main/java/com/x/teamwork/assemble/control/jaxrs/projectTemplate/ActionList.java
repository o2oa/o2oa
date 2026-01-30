package com.x.teamwork.assemble.control.jaxrs.projectTemplate;

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
import com.x.teamwork.core.entity.TaskListTemplate;

public class ActionList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionList.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<TaskListTemplate> projectGroups = null;
		Boolean check = true;

		/*if( Boolean.TRUE.equals( check ) ){
			try {
				projectGroups = projectGroupQueryService.listGroupByPerson( effectivePerson.getDistinguishedName() );
				if( ListTools.isNotEmpty( projectGroups )) {
					wos = Wo.copier.copy( projectGroups );
					
					SortTools.asc( wos, "createTime");
					
					result.setData(wos);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectTemplateQueryException(e, "根据用户拥有的项目组信息列表时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}*/
		
		return result;
	}

	public static class Wo extends TaskListTemplate {
		
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskListTemplate, Wo> copier = WrapCopierFactory.wo( TaskListTemplate.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}