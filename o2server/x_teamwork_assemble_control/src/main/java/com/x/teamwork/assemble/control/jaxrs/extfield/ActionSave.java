package com.x.teamwork.assemble.control.jaxrs.extfield;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.CacheManager;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.CustomExtFieldRele;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		CustomExtFieldRele customExtFieldRele = null;
		CustomExtFieldRele customExtFieldRele_old = null;
		Project project = null;
		Task task = null;
		Wi wi = null;
		Wo wo = new Wo();
		String fieldName = null;
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
			customExtFieldRele = Wi.copier.copy( wi );
		} catch (Exception e) {
			check = false;
			Exception exception = new CustomExtFieldRelePersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		/*if( Boolean.TRUE.equals( check ) ){
			if( StringUtils.isEmpty( customExtFieldRele.getProjectId() )) {
				check = false;
				Exception exception = new ProjectFlagForQueryEmptyException();
				result.error( exception );
			}
		}*/
		
		if( Boolean.TRUE.equals( check ) ){
			if(StringUtils.isNotEmpty( customExtFieldRele.getCorrelationId() )){
				try {
					if(StringUtils.equals(customExtFieldRele.getType(), "project")){
						project = projectQueryService.get( customExtFieldRele.getCorrelationId() );
						if ( project == null) {
							check = false;
							Exception exception = new ProjectNotExistsException( customExtFieldRele.getCorrelationId() );
							result.error( exception );
						}
					}
					if(StringUtils.equals(customExtFieldRele.getType(), "task")){
						task = taskQueryService.get( customExtFieldRele.getCorrelationId() );
						if ( task == null) {
							check = false;
							Exception exception = new TaskNotExistsException( customExtFieldRele.getCorrelationId() );
							result.error( exception );
						}
					}
					
				} catch (Exception e) {
					check = false;
					Exception exception = new CustomExtFieldRelePersistException(e, "根据指定flag查询应用项目信息对象时发生异常。flag:" +  customExtFieldRele.getCorrelationId() );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
		}
		
		if( Boolean.TRUE.equals( check ) ){
			if( StringUtils.isNotEmpty( customExtFieldRele.getId() )) {
				try {
					customExtFieldRele_old = customExtFieldReleQueryService.get( customExtFieldRele.getId() );
				} catch (Exception e) {
					check = false;
					Exception exception = new CustomExtFieldReleQueryException(e, "系统在根据ID查询指定的扩展属性关联信息时发生异常。ID:" +  customExtFieldRele.getId() );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if( Boolean.TRUE.equals( check ) ){
			if( customExtFieldRele_old == null ) { //新建
				fieldName = customExtFieldReleQueryService.getNextUseableExtFieldName( customExtFieldRele.getCorrelationId(), customExtFieldRele.getDisplayType() );
			}else {
				//判断是否属性在富文本和普通 文本之间发生了变换
				if( (StringUtils.equals("RICHTEXT",wi.getDisplayType()) && !StringUtils.equals("RICHTEXT",customExtFieldRele_old.getDisplayType()))
						|| !StringUtils.equals("RICHTEXT",wi.getDisplayType()) && StringUtils.equals("RICHTEXT",customExtFieldRele_old.getDisplayType())) {
					//判断当前所需要的类型的备用属性是否足够
					fieldName = customExtFieldReleQueryService.getNextUseableExtFieldName( customExtFieldRele.getCorrelationId(), customExtFieldRele.getDisplayType() );
				}else {
					fieldName = customExtFieldRele_old.getExtFieldName();
				}
			}
		}

		if( Boolean.TRUE.equals( check ) ){
			if( StringUtils.isEmpty(  fieldName )) {
				//备用属性已经用完了，无法再添加新的属性
				check = false;
				wo.setUseable(false);
				/*Exception exception = new CustomExtFieldRelePersistException( "扩展属性不足(备用属性已用完)，系统无法为该对象分配["+ customExtFieldRele.getDisplayType() +"]。"  );
				result.error(exception);*/
			}else {
				customExtFieldRele.setExtFieldName( fieldName );
			}
		}

		
		if( Boolean.TRUE.equals( check ) ){
			try {
				
				customExtFieldRele = customExtFieldRelePersistService.save( customExtFieldRele, effectivePerson );
				
				// 更新缓存
				CacheManager.notify( CustomExtFieldRele.class );
				
				wo.setId( customExtFieldRele.getId()  );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new CustomExtFieldRelePersistException(e, "扩展属性关联信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}			
		}
		if( Boolean.TRUE.equals( check ) ){
			try {					
				Dynamic dynamic = dynamicPersistService.projectExtFieldReleSaveDynamic(customExtFieldRele_old, customExtFieldRele, effectivePerson);
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

		@FieldDescribe("显示属性名称（必填）")
		private String displayName;

		@FieldDescribe("显示方式：TEXT|RADIO|CHECKBOX|SELECT|MUTISELECT|RICHTEXT（必填）")
		private String displayType="TEXT";
		
		@FieldDescribe("类型：project|task（必填）")
		private String type;

		@FieldDescribe("说明信息（非必填）")
		private String description;
		
		@FieldDescribe("关联ID（（非必填）")
		private String correlationId;

		@FieldDescribe("选择荐的备选数据，数据Json， displayType=RADIO|CHECKBOX|SELECT|MUTISELECT时必须填写，否则无选择项")
		private String optionsData;
		
		public static WrapCopier<Wi, CustomExtFieldRele> copier = WrapCopierFactory.wi( Wi.class, CustomExtFieldRele.class, null, null );
		
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

		public String getCorrelationId() {
			return correlationId;
		}

		public void setCorrelationId(String correlationId) {
			this.correlationId = correlationId;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
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
		
		@FieldDescribe("备用属性是否可用")
		Boolean useable = true;
		
		public Boolean getUseable(){
			return useable;
		}
		
		public void setUseable(Boolean useable){
			this.useable = useable;
		}

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