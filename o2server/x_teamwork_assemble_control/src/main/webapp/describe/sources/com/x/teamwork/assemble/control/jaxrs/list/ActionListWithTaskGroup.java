package com.x.teamwork.assemble.control.jaxrs.list;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.TaskList;

import net.sf.ehcache.Element;

public class ActionListWithTaskGroup extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListWithTaskGroup.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String taskGroupId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<TaskList> taskLists = null;
		Boolean check = true;

		String cacheKey = ApplicationCache.concreteCacheKey( "list.my.taskgroup", taskGroupId, effectivePerson.getDistinguishedName() );
		Element element = taskListCache.get( cacheKey );

		if ((null != element) && (null != element.getObjectValue())) {
			wos = (List<Wo>) element.getObjectValue();
			result.setData( wos );
		} else {
			if (check) {
				try {
					taskLists = taskListQueryService.listWithTaskGroup( effectivePerson.getDistinguishedName(), taskGroupId );
					if( ListTools.isNotEmpty( taskLists )) {
						wos = Wo.copier.copy( taskLists );
						if( ListTools.isNotEmpty( wos )) {
							for( Wo wo : wos ) {
								//计算当前List里的任务数量
								wo.setTaskCount(taskListQueryService.countTaskWithTaskListId( effectivePerson.getDistinguishedName(), wo.getId(), wo.getTaskGroup() ));
								if( "NoneList".equalsIgnoreCase( wo.getMemo() )) {
									wo.setControl( new Control(false, false, false ));
								}else {
									wo.setControl( new Control(true, true, true ));
								}
							}
						}
						taskListCache.put(new Element(cacheKey, wos));
						result.setData(wos);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskListQueryException(e, "根据用户在指定工作任务组拥有的工作任务列表信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends TaskList {
		
		private Long rank;
		
		@FieldDescribe("工作任务数量.")
		private Long taskCount = 0L;
		
		@FieldDescribe("工作任务列表操作权限.")
		private Control control;
		
		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public Long getTaskCount() {
			return taskCount;
		}

		public void setTaskCount(Long taskCount) {
			this.taskCount = taskCount;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskList, Wo> copier = WrapCopierFactory.wo( TaskList.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
	
	public static class Control{
		 
		private Boolean delete = false;
		
		private Boolean edit = false;
		
		private Boolean sortable = true;

		public Control( Boolean edit, Boolean delete, Boolean sortable ) {
			this.delete = delete;
			this.edit = edit;
			this.sortable = sortable;
		}
		public Boolean getDelete() {
			return delete;
		}

		public void setDelete(Boolean delete) {
			this.delete = delete;
		}

		public Boolean getEdit() {
			return edit;
		}

		public void setEdit(Boolean edit) {
			this.edit = edit;
		}
		public Boolean getSortable() {
			return sortable;
		}
		public void setSortable(Boolean sortable) {
			this.sortable = sortable;
		}
	}
}