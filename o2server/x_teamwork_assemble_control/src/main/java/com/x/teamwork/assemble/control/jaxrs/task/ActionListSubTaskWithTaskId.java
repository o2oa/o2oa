package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.CustomExtFieldRele;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskTag;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class ActionListSubTaskWithTaskId extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListSubTaskWithTaskId.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String taskId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Task task = taskQueryService.get( taskId );
		if(task == null){
			throw new ExceptionEntityNotExist(taskId);
		}

		if(!isReader(task.getId(), effectivePerson)){
			throw new ExceptionAccessDenied(effectivePerson);
		}

		List<Task> taskList = taskQueryService.listTaskWithParentId( taskId, effectivePerson );

		List<Wo> wos = Wo.copier.copy( taskList );
		if(ListTools.isNotEmpty( wos )) {
			for( Wo wo : wos ) {
				List<TaskTag> tags = taskTagQueryService.listWithTaskAndPerson(effectivePerson, wo );
				if( ListTools.isNotEmpty( tags )) {
					wo.setTags( WoTaskTag.copier.copy( tags ));
				}
			}
		}
		result.setCount( Long.valueOf(wos.size()) );
		result.setData( wos );

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

		static WrapCopier<CustomExtFieldRele, WoExtFieldRele> copier = WrapCopierFactory.wo( CustomExtFieldRele.class, WoExtFieldRele.class, null, ListTools.toList(JpaObject.FieldsInvisible));

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
