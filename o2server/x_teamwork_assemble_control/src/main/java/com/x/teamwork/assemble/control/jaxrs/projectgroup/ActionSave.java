package com.x.teamwork.assemble.control.jaxrs.projectgroup;

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
import com.x.teamwork.core.entity.ProjectGroup;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ProjectGroup projectGroup = null;
		ProjectGroup projectGroup_old = null;
		Wi wi = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new ProjectGroupPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			projectGroup_old = projectGroupQueryService.get( wi.getId() );
		}
		
		if (check) {
			try {					
				projectGroup = projectGroupPersistService.save( wi, effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( Project.class );
				ApplicationCache.notify( ProjectGroup.class );				
				
				Wo wo = new Wo();
				wo.setId( projectGroup.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectGroupPersistException(e, "项目信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}			
		}
		if (check) {
			try {					
				dynamicPersistService.projectGroupSaveDynamic(projectGroup_old, projectGroup, effectivePerson,  jsonElement.toString() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}	

	public static class Wi extends ProjectGroup {
		private static final long serialVersionUID = -6314932919066148113L;
		
		public static WrapCopier<Wi, ProjectGroup> copier = WrapCopierFactory.wi( Wi.class, ProjectGroup.class, null, null );

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