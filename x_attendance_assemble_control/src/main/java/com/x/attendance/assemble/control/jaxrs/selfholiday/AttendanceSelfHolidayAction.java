package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceSelfHolidayFactory;
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
import com.x.organization.core.express.wrap.WrapDepartment;


@Path("attendanceselfholiday")
public class AttendanceSelfHolidayAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceSelfHolidayAction.class );
	private AttendanceStatisticalCycleServiceAdv attendanceStatisticCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
	private AttendanceDetailAnalyseServiceAdv attendanceDetailAnalyseServiceAdv = new AttendanceDetailAnalyseServiceAdv();
	private BeanCopyTools<WrapInAttendanceSelfHoliday, AttendanceSelfHoliday> wrapin_copier = BeanCopyToolsBuilder.create( WrapInAttendanceSelfHoliday.class, AttendanceSelfHoliday.class, null, WrapInAttendanceSelfHoliday.Excludes );
	private BeanCopyTools<AttendanceSelfHoliday, WrapOutAttendanceSelfHoliday> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceSelfHoliday.class, WrapOutAttendanceSelfHoliday.class, null, WrapOutAttendanceSelfHoliday.Excludes);
	
	@HttpMethodDescribe(value = "获取所有休假申请数据列表", response = WrapOutAttendanceSelfHoliday.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAttendanceSelfHoliday(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAttendanceSelfHoliday>> result = new ActionResult<>();
		List<WrapOutAttendanceSelfHoliday> wraps = null;
		List<String> ids = null;
		List<AttendanceSelfHoliday> attendanceSelfHolidayList = null;
		Business business = null;
		AttendanceSelfHolidayFactory attendanceSelfHolidayFactory = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			business = new Business(emc);					
			attendanceSelfHolidayFactory  = business.getAttendanceSelfHolidayFactory();			
			//获取所有应用列表
			ids = attendanceSelfHolidayFactory.listAll();			
			//查询ID IN ids 的所有应用信息列表
			attendanceSelfHolidayList = attendanceSelfHolidayFactory.list( ids );	
			//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			wraps = wrapout_copier.copy( attendanceSelfHolidayList );
			//对查询的列表进行排序				
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceSelfHoliday休假申请数据对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInAttendanceSelfHoliday wrapIn = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		//获取到当前用户信息
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
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
				if( wrapIn != null && wrapIn.getId() !=null && wrapIn.getId().length() > 10 ){
					
					if( wrapIn.getId() !=null && wrapIn.getId().length() > 10 ){
						//根据ID查询信息是否存在，如果存在就update，如果不存在就create
						_attendanceSelfHoliday = emc.find( wrapIn.getId(), AttendanceSelfHoliday.class );
						if( _attendanceSelfHoliday != null ){
							//更新
							emc.beginTransaction( AttendanceSelfHoliday.class );
							wrapin_copier.copy( wrapIn, _attendanceSelfHoliday );
							emc.check( _attendanceSelfHoliday, CheckPersistType.all);	
							emc.commit();
							result.setData( new WrapOutId( _attendanceSelfHoliday.getId() ) );
						}else{
							emc.beginTransaction( AttendanceSelfHoliday.class );
							wrapin_copier.copy( wrapIn, attendanceSelfHoliday );
							attendanceSelfHoliday.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
							emc.persist( attendanceSelfHoliday, CheckPersistType.all);	
							emc.commit();
							result.setData( new WrapOutId( attendanceSelfHoliday.getId() ) );
						}
					}else{
						//没有传入指定的ID
						emc.beginTransaction( AttendanceSelfHoliday.class );
						wrapin_copier.copy( wrapIn, attendanceSelfHoliday );
						emc.persist( attendanceSelfHoliday, CheckPersistType.all);	
						emc.commit();
						result.setData( new WrapOutId( attendanceSelfHoliday.getId() ) );
					}
					
					//根据员工休假数据来记录与这条数据相关的统计需求记录
					//new AttendanceDetailAnalyseService().recordStatisticRequireLog( attendanceSelfHoliday );
					
					//休假数据有更新，对该员工的该请假时间内的所有打卡记录进行分析
					List<String> ids = attendanceDetailAnalyseServiceAdv.getAnalyseAttendanceDetailIds( attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime() );
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

	@HttpMethodDescribe(value = "根据ID删除AttendanceSelfHoliday休假申请数据对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			AttendanceSelfHoliday attendanceSelfHoliday = emc.find(id, AttendanceSelfHoliday.class);
			if (null == attendanceSelfHoliday) {
				Exception exception = new AttendanceSelfHolidayNotExistsException( id );
				result.error( exception );
				//logger.error( e, currentPerson, request, null);
			}else{
				emc.beginTransaction( AttendanceSelfHoliday.class );
				emc.remove( attendanceSelfHoliday, CheckRemoveType.all );
				emc.commit();
				result.setData( new WrapOutId(id) );
				//根据员工休假数据来记录与这条数据相关的统计需求记录
				List<String> ids = attendanceDetailAnalyseServiceAdv.getAnalyseAttendanceDetailIds( attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime() );
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
			Exception exception = new AttendanceSelfHolidayDeleteException( e, id );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的AttendanceSelfHoliday,下一页.", response = WrapOutAttendanceSelfHoliday.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceSelfHoliday>> result = new ActionResult<>();
		List<WrapOutAttendanceSelfHoliday> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<AttendanceSelfHoliday> detailList = null;
		List<String> companyNames = new ArrayList<String>();
		List<String> departmentNames = new ArrayList<String>();
		List<WrapCompany> companyList = null;
		List<WrapDepartment> departments = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if(check ){
			try {		
				EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Business business = new Business(emc);
				
				//查询出ID对应的记录的sequence
				Object sequence = null;
				if( id == null || "(0)".equals(id) || id.isEmpty() ){
					//logger.info( "第一页查询，没有id传入" );
				}else{
					if (!StringUtils.equalsIgnoreCase(id,StandardJaxrsAction.EMPTY_SYMBOL)) {
						sequence = PropertyUtils.getProperty(emc.find( id, AttendanceSelfHoliday.class ), "sequence");
					}
				}
				
				//处理一下公司，查询下级公司
				if( wrapIn.getQ_companyName() != null && !wrapIn.getQ_companyName().isEmpty() ){
					companyNames.add( wrapIn.getQ_companyName() );
					try{
						companyList = business.organization().company().listSubNested( wrapIn.getQ_companyName() );
					}catch(Exception e){
						Exception exception = new ListCompaniesWithParentNameException( e, wrapIn.getQ_companyName() );
						result.error( exception );
						logger.error( e, currentPerson, request, null);
					}
					if( companyList != null && companyList.size() > 0 ){
						for( WrapCompany company : companyList){
							companyNames.add(company.getName());
						}
					}
					wrapIn.setCompanyNames(companyNames);
				}
				
				//处理一下部门,查询下级部门
				if( wrapIn.getQ_departmentName() != null && !wrapIn.getQ_departmentName().isEmpty() ){
					departmentNames.add(wrapIn.getQ_departmentName());
					try{
						departments = business.organization().department().listSubNested( wrapIn.getQ_departmentName() );
					}catch(Exception e){
						Exception exception = new ListDepartmentsWithParentNameException( e, wrapIn.getQ_departmentName() );
						result.error( exception );
						logger.error( e, currentPerson, request, null);
					}
					if( departments != null && departments.size() > 0 ){
						for( WrapDepartment department : departments){
							departmentNames.add( department.getName() );
						}
					}
					wrapIn.setDepartmentNames(departmentNames);
				}
				
				//从数据库中查询符合条件的一页数据对象
				detailList = business.getAttendanceSelfHolidayFactory().listIdsNextWithFilter( id, count, sequence, wrapIn );
				
				//从数据库中查询符合条件的对象总数
				total = business.getAttendanceSelfHolidayFactory().getCountWithFilter( wrapIn );

				//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier.copy( detailList );
				
				//对查询的列表进行排序
				result.setCount( total );
				result.setData(wraps);

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的AttendanceSelfHoliday,上一页.", response = WrapOutAttendanceSelfHoliday.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceSelfHoliday>> result = new ActionResult<>();
		List<WrapOutAttendanceSelfHoliday> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<AttendanceSelfHoliday> detailList = null;
		List<String> companyNames = new ArrayList<String>();
		List<String> departmentNames = new ArrayList<String>();
		List<WrapCompany> companyList = null;
		List<WrapDepartment> departments = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if(check ){
			try {		
				EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Business business = new Business(emc);
				
				//查询出ID对应的记录的sequence
				Object sequence = null;
				logger.info( "传入的ID=" + id );
				if( id == null || "(0)".equals(id) || id.isEmpty() ){
					//logger.info( "第一页查询，没有id传入" );
				}else{
					if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
						sequence = PropertyUtils.getProperty(emc.find(id, AttendanceSelfHoliday.class ), "sequence");
					}
				}		
				
				//处理一下公司，查询下级公司
				if( wrapIn.getQ_companyName() != null && !wrapIn.getQ_companyName().isEmpty() ){
					companyNames.add( wrapIn.getQ_companyName() );
					try{
						companyList = business.organization().company().listSubNested( wrapIn.getQ_companyName() );
					}catch(Exception e){
						Exception exception = new ListCompaniesWithParentNameException( e, wrapIn.getQ_companyName() );
						result.error( exception );
						logger.error( e, currentPerson, request, null);
					}
					if( companyList != null && companyList.size() > 0 ){
						for( WrapCompany company : companyList){
							companyNames.add(company.getName());
						}
					}
					wrapIn.setCompanyNames(companyNames);
				}
				
				//处理一下部门,查询下级部门
				if( wrapIn.getQ_departmentName() != null && !wrapIn.getQ_departmentName().isEmpty() ){
					departmentNames.add(wrapIn.getQ_departmentName());
					try{
						departments = business.organization().department().listSubNested( wrapIn.getQ_departmentName() );
					}catch(Exception e){
						Exception exception = new ListDepartmentsWithParentNameException( e, wrapIn.getQ_departmentName() );
						result.error( exception );
						logger.error( e, currentPerson, request, null);
					}
					if( departments != null && departments.size() > 0 ){
						for( WrapDepartment department : departments){
							departmentNames.add( department.getName() );
						}
					}
					wrapIn.setDepartmentNames(departmentNames);
				}
				
				//从数据库中查询符合条件的一页数据对象
				detailList = business.getAttendanceSelfHolidayFactory().listIdsPrevWithFilter( id, count, sequence, wrapIn );
				
				//从数据库中查询符合条件的对象总数
				total = business.getAttendanceSelfHolidayFactory().getCountWithFilter( wrapIn );

				//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier.copy( detailList );

				result.setCount( total );
				result.setData(wraps);

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}