package com.x.attendance.assemble.control.jaxrs.attendancestatisticrequirelog;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceStatisticRequireLogFactory;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
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
		List<AttendanceStatisticRequireLog> attendanceStatisticRequireLogList = null;
		Business business = null;
		AttendanceStatisticRequireLogFactory attendanceStatisticRequireLogFactory = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			business = new Business(emc);					
			attendanceStatisticRequireLogFactory  = business.getAttendanceStatisticRequireLogFactory();					
			//查询ID IN ids 的所有应用信息列表
			attendanceStatisticRequireLogList = attendanceStatisticRequireLogFactory.listAll();	
			//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = Wo.copier.copy( attendanceStatisticRequireLogList );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}		
		result.setData(wraps);
		return result;
	}
	
	public static class Wo extends AttendanceStatisticRequireLog  {
		
		private static final long serialVersionUID = -5076990764713538973L;		
		
		public static WrapCopier<AttendanceStatisticRequireLog, Wo> copier = 
				WrapCopierFactory.wo( AttendanceStatisticRequireLog.class, Wo.class, null,JpaObject.FieldsInvisible);
	}
}