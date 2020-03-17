package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.x.teamwork.core.entity.ProjectExtFieldRele;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskTag;

public class ActionListSubTaskWithTaskId extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListSubTaskWithTaskId.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String taskId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		Task task = null;
		List<Task> taskList = null;
		Boolean check = true;
		List<TaskTag> tags = null;

		if ( StringUtils.isEmpty( taskId ) ) {
			check = false;
			Exception exception = new TaskFlagForQueryEmptyException();
			result.error( exception );
		}

		if (check) {
			try {
				task = taskQueryService.get( taskId );
				if ( task == null) {
					check = false;
					Exception exception = new TaskNotExistsException(taskId);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定flag查询工作任务信息对象时发生异常。flag:" + taskId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				taskList = taskQueryService.listTaskWithParentId( taskId, effectivePerson );
				if( taskList == null ) {
					taskList = new ArrayList<>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定flag查询子任务列表时发生异常。flag:" + taskId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
				
		if (check) {
			try {
				wos = Wo.copier.copy( taskList );
				if(ListTools.isNotEmpty( wos )) {
					for( Wo wo : wos ) {
						tags = taskTagQueryService.listWithTaskAndPerson(effectivePerson, wo );
						if( ListTools.isNotEmpty( tags )) {
							wo.setTags( WoTaskTag.copier.copy( tags ));
						}
					}
				}
				result.setCount( Long.parseLong( wos.size() + "" ) );
				result.setData( wos );
			} catch (Exception e) {
				Exception exception = new TaskQueryException(e, "将查询出来的工作任务信息对象转换为可输出的数据信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			} 
		} 
		
		return result;
	}

	public static class Wo extends Task {

		@FieldDescribe("任务标签")
		private List<WoTaskTag> tags = null;
		
		public List<WoTaskTag> getTags() {
			return tags;
		}

		public void setTags(List<WoTaskTag> tags) {
			this.tags = tags;
		}
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo( Task.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
	
	public static class WoTaskTag extends TaskTag {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskTag, WoTaskTag> copier = WrapCopierFactory.wo( TaskTag.class, WoTaskTag.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}

	public static class WoExtFieldRele{
	
		@FieldDescribe("备用列名称")
		private String extFieldName;
		
		@FieldDescribe("显示属性名称")
		private String displayName;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<ProjectExtFieldRele, WoExtFieldRele> copier = WrapCopierFactory.wo( ProjectExtFieldRele.class, WoExtFieldRele.class, null, ListTools.toList(JpaObject.FieldsInvisible));

		public String getExtFieldName() {
			return extFieldName;
		}

		public void setExtFieldName(String extFieldName) {
			this.extFieldName = extFieldName;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
	}
}