package com.x.teamwork.assemble.control.jaxrs.project;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Project;

public class ActionUpdateIcon extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionUpdateIcon.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String projectId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		Project project = null;
		Boolean check = true;
		
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
			try {
				project = projectQueryService.get( projectId );
				if ( project == null) {
					check = false;
					Exception exception = new ProjectNotExistsException( projectId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectQueryException(e, "根据指定flag查询应用项目信息对象时发生异常。ID:" + projectId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {	
				projectPersistService.saveProjectIcon( projectId, wi.getIcon() );

				// 更新缓存
				ApplicationCache.notify( Project.class );
				Wo wo = new Wo();
				wo.setId( project.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectPersistException(e, "项目图标信息更新时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
			
			try {					
				dynamicPersistService.projectIconSaveDynamic( project, effectivePerson,  jsonElement.toString() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

public static class Wi {

		@FieldDescribe("图标文件ID")
		private String icon;

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}
	}

	public static class Wo extends WoId {
	}
	
}