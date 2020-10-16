package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.Business;
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

public class ActionDeleteByWfDocId extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionDeleteByWfDocId.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String wfDocId ) throws Exception {
		logger.debug( effectivePerson, ">>>>>>>>>>method delete has been called, try to delete attendanceSelfHoliday{'wfDocId':'" + wfDocId + "'}......");
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		logger.debug( effectivePerson, ">>>>>>>>>>user " + currentPerson.getDistinguishedName() + "try to delete AttendanceSelfHoliday......");
		List<String> ids = null;
		List<AttendanceSelfHoliday> attendanceSelfHolidays = null;
		Business business = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			// 先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			ids = business.getAttendanceSelfHolidayFactory().listIdsWithBatchFlag(wfDocId);
			if ( ListTools.isNotEmpty( ids ) ) {
				attendanceSelfHolidays = business.getAttendanceSelfHolidayFactory().list(ids);
				if (attendanceSelfHolidays != null && attendanceSelfHolidays.size() > 0) {
					for (AttendanceSelfHoliday attendanceSelfHoliday : attendanceSelfHolidays) {
						logger.debug( effectivePerson, ">>>>>>>>>>System trying to beginTransaction to delete attendanceSelfHoliday......");
						emc.beginTransaction(AttendanceSelfHoliday.class);
						emc.remove(attendanceSelfHoliday, CheckRemoveType.all);
						emc.commit();

						//清除缓存
						CacheManager.notify( AttendanceSelfHoliday.class );

						logger.debug( effectivePerson, ">>>>>>>>>>System delete attendanceSelfHoliday success......");

						// 应该只需要重新分析该用户在请假期间已经存在的打卡数据即可
						ids = attendanceDetailAnalyseServiceAdv.listAnalyseAttendanceDetailIds(
								attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(),
								attendanceSelfHoliday.getEndTime(), effectivePerson.getDebugger() );
//						if ( ListTools.isNotEmpty( ids ) ) {
//							attendanceDetailAnalyseServiceAdv.analyseAttendanceDetails( attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime(), effectivePerson.getDebugger() );
//						}
						if( ListTools.isNotEmpty( ids ) ){
							for( String id : ids ){
								try { //分析保存好的考勤数据
									ThisApplication.detailAnalyseQueue.send( id );
								} catch ( Exception e1 ) {
									e1.printStackTrace();
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			Exception exception = new ExceptionSelfHolidayProcess( e, "系统在根据流程ID删除同步的员工请假记录信息时发生异常.DocId：" + wfDocId );
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		result.setData( new Wo( wfDocId ) );
		return result;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}