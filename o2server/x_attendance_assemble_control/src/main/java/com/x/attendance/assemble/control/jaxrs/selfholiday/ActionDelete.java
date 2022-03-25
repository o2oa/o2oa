package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionDelete extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			AttendanceSelfHoliday attendanceSelfHoliday = emc.find(id, AttendanceSelfHoliday.class);
			if (null == attendanceSelfHoliday) {
				Exception exception = new ExceptionSelfHolidayNotExists( id );
				result.error( exception );
			}else{
				emc.beginTransaction( AttendanceSelfHoliday.class );
				emc.remove( attendanceSelfHoliday, CheckRemoveType.all );
				emc.commit();

				//清除缓存
				CacheManager.notify( AttendanceSelfHoliday.class );

				result.setData( new Wo(id) );

				//根据员工休假数据来记录与这条数据相关的统计需求记录
				List<String> ids = attendanceDetailAnalyseServiceAdv.listAnalyseAttendanceDetailIds( attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime(), effectivePerson.getDebugger() );
//				if( ListTools.isNotEmpty( ids ) ){
//					attendanceDetailAnalyseServiceAdv.analyseAttendanceDetails( attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime(), effectivePerson.getDebugger()  );
//				}
				if( ListTools.isNotEmpty( ids ) ){
					for( String _id : ids ){
						try { //分析保存好的考勤数据
							ThisApplication.detailAnalyseQueue.send( _id );
						} catch ( Exception e1 ) {
							e1.printStackTrace();
						}
					}
				}
			}			
		} catch ( Exception e ) {
			Exception exception = new ExceptionSelfHolidayProcess( e, "系统在删除员工请假记录信息时发生异常.ID：" + id );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}