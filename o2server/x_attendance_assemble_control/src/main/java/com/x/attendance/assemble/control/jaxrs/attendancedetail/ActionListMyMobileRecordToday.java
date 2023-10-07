package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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
		
		//列示排班详情
		 List<WoSignFeature> scheduleInfos = new ArrayList<>();
				if (check
						&& !StringUtils.equalsAnyIgnoreCase("xadmin", effectivePerson.getName())
						&& !StringUtils.equalsAnyIgnoreCase("cipher", effectivePerson.getName())) {
					//打卡策略：1-两次打卡（上午上班，下午下班） 2-三次打卡（上午上班，下午下班加中午一次共三次） 3-四次打卡（上午下午都打上班下班

					if( woScheduleSetting != null ){
						WoSignFeature scheduleInfo1 = new WoSignFeature();
						scheduleInfo1.setSignSeq(1);
						scheduleInfo1.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
						scheduleInfo1.setSignTime(scheduleSetting.getOnDutyTime());
						scheduleInfo1.setSignDate( signDate );
						scheduleInfos.add(scheduleInfo1);
						

						if( woScheduleSetting.getSignProxy() == 3 ){
							//3-四次打卡（上午下午都打上班下班卡）
							WoSignFeature scheduleInfo2 = new WoSignFeature();
							scheduleInfo2.setSignSeq(2);
							scheduleInfo2.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY );
							scheduleInfo2.setSignTime(scheduleSetting.getMiddayRestStartTime());
							scheduleInfo2.setSignDate( signDate );
							scheduleInfos.add(scheduleInfo2);
							
							WoSignFeature scheduleInfo3 = new WoSignFeature();
							scheduleInfo3.setSignSeq(3);
							scheduleInfo3.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY );
							scheduleInfo3.setSignTime(scheduleSetting.getMiddayRestEndTime());
							scheduleInfo3.setSignDate( signDate );
							scheduleInfos.add(scheduleInfo3);
							
						}else if( woScheduleSetting.getSignProxy() == 2 ){
							//2-三次打卡（上午上班，下午下班加中午一次共三次）
							WoSignFeature scheduleInfo3 = new WoSignFeature();
							scheduleInfo3.setSignSeq(3);
							scheduleInfo3.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON );
							scheduleInfo3.setSignTime(scheduleSetting.getMiddayRestEndTime());
							scheduleInfo3.setSignDate( signDate );
							scheduleInfos.add(scheduleInfo3);
						}else{
							//1-两次打卡（上午上班，下午下班）
						}
						
						WoSignFeature scheduleInfo4 = new WoSignFeature();
						scheduleInfo4.setSignSeq(4);
						scheduleInfo4.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						scheduleInfo4.setSignTime(scheduleSetting.getOffDutyTime());
						scheduleInfo4.setSignDate( signDate );
						scheduleInfos.add(scheduleInfo4);
					}
					/*if( scheduleIf != null ){
						scheduleIf.setSignDate( signDate );
					}*/
				}

		Wo wo = new Wo();
		wo.setRecords( wraps );
		wo.setFeature( woSignFeature );
		wo.setScheduleSetting( woScheduleSetting );
		wo.setScheduleInfos(scheduleInfos);
		result.setCount(total);
		result.setData(wo);

		return result;
	}

	public static class Wo{

		@FieldDescribe("所有的打卡记录.")
		private List<WoMobileRecord> records;

		@FieldDescribe("个人相关的排班信息")
		private WoScheduleSetting scheduleSetting;

		@FieldDescribe("下一次打卡信息")
		private WoSignFeature feature;
		
		@FieldDescribe("排班详情")
		private List<WoSignFeature> scheduleInfos;

		public WoScheduleSetting getScheduleSetting() { return scheduleSetting; }

		public void setScheduleSetting(WoScheduleSetting scheduleSetting) { this.scheduleSetting = scheduleSetting; }

		public List<WoMobileRecord> getRecords() { return records; }

		public void setRecords(List<WoMobileRecord> records) { this.records = records; }

		public WoSignFeature getFeature() { return feature; }

		public void setFeature(WoSignFeature feature) { this.feature = feature; }
		
		public List<WoSignFeature> getScheduleInfos() { return scheduleInfos; }

		public void setScheduleInfos(List<WoSignFeature> scheduleInfos) { this.scheduleInfos = scheduleInfos; }
	}

	public static class WoScheduleSetting extends AttendanceScheduleSetting {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceScheduleSetting, WoScheduleSetting> copier = WrapCopierFactory.wo(AttendanceScheduleSetting.class,
				WoScheduleSetting.class, null, JpaObject.FieldsInvisible);
	}


}