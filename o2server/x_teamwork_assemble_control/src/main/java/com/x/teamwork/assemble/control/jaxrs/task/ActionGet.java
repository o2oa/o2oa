package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author sword
 */
public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		Task task = taskQueryService.getFromCache( flag );
		if ( task == null) {
			throw new TaskNotExistsException(flag);
		}

		if(!isReader(task.getId(), effectivePerson)){
			throw new ExceptionAccessDenied(effectivePerson);
		}

		TaskExtField taskExtField = taskQueryService.getExtField( flag );
		List<CustomExtFieldRele> extFieldRelList = customExtFieldReleQueryService.listReleWithCorrelation( task.getId() );

		Wo wo = Wo.copier.copy( task );

		wo.setParentName(" ");
		if(!Task.TOP_TASK.equals(task.getParent())){
			Task parentTask = taskQueryService.getFromCache( task.getParent() );
			if(parentTask != null){
				wo.setParentName(parentTask.getName());
			}
		}

		if( taskExtField != null ) {
			wo.setExtField( WoTaskExtField.copier.copy( taskExtField ));
		}

		if( ListTools.isNotEmpty( extFieldRelList )) {
			List<WoExtFieldRele> relList = WoExtFieldRele.copier.copy( extFieldRelList ) ;
			for( WoExtFieldRele woExtFieldRele :  relList ) {
				woExtFieldRele.setValue( taskQueryService.getValueFromTaskExtField( taskExtField, woExtFieldRele.getExtFieldName() ));
			}
			wo.setExtFieldConfigs( relList );
		}

		List<TaskTag> tags = taskTagQueryService.listWithTaskAndPerson(effectivePerson, task);
		if( ListTools.isNotEmpty( tags )) {
			wo.setTags( WoTaskTag.copier.copy( tags ));
		}

		WrapOutControl control = new WrapOutControl();
		if( this.canEdit(task, effectivePerson)){
			control.setDelete( true );
			control.setSortable( true );
			control.setChangeExecutor( true );
			control.setEdit( true );
		}
		control.setFounder(this.isParentManager(task, effectivePerson));

		wo.setControl(control);

		List<String> groupIds = taskGroupQueryService.listGroupIdsByPersonAndProject(effectivePerson,task.getProject());
		if( ListTools.isNotEmpty( groupIds )){
			wo.setTaskGroupId( groupIds.get(0) );
		}

		List<String> listIds = taskListQueryService.listTaskListIdWithTaskId( task.getId(), wo.getTaskGroupId() );
		if( ListTools.isNotEmpty( listIds )){
			wo.setTaskListId( listIds.get(0) );
		}else{
			List<TaskList> taskList = taskListQueryService.listWithTaskGroup( effectivePerson.getDistinguishedName(), wo.getTaskGroupId() );
			if(taskList !=null){
				wo.setTaskListId(taskList.get(0).getId());
			}
		}

		result.setData(wo);
		return result;
	}

	public static class Wo extends Task {

		@FieldDescribe("扩展属性信息(对象)")
		private WoTaskExtField extField;

		@FieldDescribe("任务标签(列表)")
		private List<WoTaskTag> tags = null;

		@FieldDescribe("所属项目的扩展列设定(配置列表)")
		private List<WoExtFieldRele> extFieldConfigs;

		@FieldDescribe("任务权限")
		private WrapOutControl control = null;

		@FieldDescribe("工作任务所属的工作任务组信息ID")
		private String taskGroupId = null;

		@FieldDescribe("工作任务所属的工作任务列表（泳道）信息ID")
		private String taskListId = null;

		@FieldDescribe("上级工作任务名称")
		private String parentName;

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

		public String getParentName() {
			return parentName;
		}

		public void setParentName(String parentName) {
			this.parentName = parentName;
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

		@FieldDescribe("关联ID（必填）")
		private String correlationId;

		@FieldDescribe("备用列名（必填）")
		private String extFieldName;

		@FieldDescribe("显示属性名称（必填）")
		private String displayName;

		@FieldDescribe("类型：project|task（必填）")
		private String type;

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

		static WrapCopier<CustomExtFieldRele, WoExtFieldRele> copier = WrapCopierFactory.wo( CustomExtFieldRele.class, WoExtFieldRele.class, null, ListTools.toList(JpaObject.FieldsInvisible));

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

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getCorrelationId() {
			return correlationId;
		}

		public void setCorrelationId(String correlationId) {
			this.correlationId = correlationId;
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
