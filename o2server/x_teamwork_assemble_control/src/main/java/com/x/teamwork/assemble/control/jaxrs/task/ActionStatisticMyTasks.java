package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskView;

import net.sf.ehcache.Element;

public class ActionStatisticMyTasks extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionStatisticMyTasks.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String projectId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		List<Task>  taskList = null;
		List<TaskGroup>  taskGroupList = null;
		List<TaskView> taskViewList = null;
		List<WoTaskGroup> woGroupList = null;
		List<WoTaskView> woViewList = null;
		Boolean check = true;

		String cacheKey = ApplicationCache.concreteCacheKey( "ActionStatisticMyTasks", projectId, effectivePerson.getDistinguishedName() );
		Element element = taskCache.get( cacheKey );

		if ((null != element) && (null != element.getObjectValue())) {
			wo = ( Wo ) element.getObjectValue();
			result.setData( wo );
		} else {
			Integer taskTotal = 0;
			Integer completedTotal = 0;
			Integer overtimeTotal = 0;
			
			if (check) {
				try {
					//查询用户在该项目中所有工作任务组信息，如果没有，则需要创建一个工作任务组，将所有的工作任务添加到该工作任务组中
					taskGroupList = taskGroupQueryService.listGroupByPersonAndProject( effectivePerson, projectId );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException( e, "查询用户在该项目中的所有工作任务组信息列表时发生异常。" );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				try {
					//查询用户在该项目中所有的视图信息
					taskViewList = taskViewQueryService.listViewWithPersonAndProject( effectivePerson, projectId );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException( e, "查询用户在该项目中的所有视图信息列表时发生异常。" );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if ( check ) {
				if( ListTools.isNotEmpty( taskGroupList )) {
					woGroupList = WoTaskGroup.copier.copy( taskGroupList );
				}
				if( ListTools.isNotEmpty( woGroupList )) {
					for( WoTaskGroup woGroup : woGroupList ) {
						taskTotal = 0;
						completedTotal = 0;
						overtimeTotal = 0;
						taskList = taskQueryService.listTaskWithProjectAndPerson( woGroup.getProject(), effectivePerson );
						if( ListTools.isNotEmpty( taskList )) {
							for( Task task : taskList ) {
								taskTotal ++;
								if( task.getCompleted() ) {
									completedTotal++;
								}
								if( task.getOvertime() ) {
									overtimeTotal++;
								}
							}
							if( woGroup.getTaskTotal() != taskTotal || woGroup.getCompletedTotal() != completedTotal || woGroup.getOvertimeTotal() != overtimeTotal ) {
								//如果数据不一致，就更新一下
								taskGroupPersistService.updateTaskTotal( woGroup.getId(), taskTotal, completedTotal, overtimeTotal );
							}
							woGroup.setCompletedTotal(completedTotal);
							woGroup.setOvertimeTotal(overtimeTotal);
							woGroup.setTaskTotal(taskTotal);
						}
						//woGroup.setTaskTotal( taskQueryService.countWithTaskGroupId( woGroup.getId(), effectivePerson ));
					}
				}
			}
			
			if ( check ) {
				if( ListTools.isNotEmpty( taskViewList )) {
					woViewList = WoTaskView.copier.copy( taskViewList );
					SortTools.asc( woViewList, "createTime");
				}
			}
			
			if ( check ) {
				try {
					//SortTools.asc( woGroupList, "createTime");
					wo.setGroups( woGroupList );
					wo.setViews( woViewList );
					taskCache.put( new Element( cacheKey, wo) );
					result.setData(wo);
				} catch (Exception e) {
					Exception exception = new TaskQueryException(e, "将查询出来的工作任务组和视图列表信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
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