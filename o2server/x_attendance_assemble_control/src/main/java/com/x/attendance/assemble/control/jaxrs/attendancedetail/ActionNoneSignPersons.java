package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ListTools;

/**
 * 获取上班时间前X分钟未打卡人员信息的接口
 * 1、获取所有需要打卡的人员信息列表
 * 2、获取指定时间前已经打过卡的人员列表
 * 3、从所有要打卡的人员信息列表中排除已经打过卡的人员后返回
 */
public class ActionNoneSignPersons extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionNoneSignPersons.class );
	
	protected ActionResult<List<String>> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<List<String>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigList = null;
		Wi wrapIn = null;
		Boolean check = true;
		DateOperation dateOperation = new DateOperation();
		List<String> allNeedSignPersons = new ArrayList<>();
		List<String> signedPersons = new ArrayList<>();
		List<String> wos = new ArrayList<>();
		String signType = "all";
		String[] signTypeArray = null;
		List<String> signTypeList = new ArrayList<>();
		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}

		if( StringUtils.isEmpty( wrapIn.getDeadline() ) ) {
			wrapIn.setDeadline( dateOperation.getNowDateTime() );
		}

		if( StringUtils.isEmpty( wrapIn.getSignType() )){
			signType = "all";
		}else{
			signType = wrapIn.getSignType();
		}

		signTypeArray = signType.split("#");
		for( String type : signTypeArray ){
			signTypeList.add( type );
		}

		if ( check ) {
			try {
				attendanceEmployeeConfigList = attendanceEmployeeConfigServiceAdv.listByConfigType( "REQUIRED" );
				if( ListTools.isEmpty( attendanceEmployeeConfigList ) ) {
					//如果没有配置需要考勤的人员则为全员考勤
					List<Person> allPersonObjs = userManagerService.listAllPersons();
					if(ListTools.isNotEmpty( allPersonObjs )){
						for( Person person : allPersonObjs ){
							allNeedSignPersons.add( person.getDistinguishedName() );
						}
					}
				}else{
					for( AttendanceEmployeeConfig person : attendanceEmployeeConfigList ){
						allNeedSignPersons.add( person.getEmployeeName() );
					}
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在查询需要考勤的人员配置列表时发生异常." );
				result.error( exception );
				logger.error( e, currentPerson, request, null );
			}
		}

		if (check) {
			//查询在指定截止日期前已经打过卡的人员
			if( signTypeList.contains( "all" )){
				signedPersons = attendanceDetailServiceAdv.listSignedPersonsWithDeadLine( wrapIn.getDeadline(), "all" );
			}else {
				if( signTypeList.contains( "onDuty" )){
					List<String> signedPersons_tmp = attendanceDetailServiceAdv.listSignedPersonsWithDeadLine( wrapIn.getDeadline(), "onDuty" );
					if(ListTools.isNotEmpty( signedPersons_tmp )){
						for( String tmp : signedPersons_tmp ){
							if( !signedPersons.contains( tmp )){
								signedPersons.add( tmp );
							}
						}
					}
				}
				if( signTypeList.contains( "offDuty" )){
					List<String> signedPersons_tmp = attendanceDetailServiceAdv.listSignedPersonsWithDeadLine( wrapIn.getDeadline(), "offDuty" );
					if(ListTools.isNotEmpty( signedPersons_tmp )){
						for( String tmp : signedPersons_tmp ){
							if( !signedPersons.contains( tmp )){
								signedPersons.add( tmp );
							}
						}
					}
				}
				if( signTypeList.contains( "morningOffDuty" )){
					List<String> signedPersons_tmp = attendanceDetailServiceAdv.listSignedPersonsWithDeadLine( wrapIn.getDeadline(), "morningOffDuty" );
					if(ListTools.isNotEmpty( signedPersons_tmp )){
						for( String tmp : signedPersons_tmp ){
							if( !signedPersons.contains( tmp )){
								signedPersons.add( tmp );
							}
						}
					}
				}
				if( signTypeList.contains( "afternoonOnDuty" )){
					List<String> signedPersons_tmp = attendanceDetailServiceAdv.listSignedPersonsWithDeadLine( wrapIn.getDeadline(), "afternoonOnDuty" );
					if(ListTools.isNotEmpty( signedPersons_tmp )){
						for( String tmp : signedPersons_tmp ){
							if( !signedPersons.contains( tmp )){
								signedPersons.add( tmp );
							}
						}
					}
				}
			}
		}

		if (check) {
			//排除已经打过卡的人
			if( ListTools.isNotEmpty( signedPersons )){
				for( String distinguishedName : signedPersons ){
					ListTools.removeStringFromList( distinguishedName, allNeedSignPersons);
				}
			}
		}

		if (check) {
			for( String distinguishedName : allNeedSignPersons ){
				wos.add( distinguishedName );
			}
		}

		result.setData( wos );
		return result;
	}

	public static class Wi{

		@FieldDescribe( "截止时间点，如果不填写，则以当前时间作为截止时间" )
		private String deadline = null;

		@FieldDescribe( "未打卡类型：all#onDuty#offDuty#morningOffDuty#afternoonOnDuty, 可以多选#分隔" )
		private String signType = null;

		public String getDeadline() {
			return deadline;
		}

		public void setDeadline(String deadline) {
			this.deadline = deadline;
		}

		public String getSignType() { return signType; }

		public void setSignType(String signType) { this.signType = signType; }
	}

	public static class Wo extends WoText {
		public Wo( String text ) {
			setText( text );
		}
	}
}