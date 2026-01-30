package com.x.teamwork.assemble.control.jaxrs.task;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.assemble.control.service.MessageFactory;
import com.x.teamwork.core.entity.*;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 更新任务信息
 * @author O2LEE
 */
public class ActionUpdateSingleProperty extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger( ActionUpdateSingleProperty.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String taskId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn( jsonElement, Wi.class );
		Wo wo = new Wo();

		Task oldTask = taskQueryService.get( taskId );
		if( oldTask == null ) {
			throw new TaskNotExistsException(taskId);
		}

		this.checkUpdate(wi, oldTask, effectivePerson);

		Dynamic dynamicInfo = this.changeTaskInfo(wi, oldTask, effectivePerson);
		Task newTask = taskQueryService.get( taskId );

		wo.setId( taskId );

		List<Dynamic> dynamics = dynamicPersistService.taskUpdatePropertyDynamic( oldTask,  dynamicInfo.getTitle(),
				dynamicInfo.getOptType(), dynamicInfo.getDescription(), effectivePerson );
		if(dynamics!=null) {
			wo.setDynamics(WoDynamic.copier.copy(dynamics));
		}
		CacheManager.notify( Task.class );

		if(!oldTask.getExecutor().equals(newTask.getExecutor())) {
			new BatchOperationPersistService().addOperation(
					BatchOperationProcessService.OPT_OBJ_TASK,
					BatchOperationProcessService.OPT_TYPE_PERMISSION, oldTask.getId(), oldTask.getId(), "变更任务信息，刷新Review：ID=" + taskId);
		}

		this.sendMsg( oldTask, newTask );

		if(!oldTask.getWorkStatus().equals(newTask.getWorkStatus())
				&& ProjectStatusEnum.isEndStatus(newTask.getWorkStatus())) {
			Wi statusWi = new Wi();
			statusWi.setProperty(Task.workStatus_FIELDNAME);
			statusWi.setMainValue(newTask.getWorkStatus());
			this.updateSubTaskStatus(statusWi, newTask.getId(), effectivePerson);
		}

		if(!oldTask.getProgress().equals(newTask.getProgress()) ||
				!oldTask.getWorkStatus().equals(newTask.getWorkStatus())){
			this.updateParentProgress(newTask.getParent(), effectivePerson);
		}

		CacheManager.notify( Task.class );
		result.setData( wo );
		return result;
	}

	/**
	 * 校验是否允许修改
	 * @param wi
	 * @param oldTask
	 * @param effectivePerson
	 * @throws Exception
	 */
	private void checkUpdate(Wi wi, Task oldTask, EffectivePerson effectivePerson) throws Exception {
		if(ProjectStatusEnum.isEndStatus(oldTask.getWorkStatus()) && !Task.workStatus_FIELDNAME.equals(wi.getProperty())){
			throw new TaskPersistException("当前任务不允许修改!");
		}

		if(Wi.modifyFields.contains(wi.getProperty())){
			if(!this.isManager(oldTask.getId(), effectivePerson)){
				throw new TaskPersistException("权限不足!");
			}
		} else if(!this.isParentManager(oldTask, effectivePerson)){
			throw new TaskPersistException("权限不足!");
		}

		if(StringUtils.isNotEmpty(oldTask.getParent()) && !oldTask.getParent().equals( Task.TOP_TASK )){
			Task parentTask = taskQueryService.get( oldTask.getParent() );
			if( parentTask == null ) {
				throw new TaskPersistException("当前任务归属的上级任务不存在!PID=" + oldTask.getParent() );
			}
			if(ProjectStatusEnum.isEndStatus(parentTask.getWorkStatus())){
				throw new TaskPersistException("当前任务不允许修改!");
			}
		}

		Project project = projectQueryService.get( oldTask.getProject() );
		if( project == null ) {
			throw new TaskPersistException( "任务归属的项目不存在！projectID:" + oldTask.getProject() );
		}
		if(ProjectStatusEnum.isEndStatus(project.getWorkStatus())){
			throw new TaskPersistException("当前任务不允许修改!");
		}
	}

	/**
	 * 任务进度变更引起父任务的进度变更(往上递归)
	 * @param parentTaskId
	 * @param effectivePerson
	 */
	private void updateParentProgress(String parentTaskId, EffectivePerson effectivePerson){
		if(Task.TOP_TASK.equals(parentTaskId)){
			return;
		}
		try {
			Task oldTask = taskQueryService.get(parentTaskId);
			if(oldTask != null) {
				List<Task> taskList = taskQueryService.listTaskWithParentId(parentTaskId, Boolean.FALSE);
				long progress = Math.round(taskList.stream().collect(Collectors.averagingInt(Task::getProgress)));
				if(!oldTask.getProgress().equals(String.valueOf(progress))) {
					Wi wi = new Wi();
					wi.setProperty(Task.progress_FIELDNAME);
					wi.setMainValue(String.valueOf(progress));

					Dynamic dynamicInfo = this.changeTaskInfo(wi, oldTask, effectivePerson);
					Task newTask = taskQueryService.get(oldTask.getId());

					this.dynamicPersistService.taskUpdatePropertyDynamic(oldTask, dynamicInfo.getTitle(),
							dynamicInfo.getOptType(), dynamicInfo.getDescription(), effectivePerson);

					this.sendMsg(oldTask, newTask);

					this.updateParentProgress(oldTask.getParent(), effectivePerson);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * 任务的状态变更为取消或已完成引起子任务状态变更(往下递归)
	 * @param wi
	 * @param parentTaskId
	 * @param effectivePerson
	 */
	private void updateSubTaskStatus(Wi wi, String parentTaskId, EffectivePerson effectivePerson){
		List<Task> taskList = new ArrayList<>();
		try {
			taskList = taskQueryService.allUnCompletedSubTasks(parentTaskId);
		} catch (Exception e) {
			logger.error(e);
		}
		for (Task oldTask : taskList){
			try {
				this.updateSubTaskStatus(wi, oldTask.getId(), effectivePerson);
				Dynamic dynamicInfo = this.changeTaskInfo(wi, oldTask, effectivePerson);
				Task newTask = taskQueryService.get( oldTask.getId() );

				this.dynamicPersistService.taskUpdatePropertyDynamic( oldTask,  dynamicInfo.getTitle(),
						dynamicInfo.getOptType(), dynamicInfo.getDescription(), effectivePerson );

				this.sendMsg(oldTask, newTask);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 更新任务的一个字段信息
	 * @param wi
	 * @param oldTask
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	private Dynamic changeTaskInfo( Wi wi, Task oldTask, EffectivePerson effectivePerson) throws Exception{
		TaskExtField oldExtField = taskQueryService.getExtField( oldTask.getId() );
		if( oldExtField == null ) {
			oldExtField = new TaskExtField();
		}
		Dynamic dynamicInfo;
		if( Task.description_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), "任务说明", "UPDATE_DESCRIPTION",
					wi.getProperty(), oldTask.getDescription(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
		} else if( Task.detail_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), "进展说明", "UPDATE_DETAIL",
					wi.getProperty(), oldTask.getDetail(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
		} else if( Task.workStatus_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), "状态", "UPDATE_WORKSTATUS",
					wi.getProperty(), oldTask.getWorkStatus(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), true );
		} else if( Task.source_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), "来源", "UPDATE_SOURCE",
					wi.getProperty(), oldTask.getSource(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
		} else if( Task.priority_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), "优先级", "UPDATE_PRIORITY",
					wi.getProperty(), oldTask.getPriority(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
		} else if( Task.important_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), "重要程度", "UPDATE_IMPORTANT",
					wi.getProperty(), oldTask.getImportant(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
		} else if( Task.urgency_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), "紧急程度", "UPDATE_URGENCY",
					wi.getProperty(), oldTask.getUrgency(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
		} else if( Task.progress_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), "进度", "UPDATE_PROGRESS",
					wi.getProperty(), Objects.toString(oldTask.getProgress(), "0"), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
		} else if( Task.name_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), "名称", "UPDATE_NAME",
					wi.getProperty(), oldTask.getName(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
		} else if( Task.executor_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), "负责人", "UPDATE_EXECUTOR",
					wi.getProperty(), oldTask.getExecutor(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
		} else if( Task.startTime_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), "开始日期", "UPDATE_STARTDATE",
					wi.getProperty(), oldTask.getExecutor(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
		} else if( Task.endTime_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), "截止日期", "UPDATE_WORKDATE",
					wi.getProperty(), oldTask.getExecutor(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
		} else if( TaskExtField.memoString_1_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeExtTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), wi.getProperty(), oldExtField.getMemoString_1(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
		} else if( TaskExtField.memoString_2_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeExtTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), wi.getProperty(), oldExtField.getMemoString_2(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
		} else if( TaskExtField.memoString_3_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeExtTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), wi.getProperty(), oldExtField.getMemoString_3(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
		} else if( TaskExtField.memoString_4_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeExtTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), wi.getProperty(), oldExtField.getMemoString_4(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
		} else if( TaskExtField.memoString_5_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeExtTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), wi.getProperty(), oldExtField.getMemoString_5(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
		} else if( TaskExtField.memoString_6_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeExtTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), wi.getProperty(), oldExtField.getMemoString_6(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
		} else if( TaskExtField.memoString_7_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeExtTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), wi.getProperty(), oldExtField.getMemoString_7(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
		} else if( TaskExtField.memoString_8_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeExtTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), wi.getProperty(), oldExtField.getMemoString_8(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
		}  else if( TaskExtField.memoString_1_lob_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeExtTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), wi.getProperty(), oldExtField.getMemoString_1_lob(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
		}  else if( TaskExtField.memoString_2_lob_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeExtTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), wi.getProperty(), oldExtField.getMemoString_2_lob(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
		}  else if( TaskExtField.memoString_3_lob_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeExtTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), wi.getProperty(), oldExtField.getMemoString_3_lob(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
		}  else if( TaskExtField.memoString_4_lob_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
			dynamicInfo = changeExtTaskProperty(
					effectivePerson.getName(), oldTask.getProject(), oldTask.getId(), wi.getProperty(), oldExtField.getMemoString_4_lob(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
		} else {
			throw new TaskPersistException( "工作任务修改暂不支持属性:" + wi.getProperty() );
		}
		return dynamicInfo;
	}

	private Dynamic changeTaskProperty( String personName,  String projectId, String taskId, String dynamicTitle, String dynamicOptType, String property, String oldValue, String mainValue, String secondaryValue, String dataType, Boolean nullable ) throws Exception {

		taskPersistService.changeTaskProperty( taskId, property, mainValue, secondaryValue );
		if(Task.priority_FIELDNAME.equalsIgnoreCase( property )){
			mainValue = mainValue.split("\\|\\|")[0];
		}
		if(Task.workStatus_FIELDNAME.equals(property)) {
			mainValue = ProjectStatusEnum.getNameByValue(mainValue);
		}

		String dynamicDescription =  personName + "将工作任务的[" + dynamicTitle + "]变更为：[" + mainValue + "]。";
		if(  StringUtils.isEmpty( mainValue ) && nullable ) {
			Exception exception = new TaskPersistException( "工作任务属性["+ dynamicTitle +"]不允许为空，请检查您的输入。");
			throw exception;
		}
		if(  StringUtils.isEmpty( mainValue ) ) {
			if( StringUtils.isNotEmpty( oldValue )) {
				dynamicDescription = personName + "清除了工作任务的["+dynamicTitle+"]信息["+ transferOrganNameToShort(oldValue) +"]。";
			}
		}else {
			if(  StringUtils.isEmpty( oldValue ) ) {
				if( "RichText".equalsIgnoreCase(dataType )) {
					dynamicDescription =personName + "设置了工作任务属性["+ dynamicTitle +"]为：["+ transferOrganNameToShort(secondaryValue) +"]。";
				}else {
					dynamicDescription = personName + "设置了工作任务属性["+ dynamicTitle +"]为：["+ transferOrganNameToShort(mainValue) +"]。";
				}
			}else {
				if( "RichText".equalsIgnoreCase(dataType )) {
					dynamicDescription =personName + "将工作任务属性["+ dynamicTitle +"]变更为：["+ transferOrganNameToShort(secondaryValue) +"]。";
				}else {
					dynamicDescription = personName + "将工作任务属性["+ dynamicTitle +"]变更为：["+ transferOrganNameToShort(mainValue) +"]。";
				}
			}
		}

		Dynamic dynamic_info = new Dynamic();
		dynamic_info.setTitle( "工作任务的" + dynamicTitle  );
		dynamic_info.setOptType( dynamicOptType );
		dynamic_info.setDescription( dynamicDescription );
		return dynamic_info;
	}

	private Dynamic changeExtTaskProperty( String personName,  String projectId, String taskId, String property, String oldValue, String mainValue, String secondaryValue, String dataType ) throws Exception {
		CustomExtFieldRele projectExtFieldRele = customExtFieldReleQueryService.getExtFieldRele(projectId, property);
		if( projectExtFieldRele == null || StringUtils.isEmpty( projectExtFieldRele.getDisplayName() )) {
			Exception exception = new TaskPersistException( "工作任务未配置扩展属性:" + property );
			throw exception;
		}

		String dynamicTitle = projectExtFieldRele.getDisplayName();
		String dynamicOptType = "UPDATE_EXTFIELD";
		String dynamicDescription = personName + "将工作任务的[" + dynamicTitle + "]变更为：[" + mainValue + "]。";
		if(  StringUtils.isEmpty( mainValue ) && projectExtFieldRele.getNullable() ) {
			Exception exception = new TaskPersistException( "工作任务属性["+ dynamicTitle +"]不允许为空，请检查您的输入。");
			throw exception;
		}

		if(  StringUtils.isEmpty( mainValue ) ) {
			if( StringUtils.isNotEmpty( oldValue )) {
				dynamicDescription = personName + "清除了工作任务的["+dynamicTitle+"]信息["+ transferOrganNameToShort(oldValue) +"]。";
			}
		}else {
			if(  StringUtils.isEmpty( oldValue ) ) {
				if( "RichText".equalsIgnoreCase(dataType )) {
					dynamicDescription =personName + "设置了工作任务属性["+ dynamicTitle +"]为：["+ transferOrganNameToShort(secondaryValue) +"]。";
				}else {
					dynamicDescription = personName + "设置了工作任务属性["+ dynamicTitle +"]为：["+ transferOrganNameToShort(mainValue) +"]。";
				}
			}else {
				if( "RichText".equalsIgnoreCase(dataType )) {
					dynamicDescription =personName + "将工作任务属性["+ dynamicTitle +"]变更为：["+ transferOrganNameToShort(secondaryValue) +"]。";
				}else {
					dynamicDescription = personName + "将工作任务属性["+ dynamicTitle +"]变更为：["+ transferOrganNameToShort(mainValue) +"]。";
				}
			}
		}
		taskPersistService.changeTaskProperty( taskId, property, mainValue, secondaryValue );

		Dynamic dynamic_info = new Dynamic();
		dynamic_info.setTitle( "工作任务的" + dynamicTitle  );
		dynamic_info.setOptType( dynamicOptType );
		dynamic_info.setDescription( dynamicDescription );
		return dynamic_info;
	}

	private void sendMsg(Task oldTask, Task newTask){
		try {
			MessageFactory.message_to_teamWorkUpdate( oldTask, newTask );
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * 将值里的所有数据中组织类别相关的数据改成简称，人员，身份，组织，群组等，适应列表数据（使用##分隔）
	 * @param oldValue
	 * @return
	 */
	private String transferOrganNameToShort(String oldValue) {
		String[] array = oldValue.split("##");
		StringBuffer sb = new StringBuffer();
		if( array != null && array.length > 0) {
			for( int i = 0; i< array.length; i++ ) {
				if( i == 0) {
					sb.append( array[i].split("@")[0]);
				}else {
					sb.append("##").append( array[i].split("@")[0]);
				}
			}
		}
		return sb.toString();
	}

	public static class Wi {

		public static final Set<String> modifyFields = Set.of(Task.important_FIELDNAME, Task.urgency_FIELDNAME,
				Task.progress_FIELDNAME, Task.detail_FIELDNAME, Task.workStatus_FIELDNAME);

		public static WrapCopier<Wi, Task> copier = WrapCopierFactory.wi( Wi.class, Task.class, null, null );

		@FieldDescribe("需要修改的属性标识，<font style='color:red'>必填</font>。" )
		private String property;

		@FieldDescribe("属性的主要值，<font style='color:red'>必填</font>")
		private String mainValue;

		@FieldDescribe("属性次要值，如果工作任务时间中的结束时间，非必填")
		private String secondaryValue;

		@FieldDescribe("值的类别Number|Text|RichText，默认Text，如果是RichText, secondaryValue需要填写去除标签的文字内容（<70字），会记录到动态内容")
		private String dataType ="Text";

		public String getDataType() {
			return dataType;
		}

		public void setDataType(String dataType) {
			this.dataType = dataType;
		}

		public String getProperty() {
			return property;
		}

		public void setProperty(String property) {
			this.property = property;
		}

		public String getMainValue() {
			return mainValue;
		}

		public void setMainValue(String mainValue) {
			this.mainValue = mainValue;
		}

		public String getSecondaryValue() {
			return secondaryValue;
		}

		public void setSecondaryValue(String secondaryValue) {
			this.secondaryValue = secondaryValue;
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

		public static WrapCopier<Dynamic, WoDynamic> copier = WrapCopierFactory.wo( Dynamic.class, WoDynamic.class, null, null);

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}

}
