package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.service.AttendanceAppealInfoServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailAnalyseServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceScheduleSettingServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceWorkDayConfigServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;

public class BaseAction extends StandardJaxrsAction{
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected AttendanceDetailAnalyseServiceAdv attendanceDetailAnalyseServiceAdv = new AttendanceDetailAnalyseServiceAdv();
	protected AttendanceWorkDayConfigServiceAdv attendanceWorkDayConfigServiceAdv = new AttendanceWorkDayConfigServiceAdv();
	protected AttendanceStatisticalCycleServiceAdv attendanceStatisticCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
	protected AttendanceDetailServiceAdv attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
	protected AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
	protected AttendanceScheduleSettingServiceAdv attendanceScheduleSettingServiceAdv = new AttendanceScheduleSettingServiceAdv();
	protected AttendanceAppealInfoServiceAdv attendanceAppealInfoServiceAdv = new AttendanceAppealInfoServiceAdv();

	public static class WoSignFeature{

		@FieldDescribe("下一次打卡次数，第一次为1")
		private Integer signSeq = 1;

		@FieldDescribe("打卡日期：yyyy-mm-dd.")
		private String signDate = null;

		@FieldDescribe("打卡时间：HH:mi:ss.")
		private String signTime = null;

		@FieldDescribe("打卡操作名称：上班打卡|下班打卡|午休打卡|上午下班打卡|下午上班打卡(根据不同的打卡策略稍有不同)....")
		private String checkinType = "上班打卡";

		@FieldDescribe("最晚打卡时间")
		private Date latestSignTime;

		public String getSignTime() { return signTime; }

		public void setSignTime(String signTime) { this.signTime = signTime; }

		public Integer getSignSeq() { return signSeq; }

		public void setSignSeq(Integer signSeq) { this.signSeq = signSeq; }

		public String getSignDate() { return signDate; }

		public void setSignDate(String signDate) { this.signDate = signDate; }

		public String getCheckinType() { return checkinType; }

		public void setCheckinType(String checkinType) { this.checkinType = checkinType; }

		public Date getLatestSignTime() { return latestSignTime; }

		public void setLatestSignTime(Date latestSignTime) { this.latestSignTime = latestSignTime; }

	}


