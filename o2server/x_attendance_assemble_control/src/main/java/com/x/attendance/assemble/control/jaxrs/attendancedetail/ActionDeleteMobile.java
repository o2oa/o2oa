package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionDeleteMobile extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDeleteMobile.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		
		ActionResult<Wo> result = new ActionResult<>();

		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			AttendanceDetailMobile attendanceDetailMobile = emc.find(id, AttendanceDetailMobile.class);
			if ( null == attendanceDetailMobile ) {
				Exception exception = new ExceptionDetaillMobileNotExists( id );
				result.error( exception );
			}else{
				//进行数据库持久化操作				
				emc.beginTransaction( AttendanceDetailMobile.class );
				emc.remove( attendanceDetailMobile, CheckRemoveType.all );
				emc.commit();
				result.setData( new Wo(id) );
				logger.info( "成功删除打卡数据信息。id=" + id );
			}			
		} catch ( Exception e ) {
			Exception exception = new ExceptionDetaillMobileNotExists( id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}