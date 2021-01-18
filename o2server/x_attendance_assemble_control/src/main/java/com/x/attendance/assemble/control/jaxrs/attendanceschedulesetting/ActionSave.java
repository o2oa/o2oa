package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		Wi wrapIn = null;
		String topUnitName = null;
		String unitName = null;
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
			if (StringUtils.isEmpty(wrapIn.getUnitName())) {
				check = false;
				Exception exception = new ExceptionScheduleUnitEmpty();
				result.error(exception);
			}else {
				try {
					unitName = userManagerService.checkUnitNameExists(wrapIn.getUnitName());
					System.out.println(">>>>>>>>>wrapIn.getUnitName()不为空，判断unitName是否存在！ unitName：" + unitName );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceScheduleProcess(e, "系统根据组织名称查询组织信息时发生异常. Name:" + wrapIn.getUnitName());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if (StringUtils.isEmpty(unitName) ) {
				check = false;
				Exception exception = new ExceptionCanNotFindUnitWithUnitName(wrapIn.getUnitName());
				result.error(exception);
			}
		}
		
		//查询顶层组织
		if (check) {
			if (StringUtils.isNotEmpty(unitName) ) {
				try {
					topUnitName = userManagerService.getTopUnitNameWithUnitName(unitName);
					System.out.println(">>>>>>>>>userManagerService.getTopUnitNameWithUnitName(unitName) = " + topUnitName );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceScheduleProcess(e, "系统根据组织名称查询所属顶层组织信息时发生异常. unitName:" + unitName);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if (check) {
			wrapIn.setUnitName(unitName);
			wrapIn.setTopUnitName(topUnitName);
		}
		
		if (check) {
			attendanceScheduleSetting = new AttendanceScheduleSetting();
			try {
				attendanceScheduleSetting = Wi.copier.copy(wrapIn);
				if ( StringUtils.isNotEmpty( wrapIn.getId() )) {
					attendanceScheduleSetting.setId(wrapIn.getId());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceScheduleProcess(e,
						"将所有查询出来的有状态的导入文件对象转换为可以输出的过滤过属性的对象时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			//计算由于打卡次数发生变化引起的打开时间值变化
			Integer signProxy = attendanceScheduleSetting.getSignProxy();
			switch(signProxy){
				case 1 :
					attendanceScheduleSetting.setMiddayRestStartTime("");
					attendanceScheduleSetting.setMiddayRestEndTime("");
					attendanceScheduleSetting.setLateStartTimeAfternoon("");
					attendanceScheduleSetting.setLeaveEarlyStartTimeMorning("");
					break;
				case 2 :
					attendanceScheduleSetting.setLateStartTimeAfternoon("");
					break;
			}


			try {
				attendanceScheduleSetting = attendanceScheduleSettingServiceAdv.save(attendanceScheduleSetting);
				result.setData(new Wo(attendanceScheduleSetting.getId()));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceScheduleProcess(e, "保存组织排班信息时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends AttendanceScheduleSetting {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, AttendanceScheduleSetting> copier = WrapCopierFactory.wi(Wi.class,
				AttendanceScheduleSetting.class, null, JpaObject.FieldsUnmodify);

		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}

	public static class Wo extends WoId {
		public Wo(String id) {
			setId(id);
		}
	}
}