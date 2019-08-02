package com.x.teamwork.assemble.control.jaxrs.extfield;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.ProjectExtFieldRele;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ProjectExtFieldRele projectExtFieldRele = null;
		ProjectExtFieldRele projectExtFieldRele_old = null;
		Wi wi = null;
		Wo wo = new Wo();
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
				
				wo.setId( projectExtFieldRele.getId()  );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectExtFieldRelePersistException(e, "项目扩展属性关联信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}			
		}
		if (check) {
			try {					
				Dynamic dynamic = dynamicPersistService.projectExtFieldReleSaveDynamic(projectExtFieldRele_old, projectExtFieldRele, effectivePerson);
				if( dynamic != null ) {
					List<WoDynamic> dynamics = new ArrayList<>();
					dynamics.add( WoDynamic.copier.copy( dynamic ) );
					if( wo != null ) {
						wo.setDynamics(dynamics);
					}
				}
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData( wo );
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

		@FieldDescribe("显示方式：TEXT|RADIO|CHECKBOX|SELECT|MUTISELECT|RICHTEXT（必填）")
		private String displayType="TEXT";

		@FieldDescribe("选择荐的备选数据，数据Json， displayType=RADIO|CHECKBOX|SELECT|MUTISELECT时必须填写，否则无选择项")
		private String optionsData;
		
		public static WrapCopier<Wi, ProjectExtFieldRele> copier = WrapCopierFactory.wi( Wi.class, ProjectExtFieldRele.class, null, null );
		
		public String getDisplayType() {
			return displayType;
		}

		public void setDisplayType(String displayType) {
			this.displayType = displayType;
		}

		public String getOptionsData() {
			return optionsData;
		}

		public void setOptionsData(String optionsData) {
			this.optionsData = optionsData;
		}

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
		
		@FieldDescribe("操作引起的动态内容")
		List<WoDynamic> dynamics = new ArrayList<>();

		public List<WoDynamic> getDynamics() {
			return dynamics;
		}

		public void setDynamics(List<WoDynamic> dynamics) {
			this.dynamics = dynamics;
		}
		
	}
	
	public static class WoDynamic extends Dynamic{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Dynamic, WoDynamic> copier = WrapCopierFactory.wo( Dynamic.class, WoDynamic.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}		
	}
	
}