package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.*;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

import net.sf.ehcache.Element;

public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		Task task = null;
		TaskDetail taskDetail = null;
		TaskExtField taskExtField = null;
		List<ProjectExtFieldRele> extFieldReleList = null;
		List<TaskTag> tags = null;
		Boolean check = true;
		WrapOutControl control = null;

		if ( StringUtils.isEmpty( flag ) ) {
			check = false;
			Exception exception = new TaskFlagForQueryEmptyException();
			result.error( exception );
		}

		String cacheKey = ApplicationCache.concreteCacheKey( flag,effectivePerson );
		Element element = taskCache.get( cacheKey );

		if ((null != element) && (null != element.getObjectValue())) {
			wo = (Wo) element.getObjectValue();
			result.setData( wo );
		} else {
			if( Boolean.TRUE.equals( check ) ){
				try {
					task = taskQueryService.get( flag );
					if ( task == null) {
						check = false;
						Exception exception = new TaskNotExistsException(flag);
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException(e, "根据指定flag查询工作任务信息对象时发生异常。flag:" + flag);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if( Boolean.TRUE.equals( check ) ){
				try {
					taskDetail = taskQueryService.getDetail( flag );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException(e, "根据指定flag查询工作任务信息对象时发生异常。flag:" + flag);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if( Boolean.TRUE.equals( check ) ){
				try {
					taskExtField = taskQueryService.getExtField( flag );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException(e, "根据指定flag查询工作任务扩展属性信息对象时发生异常。flag:" + flag);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if( Boolean.TRUE.equals( check ) ){
				try {
					extFieldReleList = projectExtFieldReleQueryService.listReleWithProject( task.getProject() );
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException(e, "根据指定projectId查询项目扩展列配置信息对象时发生异常。projectId:" + task.getProject());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if( Boolean.TRUE.equals( check ) ){
				try {
					wo = Wo.copier.copy( task );
					if ( wo != null && taskDetail != null) {
						wo.setDetail( taskDetail.getDetail() );
						wo.setDescription( taskDetail.getDescription() );
					}

					if( taskExtField != null ) {
						wo.setExtField( WoTaskExtField.copier.copy( taskExtField ));
					}

					if( ListTools.isNotEmpty( extFieldReleList )) {
						List<WoExtFieldRele> reles = WoExtFieldRele.copier.copy( extFieldReleList ) ;
						for( WoExtFieldRele woExtFieldRele :  reles ) {
							woExtFieldRele.setValue( taskQueryService.getValueFromTaskExtField( taskExtField, woExtFieldRele.getExtFieldName() ));
						}
						wo.setExtFieldConfigs( reles );
					}
					
					tags = taskTagQueryService.listWithTaskAndPerson(effectivePerson, task);
					if( ListTools.isNotEmpty( tags )) {
						wo.setTags( WoTaskTag.copier.copy( tags ));
					}

				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException(e, "将查询出来的工作任务信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			//计算权限
			if( Boolean.TRUE.equals( check ) ){
				Business business = null;
				try (EntityManagerContainer bc = EntityManagerContainerFactory.instance().create()) {
					business = new Business(bc);
				}
				try {
					control = new WrapOutControl();
					if( business.isManager(effectivePerson) 
							|| effectivePerson.getDistinguishedName().equalsIgnoreCase( task.getCreatorPerson() )
							|| task.getManageablePersonList().contains( effectivePerson.getDistinguishedName() )){
						control.setDelete( true );
						control.setEdit( true );
						control.setSortable( true );
						control.setChangeExecutor( true );
					}else{
						control.setDelete( false );
						control.setEdit( false );
						control.setSortable( false );
						control.setChangeExecutor( false );
					}
					if(effectivePerson.getDistinguishedName().equalsIgnoreCase( task.getExecutor())){
						control.setChangeExecutor( true );
					}
					if(effectivePerson.getDistinguishedName().equalsIgnoreCase( task.getCreatorPerson())){
						control.setFounder( true );
					}else{
						control.setFounder( false );
					}
					wo.setControl(control);
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException(e, "根据指定flag查询工作任务权限信息时发生异常。flag:" + flag);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			//查询任务所在的Group信息
			if( Boolean.TRUE.equals( check ) ){
				List<String> groupIds = null;
				try {
					//groupIds = taskGroupQueryService.listGroupIdsByTask( task.getId() );
					groupIds = taskGroupQueryService.listGroupIdsByPersonAndProject(effectivePerson,task.getProject());
					if( ListTools.isNotEmpty( groupIds )){
						wo.setTaskGroupId( groupIds.get(0) );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException(e, "根据指定projectId查询项目扩展列配置信息对象时发生异常。projectId:" + task.getProject());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}

			//查询任务所在的List信息
			if( Boolean.TRUE.equals( check ) ){
				if( StringUtils.isNotEmpty(wo.getTaskGroupId() )){
					List<String> listIds = null;
					try {
						listIds = taskListQueryService.listTaskListIdWithTaskId( task.getId(), wo.getTaskGroupId() );
						if( ListTools.isNotEmpty( listIds )){
							wo.setTaskListId( listIds.get(0) );
						}else{
							//返回当前项目的未分类taskListId
							List<TaskList> taskList= null;
							taskList = taskListQueryService.listWithTaskGroup( effectivePerson.getDistinguishedName(), wo.getTaskGroupId() );
							if(taskList !=null){
								wo.setTaskListId(taskList.get(0).getId());
							}
						}
					} catch (Exception e) {
						Exception exception = new TaskQueryException(e, "根据指定projectId查询项目扩展列配置信息对象时发生异常。projectId:" + task.getProject());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
		}
		taskCache.put(new Element( cacheKey, wo ));
		result.setData(wo);
		return result;
	}

	public static class Wo extends Task {
		
		@FieldDescribe("工作内容(128K)")
		private String detail;

		@FieldDescribe("说明详细信息(10M)")
		private String description;
		
		@FieldDescribe("扩展属性信息(对象)")
		private WoTaskExtField extField;
		
		@FieldDescribe("任务标签(列表)")
		private List<WoTaskTag> tags = null;
		
		@FieldDescribe("所属项目的扩展列设定(配置列表)")
		private List<WoExtFieldRele> extFieldConfigs;
		
		@FieldDescribe("任务权限")
		private WrapOutControl control = null;	

		@FieldDescribe("工作任务所属的工作任务组信息ID")
		String taskGroupId = null;

		@FieldDescribe("工作任务所属的工作任务列表（泳道）信息ID")
		String taskListId = null;

		private Long rank;

		public String getTaskGroupId() {
			return taskGroupId;
		}

		public void setTaskGroupId(String taskGroupId) {
			this.taskGroupId = taskGroupId;
		}

		public String getTaskListId() {
			return taskListId;
		}

		public void setTaskListId(String taskListId) {
			this.taskListId = taskListId;
		}

		public WoTaskExtField getExtField() {
			return extField;
		}

		public void setExtField(WoTaskExtField extField) {
			this.extField = extField;
		}

		public List<WoExtFieldRele> getExtFieldConfigs() {
			return extFieldConfigs;
		}

		public void setExtFieldConfigs(List<WoExtFieldRele> extFieldConfigs) {
			this.extFieldConfigs = extFieldConfigs;
		}

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
		public List<WoTaskTag> getTags() {
			return tags;
		}

		public void setTags(List<WoTaskTag> tags) {
			this.tags = tags;
		}
		
		public WrapOutControl getControl() {
			return control;
		}

		public void setControl(WrapOutControl control) {
			this.control = control;
		}

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<Task, Wo> copier = WrapCopierFactory.wo( Task.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
	
	public static class WoTaskExtField extends TaskExtField {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskExtField, WoTaskExtField> copier = WrapCopierFactory.wo( TaskExtField.class, WoTaskExtField.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}

	public static class WoTaskTag extends TaskTag {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<TaskTag, WoTaskTag> copier = WrapCopierFactory.wo( TaskTag.class, WoTaskTag.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}
	
	public static class WoExtFieldRele{

		@FieldDescribe("项目ID（必填）")
		private String projectId;

		@FieldDescribe("备用列名（必填）")
		private String extFieldName;

		@FieldDescribe("显示属性名称（必填）")
		private String displayName;

		@FieldDescribe("显示方式：TEXT|SELECT|MUTISELECT|RICHTEXT|DATE|DATETIME|PERSON|IDENTITY|UNIT|GROUP|（必填）")
		private String displayType="TEXT";
		
		@FieldDescribe("选择荐的备选数据，数据Json， displayType=SELECT|MUTISELECT时必须填写，否则无选择项")
		private String optionsData;
		
		@FieldDescribe("排序号（非必填）")
		private Integer order= 0 ;

		@FieldDescribe("是否允许为空（非必填）")
		private Boolean nullable = true ;

		@FieldDescribe("说明信息（非必填）")
		private String description;
		
		@FieldDescribe("属性值")
		private String value = "";

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<ProjectExtFieldRele, WoExtFieldRele> copier = WrapCopierFactory.wo( ProjectExtFieldRele.class, WoExtFieldRele.class, null, ListTools.toList(JpaObject.FieldsInvisible));

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

		public String getProjectId() {
			return projectId;
		}

		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}

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

		public Integer getOrder() {
			return order;
		}

		public void setOrder(Integer order) {
			this.order = order;
		}

		public Boolean getNullable() {
			return nullable;
		}

		public void setNullable(Boolean nullable) {
			this.nullable = nullable;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}