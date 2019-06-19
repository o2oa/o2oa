package com.x.temwork.assemble.control.jaxrs.project;

import java.util.List;

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
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectDetail;
import com.x.teamwork.core.entity.ProjectGroup;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Project project = null;
		Wi wi = null;
		Boolean check = true;
		String optType = "CREATE";

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new ProjectPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			if ( !projectPersistService.checkPermissionForPersist( effectivePerson, systemConfigQueryService.getValueByCode("PROJECT_CREATOR")) ) {
				check = false;
				Exception exception = new ProjectPersistException("project save permission denied!" );
				result.error(exception);
			}
		}
		
		if (check) {	
			project = projectQueryService.get( wi.getId() );
			if( project == null ) {
				optType = "CREATE";
			}else {
				optType = "UPDATE";
			}
		}
		
		if (check) {
			ProjectDetail projectDetail = new ProjectDetail();
			projectDetail.setDescription( wi.getDescription() );
			
			try {	
				if( ListTools.isNotEmpty( wi.getGroups() )) {
					wi.setGroupCount( wi.getGroups().size() );
				}
				
				project = projectPersistService.save( wi, projectDetail, effectivePerson );
				
				//将项目添加到指定的项目组
				projectGroupPersistService.releProjectToGroup(  project.getId(), wi.getGroups() );
				
				// 更新缓存
				ApplicationCache.notify( Project.class );
				ApplicationCache.notify( ProjectGroup.class );
				Wo wo = new Wo();
				wo.setId( project.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectPersistException(e, "项目信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {					
				dynamicPersistService.save( project, optType, effectivePerson, jsonElement.toString() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

	public static class Wi extends Project {
		
		private static final long serialVersionUID = -6314932919066148113L;
		
		public static WrapCopier<Wi, Project> copier = WrapCopierFactory.wi( Wi.class, Project.class, null, null );

		@FieldDescribe("说明信息")
		private String description;
		
		@FieldDescribe("项目所属的项目组，可多值.")
		private List<String> groups = null;

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public List<String> getGroups() {
			return groups;
		}

		public void setGroups(List<String> groups) {
			this.groups = groups;
		}
		
	}

	public static class Wo extends WoId {
	}
	
}