	/**
	 * 计算下一次打卡是什么打卡
	 * 3-四次打卡（上午下午都打上班下班卡）
	 *
	 * @param wraps
	 * @param scheduleSetting
	 * @return
	 */
	protected WoSignFeature getWoSignFeatureWithProxy3(List<ActionListMyMobileRecordToday.WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) throws Exception {
		DateOperation dateOperation = new DateOperation();
		WoSignFeature woSignFeature = new WoSignFeature();
		Date now = new Date();
		Date onDutyTime = null, offDutyTime = null;
		Date morningOffdutyTime = null, afternoonOndutyTime = null;
		String todayDateStr = dateOperation.getDateStringFromDate( now, "YYYY-MM-DD");
		ActionListMyMobileRecordToday.SignRecordStatus signRecordStatus = null;

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

		signRecordStatus = getSignRecordStatus( wraps, onDutyTime, morningOffdutyTime, afternoonOndutyTime, offDutyTime );

		if( ListTools.isEmpty( wraps )){
			//一次都没有打过，看看当前时间是上班还是下午，如果是上午，就是上午签到，如果是下午，就是下午签到
			if( now.after(afternoonOndutyTime)){
				//在下午下班时间之后了，下午上班签到打卡
				woSignFeature.setSignSeq(1);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY );
				woSignFeature.setSignTime(scheduleSetting.getOnDutyTime());
			}else{
				woSignFeature.setSignSeq(1);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
				woSignFeature.setSignTime(scheduleSetting.getOnDutyTime());
			}
		}else{
			woSignFeature.setSignSeq(2);
			//当前是什么区间
			if( onDutyTime.after(now)){
				//上午上班之前，无论几次，都只可能是上午的下班卡
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY );
				woSignFeature.setSignTime(scheduleSetting.getMiddayRestStartTime());
				woSignFeature.setSignSeq(2);
			}else if( now.after(onDutyTime) && morningOffdutyTime.after(now)){
				//上午上班时段: 上午签退
				woSignFeature.setSignSeq(2);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY );
				woSignFeature.setSignTime(scheduleSetting.getMiddayRestStartTime());
			}else if( now.after(morningOffdutyTime) && afternoonOndutyTime.after( now )){
				//午休时段：前一次打卡有可能上午签到卡，可能下午签到卡
				if( signRecordStatus.alreadyOnduty ){ //已经上午签到过了，只有一次卡，应该就是签到
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY );
					woSignFeature.setSignTime(scheduleSetting.getMiddayRestStartTime());
				}else if( signRecordStatus.alreadyAfternoonOnDuty){
					//如果上午没有签到，是下午的签到的话，第二次就应该是下午签退打卡了
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
					woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
				}
			}else if( now.after(afternoonOndutyTime) && offDutyTime.after(now)){
				//下午上班时段，如果前一次是下午签到，那么下一次就应该是下午签退了，否则，就是下午签到
				if( signRecordStatus.alreadyAfternoonOnDuty){
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY  );
					woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
				}else{
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY  );
					woSignFeature.setSignTime(scheduleSetting.getMiddayRestEndTime());
				}
			}else{
				//下午下班之后，只可能是下午的签到签退卡了
				if( signRecordStatus.alreadyAfternoonOnDuty){
					woSignFeature.setSignSeq(-1);
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY  );
					woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
				}else{
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY  );
					woSignFeature.setSignTime(scheduleSetting.getMiddayRestEndTime());
				}
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
	protected WoSignFeature getWoSignFeatureWithProxy2(List<ActionListMyMobileRecordToday.WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) throws Exception {
		DateOperation dateOperation = new DateOperation();
		WoSignFeature woSignFeature = new WoSignFeature();
		ActionListMyMobileRecordToday.SignRecordStatus signRecordStatus = null;

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
		signRecordStatus = getSignRecordStatus( wraps, onDutyTime, morningOffdutyTime, afternoonOndutyTime, offDutyTime );

		if( ListTools.isEmpty( wraps )){
			//一次都没有打过，只能打上班卡
			if( woSignFeature.getSignSeq() == 0 ){
				woSignFeature.setSignSeq(1);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
				woSignFeature.setSignTime(scheduleSetting.getOnDutyTime());
			}
		}else{
			//打了一次，之后，有可能是中午签到打卡，有可能是下午下班签退打卡
			woSignFeature.setSignSeq( wraps.size() + 1 );
			//可能是午休或者下班卡了，上班打不可能打了
			//看当前时间，有没有过中午签到时间
			if( morningOffdutyTime !=null && morningOffdutyTime.before( now ) ){//现在已经是午休之后了
				//看看下班没，如果下班了，就直接打下班卡了，否则，还可以打个午休卡
				if( offDutyTime !=null && offDutyTime.before( now ) ){
					//下班了，要么打下班卡
					if( !signRecordStatus.alreadyOffDuty ){
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
					}else{
						//下班卡打过了，不用再打卡了
						woSignFeature.setSignSeq(-1);
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
					}
				}else{
					//还没有下午，可以打午休卡，看看是否已经打过午休卡了
					if( !signRecordStatus.alreadyAfternoon ){
						//没有打过午休卡，那么可以打一下午休卡
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON );
						woSignFeature.setSignTime(scheduleSetting.getMiddayRestEndTime());
					}else{
						//午休卡打过了，看看下班卡打过没有，如果没有，可以打下班卡
						if( !signRecordStatus.alreadyOffDuty ){
							woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
							woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
						}else{
							//下班卡打过了，不用再打卡了
							woSignFeature.setSignSeq(-1);
							woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
							woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
						}
					}
				}
			}else{
				//现在在午休之前，要么上班卡，要么午休卡，已经打过一次了，肯定不能再打上班卡了，看看是否已经打过了午休卡
				if( !signRecordStatus.alreadyAfternoon ){
					//没有打过午休卡，那么可以打午休卡
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON );
					woSignFeature.setSignTime(scheduleSetting.getMiddayRestEndTime());
				}else{
					//午休卡打过了，看看下班卡打过没有，如果没有，可以打下班卡
					if( !signRecordStatus.alreadyOffDuty ){
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
					}else{
						//下班卡打过了，不用再打卡了
						woSignFeature.setSignSeq(-1);
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
					}
				}
			}
		}

		return woSignFeature;
	}

	/**
	 * 计算下一次打卡是什么打卡
	 * 1-两次打卡（上午上班，下午下班）
	 * @param wraps
	 * @param scheduleSetting
	 * @return
	 */
	protected WoSignFeature getWoSignFeatureWithProxy1(List<ActionListMyMobileRecordToday.WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) {
		WoSignFeature woSignFeature = new WoSignFeature();
		if( ListTools.isEmpty( wraps )){
			//一次都没有打过，不管几点，都是上班打卡
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
			woSignFeature.setSignTime(scheduleSetting.getOnDutyTime());
		}else{
			//打了一次，就是下班打卡，打了两次，就没有了
			if( wraps.size() == 1 ){
				woSignFeature.setSignSeq(2);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
				woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
			}else{
				woSignFeature.setSignSeq(-1); //没有需要的打卡了
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
				woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
			}
		}
		return woSignFeature;
	}

	private ActionListMyMobileRecordToday.SignRecordStatus getSignRecordStatus(List<ActionListMyMobileRecordToday.WoMobileRecord> wraps, Date onDutyTime, Date morningOffdutyTime, Date afternoonOndutyTime, Date offDutyTime) {
		ActionListMyMobileRecordToday.SignRecordStatus signRecordStatus = new ActionListMyMobileRecordToday.SignRecordStatus();
		if( ListTools.isNotEmpty( wraps )){
			for( ActionListMyMobileRecordToday.WoMobileRecord record : wraps ){
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

	public static class SignRecordStatus {

		@FieldDescribe("是否上午上班打卡过了ONDUTY")
		private Boolean alreadyOnduty = false;

		@FieldDescribe("是否上午下班打卡过了MORNING_OFFDUTY.")
		private Boolean alreadyMorningOffDuty = false;

		@FieldDescribe("是否下午上班打卡过了AFTERNOON_ONDUTY")
		private Boolean alreadyAfternoonOnDuty = false;

		@FieldDescribe("是否下午下班打卡过了OFFDUTY.")
		private Boolean alreadyOffDuty = false;

		@FieldDescribe("是否午间打卡过了AfternoonSign.")
		private Boolean alreadyAfternoon = false;

		public Boolean getAlreadyOnduty() { return alreadyOnduty; }

		public void setAlreadyOnduty(Boolean alreadyOnduty) { this.alreadyOnduty = alreadyOnduty; }

		public Boolean getAlreadyMorningOffDuty() { return alreadyMorningOffDuty; }

		public void setAlreadyMorningOffDuty(Boolean alreadyMorningOffDuty) { this.alreadyMorningOffDuty = alreadyMorningOffDuty; }

		public Boolean getAlreadyAfternoonOnDuty() { return alreadyAfternoonOnDuty; }

		public void setAlreadyAfternoonOnDuty(Boolean alreadyAfternoonOnDuty) { this.alreadyAfternoonOnDuty = alreadyAfternoonOnDuty; }

		public Boolean getAlreadyOffDuty() { return alreadyOffDuty; }

		public void setAlreadyOffDuty(Boolean alreadyOffDuty) { this.alreadyOffDuty = alreadyOffDuty; }

		public Boolean getAlreadyAfternoon() { return alreadyAfternoon; }

		public void setAlreadyAfternoon(Boolean alreadyAfternoon) { this.alreadyAfternoon = alreadyAfternoon; }
	}

	public static class WoMobileRecord extends AttendanceDetailMobile {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceDetailMobile, WoMobileRecord> copier = WrapCopierFactory.wo(AttendanceDetailMobile.class,
				WoMobileRecord.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoAttendanceAppealInfo extends AttendanceAppealInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		@FieldDescribe("考勤申诉内容")
		private WoAttendanceAppealAuditInfo appealAuditInfo = null;

		public WoAttendanceAppealAuditInfo getAppealAuditInfo() { return appealAuditInfo; }

		public void setAppealAuditInfo(WoAttendanceAppealAuditInfo appealAuditInfo) { this.appealAuditInfo = appealAuditInfo; }

		public static WrapCopier<AttendanceAppealInfo, WoAttendanceAppealInfo> copier = WrapCopierFactory.wo(AttendanceAppealInfo.class,
				WoAttendanceAppealInfo.class, null, JpaObject.FieldsInvisible);

	}

	public static class WoAttendanceAppealAuditInfo extends AttendanceAppealAuditInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceAppealAuditInfo, WoAttendanceAppealAuditInfo> copier = WrapCopierFactory.wo(AttendanceAppealAuditInfo.class,
				WoAttendanceAppealAuditInfo.class, null, JpaObject.FieldsInvisible);

	}
}
