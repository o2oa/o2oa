package com.x.attendance.assemble.control.jaxrs.attendancedetail;

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
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

		//根据最后一次打卡信息，计算下一次打卡的信息
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

		if( ListTools.isEmpty( wraps )){
			//一次都没有打过，不管几点，都是上班打卡
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
			//判断如果此时打卡，是否是正常|迟到|缺勤|
			if(onDutyTime != null && onDutyTime.after(now)){
				woSignFeature.setSignSeq(1);
			}else if( absenceStartTime != null && now.after( absenceStartTime )){
				woSignFeature.setSignSeq(3);
			}else if(lateStartTime != null && now.after( lateStartTime )){
				woSignFeature.setSignSeq(2);
			}
		}else{
			//打了一次，就是下班打卡，打了两次，就没有了
			if( wraps.size() == 1 ){
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
				if(offDutyTime !=null && now.after( offDutyTime )){
					woSignFeature.setSignSeq(1);
				}else if(leaveEarlyStartTime != null && leaveEarlyStartTime.after(now)){
					woSignFeature.setSignSeq(4);
				}
			}else{
				woSignFeature.setSignSeq(5); //没有需要的打卡了
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
			}
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
			onDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOnDutyTime() );
		}
		if( StringUtils.isNotEmpty( scheduleSetting.getOffDutyTime())) {
			offDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOffDutyTime() );
		}
		if( StringUtils.isNotEmpty( scheduleSetting.getMiddayRestStartTime())) {
			morningOffdutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestStartTime() );
		}
		if( StringUtils.isNotEmpty( scheduleSetting.getMiddayRestEndTime())) {
			afternoonOndutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestEndTime() );
		}
		//排班设置中的迟到时间
		Date lateStartTime = dateOperation.getDateFromString( todayDateStr+ " " + scheduleSetting.getLateStartTime() );
		//排班中的缺勤时间
		Date absenceStartTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getAbsenceStartTime() );
		//排班中的早退时间
		Date leaveEarlyStartTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getLeaveEarlyStartTime() );
		signRecordStatus = getSignRecordStatus( wraps, onDutyTime, morningOffdutyTime, afternoonOndutyTime, offDutyTime );

		if( ListTools.isEmpty( wraps )){
			//一次都没有打过，只能打上班卡
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
			//判断如果此时打卡，是否是正常|迟到|缺勤|
			if(onDutyTime != null && onDutyTime.after(now)){
				woSignFeature.setSignSeq(1);
			}else if( absenceStartTime != null && now.after( absenceStartTime )){
				woSignFeature.setSignSeq(3);
			}else if(lateStartTime != null && now.after( lateStartTime )){
				woSignFeature.setSignSeq(2);
			}
		}else{
			//打了一次，之后，有可能是中午签到打卡，有可能是下午下班签退打卡
			//woSignFeature.setSignSeq( wraps.size() + 1 );
			//可能是午休或者下班卡了，上班打不可能打了
			//看当前时间，有没有过中午签到时间
			if( morningOffdutyTime !=null && morningOffdutyTime.before( now ) ){//现在已经是午休之后了
				//看看下班没，如果下班了，就直接打下班卡了，否则，还可以打个午休卡
				if( offDutyTime !=null && offDutyTime.before( now ) ){
					//下班了，要么打下班卡
					if( !signRecordStatus.getAlreadyOffDuty()){
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						if(offDutyTime !=null && now.after( offDutyTime )){
							woSignFeature.setSignSeq(1);
						}else if(leaveEarlyStartTime != null && leaveEarlyStartTime.after(now)){
							woSignFeature.setSignSeq(4);
						}
					}else{
						//下班卡打过了，不用再打卡了
						woSignFeature.setSignSeq(5);
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
					}
				}else{
					//还没有下午，可以打午休卡，看看是否已经打过午休卡了
					if( !signRecordStatus.getAlreadyAfternoon()){
						//没有打过午休卡，那么可以打一下午休卡
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON );
						woSignFeature.setSignSeq(1);
					}else{
						//午休卡打过了，看看下班卡打过没有，如果没有，可以打下班卡
						if( !signRecordStatus.getAlreadyOffDuty() ){
							woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
							if(offDutyTime !=null && now.after( offDutyTime )){
								woSignFeature.setSignSeq(1);
							}else if(leaveEarlyStartTime != null && leaveEarlyStartTime.after(now)){
								woSignFeature.setSignSeq(4);
							}
						}else{
							//下班卡打过了，不用再打卡了
							woSignFeature.setSignSeq(5);
							woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						}
					}
				}
			}else{
				//现在在午休之前，要么上班卡，要么午休卡，已经打过一次了，肯定不能再打上班卡了，看看是否已经打过了午休卡
				if( !signRecordStatus.getAlreadyAfternoon() ){
					//没有打过午休卡，那么可以打午休卡
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON );
					woSignFeature.setSignSeq(1);
				}else{
					//午休卡打过了，看看下班卡打过没有，如果没有，可以打下班卡
					if( !signRecordStatus.getAlreadyOffDuty()){
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						if(offDutyTime !=null && now.after( offDutyTime )){
							woSignFeature.setSignSeq(1);
						}else if(leaveEarlyStartTime != null && leaveEarlyStartTime.after(now)){
							woSignFeature.setSignSeq(4);
						}
					}else{
						//下班卡打过了，不用再打卡了
						woSignFeature.setSignSeq(5);
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
					}
				}
			}
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

		if( ListTools.isEmpty( wraps )){
			//一次都没有打过，看看当前时间是上班还是下午，如果是上午，就是上午签到，如果是下午，就是下午签到
			if( now.after(afternoonOndutyTime)){
				//在下午下班时间之后了，下午上班签到打卡
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY );
				if( absenceStartTime != null && now.after( absenceStartTime )){
					woSignFeature.setSignSeq(3);
				}else if(lateStartTimeAfternoon != null && now.after( lateStartTimeAfternoon )){
					woSignFeature.setSignSeq(2);
				}
			}else{
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
				if(onDutyTime != null && onDutyTime.after(now)){
					woSignFeature.setSignSeq(1);
				}else if( absenceStartTime != null && now.after( absenceStartTime )){
					woSignFeature.setSignSeq(3);
				}else if(lateStartTime != null && now.after( lateStartTime )){
					woSignFeature.setSignSeq(2);
				}
			}
		}else{
			//当前是什么区间
			if( onDutyTime.after(now)){
				//上午上班之前，无论几次，都只可能是上午的下班卡
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY );
				if(leaveEarlyStartTimeMorning!=null && now.after(leaveEarlyStartTimeMorning)){
					woSignFeature.setSignSeq(4);
				}else{
					woSignFeature.setSignSeq(1);
				}
			}else if( now.after(onDutyTime) && morningOffdutyTime.after(now)){
				//上午上班时段: 上午签退
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY );
				woSignFeature.setSignSeq(1);
			}else if( now.after(morningOffdutyTime) && afternoonOndutyTime.after( now )){
				//午休时段：前一次打卡有可能上午签到卡，可能下午签到卡
				if( signRecordStatus.getAlreadyOnduty() ){ //已经上午签到过了，只有一次卡，应该就是签到
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY );
					if(leaveEarlyStartTimeMorning!=null && now.after(leaveEarlyStartTimeMorning)){
						woSignFeature.setSignSeq(4);
					}else{
						woSignFeature.setSignSeq(1);
					}
				}else if( signRecordStatus.getAlreadyAfternoonOnDuty()){
					//如果上午没有签到，是下午的签到的话，第二次就应该是下午签退打卡了
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
					if(leaveEarlyStartTime!=null && now.before(leaveEarlyStartTime)){
						woSignFeature.setSignSeq(4);
					}else{
						woSignFeature.setSignSeq(1);
					}
				}
			}else if( now.after(afternoonOndutyTime) && offDutyTime.after(now)){
				//下午上班时段，如果前一次是下午签到，那么下一次就应该是下午签退了，否则，就是下午签到
				if( signRecordStatus.getAlreadyAfternoonOnDuty()){
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY  );
					 if(leaveEarlyStartTime != null && leaveEarlyStartTime.after(now)){
						woSignFeature.setSignSeq(4);
					}else{
						 woSignFeature.setSignSeq(1);
					 }
				}else{
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY  );
					if(lateStartTimeAfternoon!=null && now.after(lateStartTimeAfternoon)){
						woSignFeature.setSignSeq(2);
					}else{
						woSignFeature.setSignSeq(1);
					}
				}
			}else{
				//下午下班之后，只可能是下午的签到签退卡了
				if( signRecordStatus.getAlreadyAfternoonOnDuty()){
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY  );
					woSignFeature.setSignSeq(1);
				}else{
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY  );
					if(lateStartTimeAfternoon!=null && now.after(lateStartTimeAfternoon)){
						woSignFeature.setSignSeq(2);
					}else{
						woSignFeature.setSignSeq(1);
					}
				}
			}
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