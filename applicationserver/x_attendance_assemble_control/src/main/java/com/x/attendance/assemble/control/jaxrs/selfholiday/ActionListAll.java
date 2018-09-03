package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceSelfHolidayFactory;
import com.x.attendance.entity.AttendanceSelfHoliday;
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
		List<String> ids = null;
		List<AttendanceSelfHoliday> attendanceSelfHolidayList = null;
		Business business = null;
		AttendanceSelfHolidayFactory attendanceSelfHolidayFactory = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {			
			business = new Business(emc);					
			attendanceSelfHolidayFactory  = business.getAttendanceSelfHolidayFactory();			
			//获取所有应用列表
			ids = attendanceSelfHolidayFactory.listAll();			
			//查询ID IN ids 的所有应用信息列表
			attendanceSelfHolidayList = attendanceSelfHolidayFactory.list( ids );	
			//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = Wo.copier.copy( attendanceSelfHolidayList );
			
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}				
		result.setData(wraps);
		return result;
	}
	
	public static class Wo extends AttendanceSelfHoliday  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier<AttendanceSelfHoliday, Wo> copier = 
				WrapCopierFactory.wo( AttendanceSelfHoliday.class, Wo.class, null,JpaObject.FieldsInvisible);
	}
}