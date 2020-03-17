package com.x.teamwork.assemble.control.jaxrs.task;

import java.util.ArrayList;
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
import com.x.teamwork.assemble.control.service.MessageFactory;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.ProjectExtFieldRele;
import com.x.teamwork.core.entity.Review;
import com.x.teamwork.core.entity.Task;
import com.x.teamwork.core.entity.TaskDetail;
import com.x.teamwork.core.entity.TaskExtField;
import com.x.teamwork.core.entity.TaskStatuType;

public class ActionUpdateSingleProperty extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionUpdateSingleProperty.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String taskId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Task oldTask = null;
		TaskDetail oldDetail = null;
		TaskExtField oldExtField = null;
		Wi wi = null;
		Wo wo = new Wo();
		Boolean check = true;
		Dynamic dynamicInfo = new Dynamic();
		List<Dynamic> dynamics = null;

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
				oldTask = taskQueryService.get( taskId );
				if( oldTask == null ) {
					check = false;
					Exception exception = new TaskNotExistsException(taskId);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定ID查询工作任务信息对象时发生异常。ID:" + taskId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				oldDetail = taskQueryService.getDetail( taskId );
				if( oldDetail == null ) {
					oldDetail = new  TaskDetail();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定ID查询工作任务信息对象时发生异常。ID:" + taskId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				oldExtField = taskQueryService.getExtField( taskId );
				if( oldExtField == null ) {
					oldExtField = new TaskExtField();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskQueryException(e, "根据指定ID查询工作任务扩展属性信息对象时发生异常。ID:" + taskId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			//如果是标识为已完成，那么需要判断一下，该任务的所有下级任务是否全部都已经完成，否则不允许标识为已完成
			if( Task.workStatus_FIELDNAME.equalsIgnoreCase(wi.getProperty()) && TaskStatuType.completed.name().equalsIgnoreCase( wi.getMainValue().toString() )) {
				List<Task> unCompletedTasks =  taskQueryService.allUnCompletedSubTasks( taskId );
				if( ListTools.isNotEmpty( unCompletedTasks )) {
					check = false;
					Exception exception = new TaskPersistException( "当前任务还有"+ unCompletedTasks.size() +"个子任务未完成，暂无法设定为已完成任务。");
					result.error(exception);
				}
			}
		}
		
		if (check) {	
			try {
				if( TaskDetail.description_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, "任务说明", "UPDATE_DESCRIPTION", 
							wi.getProperty(), oldDetail.getDescription(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), true );
				} else  if( TaskDetail.detail_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, "详细内容", "UPDATE_DETAIL", 
							wi.getProperty(), oldDetail.getDetail(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), true );
				}  else if( Task.workStatus_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, "任务状态", "UPDATE_WORKSTATUS", 
							wi.getProperty(), oldTask.getWorkStatus(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
				} else if( Task.priority_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, "优先级", "UPDATE_PRIORITY", 
							wi.getProperty(), oldTask.getPriority(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
				}   else if( Task.name_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, "任务名称", "UPDATE_NAME", 
							wi.getProperty(), oldTask.getName(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
				}   else if( Task.executor_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, "工作负责人", "UPDATE_EXECUTOR", 
							wi.getProperty(), oldTask.getExecutor(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
				} else if( Task.startTime_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, "任务开始日期", "UPDATE_STARTDATE", 
							wi.getProperty(), oldTask.getExecutor(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );	
				} else if( Task.endTime_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, "任务截止日期", "UPDATE_WORKDATE", 
							wi.getProperty(), oldTask.getExecutor(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType(), false );
				} else if( TaskExtField.memoString_1_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeExtTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, wi.getProperty(), oldExtField.getMemoString_1(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
				} else if( TaskExtField.memoString_2_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeExtTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, wi.getProperty(), oldExtField.getMemoString_2(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
				} else if( TaskExtField.memoString_3_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeExtTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, wi.getProperty(), oldExtField.getMemoString_3(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
				} else if( TaskExtField.memoString_4_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeExtTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, wi.getProperty(), oldExtField.getMemoString_4(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
				} else if( TaskExtField.memoString_5_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeExtTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, wi.getProperty(), oldExtField.getMemoString_5(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
				} else if( TaskExtField.memoString_6_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeExtTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, wi.getProperty(), oldExtField.getMemoString_6(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
				} else if( TaskExtField.memoString_7_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeExtTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, wi.getProperty(), oldExtField.getMemoString_7(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
				} else if( TaskExtField.memoString_8_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeExtTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, wi.getProperty(), oldExtField.getMemoString_8(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
				}  else if( TaskExtField.memoString_1_lob_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeExtTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, wi.getProperty(), oldExtField.getMemoString_1_lob(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
				}  else if( TaskExtField.memoString_2_lob_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeExtTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, wi.getProperty(), oldExtField.getMemoString_2_lob(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
				}  else if( TaskExtField.memoString_3_lob_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeExtTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, wi.getProperty(), oldExtField.getMemoString_3_lob(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
				}  else if( TaskExtField.memoString_4_lob_FIELDNAME.equalsIgnoreCase( wi.getProperty() )) {
					dynamicInfo = changeExtTaskProperty( 
							effectivePerson.getName(), oldTask.getProject(), taskId, wi.getProperty(), oldExtField.getMemoString_4_lob(), wi.getMainValue(), wi.getSecondaryValue(), wi.getDataType() );
				} else {
					check = false;
					Exception exception = new TaskPersistException( "工作任务属性暂不支持属性名称:" + wi.getProperty() );
					result.error(exception);
				}
				
				// 缓存
				ApplicationCache.notify( Task.class );
				ApplicationCache.notify( Review.class );					
				
				wo.setId( taskId );			
				
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
						BatchOperationProcessService.OPT_TYPE_PERMISSION,  oldTask.getId(),  oldTask.getId(), "变更任务信息，刷新Review：ID=" +   taskId );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		
		if (check) {
			try {
				MessageFactory.message_to_teamWorkUpdate( oldTask );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			//记录工作任务属性信息变化记录
			try {
				dynamics = dynamicPersistService.taskUpdatePropertyDynamic( oldTask,  dynamicInfo.getTitle(), dynamicInfo.getOptType(), dynamicInfo.getDescription(), effectivePerson );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( ListTools.isNotEmpty( dynamics ) ) {
			wo.setDynamics( WoDynamic.copier.copy( dynamics ) );
		}
		result.setData( wo );
		return result;
	}	

	private Dynamic changeTaskProperty( String personName,  String projectId, String taskId, String dynamicTitle, String dynamicOptType, String property, String oldValue, String mainValue, String secondaryValue, String dataType, Boolean nullable ) throws Exception {
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
		taskPersistService.changeTaskProperty( taskId, property, mainValue, secondaryValue );
		
		Dynamic dynamic_info = new Dynamic();
		dynamic_info.setTitle( dynamicTitle  );
		dynamic_info.setOptType( dynamicOptType );
		dynamic_info.setDescription( dynamicDescription );
		return dynamic_info;
	}
	
	private Dynamic changeExtTaskProperty( String personName,  String projectId, String taskId, String property, String oldValue, String mainValue, String secondaryValue, String dataType ) throws Exception {
		ProjectExtFieldRele projectExtFieldRele = projectExtFieldReleQueryService.getExtFieldRele(projectId, property);
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
		dynamic_info.setTitle( dynamicTitle  );
		dynamic_info.setOptType( dynamicOptType );
		dynamic_info.setDescription( dynamicDescription );
		return dynamic_info;
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