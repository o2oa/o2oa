package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.service.AttendanceDetailAnalyseServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.core.express.wrap.WrapCompany;


@Path("selfholidaysimple")
public class AttendanceSelfHolidaySimpleAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceSelfHolidaySimpleAction.class );
	private AttendanceStatisticalCycleServiceAdv attendanceStatisticCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
	private AttendanceDetailAnalyseServiceAdv attendanceDetailAnalyseServiceAdv = new AttendanceDetailAnalyseServiceAdv();
	private BeanCopyTools<WrapInAttendanceSelfHoliday, AttendanceSelfHoliday> wrapin_copier = BeanCopyToolsBuilder.create( WrapInAttendanceSelfHoliday.class, AttendanceSelfHoliday.class, null, WrapInAttendanceSelfHoliday.Excludes );
		
	@HttpMethodDescribe(value = "新建或者更新AttendanceSelfHoliday休假申请数据对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapInAttendanceSelfHoliday wrapIn = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		WrapCompany wrapCompany = null;
		Boolean check = true;
		Business business = null;

		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceSelfHoliday.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				AttendanceSelfHoliday _attendanceSelfHoliday = null;
				AttendanceSelfHoliday attendanceSelfHoliday = new AttendanceSelfHoliday();
				business = new Business(emc);
				logger.info("System trying to beginTransaction to update attendanceSelfHoliday......" );
				if( wrapIn != null && wrapIn.getId() !=null && wrapIn.getId().length() > 10 ){
					
					wrapCompany = business.organization().company().getWithDepartment(wrapIn.getOrganizationName());
					wrapIn.setCompanyName( wrapCompany.getName() );
					
					if( wrapIn.getId() !=null && wrapIn.getId().length() > 10 ){
						//根据ID查询信息是否存在，如果存在就update，如果不存在就create
						_attendanceSelfHoliday = emc.find( wrapIn.getId(), AttendanceSelfHoliday.class );
						
						if( _attendanceSelfHoliday != null ){
							//更新
							emc.beginTransaction( AttendanceSelfHoliday.class );
							wrapin_copier.copy( wrapIn, _attendanceSelfHoliday );
							emc.check( _attendanceSelfHoliday, CheckPersistType.all);	
							emc.commit();
							logger.info("System update attendanceSelfHoliday success！" );
						}else{
							emc.beginTransaction( AttendanceSelfHoliday.class );
							wrapin_copier.copy( wrapIn, attendanceSelfHoliday );
							attendanceSelfHoliday.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
							emc.persist( attendanceSelfHoliday, CheckPersistType.all);	
							emc.commit();
							logger.info("System save attendanceSelfHoliday success！" );
						}
					}else{
						//没有传入指定的ID
						emc.beginTransaction( AttendanceSelfHoliday.class );
						wrapin_copier.copy( wrapIn, attendanceSelfHoliday );
						emc.persist( attendanceSelfHoliday, CheckPersistType.all);	
						emc.commit();
						result.setData( new WrapOutId(attendanceSelfHoliday.getId()) );
						logger.info("System save attendanceSelfHoliday success！" );
					}
					
					//根据员工休假数据来记录与这条数据相关的统计需求记录
					//new AttendanceDetailAnalyseService().recordStatisticRequireLog( attendanceSelfHoliday );
					
					//应该只需要重新分析该用户在请假期间已经存在的打卡数据即可			
					List<String> ids = attendanceDetailAnalyseServiceAdv.getAnalyseAttendanceDetailIds(attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime());
					if( ids != null && ids.size() > 0 ){
						try {//查询所有的周期配置，组织成Map
							companyAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles();
						} catch (Exception e) {
							Exception exception = new GetCycleMapFromAllCyclesException( e );
							result.error( exception );
							logger.error( e, currentPerson, request, null);
						}
						attendanceDetailAnalyseServiceAdv.analyseAttendanceDetails( attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime(), companyAttendanceStatisticalCycleMap );
					}
				}
			} catch ( Exception e ) {
				Exception exception = new AttendanceSelfHolidaySaveException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据流程文档ID删除AttendanceSelfHoliday休假申请数据对象.", response = WrapOutId.class)
	@DELETE
	@Path("docId/{docId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("docId") String docId) {
		logger.info("method delete has been called, try to delete attendanceSelfHoliday{'docId':'"+docId+"'}......" );
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapOutId wrapOutMessage = new WrapOutId();
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		logger.info("user " + currentPerson.getName() + "try to delete AttendanceSelfHoliday......" );
		List<String> ids = null;
		List<AttendanceSelfHoliday> attendanceSelfHolidays = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			ids = business.getAttendanceSelfHolidayFactory().getByWorkFlowDocId(docId);
			if( ids != null && ids.size() > 0 ){
				attendanceSelfHolidays = business.getAttendanceSelfHolidayFactory().list(ids);
				if( attendanceSelfHolidays != null && attendanceSelfHolidays.size() > 0 ){
					for( AttendanceSelfHoliday attendanceSelfHoliday : attendanceSelfHolidays){
						logger.info("System trying to beginTransaction to delete attendanceSelfHoliday......" );						
						emc.beginTransaction( AttendanceSelfHoliday.class );
						emc.remove( attendanceSelfHoliday, CheckRemoveType.all );
						emc.commit();
						logger.info("System delete attendanceSelfHoliday success......" );
						
						//根据员工休假数据来记录与这条数据相关的统计需求记录
						//new AttendanceDetailAnalyseService().recordStatisticRequireLog( attendanceSelfHoliday );
						
						//应该只需要重新分析该用户在请假期间已经存在的打卡数据即可
						ids = attendanceDetailAnalyseServiceAdv.getAnalyseAttendanceDetailIds(attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime());
						if( ids != null && ids.size() > 0 ){
							try {//查询所有的周期配置，组织成Map
								companyAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles();
							} catch (Exception e) {
								Exception exception = new GetCycleMapFromAllCyclesException( e );
								result.error( exception );
								logger.error( e, currentPerson, request, null);
							}
							attendanceDetailAnalyseServiceAdv.analyseAttendanceDetails( attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime(), companyAttendanceStatisticalCycleMap );
						}
					}
				}
			}
		} catch ( Exception e ) {
			Exception exception = new AttendanceSelfHolidayDeleteByDocIdException( e, docId );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		result.setData( wrapOutMessage );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}