package com.x.teamwork.assemble.control.jaxrs.project;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.teamwork.core.entity.ProjectStatusEnum;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.InTerm;
import com.x.teamwork.core.entity.tools.filter.term.MemberTerm;

/**
 * @author sword
 */
public class ActionListStarNextWithFilter extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListStarNextWithFilter.class);

	protected ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer pageNum, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		wrapIn.setIsStar(true);
		QueryFilter queryFilter = wrapIn.getQueryFilter(effectivePerson);

		String person = this.isSysManager(effectivePerson) ? "" : effectivePerson.getDistinguishedName();

		Long total = projectQueryService.countWithCondition(person, queryFilter);
		if(total > 0){
			List<Project> projectList = projectQueryService.listPagingWithCondition(person,
					wrapIn.getOrderField(), wrapIn.getOrderType(), queryFilter, pageNum, count);

			for( Project project : projectList ) {
				Wo wo = Wo.copier.copy(project);
				wo.setStar( true );

				WrapOutControl control = new WrapOutControl();
				if(this.canEdit(project, effectivePerson)) {
					control.setDelete( true );
					control.setEdit( true );
					control.setSortable( true );
				}
				if(ProjectStatusEnum.isEndStatus(project.getWorkStatus())){
					control.setTaskCreate(false);
				}
				control.setFounder( control.getEdit() ? true : this.isManager(project.getId(), effectivePerson) );
				control.setSysAdmin(this.isSysManager(effectivePerson));
				wo.setControl(control);

				wo.setTaskStatistics(this.statTask(person, project.getId()));
				wos.add( wo );
			}
		}
		result.setCount( total );
		result.setData( wos );

		return result;
	}

	public static class Wi extends WrapInQueryProject{
	}

	public static class Wo extends WrapOutProject {

		private static final long serialVersionUID = -1549263319343132545L;
		@FieldDescribe("项目任务的统计信息:allCount|completedCount|processingCount|delayCount|deleteCount")
		private TaskStatistics taskStatistics;

		static WrapCopier<Project, Wo> copier = WrapCopierFactory.wo( Project.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

		public TaskStatistics getTaskStatistics() {
			return taskStatistics;
		}

		public void setTaskStatistics(TaskStatistics taskStatistics) {
			this.taskStatistics = taskStatistics;
		}

	}
}
