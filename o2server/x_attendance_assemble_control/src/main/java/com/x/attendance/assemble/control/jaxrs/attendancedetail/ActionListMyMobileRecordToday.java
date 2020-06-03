package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.attendance.assemble.common.date.DateOperation;
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
 * 查询登录者当天的所有移动打卡信息
 */
public class ActionListMyMobileRecordToday extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListMyMobileRecordToday.class);
	private DateOperation dateOperation = new DateOperation();

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
		WoSignFeature woSignFeature = null;
		if (check
				&& !StringUtils.equalsAnyIgnoreCase("xadmin", effectivePerson.getName())
				&& !StringUtils.equalsAnyIgnoreCase("cipher", effectivePerson.getName())) {
			//打卡策略：1-两次打卡（上午上班，下午下班） 2-三次打卡（上午上班，下午下班加中午一次共三次） 3-四次打卡（上午下午都打上班下班卡）
			scheduleSetting = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithPerson( effectivePerson.getDistinguishedName(), effectivePerson.getDebugger() );

			if( scheduleSetting != null ){
				woScheduleSetting = WoScheduleSetting.copier.copy(scheduleSetting);

				if( woScheduleSetting.getSignProxy() == 3 ){
					//3-四次打卡（上午下午都打上班下班卡）
					woSignFeature = getWoSignFeatureWithProxy3(wraps, woScheduleSetting);
				}else if( woScheduleSetting.getSignProxy() == 2 ){
					//2-三次打卡（上午上班，下午下班加中午一次共三次）
					woSignFeature = getWoSignFeatureWithProxy2(wraps, woScheduleSetting);
				}else{
					//1-两次打卡（上午上班，下午下班）
					woSignFeature = getWoSignFeatureWithProxy1(wraps, woScheduleSetting);
				}
			}
			if( woSignFeature != null ){
				woSignFeature.setSignDate( signDate );
			}
		}

		Wo wo = new Wo();
		wo.setRecords( wraps );
		wo.setFeature( woSignFeature );
		wo.setScheduleSetting( woScheduleSetting );
		result.setCount(total);
		result.setData(wo);

		return result;
	}

	/**
	 * 计算下一次打卡是什么打卡
	 * 3-四次打卡（上午下午都打上班下班卡）
	 *
	 * @param wraps
	 * @param scheduleSetting
	 * @return
	 */
	private WoSignFeature getWoSignFeatureWithProxy3(List<WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) throws Exception {
		WoSignFeature woSignFeature = new WoSignFeature();
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
	private WoSignFeature getWoSignFeatureWithProxy2(List<WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) throws Exception {
		WoSignFeature woSignFeature = new WoSignFeature();
		SignRecordStatus signRecordStatus = null;

		Date now = new Date();
		Date middayRestEndTime = null;
		Date onDutyTime = null, offDutyTime = null;
		Date morningOffdutyTime = null, afternoonOndutyTime = null;
		String todayDateStr = dateOperation.getDateStringFromDate( now, "YYYY-MM-DD");

		//计算，上班下班时间
		onDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOnDutyTime() );
		morningOffdutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestStartTime() );
		afternoonOndutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestEndTime() );
		offDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOffDutyTime() );

		signRecordStatus = getSignRecordStatus( wraps, onDutyTime, morningOffdutyTime, afternoonOndutyTime, offDutyTime );

		if( ListTools.isEmpty( wraps )){
			//一次都没有打过，不管几点，都是上班打卡
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( "上班签到" );
			woSignFeature.setSignTime(scheduleSetting.getOnDutyTime());
		}else{
			//打了一次，之后，有可能是中午签到打卡，有可能是下午下班签退打卡
			if( wraps.size() == 1 ){
				woSignFeature.setSignSeq(2);
				//第一次打卡，肯定是打了上班卡，第二次就可能是午休或者下班卡了
				if( StringUtils.isNotEmpty( scheduleSetting.getMiddayRestEndTime())){
					middayRestEndTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestEndTime() );
					//看当前时间，有没有过中午签到时间
					if( middayRestEndTime !=null && middayRestEndTime.before( now )){
						//在午休结束之后了，就是下班打卡了，上班和午休都缺卡了
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
					}else{
						//午休卡
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON );
						woSignFeature.setSignTime(scheduleSetting.getMiddayRestEndTime());
					}
				}
			}else if( wraps.size() == 2 ){
				if( StringUtils.isNotEmpty( scheduleSetting.getMiddayRestEndTime())) {
					middayRestEndTime = dateOperation.getDateFromString(todayDateStr + " " + scheduleSetting.getMiddayRestEndTime());
					//只可能是签退卡了，如果签退卡都打了，那么就是不需要再打卡了。
					//判断是否已经打了下班签退卡，看看是否有午休结束时间之后的打卡
					Boolean exists_offDuty = false;
					Date signTime = null;
					for( WoMobileRecord record : wraps ){
						signTime = dateOperation.getDateFromString(todayDateStr + " " + record.getSignTime() );
						if( signTime.after( middayRestEndTime )){
							exists_offDuty = true;
							break;
						}
					}
					if( exists_offDuty ){
						woSignFeature.setSignSeq(-1);
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
					}else{
						woSignFeature.setSignSeq(3);
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
					}
				}
			}else{
				woSignFeature.setSignSeq(-1);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
				woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
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
	private WoSignFeature getWoSignFeatureWithProxy1(List<WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) {
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

	public static class Wo{

		@FieldDescribe("所有的打卡记录.")
		private List<WoMobileRecord> records;

		@FieldDescribe("个人相关的排班信息")
		private WoScheduleSetting scheduleSetting;

		@FieldDescribe("下一次打卡信息")
		private WoSignFeature feature;

		public WoScheduleSetting getScheduleSetting() { return scheduleSetting; }

		public void setScheduleSetting(WoScheduleSetting scheduleSetting) { this.scheduleSetting = scheduleSetting; }

		public List<WoMobileRecord> getRecords() { return records; }

		public void setRecords(List<WoMobileRecord> records) { this.records = records; }

		public WoSignFeature getFeature() { return feature; }

		public void setFeature(WoSignFeature feature) { this.feature = feature; }
	}

	public static class WoMobileRecord extends AttendanceDetailMobile {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceDetailMobile, WoMobileRecord> copier = WrapCopierFactory.wo(AttendanceDetailMobile.class,
				WoMobileRecord.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoScheduleSetting extends AttendanceScheduleSetting {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceScheduleSetting, WoScheduleSetting> copier = WrapCopierFactory.wo(AttendanceScheduleSetting.class,
				WoScheduleSetting.class, null, JpaObject.FieldsInvisible);
	}

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
}