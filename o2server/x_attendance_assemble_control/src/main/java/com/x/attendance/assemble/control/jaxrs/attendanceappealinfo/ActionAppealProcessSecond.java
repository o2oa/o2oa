package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.jaxrs.attendanceappealinfo.exception.ExceptionAttendanceAppealNotExists;
import com.x.attendance.assemble.control.jaxrs.attendanceappealinfo.exception.ExceptionAttendanceAppealProcess;
import com.x.attendance.assemble.control.jaxrs.attendanceappealinfo.exception.ExceptionPersonHasNoUnit;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionAppealProcessSecond extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger( ActionAppealProcessSecond.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		String unitName = null;
		String topUnitName = null;
		AttendanceAppealInfo attendanceAppealInfo = null;
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
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.get(id);
				if (attendanceAppealInfo == null) {
					check = false;
					Exception exception = new ExceptionAttendanceAppealNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "系统在根据ID查询考勤申诉信息记录数据时发生异常。ID:"+ id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				unitName = userManagerService.getUnitNameWithPersonName(effectivePerson.getDistinguishedName());
				if (unitName != null) {
					topUnitName = userManagerService.getTopUnitNameWithUnitName( unitName );
				} else {
					check = false;
					Exception exception = new ExceptionPersonHasNoUnit(effectivePerson.getDistinguishedName());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				Exception exception = new ExceptionAttendanceAppealProcess(e, "系统根据员工姓名查询组织信息时发生异常！name:"+effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.secondProcessAttendanceAppeal(id, unitName, topUnitName, effectivePerson.getDistinguishedName(), // processorName
						new Date(), // processTime
						wrapIn.getOpinion2(), // opinion
						wrapIn.getStatus() // status
				);
				result.setData(new Wo(id));
			} catch (Exception e) {
				check = false;
				result.error(e);
				Exception exception = new ExceptionAttendanceAppealProcess(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends AttendanceAppealInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}