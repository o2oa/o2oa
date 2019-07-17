package com.x.teamwork.assemble.control.jaxrs.extfield;

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
import com.x.teamwork.core.entity.ProjectExtFieldRele;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ProjectExtFieldRele projectExtFieldRele = null;
		ProjectExtFieldRele projectExtFieldRele_old = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new ProjectExtFieldRelePersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			projectExtFieldRele_old = projectExtFieldReleQueryService.get( wi.getId() );
		}
		
		if (check) {
			try {					
				projectExtFieldRele = projectExtFieldRelePersistService.save( Wi.copier.copy( wi ), effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( ProjectExtFieldRele.class );
				
				Wo wo = new Wo();
				wo.setId( projectExtFieldRele.getId()  );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectExtFieldRelePersistException(e, "项目扩展属性关联信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}			
		}
		if (check) {
			try {					
				dynamicPersistService.projectExtFieldReleSaveDynamic(projectExtFieldRele_old, projectExtFieldRele, effectivePerson);
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

	public static class Wi {
		
		@FieldDescribe("ID.")
		private String id;
		
		@FieldDescribe("项目ID（必填）")
		private String projectId;

		@FieldDescribe("备用列名称（必填）")
		private String extFieldName;

		@FieldDescribe("显示属性名称（必填）")
		private String displayName;
		
		public static WrapCopier<Wi, ProjectExtFieldRele> copier = WrapCopierFactory.wi( Wi.class, ProjectExtFieldRele.class, null, null );

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getProjectId() {
			return projectId;
		}

		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}

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

	public static class Wo extends WoId {
	}
	
}