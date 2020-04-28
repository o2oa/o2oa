package com.x.teamwork.assemble.control.jaxrs.projectTemplate;

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
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.ProjectTemplate;
import com.x.teamwork.core.entity.TaskListTemplate;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		ProjectTemplate template = null;
		ProjectTemplate old_template = null;
		Wi wi = null;
		Wo wo = new Wo();
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new ProjectTemplatePersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		
		if( Boolean.TRUE.equals( check ) ){
			old_template = projectTemplateQueryService.get( wi.getId() );
		}
		
		if( Boolean.TRUE.equals( check ) ){
			try {
				template = projectTemplatePersistService.save( Wi.copier.copy(wi), effectivePerson );
				
				//添加模板对应的泳道
				projectTemplatePersistService.createTaskList( template ,effectivePerson.getDistinguishedName());
				
				// 更新缓存
				ApplicationCache.notify( ProjectTemplate.class );
				ApplicationCache.notify( TaskListTemplate.class );			
				
				wo.setId( template.getId() );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new ProjectTemplatePersistException(e, "项目信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( Boolean.TRUE.equals( check ) ){
			try {					
				new BatchOperationPersistService().addOperation( 
						BatchOperationProcessService.OPT_OBJ_PROJECT, 
						BatchOperationProcessService.OPT_TYPE_PERMISSION,  template.getId(),  template.getId(), "刷新文档权限：ID=" +   template.getId() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		
		if( Boolean.TRUE.equals( check ) ){
			try {					
				List<Dynamic> dynamics = dynamicPersistService.projectTemplateSaveDynamic(old_template, template, effectivePerson,  jsonElement.toString() );
				if( dynamics == null ) {
					dynamics = new ArrayList<>();
				}
				if( wo != null ) {
					wo.setDynamics(WoDynamic.copier.copy(dynamics));
				}
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData( wo );
		return result;
	}	

	public static class Wi {
		
		@FieldDescribe("数据库主键，自动生成，非必填.")
		private String id;
		
		@FieldDescribe("模板名称，必填")
		private String title;

		@FieldDescribe("排序号，非必填")
		private Integer order;

		@FieldDescribe("模板类型，非必填")
		private String type;

		@FieldDescribe("图标文件ID，非必填")
		private String icon = null;

		@FieldDescribe("说明信息(1M)，非必填")
		private String description;
		
		@FieldDescribe("模板包含的永道，可多值，非必填.")
		private List<String> taskList = null;
		
		public static WrapCopier<Wi, ProjectTemplate> copier = WrapCopierFactory.wi( Wi.class, ProjectTemplate.class, null, null );
		
		private String owner = null;
	
		private Boolean deleted = false;
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Integer getOrder() {
			return order;
		}

		public void setOrder(Integer order) {
			this.order = order;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}
		
		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public List<String> getTaskList() {
			return taskList;
		}

		public void setTaskList(List<String> taskList) {
			this.taskList = taskList;
		}
		
		public Boolean getDeleted() {
			return deleted;
		}

		public void setDeleted(Boolean deleted) {
			this.deleted = deleted;
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