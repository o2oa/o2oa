package com.x.attendance.assemble.control.jaxrs.attendancestatisticrequirelog;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

public class ActionGet extends BaseAction {
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		AttendanceStatisticRequireLog attendanceStatisticRequireLog = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			attendanceStatisticRequireLog = emc.find( id, AttendanceStatisticRequireLog.class );
			if( attendanceStatisticRequireLog != null ){
				//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wrap = Wo.copier.copy(attendanceStatisticRequireLog);				
				//对查询的列表进行排序				
				result.setData(wrap);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

	public static class Wo extends AttendanceStatisticRequireLog  {
		
		private static final long serialVersionUID = -5076990764713538973L;		
		
		public static WrapCopier<AttendanceStatisticRequireLog, Wo> copier = 
				WrapCopierFactory.wo( AttendanceStatisticRequireLog.class, Wo.class, null,JpaObject.FieldsInvisible);
	}

}