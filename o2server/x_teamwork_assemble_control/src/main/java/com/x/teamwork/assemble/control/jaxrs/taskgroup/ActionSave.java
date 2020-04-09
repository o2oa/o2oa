package com.x.teamwork.assemble.control.jaxrs.taskgroup;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskGroup;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		TaskGroup taskGroup = null;
		Wi wi = null;
		Project project = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskGroupPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			try {
				project = projectQueryService.get( wi.getProject() );
				if( project == null ) {
					check = false;
					Exception exception = new TaskGroupPersistException( "指定的项目信息不存在！projectID:" + wi.getProject() );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskGroupPersistException(e, "根据指定ID查询工作任务组信息对象时发生异常.projectID:" + wi.getProject() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {					
				taskGroup = taskGroupPersistService.save( wi, effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( Project.class );
				ApplicationCache.notify( TaskGroup.class );
				ApplicationCache.notify( Task.class, ApplicationCache.concreteCacheKey( "ActionStatisticMyTasks", project.getId(), effectivePerson.getDistinguishedName() )  );
				ApplicationCache.notify( Task.class, ApplicationCache.concreteCacheKey( "ActionStatisticMyProjectsGroups", project.getId(), effectivePerson.getDistinguishedName() )  );
				
				Wo wo = new Wo();
				wo.setId( taskGroup.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskGroupPersistException(e, "工作任务信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}			
		}
		return result;
	}	

	public static class Wi extends TaskGroup {
		private static final long serialVersionUID = -6314932919066148113L;
		
		public static WrapCopier<Wi, TaskGroup> copier = WrapCopierFactory.wi( Wi.class, TaskGroup.class, null, null );

		@FieldDescribe("说明信息")
		private String description;

		@FieldDescribe("图标icon Base64编码后的文本.")
		private String icon;

		@FieldDescribe("图标主色调.")
		private String iconColor;
		
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getIconColor() {
			return iconColor;
		}

		public void setIconColor(String iconColor) {
			this.iconColor = iconColor;
		}
	}

	public static class Wo extends WoId {
	}
	
}