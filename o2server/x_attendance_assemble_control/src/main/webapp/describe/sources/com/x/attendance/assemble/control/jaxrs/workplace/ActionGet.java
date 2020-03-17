package com.x.attendance.assemble.control.jaxrs.workplace;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.jaxrs.workplace.exception.ExceptionWorkPlaceProcess;
import com.x.attendance.entity.AttendanceWorkPlace;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGet.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Wo wrap = null;
		AttendanceWorkPlace attendanceWorkPlace = null;
		Boolean check = true;
		if (check) {
			try {
				attendanceWorkPlace = attendanceWorkPlaceServiceAdv.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkPlaceProcess(e, "系统根据ID查询工作场所对象信息时发生异常。ID:" + id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			if (attendanceWorkPlace != null) {
				try {
					wrap = Wo.copier.copy(attendanceWorkPlace);
					result.setData(wrap);
				} catch (Exception e) {
					Exception exception = new ExceptionWorkPlaceProcess(e, "系统将查询结果转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}

			}
		}
		return result;
	}

	public static class Wo extends AttendanceWorkPlace  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier<AttendanceWorkPlace, Wo> copier = 
				WrapCopierFactory.wo( AttendanceWorkPlace.class, Wo.class, null,JpaObject.FieldsInvisible);
	}

}