package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ListTools;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		List<String> signedPersons = null;
		List<String> wos = new ArrayList<>();

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
			signedPersons = attendanceDetailServiceAdv.listSignedPersonsWithDeadLine( wrapIn.getDeadline() );
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

		public String getDeadline() {
			return deadline;
		}

		public void setDeadline(String deadline) {
			this.deadline = deadline;
		}
	}

	public static class Wo extends WoText {
		public Wo( String text ) {
			setText( text );
		}
	}
}