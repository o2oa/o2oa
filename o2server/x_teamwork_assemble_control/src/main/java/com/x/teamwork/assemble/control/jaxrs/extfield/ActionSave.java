package com.x.teamwork.assemble.control.jaxrs.extfield;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

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
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectExtFieldRele;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ProjectExtFieldRele projectExtFieldRele = null;
		ProjectExtFieldRele projectExtFieldRele_old = null;
		Project project = null;
		Wi wi = null;
		Wo wo = new Wo();
		String fieldName = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
			projectExtFieldRele = Wi.copier.copy( wi );
		} catch (Exception e) {
			check = false;
			Exception exception = new ProjectExtFieldRelePersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			if( StringUtils.isEmpty( projectExtFieldRele.getProjectId() )) {
				check = false;
				Exception exception = new ProjectFlagForQueryEmptyException();
				result.error( exception );
			}
		}
		
		if (check) {
			try {
				project = projectQueryService.get( projectExtFieldRele.getProjectId() );
				if ( project == null) {
					check = false;
					Exception exception = new ProjectNotExistsException( projectExtFieldRele.getProjectId() );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectExtFieldRelePersistException(e, "根据指定flag查询应用项目信息对象时发生异常。flag:" +  projectExtFieldRele.getProjectId() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if( StringUtils.isNotEmpty( projectExtFieldRele.getId() )) {
				try {
					projectExtFieldRele_old = projectExtFieldReleQueryService.get( projectExtFieldRele.getId() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ProjectExtFieldReleQueryException(e, "系统在根据ID查询指定的扩展属性关联信息时发生异常。ID:" +  projectExtFieldRele.getId() );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if (check) {
			if( projectExtFieldRele_old == null ) { //新建
				fieldName = projectExtFieldReleQueryService.getNextUseableExtFieldName( projectExtFieldRele.getProjectId(), projectExtFieldRele.getDisplayType() );
			}else {
				//判断是否属性在富文本和普通 文本之间发生了变换
				if( ("RICHTEXT".equals( wi.getDisplayType() ) && !"RICHTEXT".equals( projectExtFieldRele_old.getDisplayType() ))
						|| !"RICHTEXT".equals( wi.getDisplayType() ) && "RICHTEXT".equals( projectExtFieldRele_old.getDisplayType() ) ) {
					//判断当前所需要的类型的备用属性是否足够
					fieldName = projectExtFieldReleQueryService.getNextUseableExtFieldName( projectExtFieldRele.getProjectId(), projectExtFieldRele.getDisplayType() );
				}else {
					fieldName = projectExtFieldRele_old.getExtFieldName();
				}
			}
		}
		
		if( check ) {
			if( StringUtils.isEmpty(  fieldName )) {
				//备用属性已经用完了，无法再添加新的属性
				check = false;
				Exception exception = new ProjectExtFieldRelePersistException( "扩展属性不足，系统无法为该项目分配["+ projectExtFieldRele.getDisplayType() +"]。"  );
				result.error(exception);
			}else {
				projectExtFieldRele.setExtFieldName( fieldName );
			}
		}

		if (check) {
			try {
				
				projectExtFieldRele = projectExtFieldRelePersistService.save( projectExtFieldRele, effectivePerson );
				
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
		
		@FieldDescribe("ID，为空时为新建")
		private String id;
		
		@FieldDescribe("项目ID（必填）")
		private String projectId;

		@FieldDescribe("显示属性名称（必填）")
		private String displayName;

		@FieldDescribe("显示方式：TEXT|RADIO|CHECKBOX|SELECT|MUTISELECT|RICHTEXT（必填）")
		private String displayType="TEXT";

		@FieldDescribe("说明信息（非必填）")
		private String description;

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

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
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