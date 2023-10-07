package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ListTools;

public class ActionReciveAttendanceMobile extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionReciveAttendanceMobile.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Date datetime = null;
		DateOperation dateOperation = new DateOperation();
		EffectivePerson currentPerson = this.effectivePerson( request );
		AttendanceDetailMobile attendanceDetailMobile = new AttendanceDetailMobile();
		List<AttendanceDetailMobile> attendanceDetailMobileList = null;
		List<WoMobileRecord> wraps = new ArrayList<>();
		Wi wrapIn = null;
		Boolean check = true;
		Date now = null;
		String signDate = null;
		String signTime = null;
		
		// 检查配置 是否禁用旧版考勤
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AttendanceV2Config config = null; // 配置对象
			List<AttendanceV2Config> configs = emc.listAll(AttendanceV2Config.class);
			if (configs != null && !configs.isEmpty()) {
					config = configs.get(0);
			}
			if (config != null && BooleanUtils.isTrue(config.getCloseOldAttendance())) {
				throw new ExceptionCloseOldAttendance();
			}
		}

		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
			//1000000000000L = Sun Sep 09 2001 09:46:40 GMT+0800 (中国标准时间)
			if( wrapIn.getCheckin_time() > 1000000000000L  ){
				now = new Date( wrapIn.getCheckin_time() );
			}else{
				now = new Date();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}

		if( check ){
			try {
				signDate = dateOperation.getDateStringFromDate( now, "YYYY-MM-DD");
				signTime = dateOperation.getDateStringFromDate( now, "HH:mm:ss");

				attendanceDetailMobile.setRecordDateString( signDate ); //打卡日期
				attendanceDetailMobile.setSignTime( signTime ); //打卡时间
				attendanceDetailMobile.setCheckin_time( now.getTime() );
				attendanceDetailMobile.setRecordDate( now );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWrapInConvert(e, jsonElement);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if( check ){
			if (StringUtils.isNotEmpty(wrapIn.getDescription())) {
				attendanceDetailMobile.setDescription(wrapIn.getDescription());
			}
			if( StringUtils.isNotEmpty(wrapIn.getRecordAddress()) ){
				attendanceDetailMobile.setRecordAddress( wrapIn.getRecordAddress() );
			}
			if( StringUtils.isNotEmpty(wrapIn.getWorkAddress()) ){
				attendanceDetailMobile.setWorkAddress( wrapIn.getWorkAddress() );
			}
			if( StringUtils.isNotEmpty(wrapIn.getOptMachineType()) ){
				attendanceDetailMobile.setOptMachineType( wrapIn.getOptMachineType() );
			}
		}
		if( check ){
			if( StringUtils.isNotEmpty(wrapIn.getLatitude())){
				attendanceDetailMobile.setLatitude( wrapIn.getLatitude() );
			}
		}
		if( check ){
			if( StringUtils.isNotEmpty(wrapIn.getLongitude())){
				attendanceDetailMobile.setLongitude( wrapIn.getLongitude() );
			}
		}
		if( check ){
			//是否在范围外打卡，默认否
			if( wrapIn.getIsExternal() ){
				attendanceDetailMobile.setIsExternal(true);
			}else{
				attendanceDetailMobile.setIsExternal(false);
			}
		}
		if( check ){
			String distinguishedName = wrapIn.getEmpName();
			if( StringUtils.isEmpty( distinguishedName )){
				distinguishedName = currentPerson.getDistinguishedName();
			}

			Person person = userManagerService.getPersonObjByName( distinguishedName );

			if( person != null ){
				attendanceDetailMobile.setEmpName( person.getDistinguishedName() );
				if( StringUtils.isEmpty( wrapIn.getEmpNo() )){
					if( person != null ){
						if( StringUtils.isNotEmpty( person.getEmployee() )){
							attendanceDetailMobile.setEmpNo(person.getEmployee());
						}else{
							attendanceDetailMobile.setEmpNo( distinguishedName );
						}
					}
				}
			}else{
				//人员不存在
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(
						"考勤人员不存在.DistinguishedName:" + distinguishedName );
				result.error(exception);
			}
		}

		//计算当前打卡的checking_type
		//先查询该员工所有的考勤数据
		if (check) {
			try {
				attendanceDetailMobileList = attendanceDetailServiceAdv.listAttendanceDetailMobile( attendanceDetailMobile.getEmpName(), signDate );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e,
						"根据条件查询员工手机打卡信息列表时发生异常.DistinguishedName:" + attendanceDetailMobile.getEmpName() + ",Date:" + signDate);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}

		if (check) {
			if ( ListTools.isNotEmpty(attendanceDetailMobileList)) {
				try {
					wraps = WoMobileRecord.copier.copy(attendanceDetailMobileList);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "系统在转换员工手机打卡信息为输出对象时发生异常.");
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		//根据最后一次打卡信息，计算下一次打卡的信息
		WoSignFeature woSignFeature = null;
		if (check
				&& !StringUtils.equalsAnyIgnoreCase("xadmin", effectivePerson.getName())
				&& !StringUtils.equalsAnyIgnoreCase("cipher", effectivePerson.getName())) {
			AttendanceScheduleSetting scheduleSetting = null;
			//打卡策略：1-两次打卡（上午上班，下午下班） 2-三次打卡（上午上班，下午下班加中午一次共三次） 3-四次打卡（上午下午都打上班下班卡）
			scheduleSetting = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithPerson( attendanceDetailMobile.getEmpName(), effectivePerson.getDebugger() );
			if( scheduleSetting != null ){
				if( scheduleSetting.getSignProxy() == 3 ){
					//3-四次打卡（上午下午都打上班下班卡）
					woSignFeature = getWoSignFeatureWithProxy3(wraps, scheduleSetting);
				}else if( scheduleSetting.getSignProxy() == 2 ){
					//2-三次打卡（上午上班，下午下班加中午一次共三次）
					woSignFeature = getWoSignFeatureWithProxy2(wraps, scheduleSetting);
				}else{
					//1-两次打卡（上午上班，下午下班）
					woSignFeature = getWoSignFeatureWithProxy1(wraps, scheduleSetting);
				}
			}
			if( woSignFeature != null ){
				woSignFeature.setSignDate( signDate );
			}

			//attendanceDetailMobile.setCheckin_type( woSignFeature.getCheckinType() );
			attendanceDetailMobile.setCheckin_type(wrapIn.getCheckin_type());
			if( StringUtils.isEmpty( wrapIn.getSignDescription() )){
				attendanceDetailMobile.setSignDescription( wrapIn.getCheckin_type() );
			}else{
				attendanceDetailMobile.setSignDescription( wrapIn.getSignDescription() );
			}
		}

		if( check ){
			if( StringUtils.isNotEmpty( wrapIn.getId() )){
				attendanceDetailMobile.setId( wrapIn.getId() );
			}
			try {
				attendanceDetailMobile = attendanceDetailServiceAdv.save( attendanceDetailMobile );
				result.setData( new Wo( attendanceDetailMobile.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "系统在保存员工手机打卡信息时发生异常." );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}

		if( check ){
			//对该员工的所有移动考勤数据进行一个整合，并且立即send到分析队列
			attendanceDetailServiceAdv.pushToDetail( attendanceDetailMobile.getEmpName(), attendanceDetailMobile.getRecordDateString(), effectivePerson.getDebugger() );
		}
		return result;
	}
	
	public static class Wi {
		
		@FieldDescribe( "Id, 可以为空，如果ID重复，则为更新原有数据." )
		private String id;
		
//		@FieldDescribe( "员工号, 可以为空，如果为空则与empName相同." )
		private String empNo;

		@FieldDescribe( "员工标识, 可以为空但不能错误：DistinguishedName，如果为空则取当前登录人员." )
		private String empName;

//		@FieldDescribe( "打卡记录日期字符串：yyyy-mm-dd, 必须填写." )
		private String recordDateString;

		@FieldDescribe("打卡类型。字符串，目前有：上午上班打卡，上午下班打卡，下午上班打卡，下午下班打卡，外出打卡，午间打卡")
		private String checkin_type;

		@FieldDescribe("打卡时间，可以为空，为空则取服务器当前时间。Unix时间戳")
		private long checkin_time;

//		@FieldDescribe( "打卡时间: hh24:mi:ss, 必须填写." )
		private String signTime;

//		@FieldDescribe( "打卡说明:上班打卡，下班打卡, 可以为空." )
		private String signDescription;

		@FieldDescribe( "其他说明备注, 可以为空." )
		private String description;

		@FieldDescribe( "打卡地点描述, 可以为空." )
		private String recordAddress = "未知";
		
		@FieldDescribe( "经度, 可以为空." )
		private String longitude;

		@FieldDescribe( "纬度, 可以为空." )
		private String latitude;

		@FieldDescribe( "操作设备类别：手机品牌|PAD|PC|其他, 可以为空." )
		private String optMachineType = "其他";

		@FieldDescribe( "操作设备类别：Mac|Windows|IOS|Android|其他, 可以为空." )
		private String optSystemName = "其他";

		@FieldDescribe( "工作地点描述, 可以为空." )
		private String workAddress = "";

		@FieldDescribe("是否范围外打卡")
		private Boolean isExternal = false;

		public String getRecordDateString() {
			return recordDateString;
		}
		public void setRecordDateString(String recordDateString) {
			this.recordDateString = recordDateString;
		}
		public String getSignDescription() {
			return signDescription;
		}
		public void setSignDescription(String signDescription) {
			this.signDescription = signDescription;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getRecordAddress() {
			return recordAddress;
		}
		public void setRecordAddress(String recordAddress) {
			this.recordAddress = recordAddress;
		}
		public String getLongitude() {
			return longitude;
		}
		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}
		public String getLatitude() {
			return latitude;
		}
		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
		public String getOptMachineType() {
			return optMachineType;
		}
		public void setOptMachineType(String optMachineType) {
			this.optMachineType = optMachineType;
		}
		public String getOptSystemName() {
			return optSystemName;
		}
		public void setOptSystemName(String optSystemName) {
			this.optSystemName = optSystemName;
		}
		public String getEmpNo() {
			return empNo;
		}
		public void setEmpNo(String empNo) {
			this.empNo = empNo;
		}
		public String getEmpName() {
			return empName;
		}
		public void setEmpName(String empName) {
			this.empName = empName;
		}
		public String getSignTime() {
			return signTime;
		}
		public void setSignTime(String signTime) {
			this.signTime = signTime;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getCheckin_type() { return checkin_type; }
		public void setCheckin_type(String checkin_type) { this.checkin_type = checkin_type; }
		public long getCheckin_time() { return checkin_time; }
		public void setCheckin_time(long checkin_time) { this.checkin_time = checkin_time; }
		public String getWorkAddress() {
			return workAddress;
		}
		public void setWorkAddress(String workAddress) {
			this.workAddress = workAddress;
		}
		public Boolean getIsExternal() {
			return isExternal;
		}
		public void setIsExternal(Boolean isExternal) {
			this.isExternal = isExternal;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}