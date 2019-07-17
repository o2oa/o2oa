package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
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
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.Review;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.TaskGroup;
import com.x.teamwork.core.entity.TaskList;
import com.x.teamwork.core.entity.TaskStatuType;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Task task = null;
		Task oldTask = null;
		Task parentTask = null;
		Project project = null;
		TaskGroup taskGroup = null;
		Wi wi = null;
		Boolean check = true;

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
			taskDetail.setMemoLob1( wi.getMemoLob1() );
			taskDetail.setMemoLob2( wi.getMemoLob2() );
			taskDetail.setMemoLob3( wi.getMemoLob3() );
			
			try {	
				wi.setProject( project.getId() );
				task = taskPersistService.save( Wi.copier.copy(wi), taskDetail, effectivePerson );
				
				taskListPersistService.addTaskToTaskListWithOrderNumber( task.getId(), wi.getTaskListIds(), null,  effectivePerson);
				
				// 更新缓存
				ApplicationCache.notify( Task.class );
				ApplicationCache.notify( Review.class );	
				ApplicationCache.notify( TaskGroup.class );	
				ApplicationCache.notify( TaskList.class );	
				
				Wo wo = new Wo();
				wo.setId( task.getId() );			
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskPersistException(e, "工作任务信息保存时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
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
			//记录工作任务信息变化记录
			try {
				dynamicPersistService.taskSaveDynamic( oldTask, task, effectivePerson,  jsonElement.toString() );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
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

		@FieldDescribe("扩展字符串64属性1，非必填.")
		private String memoString64_1 = "";
		
		@FieldDescribe("扩展字符串64属性2，非必填.")
		private String memoString64_2 = "";
		
		@FieldDescribe("扩展字符串64属性3，非必填.")
		private String memoString64_3 = "";

		@FieldDescribe("扩展字符串255属性1，非必填.")
		private String memoString255_1 = "";
		
		@FieldDescribe("扩展字符串255属性2，非必填.")
		private String memoString255_2 = "";

		@FieldDescribe("扩展整型属性1，非必填.")
		private Integer memoInteger1 = 0;
		
		@FieldDescribe("扩展整型属性2.，非必填")
		private Integer memoInteger2 = 0;
		
		@FieldDescribe("扩展整型属性3，非必填.")
		private Integer memoInteger3 = 0;
		
		@FieldDescribe("扩展Double属性1，非必填.")
		private Double memoDouble1 = 0.0;	

		@FieldDescribe("扩展Double属性2，非必填.")
		private Double memoDouble2 = 0.0;	
		
		@FieldDescribe("工作内容(128K)，非必填")
		private String detail;
		
		@FieldDescribe("说明信息(10M)，非必填")
		private String description;
		
		@FieldDescribe("扩展LOB信息1(128K)，非必填")
		private String memoLob1;
		
		@FieldDescribe("扩展LOB信息2(128K)，非必填")
		private String memoLob2;
		
		@FieldDescribe("扩展LOB信息3(128K)，非必填")
		private String memoLob3;
		
		@FieldDescribe("工作任务组ID，非必填，与taskListIds必须填写一种")
		private String taskGroupId;
		
		@FieldDescribe("任务默认归类的任务列表ID，非必填，与taskGroupId必须填写一种")
		private List<String> taskListIds;
		
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

		public String getMemoLob1() {
			return memoLob1;
		}

		public void setMemoLob1(String memoLob1) {
			this.memoLob1 = memoLob1;
		}

		public String getMemoLob2() {
			return memoLob2;
		}

		public void setMemoLob2(String memoLob2) {
			this.memoLob2 = memoLob2;
		}

		public String getMemoLob3() {
			return memoLob3;
		}

		public void setMemoLob3(String memoLob3) {
			this.memoLob3 = memoLob3;
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

		public String getMemoString64_1() {
			return memoString64_1;
		}

		public void setMemoString64_1(String memoString64_1) {
			this.memoString64_1 = memoString64_1;
		}

		public String getMemoString64_2() {
			return memoString64_2;
		}

		public void setMemoString64_2(String memoString64_2) {
			this.memoString64_2 = memoString64_2;
		}

		public String getMemoString64_3() {
			return memoString64_3;
		}

		public void setMemoString64_3(String memoString64_3) {
			this.memoString64_3 = memoString64_3;
		}

		public String getMemoString255_1() {
			return memoString255_1;
		}

		public void setMemoString255_1(String memoString255_1) {
			this.memoString255_1 = memoString255_1;
		}

		public String getMemoString255_2() {
			return memoString255_2;
		}

		public void setMemoString255_2(String memoString255_2) {
			this.memoString255_2 = memoString255_2;
		}

		public Integer getMemoInteger1() {
			return memoInteger1;
		}

		public void setMemoInteger1(Integer memoInteger1) {
			this.memoInteger1 = memoInteger1;
		}

		public Integer getMemoInteger2() {
			return memoInteger2;
		}

		public void setMemoInteger2(Integer memoInteger2) {
			this.memoInteger2 = memoInteger2;
		}

		public Integer getMemoInteger3() {
			return memoInteger3;
		}

		public void setMemoInteger3(Integer memoInteger3) {
			this.memoInteger3 = memoInteger3;
		}

		public Double getMemoDouble1() {
			return memoDouble1;
		}

		public void setMemoDouble1(Double memoDouble1) {
			this.memoDouble1 = memoDouble1;
		}

		public Double getMemoDouble2() {
			return memoDouble2;
		}

		public void setMemoDouble2(Double memoDouble2) {
			this.memoDouble2 = memoDouble2;
		}
	}

	public static class Wo extends WoId {
	}
	
}