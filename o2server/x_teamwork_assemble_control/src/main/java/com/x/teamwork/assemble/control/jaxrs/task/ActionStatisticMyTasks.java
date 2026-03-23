package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.teamwork.core.entity.ProjectStatusEnum;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskView;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;
import com.x.teamwork.core.entity.tools.filter.term.InTerm;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * @author sword
 */
public class ActionStatisticMyTasks extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionStatisticMyTasks.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String projectId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		List<WoTaskGroup> woGroupList = new ArrayList<>();
		List<WoTaskView> woViewList = null;

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), projectId, effectivePerson.getDistinguishedName() );
		Optional<?> optional = CacheManager.get(taskCache, cacheKey );

		if (optional.isPresent()) {
			wo = ( Wo ) optional.get();
		} else {
			List<TaskGroup> taskGroupList = taskGroupQueryService.listGroupByPersonAndProject( effectivePerson, projectId );
			List<TaskView> taskViewList = taskViewQueryService.listViewWithPersonAndProject( effectivePerson, projectId );

			if( ListTools.isNotEmpty( taskGroupList )) {
				for( TaskGroup taskGroup : taskGroupList ) {
					WoTaskGroup woGroup = WoTaskGroup.copier.copy(taskGroup);
					QueryFilter queryFilter = new QueryFilter();
					queryFilter.addEqualsTerm( new EqualsTerm( Task.project_FIELDNAME, woGroup.getProject() ) );
					Integer taskTotal = Integer.valueOf(taskQueryService.countWithCondition(effectivePerson.getDistinguishedName(), queryFilter).toString());

					queryFilter.addInTerm( new InTerm(Task.workStatus_FIELDNAME, ListTools.toList(ProjectStatusEnum.COMPLETED.getValue(),
							ProjectStatusEnum.CANCELED.getValue(), ProjectStatusEnum.ARCHIVED.getValue())));
					Integer completedTotal = Integer.valueOf(taskQueryService.countWithCondition(effectivePerson.getDistinguishedName(), queryFilter).toString());
					
					Integer overtimeTotal = 0;
					if( !taskTotal.equals(woGroup.getTaskTotal()) || !completedTotal.equals(woGroup.getCompletedTotal()) || !overtimeTotal.equals(woGroup.getOvertimeTotal()) ) {
						//如果数据不一致，就更新一下
						taskGroupPersistService.updateTaskTotal( woGroup.getId(), taskTotal, completedTotal, overtimeTotal );
					}
					woGroup.setCompletedTotal(completedTotal);
					woGroup.setOvertimeTotal(overtimeTotal);
					woGroup.setTaskTotal(taskTotal);
					woGroupList.add(woGroup);
				}
			}

			if( ListTools.isNotEmpty( taskViewList )) {
				woViewList = WoTaskView.copier.copy( taskViewList );
				SortTools.asc( woViewList, JpaObject.createTime_FIELDNAME);
			}

			wo.setGroups( woGroupList );
			wo.setViews( woViewList );
			CacheManager.put(taskCache, cacheKey, wo);
		}
		result.setData( wo );

		return result;
	}

	public static class Wo{

		@FieldDescribe("工作任务分组")
		private List<WoTaskGroup> groups = null;

		@FieldDescribe("工作任务视图")
		private List<WoTaskView> views = null;

		public List<WoTaskGroup> getGroups() {
			return groups;
		}

		public void setGroups(List<WoTaskGroup> groups) {
			this.groups = groups;
		}

		public List<WoTaskView> getViews() {
			return views;
		}

		public void setViews(List<WoTaskView> views) {
			this.views = views;
		}
	}

	public static class WoTaskGroup extends TaskGroup{
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		static WrapCopier<TaskGroup, WoTaskGroup> copier = WrapCopierFactory.wo( TaskGroup.class, WoTaskGroup.class, null, Excludes);
	}

	public static class WoTaskView extends TaskView{
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		static WrapCopier<TaskView, WoTaskView> copier = WrapCopierFactory.wo( TaskView.class, WoTaskView.class, null, Excludes);
	}
}
