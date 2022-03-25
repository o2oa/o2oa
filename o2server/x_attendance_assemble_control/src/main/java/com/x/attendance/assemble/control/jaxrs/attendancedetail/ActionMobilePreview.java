package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.service.AttendanceScheduleSettingService;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

/**
 * 预打卡，根据当前人以及对应的排班设置，给出打卡的预判断
 */
public class ActionMobilePreview extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionMobilePreview.class);
	private DateOperation dateOperation = new DateOperation();
	private AttendanceScheduleSettingService attendanceScheduleSettingService = new AttendanceScheduleSettingService();

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WoMobileRecord> wraps = new ArrayList<>();
		List<AttendanceDetailMobile> attendanceDetailMobileList = null;
		Long total = 0L;
		Boolean check = true;
		AttendanceScheduleSetting scheduleSetting = null;
		WoScheduleSetting woScheduleSetting = null;
		String signDate = dateOperation.getDateStringFromDate( new Date(), "YYYY-MM-DD");

		//先查询该员工所有的考勤数据
		if (check) {
			try {
				attendanceDetailMobileList = attendanceDetailServiceAdv.listAttendanceDetailMobile( effectivePerson.getDistinguishedName(), signDate );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e,
						"根据条件查询员工手机打卡信息列表时发生异常.DistinguishedName:" + effectivePerson.getDistinguishedName() + ",Date:" + signDate);
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

		//根据当前时间和排班设置，计算当前预打卡信息
		WoSign woSignFeature = null;
		if (check) {
			//打卡策略：1-两次打卡（上午上班，下午下班） 2-三次打卡（上午上班，下午下班加中午一次共三次） 3-四次打卡（上午下午都打上班下班卡）
				scheduleSetting = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithPerson( effectivePerson.getDistinguishedName(), effectivePerson.getDebugger() );

			if( scheduleSetting != null ){
				woScheduleSetting = WoScheduleSetting.copier.copy(scheduleSetting);

				if( woScheduleSetting.getSignProxy() == 3 ){
					//3-四次打卡（上午下午都打上班下班卡）
					woSignFeature = this.getWoSignWithProxy3(wraps, woScheduleSetting);
				}else if( woScheduleSetting.getSignProxy() == 2 ){
					//2-三次打卡（上午上班，下午下班加中午一次共三次）
					woSignFeature = this.getWoSignWithProxy2(wraps, woScheduleSetting);
				}else{
					//1-两次打卡（上午上班，下午下班）
					woSignFeature = this.getWoSignWithProxy1(wraps, woScheduleSetting);
				}
			}
		}

		Wo wo = new Wo();
		wo.setFeature( woSignFeature );
		result.setCount(total);
		result.setData(wo);

		return result;
	}

	/**
	 * 计算下一次打卡是什么打卡
	 * 1-两次打卡（上午上班，下午下班）
	 * @param wraps
	 * @param scheduleSetting
	 * @return
	 */
	private WoSign getWoSignWithProxy1(List<WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) throws Exception {
		WoSign woSignFeature = new WoSign();
		Date now = new Date();
		String recordDateString = dateOperation.getDateStringFromDate( now, "YYYY-MM-DD");
		//排班设置中的上班时间
		Date onDutyTime = dateOperation.getDateFromString( recordDateString+ " " + scheduleSetting.getOnDutyTime() );
		//排班设置中的迟到时间
		Date lateStartTime = dateOperation.getDateFromString( recordDateString+ " " + scheduleSetting.getLateStartTime() );
		//排班中的缺勤时间
		Date absenceStartTime = dateOperation.getDateFromString( recordDateString + " " + scheduleSetting.getAbsenceStartTime() );
		//排班中的早退时间
		Date leaveEarlyStartTime = dateOperation.getDateFromString( recordDateString + " " + scheduleSetting.getLeaveEarlyStartTime() );
		//排班设置中的下班时间
		Date offDutyTime = dateOperation.getDateFromString( recordDateString+ " " + scheduleSetting.getOffDutyTime() );

		//根据当前时间和排班设置，判断当前时间是落在哪个区间
		if(onDutyTime.after(now)){
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(lateStartTime.after(now)){
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(lateStartTime.before(now) && absenceStartTime.after(now)){
			woSignFeature.setSignSeq(2);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(absenceStartTime.before(now) && leaveEarlyStartTime.after(now)){
			//过了缺勤时间就算是第二次打卡-签退
			woSignFeature.setSignSeq(4);
			woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY);
		}else{
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY);
		}
		return woSignFeature;
	}

	/**
	 * 计算下一次打卡是什么打卡
	 * 2-三次打卡（上午上班，下午下班加中午一次共三次）
	 *
	 * @param wraps
	 * @param scheduleSetting
	 * @return
	 */
	private WoSign getWoSignWithProxy2(List<WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) throws Exception {
		DateOperation dateOperation = new DateOperation();
		WoSign woSignFeature = new WoSign();
		SignRecordStatus signRecordStatus = null;

		Date now = new Date();
		Date middayRestEndTime = null;
		Date onDutyTime = null, offDutyTime = null;
		Date morningOffdutyTime = null, afternoonOndutyTime = null;
		String todayDateStr = dateOperation.getDateStringFromDate( now, "YYYY-MM-DD");

		//计算，上班下班时间
		if( StringUtils.isNotEmpty( scheduleSetting.getOnDutyTime())) {
			//排班设置中的上班时间
			onDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOnDutyTime() );
		}
		if( StringUtils.isNotEmpty( scheduleSetting.getOffDutyTime())) {
			//排班设置中的下班时间
			offDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOffDutyTime() );
		}
		if( StringUtils.isNotEmpty( scheduleSetting.getMiddayRestStartTime())) {
			//排班设置中的午休开始时间
			morningOffdutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestStartTime() );
		}
		if( StringUtils.isNotEmpty( scheduleSetting.getMiddayRestEndTime())) {
			//排班设置中的午休结束时间
			afternoonOndutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestEndTime() );
		}
		//排班设置中的迟到时间
		Date lateStartTime = dateOperation.getDateFromString( todayDateStr+ " " + scheduleSetting.getLateStartTime() );
		//排班中的缺勤时间
		Date absenceStartTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getAbsenceStartTime() );
		//排班中的早退时间
		Date leaveEarlyStartTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getLeaveEarlyStartTime() );
		signRecordStatus = getSignRecordStatus( wraps, onDutyTime, morningOffdutyTime, afternoonOndutyTime, offDutyTime );

		//根据当前时间和排班设置，判断当前时间是落在哪个区间
		if(onDutyTime.after(now)){
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(lateStartTime.after(now)){
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(lateStartTime.before(now) && morningOffdutyTime.after(now)){
			if(absenceStartTime.after(now)){
				woSignFeature.setSignSeq(2);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
			}else{
				woSignFeature.setSignSeq(3);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
			}
		}else if(morningOffdutyTime.before(now) && afternoonOndutyTime.after(now)){
			//过了午休开始时间就算是第二次打卡-中午签到
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON);
		}else if(afternoonOndutyTime.before(now) && leaveEarlyStartTime.after(now)){
			//过了午休结束时间-可能是中午签到或下午下班签退
			if(signRecordStatus.getAlreadyAfternoon()){
				woSignFeature.setSignSeq(1);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON);
			}else{
				woSignFeature.setSignSeq(4);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY);
			}
		} else{
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY);
		}
		return woSignFeature;
	}

	/**
	 * 计算下一次打卡是什么打卡
	 * 3-四次打卡（上午下午都打上班下班卡）
	 *
	 * @param wraps
	 * @param scheduleSetting
	 * @return
	 */
	protected WoSign getWoSignWithProxy3(List<WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) throws Exception {
		DateOperation dateOperation = new DateOperation();
		WoSign woSignFeature = new WoSign();
		Date now = new Date();
		Date onDutyTime = null, offDutyTime = null;
		Date morningOffdutyTime = null, afternoonOndutyTime = null;
		String todayDateStr = dateOperation.getDateStringFromDate( now, "YYYY-MM-DD");
		SignRecordStatus signRecordStatus = null;

		if( StringUtils.isEmpty( scheduleSetting.getMiddayRestStartTime())){
			return null;
		}

		if( StringUtils.isEmpty( scheduleSetting.getMiddayRestEndTime())){
			return null;
		}

		//计算，上班下班时间
		onDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOnDutyTime() );
		morningOffdutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestStartTime() );
		afternoonOndutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestEndTime() );
		offDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOffDutyTime() );
		//排班设置中的迟到时间(上午迟到，下午迟到)
		Date lateStartTime = dateOperation.getDateFromString( todayDateStr+ " " + scheduleSetting.getLateStartTime() );
		Date lateStartTimeAfternoon = dateOperation.getDateFromString( todayDateStr+ " " + scheduleSetting.getLateStartTimeAfternoon());
		//排班中的缺勤时间
		Date absenceStartTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getAbsenceStartTime() );
		//排班中的早退时间(上午早退，下午早退)
		Date leaveEarlyStartTimeMorning = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getLeaveEarlyStartTimeMorning() );
		Date leaveEarlyStartTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getLeaveEarlyStartTime() );
		signRecordStatus = getSignRecordStatus( wraps, onDutyTime, morningOffdutyTime, afternoonOndutyTime, offDutyTime );

		//根据当前时间和排班设置，判断当前时间是落在哪个区间
		if(onDutyTime.after(now)){
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(lateStartTime.after(now)){
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(lateStartTime.before(now) && leaveEarlyStartTimeMorning.after(now)) {
			if(signRecordStatus.getAlreadyOnduty()){
				//上午已打过上班卡，此时结果为上午早退
				woSignFeature.setSignSeq(4);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY);
			}else{
				//上午没打过上班卡,即当前是第一次打卡,此时结果为上午迟到
				woSignFeature.setSignSeq(2);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
				if (absenceStartTime.after(now)) {
					woSignFeature.setSignSeq(3);
					woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY);
				}
			}
		}else if(leaveEarlyStartTimeMorning.before(now) && morningOffdutyTime.after(now)){
			//过了上午早退时间就算第二次打卡-上午下班打卡
			if (absenceStartTime.after(now)) {
				woSignFeature.setSignSeq(1);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY);
			} else {
				woSignFeature.setSignSeq(3);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY);
			}

		}else if(morningOffdutyTime.before(now) && afternoonOndutyTime.after(now)){
			//此时打卡，可能是上午下班打卡，可能是下午上班打卡
			woSignFeature.setSignSeq(1);
			if(signRecordStatus.getAlreadyMorningOffDuty()){
				//已经打卡上午下班卡,此时是下午上班卡
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY);
			}else{
				//未打卡上午下班卡,此时是上午下班卡
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY);
			}
		}else if(afternoonOndutyTime.before(now) && leaveEarlyStartTime.after(now)){
			//过了午休结束时间-下午上班签到或下午下班
			if(signRecordStatus.getAlreadyAfternoonOnDuty()){
				//已经打过下午上班卡-此时是下午下班卡
				woSignFeature.setSignSeq(4);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY);
			}else{
				//未打过下午上班卡-此时是下午上班卡
				woSignFeature.setSignSeq(1);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY);
			}
		} else{
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY);
		}
		return woSignFeature;
	}

	private SignRecordStatus getSignRecordStatus(List<WoMobileRecord> wraps, Date onDutyTime, Date morningOffdutyTime, Date afternoonOndutyTime, Date offDutyTime) {
		SignRecordStatus signRecordStatus = new SignRecordStatus();
		if( ListTools.isNotEmpty( wraps )){
			for( WoMobileRecord record : wraps ){
				if( StringUtils.equalsAnyIgnoreCase( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY, record.getCheckin_type() )){
					signRecordStatus.setAlreadyOnduty( true );
				}
				if( StringUtils.equalsAnyIgnoreCase( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON, record.getCheckin_type() )){
					signRecordStatus.setAlreadyAfternoon( true );
				}
				if( StringUtils.equalsAnyIgnoreCase( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY, record.getCheckin_type() )){
					signRecordStatus.setAlreadyMorningOffDuty( true );
				}
				if( StringUtils.equalsAnyIgnoreCase( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY, record.getCheckin_type() )){
					signRecordStatus.setAlreadyAfternoonOnDuty( true );
				}
				if( StringUtils.equalsAnyIgnoreCase( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY, record.getCheckin_type() )){
					signRecordStatus.setAlreadyOffDuty( true );
				}
			}
		}
		return signRecordStatus;
	}

	public static class Wo{

		@FieldDescribe("下一次打卡信息")
		private WoSign feature;

		public WoSign getFeature() { return feature; }

		public void setFeature(WoSign feature) { this.feature = feature; }
	}

	public static class WoScheduleSetting extends AttendanceScheduleSetting {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceScheduleSetting, WoScheduleSetting> copier = WrapCopierFactory.wo(AttendanceScheduleSetting.class,
				WoScheduleSetting.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoSign{

		@FieldDescribe("预打卡结果类型：1正常|2迟到|3缺勤|4早退|5已完成")
		private Integer signSeq = 1;

		@FieldDescribe("打卡操作名称：上班打卡|下班打卡|午休打卡|上午下班打卡|下午上班打卡(根据不同的打卡策略稍有不同)....")
		private String checkinType = "上班打卡";

		public Integer getSignSeq() { return signSeq; }

		public void setSignSeq(Integer signSeq) { this.signSeq = signSeq; }


		public String getCheckinType() { return checkinType; }

		public void setCheckinType(String checkinType) { this.checkinType = checkinType; }

	}

}