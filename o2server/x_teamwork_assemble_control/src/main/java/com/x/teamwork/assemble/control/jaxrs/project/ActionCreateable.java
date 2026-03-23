package com.x.teamwork.assemble.control.jaxrs.project;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Project;

public class ActionCreateable extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionCreateable.class);

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

		if( Boolean.TRUE.equals( check ) ){
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
		
		if( Boolean.TRUE.equals( check ) ){
			try {	
				projectPersistService.createableProject( projectId,wi.getCreateable());

				// 更新缓存
				CacheManager.notify( Project.class );
				Wo wo = new Wo();
				wo.setId( project.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectPersistException(e, "项目状态信息更新时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
			
			try {					
				dynamicPersistService.projectCreateableDynamic( project, effectivePerson ,wi.getCreateable()); 
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

public static class Wi {

		@FieldDescribe("是否可新建任务")
		private Boolean createable = true;

		public Boolean getCreateable() {
			return createable;
		}

		public void setCreateable(Boolean createable) {
			this.createable = createable;
		}
	}

	public static class Wo extends WoId {
	}
	
}