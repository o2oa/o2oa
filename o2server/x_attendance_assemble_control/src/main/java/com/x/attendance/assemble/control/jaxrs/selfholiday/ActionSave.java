package com.x.attendance.assemble.control.jaxrs.selfholiday;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.jaxrs.selfholiday.exception.ExceptionSelfHolidayProcess;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public class ActionSave extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" );
		logger.info("++++++++++++++++++++++调用接口：com.x.attendance.assemble.control.jaxrs.selfholiday.ActionSave......" );
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		List<AttendanceSelfHoliday> holidayList = null;
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
				AttendanceSelfHoliday attendanceSelfHoliday = null;
//				AttendanceSelfHoliday attendanceSelfHoliday = new AttendanceSelfHoliday();

				if( wrapIn != null && StringUtils.isNoneEmpty( wrapIn.getEmployeeName() )
						&& StringUtils.isNoneEmpty( wrapIn.getEmployeeNumber() )
						&& StringUtils.isNoneEmpty( wrapIn.getLeaveType() )
						&& wrapIn.getStartTime() != null
						&& wrapIn.getEndTime() != null
				){
					emc.beginTransaction( AttendanceSelfHoliday.class );
					//先根据batchFlag删除原来的数据，然后再进行新数据的保存
					if(StringUtils.isNotEmpty( wrapIn.getBatchFlag() ) ){
						holidayList = attendanceSelfHolidayServiceAdv.listWithBatchFlag( wrapIn.getBatchFlag() );
						if(ListTools.isNotEmpty( holidayList )){
							logger.info("++++++++先根据batchFlag删除原来的数据，然后再进行新数据的保存+++++++++" );
							for( AttendanceSelfHoliday holiday : holidayList ){
								emc.remove( emc.find(holiday.getId(), AttendanceSelfHoliday.class ), CheckRemoveType.all );
							}
							logger.info("++++++++删除" + holidayList.size() + "条旧请假信息数据。" );
						}
					}

					if(StringUtils.isNotEmpty( wrapIn.getId() ) ){
						//根据ID查询信息是否存在，如果存在就update，如果不存在就create
						attendanceSelfHoliday = emc.find( wrapIn.getId(), AttendanceSelfHoliday.class );
						if( attendanceSelfHoliday != null ){
							//更新已经存在的信息
							wrapIn.copyTo( attendanceSelfHoliday );
							attendanceSelfHoliday.setBatchFlag(wrapIn.getBatchFlag());
							logger.info("++++++++更新：gson.toJson( attendanceSelfHoliday ) = " + gson.toJson( attendanceSelfHoliday ) );
							emc.check( attendanceSelfHoliday, CheckPersistType.all);
						}else{
							attendanceSelfHoliday = new AttendanceSelfHoliday();
							wrapIn.copyTo( attendanceSelfHoliday );
							//使用参数传入的ID作为记录的ID
							attendanceSelfHoliday.setId( wrapIn.getId() );
							attendanceSelfHoliday.setBatchFlag(wrapIn.getBatchFlag());
							logger.info("++++++++新增：gson.toJson( attendanceSelfHoliday ) = " + gson.toJson( attendanceSelfHoliday ) );
							emc.persist( attendanceSelfHoliday, CheckPersistType.all);
						}
					}else{
						//没有传入指定的ID
						attendanceSelfHoliday = new AttendanceSelfHoliday();

						wrapIn.copyTo( attendanceSelfHoliday );
						attendanceSelfHoliday.setBatchFlag(wrapIn.getBatchFlag());
						logger.info("++++++++新增,无ID：gson.toJson( attendanceSelfHoliday ) = " + gson.toJson( attendanceSelfHoliday ) );
						emc.persist( attendanceSelfHoliday, CheckPersistType.all);
						result.setData( new Wo( attendanceSelfHoliday.getId() ) );
					}
					emc.commit();
					result.setData( new Wo( attendanceSelfHoliday.getId() ) );
					
					//根据员工休假数据来记录与这条数据相关的统计需求记录
					//new AttendanceDetailAnalyseService().recordStatisticRequireLog( attendanceSelfHoliday );
					logger.info("++++++++休假数据有变动，对该员工的该请假时间内的所有打卡记录进行分析......" );
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
				}else{
					if( jsonElement == null ){
						logger.info("++++++++传入的员工请假信息JSON数据为空......" );
						Exception exception = new ExceptionSelfHolidayProcess( "传入的员工请假信息JSON数据为空，无法保存数据信息，请检查数据内容！" );
						result.error( exception );
					}else {
						logger.info("++++++++传入的数据不符合请假数据接口要求......" );
						Exception exception = new ExceptionSelfHolidayProcess( "传入的数据不符合请假数据接口要求，请检查数据内容：" + jsonElement.getAsString() );
						result.error( exception );
					}

				}
			} catch ( Exception e ) {
				Exception exception = new ExceptionSelfHolidayProcess( e, "系统在保存员工请假记录信息时发生异常." );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		logger.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" );
		return result;
	}
	
	public static class Wi extends AttendanceSelfHoliday {
		private static final long serialVersionUID = -5076990764713538973L;
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}