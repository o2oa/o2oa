package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AppealConfig;
import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionAppealCreate extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAppealCreate.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceAppealInfo attendanceAppealInfo = null;
		AttendanceAppealAuditInfo attendanceAppealAuditInfo = null;
		AttendanceDetail attendanceDetail = null;
		String appealAuditPersonName = null;
		String appealCheckPersonName = null;
		String appeal_auditor_type = null;
		String appeal_auditor_value = null;
		String appeal_checker_type = null;
		String appeal_checker_value = null;
		String appeal_flowType_value = null;
		String personName = null;
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			try {
				attendanceDetail = attendanceDetailServiceAdv.get( id );
				if ( attendanceDetail == null ) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailNotExists(id);
					result.error( exception );
				}else {
					personName = attendanceDetail.getEmpName();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在根据ID查询员工打卡信息时发生异常！ID:"+id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				appeal_auditor_type = attendanceSettingServiceAdv.getValueByCode( "APPEAL_AUDITOR_TYPE" );
				appeal_auditor_value = attendanceSettingServiceAdv.getValueByCode( "APPEAL_AUDITOR_VALUE" );
				appeal_checker_type = attendanceSettingServiceAdv.getValueByCode( "APPEAL_CHECKER_TYPE" );
				appeal_checker_value = attendanceSettingServiceAdv.getValueByCode( "APPEAL_CHECKER_VALUE" );
				appeal_flowType_value = attendanceSettingServiceAdv.getValueByCode( "APPEAL_AUDIFLOWTYPE" );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess( e, "系统在获取申诉审核配置时发生异常！" );
				result.error( exception);
				logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			// 利用打卡记录中的信息，创建一个申诉信息记录
			attendanceAppealInfo = attendanceSettingServiceAdv.composeAppealInfoWithDetailInfo( attendanceDetail, 
					wrapIn.getReason(), wrapIn.getAppealReason(),  wrapIn.getSelfHolidayType(),  wrapIn.getAddress(), 
					wrapIn.getStartTime(),  wrapIn.getEndTime(),  wrapIn.getAppealDescription() );
			// 创建一个申诉审批记录信息
			attendanceAppealAuditInfo = new AttendanceAppealAuditInfo();
			attendanceAppealAuditInfo.setId( attendanceAppealInfo.getId());
			attendanceAppealAuditInfo.setDetailId( attendanceDetail.getId() );
		}

		
		//查询申诉审核人
		if (check) {
			if( StringUtils.isNotEmpty( appeal_auditor_type ) ) {
				try {
					appealAuditPersonName = attendanceAppealInfoServiceAdv.getAppealAuditPerson( personName, attendanceAppealInfo.getUnitName(), wrapIn.getIdentity() );
					if( StringUtils.isNotEmpty( appealAuditPersonName )){
						if(StringUtils.indexOf(appealAuditPersonName, "@I")>0){
							appealAuditPersonName = userManagerService.getPersonNameByIdentity(appealAuditPersonName);
						}
						attendanceAppealAuditInfo.setProcessPerson1( appealAuditPersonName );
						attendanceAppealAuditInfo.setCurrentProcessor( appealAuditPersonName );
						attendanceAppealInfo.setCurrentProcessor( appealAuditPersonName );// 将第一个处理人设置为当前处理人
						attendanceAppealAuditInfo.setAuditFlowType( AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN );
					}

				}catch( Exception e ) {
					check = false;
					Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在根据根据考勤人员查询申诉审核人信息时发生异常！personName:"+personName );
					result.error(exception);
					logger.warn( "系统在根据根据考勤人员查询申诉审核人信息时发生异常！personName:"+personName );
					logger.error(e, effectivePerson, request, null);
				}
			}
			if(StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW)){
				attendanceAppealAuditInfo.setAuditFlowType( AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW );
				attendanceAppealAuditInfo.setCurrentProcessor( "" );
				attendanceAppealInfo.setCurrentProcessor( "" );
				if(StringUtils.isNotEmpty(wrapIn.getUnitName())){
					attendanceAppealInfo.setUnitName(wrapIn.getUnitName());
					attendanceAppealAuditInfo.setProcessPersonUnit1(wrapIn.getUnitName());
					attendanceAppealAuditInfo.setProcessPersonUnit2(wrapIn.getUnitName());
				}
				if(StringUtils.isNotEmpty(wrapIn.getWorkId())){
					attendanceAppealAuditInfo.setWorkId(wrapIn.getWorkId());
				}
			}
		}
		
		if (check && !StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW)) {
			if( StringUtils.isEmpty(appealAuditPersonName) ) {
				//申诉审核人不存在
				check = false;
				String message = null;
				String unitLevelName = null;
				if( StringUtils.isEmpty(appeal_auditor_type)) {
					message = "申诉审核人类别未配置!";
				}else if( "无".equals( appeal_auditor_type.trim() )){//当前人处理
					attendanceAppealAuditInfo.setProcessPerson1( effectivePerson.getDistinguishedName() );
					attendanceAppealAuditInfo.setCurrentProcessor( effectivePerson.getDistinguishedName() );// 将第一个处理人设置为当前处理人
					attendanceAppealInfo.setCurrentProcessor( effectivePerson.getDistinguishedName() );// 将第一个处理人设置为当前处理人
				}else if( AppealConfig.APPEAL_AUDITTYPE_UNITDUTY.equals( appeal_auditor_type )){
					unitLevelName = userManagerService.getUnitLevelNameWithName( attendanceDetail.getUnitName() );
					message = personName + "所属的部门["+unitLevelName+"]职务["+appeal_auditor_value+"]不存在!";
				}else if( AppealConfig.APPEAL_AUDITTYPE_REPORTLEADER.equals( appeal_auditor_type )){
					message = personName + "的[汇报对象]不存在!";
				}else if( AppealConfig.APPEAL_AUDITTYPE_PERSONATTRIBUTE.equals( appeal_auditor_type )){
					message = personName + "的个人属性["+appeal_auditor_value+"]不存在!";
				}else if( AppealConfig.APPEAL_AUDITTYPE_PERSON.equals( appeal_auditor_type )){
					message = "指定审核人["+appeal_auditor_value+"]不存在!";
				}else {
					message = "考勤申诉审核人类型不正确!" + appeal_auditor_type ;
				}
				Exception exception = new ExceptionAttendanceAppealProcess( message );
				result.error( exception );
			}
		}
		
		//查询申诉复核人
		if (check && (!StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW))) {
			if( StringUtils.isNotEmpty( appeal_checker_type )   && !"无".equals( appeal_checker_type  ) ) {
				try {
					appealCheckPersonName = attendanceAppealInfoServiceAdv.getAppealCheckPerson( personName, attendanceAppealInfo.getUnitName(), wrapIn.getIdentity() );
					attendanceAppealAuditInfo.setProcessPerson2( appealCheckPersonName );
				}catch( Exception e ) {
					check = false;
					Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在根据根据考勤人员查询申诉复核人信息时发生异常！personName:"+personName );
					result.error(exception);
					logger.warn( "系统在根据根据考勤人员查询申诉复核人信息时发生异常！personName:"+personName );
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check && (!StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW))) {
			if( StringUtils.isNotEmpty( appeal_checker_type )  && !StringUtils.equals( appeal_checker_type, "无" ) ) {
				if( StringUtils.isEmpty(appealCheckPersonName) ) {
					//申诉复核人不存在
					check = false;
					String message = null;
					String unitLevelName = null;
					if( StringUtils.isEmpty( appeal_checker_type )) {
						message = "申诉复核人类别未配置!";
					}else if( AppealConfig.APPEAL_AUDITTYPE_UNITDUTY.equals( appeal_checker_type )){
						unitLevelName = userManagerService.getUnitLevelNameWithName( attendanceDetail.getUnitName() );
						message = personName + "所属的部门["+unitLevelName+"]职务["+ appeal_checker_value +"]不存在!";
					}else if( AppealConfig.APPEAL_AUDITTYPE_REPORTLEADER.equals( appeal_checker_type )){
						message = personName + "的[汇报对象]不存在!";
					}else if( AppealConfig.APPEAL_AUDITTYPE_PERSONATTRIBUTE.equals( appeal_checker_type )){
						message = personName + "的个人属性["+appeal_checker_value+"]不存在!";
					}else if( AppealConfig.APPEAL_AUDITTYPE_PERSON.equals( appeal_checker_type )){
						message = "指定复核人["+appeal_checker_value+"]不存在!";
					}else {
						message = "考勤申诉复核人类型不正确!" + appeal_checker_type ;
					}
					Exception exception = new ExceptionAttendanceAppealProcess( message );
					result.error( exception );
				}
			}
			
		}
		
		//查询申诉审核人所属组织
		if (check && (!StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW))) {
			if( AppealConfig.APPEAL_AUDITTYPE_UNITDUTY.equalsIgnoreCase( appeal_auditor_type ) ) {
				attendanceAppealAuditInfo.setProcessPersonUnit1(
						attendanceAppealInfoServiceAdv.getPersonUnitName( appealAuditPersonName, attendanceDetail.getUnitName(), wrapIn.getIdentity() ) );
			}else {
				//汇报对象，指定人以及人员属性中指定的人员，根据人员姓名取首选组织（第一个）
				if( StringUtils.isNotEmpty( appealAuditPersonName ) ) {
					try {//获取审核人组织信息
						attendanceAppealAuditInfo.setProcessPersonUnit1( userManagerService.getUnitNameWithPersonName( appealAuditPersonName ));
					}catch( Exception e ) {
						check = false;
						Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在根据审核人获取组织信息时发生异常。");
						result.error(exception);
						logger.warn( "系统在根据审核人获取组织信息时发生异常！personName:"+appealAuditPersonName );
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
		}
		//根据所属组织名称查询申诉审核人所属顶层组织
		if (check && !StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW)) {
			if( StringUtils.isNotEmpty( attendanceAppealAuditInfo.getProcessPersonUnit1() )) {
				try {
					attendanceAppealAuditInfo.setProcessPersonTopUnit1( userManagerService.getTopUnitNameWithUnitName( attendanceAppealAuditInfo.getProcessPersonUnit1() ) );
				}catch( Exception e ) {
					check = false;
					Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在根据审核人获取顶层组织信息时发生异常。");
					result.error(exception);
					logger.warn( "系统在根据审核人获取顶层组织信息时发生异常！personName:"+appealAuditPersonName );
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		//查询申诉复核人所属组织
		if (check && !StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW)) {
			if( AppealConfig.APPEAL_AUDITTYPE_UNITDUTY.equalsIgnoreCase( appeal_auditor_type ) ) {
				attendanceAppealAuditInfo.setProcessPersonUnit2( attendanceAppealInfoServiceAdv.getPersonUnitName( appealCheckPersonName, attendanceDetail.getUnitName(), wrapIn.getIdentity() ) );
			}else {
				//汇报对象，指定人以及人员属性中指定的人员，根据人员姓名取首选组织（第一个）
				if( appealCheckPersonName != null  && !appealCheckPersonName.isEmpty() ) {
					try {//获取复核人组织信息
						attendanceAppealAuditInfo.setProcessPersonUnit2( userManagerService.getUnitNameWithPersonName( appealCheckPersonName ));
					}catch( Exception e ) {
						check = false;
						Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在根据复核人获取组织信息时发生异常。");
						result.error(exception);
						logger.warn( "系统在根据审核人获取组织信息时发生异常！personName:"+appealCheckPersonName );
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
		}
		//根据所属组织名称查询申诉复核人所属顶层组织
		if (check && !StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW)) {
			if( StringUtils.isNotEmpty(attendanceAppealAuditInfo.getProcessPersonUnit2())) {
				try {//获取复核人顶层组织信息
					attendanceAppealAuditInfo.setProcessPersonTopUnit2(  userManagerService.getTopUnitNameWithUnitName( attendanceAppealAuditInfo.getProcessPersonUnit2()) );
				}catch( Exception e ) {
					check = false;
					Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在根据复核人获取顶层组织信息时发生异常。");
					result.error(exception);
					logger.warn( "系统在根据审核人获取顶层组织信息时发生异常！personName:"+appealCheckPersonName );
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		//保存申诉信息
		if (check) {
			try {
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.saveNewAppeal( attendanceAppealInfo, attendanceAppealAuditInfo );
				result.setData(new Wo(attendanceAppealInfo.getId()));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在保存考勤申诉信息息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			// 填充申诉信息内容 - 申诉信息成功生成.尝试向当前处理人[" + attendanceAppealInfo.getCurrentProcessor() + "]发送消息通知......
			try {
				attendanceNoticeService.notifyAttendanceAppealProcessness1Message(attendanceAppealInfo);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "申诉信息提交成功，向申诉当前处理人发送通知消息发生异常！name:"+attendanceAppealAuditInfo.getProcessPerson1());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends AttendanceAppealInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		@FieldDescribe("申诉人的身份，考勤人员身份：如果考勤人员属于多个组织，可以选择一个身份进行申诉信息绑定.")
		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		@FieldDescribe("自定义流程workId")
		private String workId = null;

		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}