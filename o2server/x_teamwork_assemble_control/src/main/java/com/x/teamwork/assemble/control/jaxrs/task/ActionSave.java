package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
import java.util.Date;
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
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.service.BatchOperationPersistService;
import com.x.teamwork.assemble.control.service.BatchOperationProcessService;
import com.x.teamwork.assemble.control.service.MessageFactory;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Review;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.TaskExtField;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskList;
import com.x.teamwork.core.entity.TaskStatuType;
import com.x.teamwork.core.entity.TaskTag;
import com.x.teamwork.core.entity.TaskView;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Task task = null;
		Task oldTask = null;
		Task parentTask = null;
		TaskDetail oldTaskDetail = null;
		Project project = null;
		TaskGroup taskGroup = null;
		Wi wi = null;
		Boolean check = true;
		Boolean split = false;
		Wo wo = new Wo();
		List<Dynamic> dynamics  = new ArrayList<>();
		List<Dynamic> tagDynamics = new ArrayList<>();
		
		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new TaskPersistException(e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if (check) {
			try {
				oldTask = taskQueryService.get( wi.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定ID查询工作任务信息对象时发生异常。ID:" + wi.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				oldTaskDetail = taskQueryService.getDetail( wi.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定ID查询工作任务信息对象时发生异常。ID:" + wi.getId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if( StringUtils.isEmpty( wi.getProject() ) && oldTask != null ) {
				wi.setProject( oldTask.getProject() );
			}
		}
		
		if (check) {
			if( StringUtils.isEmpty( wi.getTaskGroupId() ) && StringUtils.isEmpty( wi.getProject() )  ) {
				check = false;
				Exception exception = new TaskGroupIdAndProjectEmptyException( );
				result.error(exception);
			}
		}
		
		if (check) {
			if( StringUtils.isNotEmpty( wi.getTaskGroupId()) ) {
				try {
					taskGroup = taskGroupQueryService.get( wi.getTaskGroupId() );
					if( taskGroup == null ) {
						check = false;
						Exception exception = new TaskPersistException( "指定的工作任务组不存在！taskGroupId:" + wi.getTaskGroupId() );
						result.error(exception);
					}else {
						wi.setProject( taskGroup.getProject());
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskPersistException(e, "根据指定ID查询工作任务组信息对象时发生异常.taskGroupId:" + wi.getTaskGroupId() );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if (check) {
			try {
				project = projectQueryService.get( wi.getProject() );
				if( project == null ) {
					check = false;
					Exception exception = new TaskPersistException( "指定的项目信息不存在！projectID:" + wi.getProject() );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskPersistException(e, "根据指定ID查询项目信息对象时发生异常.projectID:" + wi.getProject() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if ( !taskPersistService.checkPermissionForPersist( project, effectivePerson ) ) {
				check = false;
				Exception exception = new TaskPersistException("task save permission denied!" );
				result.error(exception);
			}
		}
		
		if (check) {
			if( taskGroup == null ) {
				//查询默认的全部工作的taskGroup
				taskGroup = taskGroupQueryService.getDefaultTaskGroupWithProject( effectivePerson, project.getId() );
			}
		}
		
		if (check) {
			//校验parentID， 确认上级任务是否存在， parentId = "0"或者为空都不会视为工作拆解
			if( StringUtils.isNotEmpty( wi.getParent() ) && !wi.getParent().equalsIgnoreCase( "0" ) && !wi.getParent().equalsIgnoreCase( wi.getId() )) {
				try {
					parentTask = taskQueryService.get( wi.getParent() );
					if( parentTask == null ) {
						check = false;
						Exception exception = new TaskPersistException("parent task not exists!PID=" + wi.getParent() );
						result.error(exception);
					}else {
						//判断是否是新的工作拆解操作
						if( StringUtils.isEmpty( wi.getId() ) || taskQueryService.get( wi.getId() ) == null ) {
							split = true; //新增下级任务
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new TaskQueryException(e, "根据指定ID查询工作任务信息对象时发生异常。ID:" + wi.getParent());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}		
		
		if (check) {
			//如果是标识为已完成，那么需要判断一下，该任务的所有下级任务是否全部都已经完成，否则不允许标识为已完成
			if( TaskStatuType.completed.name().equalsIgnoreCase( wi.getWorkStatus() )) {
				List<Task> unCompletedTasks =  taskQueryService.allUnCompletedSubTasks( wi.getId() );
				if( ListTools.isNotEmpty( unCompletedTasks )) {
					check = false;
					Exception exception = new TaskPersistException( "当前任务还有"+ unCompletedTasks.size() +"个子任务未完成，暂无法设定为已完成任务。");
					result.error(exception);
				}
			}
		}
		
		if (check) {
			TaskDetail taskDetail = new TaskDetail();
			taskDetail.setId( wi.getId() );
			taskDetail.setProject( project.getId() );
			taskDetail.setDescription( wi.getDescription() );
			taskDetail.setDetail( wi.getDetail() );
			
			TaskExtField taskExtField = new TaskExtField();
			taskExtField.setId( wi.getId());
			taskExtField.setProject( project.getId() );
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
			
			try {	
				wi.setProject( project.getId() );
				task = taskPersistService.save( Wi.copier.copy(wi), taskDetail, taskExtField,  effectivePerson );
				
				taskListPersistService.addTaskToTaskListWithOrderNumber( task.getId(), wi.getTaskListIds(), null,  effectivePerson);
				
				// 更新缓存
				ApplicationCache.notify( Task.class );
				ApplicationCache.notify( TaskView.class );
				ApplicationCache.notify( Review.class );	
				ApplicationCache.notify( TaskGroup.class );	
				ApplicationCache.notify( TaskList.class );
				
				wo.setId( task.getId() );			
				
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskPersistException(e, "工作任务信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		//检查标签是否有变动
		if (check) {
			//检查任务和标签的所有关联
			List<String> tagIds = taskTagQueryService.listTagIdsWithTask( effectivePerson, task.getId() );
			if( ListTools.isNotEmpty( wi.getTaskTagIds() )) {
				TaskTag taskTag = null; 
				for( String _tagId : wi.getTaskTagIds() ) {
					try {
						taskTag = taskTagQueryService.get( _tagId );
						if ( taskTag == null) {
							check = false;
							Exception exception = new TaskTagNotExistsException( _tagId );
							result.error( exception );
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new TaskPersistException(e, "根据指定flag查询应用工作任务标签信息对象时发生异常。ID:" + _tagId);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
						break;
					}
					if( !tagIds.contains( _tagId )) {
						//需要新增
						taskTagPersistService.addTagRele( task, taskTag, effectivePerson );
						tagDynamics.add( dynamicPersistService.addTaskTagReleDynamic( task, taskTag, effectivePerson ));
					}
				}
				if( check && ListTools.isNotEmpty( tagIds )) {
					for( String _tagId : tagIds ) {
						if( !wi.getTaskTagIds().contains( _tagId )) {
							try {
								taskTag = taskTagQueryService.get( _tagId );
								if ( taskTag == null) {
									check = false;
									Exception exception = new TaskTagNotExistsException( _tagId );
									result.error( exception );
								}
							} catch (Exception e) {
								check = false;
								Exception exception = new TaskPersistException(e, "根据指定flag查询应用工作任务标签信息对象时发生异常。ID:" + _tagId);
								result.error(exception);
								logger.error(e, effectivePerson, request, null);
								break;
							}
							//需要删除
							taskTagPersistService.removeTagRele( task.getId(), _tagId, effectivePerson);
							tagDynamics.add( dynamicPersistService.removeTaskTagReleDynamic( task, taskTag, effectivePerson ));
						}
					}
				}
			}else {
				if( ListTools.isNotEmpty( tagIds )) {
					TaskTag taskTag = null; 
					for( String _tagId : tagIds ) {
						try {
							taskTag = taskTagQueryService.get( _tagId );
							if ( taskTag == null) {
								check = false;
								Exception exception = new TaskTagNotExistsException( _tagId );
								result.error( exception );
							}
						} catch (Exception e) {
							check = false;
							Exception exception = new TaskPersistException(e, "根据指定flag查询应用工作任务标签信息对象时发生异常。ID:" + _tagId);
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
							break;
						}
						//需要删除
						taskTagPersistService.removeTagRele( task.getId(), _tagId, effectivePerson);
						tagDynamics.add( dynamicPersistService.removeTaskTagReleDynamic( task, taskTag, effectivePerson ));
					}
				}
			}
		}
		
		if (check) {
			try {					
				new BatchOperationPersistService().addOperation( 
						BatchOperationProcessService.OPT_OBJ_TASK, 
						BatchOperationProcessService.OPT_TYPE_PERMISSION,  task.getId(),  task.getId(), "刷新文档权限：ID=" +   task.getId() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		
		if (check) {
			//记录父工作任务拆解的动态记录
			if( split && parentTask != null ) {
				dynamics = new ArrayList<>();
				try {
					dynamics.add( dynamicPersistService.taskSplitDynamic( parentTask, task, effectivePerson ));
				} catch (Exception e) {
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if (check) {
			try {
				if( oldTask == null ) {
					MessageFactory.message_to_teamWorkCreate( task );
				}else {
					MessageFactory.message_to_teamWorkUpdate( task );
				}
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			//记录工作任务信息变化记录
			try {
				if( ListTools.isNotEmpty( dynamics ) ) {
					dynamicPersistService.taskSaveDynamic( oldTask, task, oldTaskDetail, effectivePerson,  jsonElement.toString() );
				}else {
					dynamics = dynamicPersistService.taskSaveDynamic( oldTask, task, oldTaskDetail, effectivePerson,  jsonElement.toString() );
				}
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( ListTools.isEmpty( dynamics ) ) {
			dynamics = new ArrayList<>();
		}
		
		dynamics.addAll( tagDynamics );
		wo.setDynamics( WoDynamic.copier.copy( dynamics ) );
		result.setData( wo );
		return result;
	}	

	public static class Wi {
		
		public static WrapCopier<Wi, Task> copier = WrapCopierFactory.wi( Wi.class, Task.class, null, null );
		
		@FieldDescribe("数据库主键，非必填，自动生成.")
		private String id;
		
		@FieldDescribe("所属项目ID，<font style='color:red'>必填</font>")
		private String project;

		@FieldDescribe("父级工作任务ID，非必填.")
		private String parent;	

		@FieldDescribe("工作任务名称（40字），<font style='color:red'>必填</font>")
		private String name;

		@FieldDescribe("工作任务概括（80字），非必填")
		private String summay;

		@FieldDescribe("工作开始时间，非必填")
		private Date startTime;

		@FieldDescribe("工作开始时间，非必填")
		private Date endTime;
		
		@FieldDescribe("工作状态：processing | completed，非必填，默认processing")
		private String workStatus = "processing";

		@FieldDescribe("工作优先级：普通 | 紧急 | 特急 ，非必填")
		private String priority = "普通";

		@FieldDescribe("提醒关联任务，非必填")
		private Boolean remindRelevance;
		
		@FieldDescribe("执行者和负责人，<font style='color:red'>必填</font>")
		private String executor;	
		
		@FieldDescribe("执行者|负责人身份，非必填，若有则以identity为准")
		private String executorIdentity;	
		
		@FieldDescribe("工作内容(128K)，非必填")
		private String detail;
		
		@FieldDescribe("说明信息(10M)，非必填")
		private String description;		
		
		@FieldDescribe("工作任务组ID，非必填，与taskListIds必须填写一种")
		private String taskGroupId;
		
		@FieldDescribe("任务默认归类的任务列表ID，非必填，与taskGroupId必须填写一种")
		private List<String> taskListIds;
		
		@FieldDescribe("工作任务标签ID列表，非必填")
		private List<String> taskTagIds;
		
		@FieldDescribe("工作任务参与者，非必填")
		private List<String> participantList;

		@FieldDescribe("工作任务管理者，非必填")
		private List<String> manageablePersonList;		

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

		public List<String> getParticipantList() {
			return participantList;
		}

		public void setParticipantList(List<String> participantList) {
			this.participantList = participantList;
		}

		public List<String> getManageablePersonList() {
			return manageablePersonList;
		}

		public void setManageablePersonList(List<String> manageablePersonList) {
			this.manageablePersonList = manageablePersonList;
		}

		public String getWorkStatus() {
			return workStatus;
		}

		public void setWorkStatus(String workStatus) {
			this.workStatus = workStatus;
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

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getProject() {
			return project;
		}

		public void setProject(String project) {
			this.project = project;
		}

		public String getParent() {
			return parent;
		}

		public void setParent(String parent) {
			this.parent = parent;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSummay() {
			return summay;
		}

		public void setSummay(String summay) {
			this.summay = summay;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getEndTime() {
			return endTime;
		}

		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}

		public String getPriority() {
			return priority;
		}

		public void setPriority(String priority) {
			this.priority = priority;
		}

		public Boolean getRemindRelevance() {
			return remindRelevance;
		}

		public void setRemindRelevance(Boolean remindRelevance) {
			this.remindRelevance = remindRelevance;
		}

		public String getExecutor() {
			return executor;
		}

		public void setExecutor(String executor) {
			this.executor = executor;
		}

		public String getExecutorIdentity() {
			return executorIdentity;
		}

		public void setExecutorIdentity(String executorIdentity) {
			this.executorIdentity = executorIdentity;
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