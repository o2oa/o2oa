package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.jaxrs.selfholiday.exception.ExceptionSelfHolidayProcess;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionSave extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = null;
		//获取到当前用户信息
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				AttendanceSelfHoliday _attendanceSelfHoliday = null;
				AttendanceSelfHoliday attendanceSelfHoliday = new AttendanceSelfHoliday();
				if( wrapIn != null && wrapIn.getId() !=null && wrapIn.getId().length() > 10 ){
					
					if( wrapIn.getId() !=null && wrapIn.getId().length() > 10 ){
						//根据ID查询信息是否存在，如果存在就update，如果不存在就create
						_attendanceSelfHoliday = emc.find( wrapIn.getId(), AttendanceSelfHoliday.class );
						if( _attendanceSelfHoliday != null ){
							//更新
							emc.beginTransaction( AttendanceSelfHoliday.class );
							Wi.copier.copy( wrapIn, _attendanceSelfHoliday );
							emc.check( _attendanceSelfHoliday, CheckPersistType.all);	
							emc.commit();
							result.setData( new Wo( _attendanceSelfHoliday.getId() ) );
						}else{
							emc.beginTransaction( AttendanceSelfHoliday.class );
							Wi.copier.copy( wrapIn, attendanceSelfHoliday );
							attendanceSelfHoliday.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
							emc.persist( attendanceSelfHoliday, CheckPersistType.all);	
							emc.commit();
							result.setData( new Wo( attendanceSelfHoliday.getId() ) );
						}
					}else{
						//没有传入指定的ID
						emc.beginTransaction( AttendanceSelfHoliday.class );
						Wi.copier.copy( wrapIn, attendanceSelfHoliday );
						emc.persist( attendanceSelfHoliday, CheckPersistType.all);	
						emc.commit();
						result.setData( new Wo( attendanceSelfHoliday.getId() ) );
					}
					
					//根据员工休假数据来记录与这条数据相关的统计需求记录
					//new AttendanceDetailAnalyseService().recordStatisticRequireLog( attendanceSelfHoliday );
					
					//休假数据有更新，对该员工的该请假时间内的所有打卡记录进行分析
					List<String> ids = attendanceDetailAnalyseServiceAdv.getAnalyseAttendanceDetailIds( attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime() );
					if( ids != null && ids.size() > 0 ){
						try {//查询所有的周期配置，组织成Map
							topUnitAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles( effectivePerson.getDebugger() );
						} catch (Exception e) {
							Exception exception = new ExceptionSelfHolidayProcess( e, "系统在查询并且组织所有的统计周期时发生异常." );
							result.error( exception );
							logger.error( e, currentPerson, request, null);
						}
						attendanceDetailAnalyseServiceAdv.analyseAttendanceDetails( attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime(), topUnitAttendanceStatisticalCycleMap, effectivePerson.getDebugger()  );
					}
				}
			} catch ( Exception e ) {
				Exception exception = new ExceptionSelfHolidayProcess( e, "系统在保存员工请假记录信息时发生异常." );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends AttendanceSelfHoliday {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier< Wi, AttendanceSelfHoliday > copier = 
				WrapCopierFactory.wi( Wi.class, AttendanceSelfHoliday.class, null, JpaObject.FieldsUnmodify );
		
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}