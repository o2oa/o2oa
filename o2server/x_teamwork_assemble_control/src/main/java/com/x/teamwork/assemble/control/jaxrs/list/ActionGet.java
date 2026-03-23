package com.x.teamwork.assemble.control.jaxrs.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.jaxrs.list.ActionListWithTaskGroup.Control;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskList;

public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String taskGroupId, String taskListId ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		TaskList taskList = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( taskListId ) ) {
			check = false;
			Exception exception = new TaskListFlagForQueryEmptyException();
			result.error( exception );
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( taskListId );
		Optional<?> optional = CacheManager.get(taskListCache, cacheKey);

		if (optional.isPresent()) {
			wo = (Wo) optional.get();
			result.setData( wo );
		} else {
			if( Boolean.TRUE.equals( check ) ){
				try {
					taskList = taskListQueryService.get( taskListId );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskListQueryException(e, "根据指定id查询工作任务列表信息对象时发生异常。ID:" + taskListId );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if( Boolean.TRUE.equals( check ) ){
				try {
					if( taskList != null ) {
						wo = Wo.copier.copy( taskList );
						wo.setTaskCount(taskListQueryService.countTaskWithTaskListId( effectivePerson.getDistinguishedName(), wo.getId(), wo.getTaskGroup() ));
					}else {
						if( StringUtils.isEmpty( taskGroupId )) {
							check = false;
							Exception exception = new TaskListQueryException( "工作任务分组ID都不能为空。"  );
							result.error( exception );
						}
						TaskGroup taskGroup = taskGroupQueryService.get( taskGroupId );
						if( taskGroup != null ) {
							wo = Wo.copier.copy( taskListQueryService.getNoneList( taskGroup.getProject(), taskGroupId, effectivePerson.getDistinguishedName() ) );
							wo.setTaskCount(taskListQueryService.countTaskWithTaskListId( effectivePerson.getDistinguishedName(), wo.getId(), wo.getTaskGroup() ));
						}
					}
					if( "NoneList".equalsIgnoreCase( wo.getMemo() )) {
						wo.setControl( new Control(false, false, false, false ));
					}else {
						wo.setControl( new Control(true, true, true, true));
					}
					CacheManager.put(taskListCache,cacheKey,wo);
					result.setData(wo);
				} catch (Exception e) {
					Exception exception = new TaskListQueryException(e, "将查询出来的工作任务列表信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends TaskList {
		
		@FieldDescribe("工作任务数量.")
		private Long taskCount = 0L;
		
		@FieldDescribe("工作任务列表操作权限.")
		private Control control;
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskList, Wo> copier = WrapCopierFactory.wo( TaskList.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

		public Long getTaskCount() {
			return taskCount;
		}

		public void setTaskCount(Long taskCount) {
			this.taskCount = taskCount;
		}

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}		
	}
}