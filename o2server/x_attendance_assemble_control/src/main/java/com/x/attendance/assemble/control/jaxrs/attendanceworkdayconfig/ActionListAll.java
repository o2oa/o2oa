package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceWorkDayConfigFactory;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

public class ActionListAll extends BaseAction {
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Business business = null;
		AttendanceWorkDayConfigFactory attendanceWorkDayConfigFactory = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			attendanceWorkDayConfigFactory = business.getAttendanceWorkDayConfigFactory();
			// 查询ID IN ids 的所有应用信息列表
			attendanceWorkDayConfigList = attendanceWorkDayConfigFactory.listAll();
			// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = Wo.copier.copy( attendanceWorkDayConfigList );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		result.setData(wraps);
		return result;
	}
	
	public static class Wo extends AttendanceWorkDayConfig  {
		
		private static final long serialVersionUID = -5076990764713538973L;		
		
		public static WrapCopier<AttendanceWorkDayConfig, Wo> copier = 
				WrapCopierFactory.wo( AttendanceWorkDayConfig.class, Wo.class, null,JpaObject.FieldsInvisible);
	}
}