package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;
import com.x.teamwork.assemble.control.Business;
import com.x.teamwork.core.entity.*;
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
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.assemble.control.service.MessageFactory;

/**
 * 任务保存
 * @author sword
 */
public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Task parentTask = null;
		Wi wi = this.convertToWrapIn( jsonElement, Wi.class );
		if(StringUtils.isBlank(wi.getId())){
			wi.setId(StringTools.uniqueToken());
		}
		Boolean split = false;

		if(StringUtils.isBlank(wi.getName())){
			throw new TaskPersistException("标题不允许为空!");
		}

		Task oldTask = taskQueryService.get( wi.getId() );
		if(oldTask != null ) {
			wi.setProject( oldTask.getProject() );
		}

		if( StringUtils.isEmpty( wi.getTaskGroupId() ) && StringUtils.isEmpty( wi.getProject() )  ) {
			throw new TaskGroupIdAndProjectEmptyException();
		}
		if( StringUtils.isNotEmpty( wi.getTaskGroupId()) ) {
			TaskGroup taskGroup = taskGroupQueryService.get( wi.getTaskGroupId() );
			if( taskGroup == null ) {
				throw new TaskPersistException( "指定的工作任务组不存在！taskGroupId:" + wi.getTaskGroupId() );
			}else {
				wi.setProject( taskGroup.getProject());
			}
		}

		String targetId = wi.getProject();
		Project project = projectQueryService.get( wi.getProject() );
		if( project == null ) {
			throw new TaskPersistException( "指定的项目信息不存在！projectID:" + wi.getProject() );
		}
		if(ProjectStatusEnum.isEndStatus(project.getWorkStatus())){
			throw new TaskPersistException("当前任务不允许保存!");
		}

		if(StringUtils.isNotEmpty(wi.getParent()) && !wi.getParent().equals( Task.TOP_TASK )){
			parentTask = taskQueryService.get( wi.getParent() );
			if( parentTask == null ) {
				throw new TaskPersistException("指定的上级任务不存在!PID=" + wi.getParent() );
			}
			if(ProjectStatusEnum.isEndStatus(parentTask.getWorkStatus())){
				throw new TaskPersistException("当前任务不允许保存!");
			}
			targetId = wi.getParent();
		}else{
			wi.setParent(Task.TOP_TASK);
			if(oldTask == null) {
				split = true;
			}
		}

		if(oldTask != null) {
			targetId = wi.getId();
		}else{
			wi.setWorkStatus(ProjectStatusEnum.PROCESSING.getValue());
		}

		if(!this.isManager(targetId, effectivePerson)){
			throw new TaskPersistException("权限不足!");
		}

		Task task = this.saveTask(wi, effectivePerson, oldTask, parentTask);
		wo.setId( task.getId() );
		List<Dynamic> dynamics = this.saveDynamic(split, oldTask, task, parentTask, effectivePerson, jsonElement);
		wo.setDynamics( WoDynamic.copier.copy( dynamics ) );

		this.clearCache();
		result.setData( wo );
		return result;
	}

	/**
	 * 新增或修改任务
	 * @param wi
	 * @param effectivePerson
	 * @param oldTask
	 * @param parentTask
	 * @return
	 * @throws Exception
	 */
	private synchronized Task saveTask(Wi wi, EffectivePerson effectivePerson, Task oldTask, Task parentTask) throws Exception{
		Task task;
		if (StringTools.utf8Length(wi.getName()) > JpaObject.length_255B) {
			wi.setName(StringTools.utf8SubString(wi.getName(), JpaObject.length_255B - 3) + "...");
		}
		if (OrganizationDefinition.isIdentityDistinguishedName(wi.getExecutor())) {
			String person = userManagerService.getPersonNameWithIdentity(wi.getExecutor());
			wi.setExecutor(person);
		}
		if(StringUtils.isBlank(wi.getExecutor())){
			wi.setExecutor(effectivePerson.getDistinguishedName());
		}
		if (StringUtils.isEmpty(wi.getWorkStatus())) {
			wi.setWorkStatus(ProjectStatusEnum.PROCESSING.getValue());
		}
		if (wi.getStartTime() == null) {
			wi.setStartTime(new Date());
		}
		if (wi.getEndTime() == null) {
			wi.setEndTime(DateTools.addDay(wi.getStartTime(), 7));
		}
		wi.setParticipantList(wi.getParticipantList().stream().distinct().collect(Collectors.toList()));
		boolean savePermission = false;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			emc.beginTransaction(Task.class);
			if(oldTask != null){
				task = emc.find(wi.getId(), Task.class);
				if(task.getPublishTime() == null) {
					task.setPublishTime(task.getCreateTime());
				}
				String executor = task.getExecutor();
				List<String> participantList = task.getParticipantList();
				wi.copyTo(task, Wi.excludes);
				emc.check(task, CheckPersistType.all);

				executor = executor.equals(task.getExecutor()) ? null : task.getExecutor();
				boolean flag = participantList.size() == task.getParticipantList().size() && participantList.containsAll(task.getParticipantList());
				participantList = flag ? null : task.getParticipantList();
				savePermission = taskPersistService.savePermission(business,task, executor, participantList);
			}else{
				task = Wi.copier.copy(wi);
				if(task.getPublishTime() == null) {
					task.setPublishTime(new Date());
				}

				Long count = business.taskFactory().countWithParentAndProject(parentTask == null ? Task.TOP_TASK : parentTask.getId(), wi.getProject()) + 1;
				String parentSerial = parentTask == null ? "" : (StringUtils.isBlank(parentTask.getSerialNumber()) ? "" : parentTask.getSerialNumber() + Task.SERIAL_NUMBER_SEPARATOR);
				task.setSerialNumber(parentSerial + String.format("%04d", count));
				task.setCreatorPerson(effectivePerson.getDistinguishedName());
				emc.persist( task, CheckPersistType.all);
				taskPersistService.savePermission(business, task, task.getExecutor(), task.getParticipantList());
			}
			emc.beginTransaction(TaskExtField.class);
			TaskExtField taskExtField = this.joinTaskExtField(wi, task.getId());
			TaskExtField oldTaskExtField = emc.find(wi.getId(), TaskExtField.class);
			if(oldTaskExtField == null){
				emc.persist( taskExtField, CheckPersistType.all);
			}else{
				taskExtField.copyTo( oldTaskExtField, JpaObject.FieldsUnmodify);
				emc.check( taskExtField, CheckPersistType.all );
			}
			emc.commit();
		}

		this.afterSave(wi, oldTask, task, effectivePerson, savePermission);
		return task;
	}

	private void afterSave(Wi wi, Task oldTask, Task task, EffectivePerson effectivePerson, boolean savePermission) throws Exception{
		if(savePermission){
			try {
				new BatchOperationPersistService().addOperation(
						BatchOperationProcessService.OPT_OBJ_TASK,
						BatchOperationProcessService.OPT_TYPE_PERMISSION,  task.getId(),  task.getId(), "刷新文档权限：ID=" +   task.getId() );
			} catch (Exception e) {
				logger.error(e);
			}
		}

		taskListPersistService.addTaskToTaskListWithOrderNumber( task.getId(), wi.getTaskListIds(), null,  effectivePerson);
		//查询该任务和任务组的绑定情况
		if( !taskGroupQueryService.existsWithTaskAndGroup( wi.getTaskGroupId(), task.getId() )){
			//添加任务和任务组的关联
			taskGroupPersistService.addTaskToGroup( task.getId(), wi.getTaskGroupId() );
			taskGroupPersistService.refreshTaskCountInTaskGroupWithTaskId( effectivePerson.getDistinguishedName(), task.getId() );
		}

		this.sendMessage(oldTask, task);
		this.updateParentProgress(task.getParent(), effectivePerson);
	}

	/**
	 * 任务保存引起父任务的进度变更(往上递归)
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

					this.changeTaskProgress(effectivePerson, oldTask, String.valueOf(progress));
					Task newTask = taskQueryService.get(oldTask.getId());

					this.sendMessage(oldTask, newTask);

					this.updateParentProgress(oldTask.getParent(), effectivePerson);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void changeTaskProgress(EffectivePerson effectivePerson, Task oldTask, String mainValue) throws Exception {
		String property = Task.progress_FIELDNAME;
		this.taskPersistService.changeTaskProperty( oldTask.getId(), property, mainValue, "" );

		String dynamicDescription =  effectivePerson.getName() + "将工作任务的[进度]变更为：[" + mainValue + "]。";

		String dynamicTitle = "工作任务的进度";
		this.dynamicPersistService.taskUpdatePropertyDynamic( oldTask,  dynamicTitle,
				"UPDATE_PROGRESS", dynamicDescription, effectivePerson );

	}

	private List<Dynamic> saveDynamic(boolean split, Task oldTask, Task task, Task parentTask,
									  EffectivePerson effectivePerson, JsonElement jsonElement){
		List<Dynamic> dynamics = new ArrayList<>();
		if( split && parentTask != null ) {
			try {
				dynamics.add( dynamicPersistService.taskSplitDynamic( parentTask, task, effectivePerson ));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		try {
			if( ListTools.isNotEmpty( dynamics ) ) {
				dynamicPersistService.taskSaveDynamic( oldTask, task, null, effectivePerson,  jsonElement.toString() );
			}else {
				dynamics = dynamicPersistService.taskSaveDynamic( oldTask, task, null, effectivePerson,  jsonElement.toString() );
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return dynamics;
	}

	private void sendMessage(Task oldTask, Task task){
		try {
			if( oldTask == null ) {
				MessageFactory.message_to_teamWorkCreate( task );
			}else {
				MessageFactory.message_to_teamWorkUpdate( oldTask, task );
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void clearCache(){
		CacheManager.notify( Task.class );
		CacheManager.notify( TaskView.class );
		CacheManager.notify( TaskGroup.class );
	}

	private TaskExtField joinTaskExtField(Wi wi, String id){
		TaskExtField taskExtField = new TaskExtField();
		taskExtField.setId( id);
		taskExtField.setProject( wi.getProject() );
		taskExtField.setName( wi.getName() );
		taskExtField.setMemoString_1( wi.getMemoString_1() );
		taskExtField.setMemoString_2( wi.getMemoString_2() );
		taskExtField.setMemoString_3( wi.getMemoString_3() );
		taskExtField.setMemoString_4( wi.getMemoString_4() );
		taskExtField.setMemoString_5( wi.getMemoString_5() );
		taskExtField.setMemoString_6( wi.getMemoString_6() );
		taskExtField.setMemoString_7( wi.getMemoString_7() );
		taskExtField.setMemoString_8( wi.getMemoString_8() );
		taskExtField.setMemoString_1_lob( wi.getMemoString_1_lob() );
		taskExtField.setMemoString_2_lob( wi.getMemoString_2_lob() );
		taskExtField.setMemoString_3_lob( wi.getMemoString_3_lob() );
		taskExtField.setMemoString_4_lob( wi.getMemoString_4_lob() );
		return taskExtField;
	}

	public static class Wi extends Task{

		public static final List<String> excludes = ListTools.toList(JpaObject.FieldsInvisible, Task.creatorPerson_FIELDNAME, Task.archive_FIELDNAME,
				Task.completed_FIELDNAME, Task.deleted_FIELDNAME, Task.claimed_FIELDNAME, Task.overtime_FIELDNAME,
				Task.relation_FIELDNAME, Task.reviewed_FIELDNAME, Task.projectName_FIELDNAME, Task.publishTime_FIELDNAME, Task.serialNumber_FIELDNAME);

		public static WrapCopier<Wi, Task> copier = WrapCopierFactory.wi( Wi.class, Task.class, null,
				excludes );

		@FieldDescribe("工作任务组ID，非必填，与taskListIds必须填写一种")
		private String taskGroupId;

		@FieldDescribe("任务默认归类的任务列表ID，非必填，与taskGroupId必须填写一种")
		private List<String> taskListIds;

		@FieldDescribe("工作任务标签ID列表，非必填")
		private List<String> taskTagIds;

		@FieldDescribe("备用属性1（最大长度：255）")
		private String memoString_1 = "";

		@FieldDescribe("备用属性2（最大长度：255）")
		private String memoString_2 = "";

		@FieldDescribe("备用属性3（最大长度：255）")
		private String memoString_3 = "";

		@FieldDescribe("备用属性4（最大长度：255）")
		private String memoString_4 = "";

		@FieldDescribe("备用属性5（最大长度：255）")
		private String memoString_5 = "";

		@FieldDescribe("备用属性6（最大长度：255）")
		private String memoString_6 = "";

		@FieldDescribe("备用属性7（最大长度：255）")
		private String memoString_7 = "";

		@FieldDescribe("备用属性8（最大长度：255）")
		private String memoString_8 = "";

		@FieldDescribe("备用长文本1（最大长度：10M）")
		private String memoString_1_lob = "";

		@FieldDescribe("备用长文本2（最大长度：10M）")
		private String memoString_2_lob = "";

		@FieldDescribe("备用长文本3（最大长度：10M）")
		private String memoString_3_lob = "";

		@FieldDescribe("备用长文本4（最大长度：10M）")
		private String memoString_4_lob = "";

		public List<String> getTaskTagIds() {
			return taskTagIds;
		}

		public void setTaskTagIds(List<String> taskTagIds) {
			this.taskTagIds = taskTagIds;
		}

		public List<String> getTaskListIds() {
			return taskListIds;
		}

		public void setTaskListIds(List<String> taskListIds) {
			this.taskListIds = taskListIds;
		}

		public String getTaskGroupId() {
			return taskGroupId;
		}

		public void setTaskGroupId(String taskGroupId) {
			this.taskGroupId = taskGroupId;
		}

		public String getMemoString_1() {
			return memoString_1;
		}

		public void setMemoString_1(String memoString_1) {
			this.memoString_1 = memoString_1;
		}

		public String getMemoString_2() {
			return memoString_2;
		}

		public void setMemoString_2(String memoString_2) {
			this.memoString_2 = memoString_2;
		}

		public String getMemoString_3() {
			return memoString_3;
		}

		public void setMemoString_3(String memoString_3) {
			this.memoString_3 = memoString_3;
		}

		public String getMemoString_4() {
			return memoString_4;
		}

		public void setMemoString_4(String memoString_4) {
			this.memoString_4 = memoString_4;
		}

		public String getMemoString_5() {
			return memoString_5;
		}

		public void setMemoString_5(String memoString_5) {
			this.memoString_5 = memoString_5;
		}

		public String getMemoString_6() {
			return memoString_6;
		}

		public void setMemoString_6(String memoString_6) {
			this.memoString_6 = memoString_6;
		}

		public String getMemoString_7() {
			return memoString_7;
		}

		public void setMemoString_7(String memoString_7) {
			this.memoString_7 = memoString_7;
		}

		public String getMemoString_8() {
			return memoString_8;
		}

		public void setMemoString_8(String memoString_8) {
			this.memoString_8 = memoString_8;
		}

		public String getMemoString_1_lob() {
			return memoString_1_lob;
		}

		public void setMemoString_1_lob(String memoString_1_lob) {
			this.memoString_1_lob = memoString_1_lob;
		}

		public String getMemoString_2_lob() {
			return memoString_2_lob;
		}

		public void setMemoString_2_lob(String memoString_2_lob) {
			this.memoString_2_lob = memoString_2_lob;
		}

		public String getMemoString_3_lob() {
			return memoString_3_lob;
		}

		public void setMemoString_3_lob(String memoString_3_lob) {
			this.memoString_3_lob = memoString_3_lob;
		}

		public String getMemoString_4_lob() {
			return memoString_4_lob;
		}

		public void setMemoString_4_lob(String memoString_4_lob) {
			this.memoString_4_lob = memoString_4_lob;
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
