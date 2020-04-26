package com.x.teamwork.assemble.control.jaxrs.taskListTemplate;

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
		TaskListTemplate template = null;
		TaskListTemplate old_template = null;
		Wi wi = null;
		Wo wo = new Wo();
		Boolean check = true;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskListTemplatePersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		
		if( Boolean.TRUE.equals( check ) ){
			old_template = taskListTemplateQueryService.get( wi.getId() );
		}
		
		if( Boolean.TRUE.equals( check ) ){
			try {
				template = taskListTemplatePersistService.save( Wi.copier.copy(wi), effectivePerson );
				
				// 更新缓存
				ApplicationCache.notify( ProjectTemplate.class );
				ApplicationCache.notify( TaskListTemplate.class );			
				
				wo.setId( template.getId() );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskListTemplatePersistException(e, "项目信息保存时发生异常。");
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
		
		result.setData( wo );
		return result;
	}	

	public static class Wi {
		
		@FieldDescribe("数据库主键，自动生成，非必填.")
		private String id;
		
		@FieldDescribe("工作任务列表（泳道）名称")
		private String name;
		
		@FieldDescribe("所属模板ID.")
		private String projectTemplate;

		@FieldDescribe("排序号，非必填")
		private Integer order;

		@FieldDescribe("列表描述，非必填")
		private String memo;

		@FieldDescribe("创建者，非必填")
		private String creatorPerson = null;

		private Boolean deleted = false;
		
		public static WrapCopier<Wi, TaskListTemplate> copier = WrapCopierFactory.wi( Wi.class, TaskListTemplate.class, null, null );
		
		private String owner = null;
		
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getProjectTemplate() {
			return projectTemplate;
		}

		public void setProjectTemplate(String projectTemplate) {
			this.projectTemplate = projectTemplate;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getOrder() {
			return order;
		}

		public void setOrder(Integer order) {
			this.order = order;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public String getCreatorPerson() {
			return creatorPerson;
		}

		public void setCreatorPerson(String creatorPerson) {
			this.creatorPerson = creatorPerson;
		}
		
		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
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