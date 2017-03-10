package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.Date;
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
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.attendanceappealinfo.WrapInFilterAppeal;
import com.x.attendance.assemble.control.service.AttendanceDetailAnalyseServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceWorkDayConfigServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpAttribute;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;


@Path("attendancedetail")
public class AttendanceDetailAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceDetailAction.class );
	private UserManagerService userManagerService = new UserManagerService();
	private AttendanceDetailAnalyseServiceAdv attendanceDetailAnalyseServiceAdv = new AttendanceDetailAnalyseServiceAdv();
	private AttendanceWorkDayConfigServiceAdv attendanceWorkDayConfigServiceAdv = new AttendanceWorkDayConfigServiceAdv();
	private AttendanceStatisticalCycleServiceAdv attendanceStatisticCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
	private AttendanceDetailServiceAdv attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
	private AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
	private BeanCopyTools<AttendanceDetail, WrapOutAttendanceDetail> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceDetail.class, WrapOutAttendanceDetail.class, null, WrapOutAttendanceDetail.Excludes);

	@HttpMethodDescribe(value = "根据ID获取AttendanceDetail对象.", response = WrapOutAttendanceDetail.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutAttendanceDetail> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapOutAttendanceDetail wrap = null;
		AttendanceDetail attendanceDetail = null;
		Boolean check = true;
		
		if( check ){
			if( id == null ){
				check = false;
				Exception exception = new AttendanceDetailIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){
			try {
				attendanceDetail = attendanceDetailServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){
			if( attendanceDetail == null ){
				check = false;
				Exception exception = new AttendanceDetailNotExistsException( id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}		
		if( check ){
			try {
				wrap = wrapout_copier.copy( attendanceDetail );
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailWrapCopyException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取数据库中指定导入文件名称的数据列表", response = WrapOutAttendanceDetail.class )
	@GET
	@Path("list/{file_id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAttendanceDetailByBatchName( @Context HttpServletRequest request, @PathParam("file_id") String file_id ) {
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutAttendanceDetail> wraps = null;
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		Boolean check = true;
		
		if( check ){
			if( file_id == null ){
				check = false;
				Exception exception = new AttendanceDetailImportFileIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				ids = attendanceDetailServiceAdv.listByBatchName( file_id );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailListByImportFileIdException( e, file_id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					attendanceDetailList = attendanceDetailServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailListByIdsException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check ){
			if( attendanceDetailList != null && !attendanceDetailList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( attendanceDetailList );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailWrapCopyException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		result.setData( wraps );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定年月的打卡数据列表", response = WrapOutAttendanceDetail.class, request = JsonElement.class)
	@PUT
	@Path("filter/list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAttendanceDetail( @Context HttpServletRequest request, JsonElement jsonElement ) {		
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutAttendanceDetail> wraps = null;		
		String q_empName = null;
		String q_year = null;
		String q_month = null;
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		Date maxRecordDate = null;
		String maxRecordDateString = null;
		DateOperation dateOperation = new DateOperation();
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		
		if( check ){
			if( wrapIn == null ){
				wrapIn = new WrapInFilter();
			}
			q_empName = wrapIn.getQ_empName();
			q_year = wrapIn.getQ_year();
			q_month = wrapIn.getQ_month();
		}
		if( check ){
			try {
				maxRecordDateString = attendanceDetailServiceAdv.getMaxRecordDate();
				maxRecordDate = dateOperation.getDateFromString( maxRecordDateString );
			} catch (Exception e) {
				check = false;
				Exception exception = new GetAttendanceDetailMaxRecordDateException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( q_year == null || q_year.isEmpty() ){
				q_year = dateOperation.getYear(maxRecordDate);
			}
			if( q_month == null || q_month.isEmpty() ){
				q_month = dateOperation.getMonth(maxRecordDate);
			}
		}
		if( check ){
			try {
				ids = attendanceDetailServiceAdv.listUserAttendanceDetailByYearAndMonth( q_empName, q_year, q_month );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailListByNameYearMonthException( e, q_empName, q_year, q_month );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					attendanceDetailList = attendanceDetailServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailListByIdsException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check ){
			if( attendanceDetailList != null ){
				try {
					wraps = wrapout_copier.copy( attendanceDetailList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailWrapCopyException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户指定年月的打卡数据列表", response = WrapOutAttendanceDetail.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/user")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listUserAttendanceDetail(@Context HttpServletRequest request, JsonElement jsonElement ) {		
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutAttendanceDetail> wraps = null;
		String q_empName = null;
		String q_year = null;
		String q_month = null;
		String cycleYear = null;
		String cycleMonth = null;
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		Date maxRecordDate = null;
		String maxRecordDateString = null;
		DateOperation dateOperation = new DateOperation();
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		
		if( check ){
			if( wrapIn == null ){
				wrapIn = new WrapInFilter();
			}
			q_empName = wrapIn.getQ_empName();
			q_year = wrapIn.getQ_year();
			q_month = wrapIn.getQ_month();
			cycleYear = wrapIn.getCycleYear();
			cycleMonth = wrapIn.getCycleMonth();
			if( q_empName == null || q_empName.isEmpty() ){
				q_empName = currentPerson.getName();
			}
		}
		if( check ){
			try {
				maxRecordDateString = attendanceDetailServiceAdv.getMaxRecordDate();
				maxRecordDate = dateOperation.getDateFromString( maxRecordDateString );
			} catch (Exception e) {
				check = false;
				Exception exception = new GetAttendanceDetailMaxRecordDateException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( q_year == null || q_year.isEmpty() ){
				q_year = dateOperation.getYear(maxRecordDate);
			}
			if( q_month == null || q_month.isEmpty() ){
				q_month = dateOperation.getMonth(maxRecordDate);
			}
		}
		if( check ){
			if( cycleYear != null && cycleMonth != null&& !cycleYear.isEmpty() && !cycleMonth.isEmpty() ){
				try {
					ids = attendanceDetailServiceAdv.listUserAttendanceDetailByCycleYearAndMonth( q_empName, cycleYear, cycleMonth );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailListByNameYearMonthException( e, q_empName, cycleYear, cycleMonth );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}else if( q_year != null && q_month != null && !q_year.isEmpty() && !q_month.isEmpty()){
				try {
					ids = attendanceDetailServiceAdv.listUserAttendanceDetailByYearAndMonth( q_empName, q_year, q_month );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailListByNameYearMonthException( e, q_empName, cycleYear, cycleMonth );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					attendanceDetailList = attendanceDetailServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailListByIdsException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check ){
			if( attendanceDetailList != null ){
				try {
					wraps = wrapout_copier.copy( attendanceDetailList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailWrapCopyException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}	
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取公司指定年月的打卡数据列表", response = WrapOutAttendanceDetail.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/company")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCompanyAttendanceDetail(@Context HttpServletRequest request, JsonElement jsonElement ) {		
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutAttendanceDetail> wraps = null;
		String q_companyName = null;
		String q_year = null;
		String q_month = null;
		List<String> ids = null;
		List<String> companyNames = new ArrayList<String>();
		List<AttendanceDetail> attendanceDetailList = null;
		Date maxRecordDate = null;
		String maxRecordDateString = null;
		DateOperation dateOperation = new DateOperation();
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}

		if( check ){
			if( wrapIn == null ){
				wrapIn = new WrapInFilter();
			}
			q_companyName = wrapIn.getQ_companyName();
			q_year = wrapIn.getQ_year();
			q_month = wrapIn.getQ_month();
		}
		if( check ){
			try {
				maxRecordDateString = attendanceDetailServiceAdv.getMaxRecordDate();
				maxRecordDate = dateOperation.getDateFromString( maxRecordDateString );
			} catch (Exception e) {
				check = false;
				Exception exception = new GetAttendanceDetailMaxRecordDateException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( q_year == null || q_year.isEmpty() ){
				q_year = dateOperation.getYear( maxRecordDate );
			}
			if( q_month == null || q_month.isEmpty() ){
				q_month = dateOperation.getMonth( maxRecordDate );
			}
		}
		if( check ){
			if( q_companyName != null && !q_companyName.isEmpty()){
				try{
					companyNames = userManagerService.listSubCompanyNameList( q_companyName );
				}catch(Exception e){
					Exception exception = new ListCompanyNameByParentNameException( e, q_companyName );
					logger.error( exception, currentPerson, request, null);
				}
				if( !companyNames.contains( q_companyName )){
					companyNames.add( q_companyName );
				}
			}
		}
		if( check ){
			try {
				ids = attendanceDetailServiceAdv.listCompanyAttendanceDetailByYearAndMonth( companyNames, q_year, q_month );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailListByCompanyYearMonthException( e, companyNames, q_year, q_month );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}	
		}
		if( check ){
			try {
				attendanceDetailList = attendanceDetailServiceAdv.list( ids );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailListByIdsException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( attendanceDetailList != null ){
				try {
					wraps = wrapout_copier.copy( attendanceDetailList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailWrapCopyException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}	
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取部门指定年月的打卡数据列表", response = WrapOutAttendanceDetail.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/department")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDepartmentAttendanceDetail(@Context HttpServletRequest request, JsonElement jsonElement ) {		
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutAttendanceDetail> wraps = null;
		String q_departmentName = null;
		String q_year = null;
		String q_month = null;
		List<String> ids = null;
		List<String> departmentNames = new ArrayList<String>();
		List<AttendanceDetail> attendanceDetailList = null;
		Date maxRecordDate = null;
		String maxRecordDateString = null;
		DateOperation dateOperation = new DateOperation();
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		
		if( check ){
			if( wrapIn == null ){
				wrapIn = new WrapInFilter();
			}
			q_departmentName = wrapIn.getQ_companyName();
			q_year = wrapIn.getQ_year();
			q_month = wrapIn.getQ_month();
		}
		if( check ){
			try {
				maxRecordDateString = attendanceDetailServiceAdv.getMaxRecordDate();
				maxRecordDate = dateOperation.getDateFromString( maxRecordDateString );
			} catch (Exception e) {
				check = false;
				Exception exception = new GetAttendanceDetailMaxRecordDateException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( q_year == null || q_year.isEmpty() ){
				q_year = dateOperation.getYear( maxRecordDate );
			}
			if( q_month == null || q_month.isEmpty() ){
				q_month = dateOperation.getMonth( maxRecordDate );
			}
		}
		if( check ){
			if( q_departmentName != null && !q_departmentName.isEmpty()){
				try{
					departmentNames = userManagerService.listSubOrganizationNameList( q_departmentName );
				}catch(Exception e){
					Exception exception = new ListDepartmentNameByParentNameException( e, q_departmentName );
					logger.error( exception, currentPerson, request, null);
				}
				if( !departmentNames.contains( q_departmentName )){
					departmentNames.add( q_departmentName );
				}
			}
		}
		if( check ){
			try {
				ids = attendanceDetailServiceAdv.listDepartmentAttendanceDetailByYearAndMonth( departmentNames, q_year, q_month );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailListByDepartmentYearMonthException( e, departmentNames, q_year, q_month );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}	
		}
		if( check ){
			try {
				attendanceDetailList = attendanceDetailServiceAdv.list( ids );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailListByIdsException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( attendanceDetailList != null ){
				try {
					wraps = wrapout_copier.copy( attendanceDetailList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceDetailWrapCopyException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}	
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}	
	
	@HttpMethodDescribe(value = "根据周期的年份月份，以及需要考勤人员的名单，检查人员在周期内每天的考核数据是否存在，如果不存在，则进行补齐", response = WrapOutId.class )
	@GET
	@Path("checkDetailWithPersonByCycle/{cycleYear}/{cycleMonth}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkDetailWithPersonByCycle(@Context HttpServletRequest request, @PathParam("cycleYear") String cycleYear , @PathParam("cycleMonth") String cycleMonth ) {
		logger.info( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>系统尝试对统计周期["+cycleYear+"-"+cycleMonth+"]的打卡数据核对......");
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		AttendanceStatisticalCycle attendanceStatisticalCycle  = null;		
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		List<String> ids = null;
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigList = null;
		Boolean check = true;
		if( check ){
			if( cycleYear == null || cycleYear.isEmpty() ){
				check = false;
				Exception exception = new AttendanceDetailCycleYearEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( cycleMonth == null || cycleMonth.isEmpty() ){
				check = false;
				Exception exception = new AttendanceDetailCycleMonthEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				ids = attendanceEmployeeConfigServiceAdv.listByConfigType( "REQUIRED" );
			} catch (Exception e) {
				check = false;
				Exception exception = new RequiredAttendanceEmployeeListException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					attendanceEmployeeConfigList = attendanceEmployeeConfigServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceEmployeeConfigListByIdsException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check && attendanceEmployeeConfigList != null && attendanceEmployeeConfigList.size() > 0){
			if( check ){
				try {//查询所有的周期配置，组织成Map
					companyAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles();
				} catch (Exception e) {
					check = false;
					Exception exception = new GetCycleMapFromAllCyclesException( e );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
			if( check ){
				Boolean globalCheck = true;
				Boolean subCheck = true;
				int count = attendanceEmployeeConfigList.size();
				int i = 0;
				for( AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigList ){
					i++;
					logger.info( ">>>>>>>>>>系统正在核对第"+ i +"/"+ count +"个配置, ["+attendanceEmployeeConfig.getEmployeeName()+"]["+cycleYear+"-"+cycleMonth+"]的考勤数据......");
					subCheck = true;
					try {
						attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.checkAttendanceEmployeeConfig( attendanceEmployeeConfig );
					} catch (Exception e ) {
						globalCheck = subCheck = false;
						Exception exception = new CheckAttendanceWithEmployeeConfigException( e );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
					if( subCheck ){
						try {//根据公司部门，年月获取到一个适合的统计周期，如果没有则新建一个新的配置
							attendanceStatisticalCycle = attendanceStatisticCycleServiceAdv.getAttendanceDetailStatisticCycle( attendanceEmployeeConfig.getCompanyName(), attendanceEmployeeConfig.getOrganizationName(), cycleYear, cycleMonth, companyAttendanceStatisticalCycleMap );
						} catch (Exception e) {
							globalCheck = subCheck = false;
							Exception exception = new GetAttendanceDetailStatisticCycleException( e, attendanceEmployeeConfig.getCompanyName(), attendanceEmployeeConfig.getOrganizationName(), cycleYear, cycleMonth );
							result.error( exception );
							logger.error( exception, currentPerson, request, null);
						}
					}
					if( subCheck ){
						if( attendanceStatisticalCycle != null ){
							try {
								attendanceDetailServiceAdv.checkAndReplenish( attendanceStatisticalCycle.getCycleStartDate(), attendanceStatisticalCycle.getCycleEndDate(), attendanceEmployeeConfig );
							} catch (Exception e) {
								globalCheck = subCheck = false;
								Exception exception = new AttendanceDetailCheckAndReplenishException( e, attendanceStatisticalCycle.getCycleStartDate(), attendanceStatisticalCycle.getCycleEndDate() );
								result.error( exception );
								logger.error( exception, currentPerson, request, null);
							}
						}
					}
				}
				if( globalCheck ){
					logger.info( "系统根据时间列表核对和补充员工打卡信息成功完成！" );
				}else{
					logger.info( "系统根据时间列表核对和补充员工打卡信息成完成，但部分数据未补充录成功！" );
				}
			}
		}
		logger.info( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>系统对统计周期["+cycleYear+"-"+cycleMonth+"]的打卡数据核对完成！");
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "分析打卡数据", response = WrapOutId.class)
	@GET
	@Path("analyse/{startDate}/{endDate}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response analyseAttendanceDetails(@Context HttpServletRequest request, @PathParam("startDate") String startDate, @PathParam("endDate") String endDate ) {
		ActionResult< WrapOutId > result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<String> ids = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		AttendanceDetail detail = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		Boolean check = true;
		
		if( check ){
			try {
				ids = attendanceDetailServiceAdv.getAllAnalysenessDetails( startDate, endDate );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailListNeedAnalyseException( e, startDate, endDate );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check && ids != null && !ids.isEmpty() ){
			try {
				attendanceWorkDayConfigList = attendanceWorkDayConfigServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceWorkDayConfigListAllException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}	
		}
		if( check && ids != null && !ids.isEmpty() ){
			try {//查询所有的周期配置，组织成Map
				companyAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles();
			} catch (Exception e) {
				check = false;
				Exception exception = new GetCycleMapFromAllCyclesException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				int count = ids.size();
				for( int i=0; i< ids.size() ; i++ ){
					try{
						detail  = attendanceDetailServiceAdv.get( ids.get(i) );
						if( (i+1) % 100 == 0){
							logger.info("已经分析["+i+"/"+count+"]条数据，请继续等待分析过程完成......");
						}
						if( detail != null  ){
							attendanceDetailAnalyseServiceAdv.analyseAttendanceDetail( detail, attendanceWorkDayConfigList, companyAttendanceStatisticalCycleMap );
							logger.info( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>第条"+ (i+1) +"考勤数据已经分析完成.员工:" + detail.getEmpName() + ", 日期:" + detail.getRecordDateString() );
						}else{
							Exception exception = new AttendanceDetailNotExistsException( ids.get(i) );
							result.error( exception );
							logger.error( exception, currentPerson, request, null);
						}						
					}catch(Exception e){
						check = false;
						Exception exception = new AttendanceDetailAnalyseException( e, ids.get(i) );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
				}
				logger.info( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>所有需要分析的考勤数据已经全部分析完成." );
			}else{
				logger.info( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>没有需要分析的员工打卡信息！" );
			}
		}
		logger.info( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>数据分析过程退出!" );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "分析打卡数据", response = WrapOutId.class)
	@GET
	@Path("analyse/id/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response analyseAttendanceDetails(@Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult< WrapOutId > result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		AttendanceDetail detail = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new AttendanceDetailIdEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				detail = attendanceDetailServiceAdv.get( id );
				if( detail == null ){
					check = false;
					Exception exception = new AttendanceDetailNotExistsException( id );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailQueryByIdException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceWorkDayConfigList = attendanceWorkDayConfigServiceAdv.listAll();
			} catch ( Exception e ) {
				check = false;
				Exception exception = new AttendanceWorkDayConfigListAllException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				companyAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles();
			} catch (Exception e) {
				check = false;
				Exception exception = new GetCycleMapFromAllCyclesException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try{
				attendanceDetailAnalyseServiceAdv.analyseAttendanceDetail( detail, attendanceWorkDayConfigList, companyAttendanceStatisticalCycleMap);
				logger.info( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>考勤数据已经分析完成.员工:" + detail.getEmpName() + ", 日期:" + detail.getRecordDateString() );
				logger.info( "打卡信息分析完成。" );
			}catch(Exception e){
				check = false;
				Exception exception = new AttendanceDetailAnalyseException( e, id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		logger.info( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>数据分析过程退出!" );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "将指定的打卡记录归档", response = WrapOutId.class )
	@GET
	@Path("archive/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response archiveAttendanceDetail( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( check ){
			if( id != null && !id.isEmpty() ){
				try{
					attendanceDetailServiceAdv.archive( id );
					logger.info( "指定打卡信息归档完成。" );
				}catch(Exception e){
					check = false;
					Exception exception = new AttendanceDetailArchiveException( e, id );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}else{
				try{
					attendanceDetailServiceAdv.archiveAll();
					logger.info( "所有打卡信息归档完成。" );
				}catch(Exception e){
					check = false;
					Exception exception = new AttendanceDetailArchiveException( e, id );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的AttendanceDetail,下一页.", response = WrapOutAttendanceDetail.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
		List<WrapOutAttendanceDetail> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<AttendanceDetail> detailList = null;
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
			logger.error( exception, currentPerson, request, null);
		}
		if(check ){
			try {		
				EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Business business = new Business(emc);
				
				//查询出ID对应的记录的sequence
				Object sequence = null;
				if( id == null || "(0)".equals(id) || id.isEmpty() ){
					//logger.debug( "第一页查询，没有id传入" );
				}else{
					if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
						sequence = PropertyUtils.getProperty(emc.find( id, AttendanceDetail.class ), "sequence");
					}
				}
				
				//处理一下公司，查询下级公司
				if( wrapIn.getQ_companyName() != null && !wrapIn.getQ_companyName().isEmpty() ){
					companyNames.add( wrapIn.getQ_companyName() );
					try{
						companyList = business.organization().company().listSubNested( wrapIn.getQ_companyName() );
					}catch(Exception e){
						Exception exception = new ListCompanyNameByParentNameException( e, wrapIn.getQ_companyName() );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
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
						Exception exception = new ListDepartmentNameByParentNameException( e, wrapIn.getQ_departmentName() );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
					if( departments != null && departments.size() > 0 ){
						for( WrapDepartment department : departments){
							departmentNames.add( department.getName() );
						}
					}
					wrapIn.setDepartmentNames(departmentNames);
				}
				
				//从数据库中查询符合条件的一页数据对象
				detailList = business.getAttendanceDetailFactory().listIdsNextWithFilter( id, count, sequence, wrapIn );
				//从数据库中查询符合条件的对象总数
				total = business.getAttendanceDetailFactory().getCountWithFilter( wrapIn );
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

	@HttpMethodDescribe(value = "列示根据过滤条件的AttendanceDetail,上一页.", response = WrapOutAttendanceDetail.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
		List<WrapOutAttendanceDetail> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<AttendanceDetail> detailList = null;
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
			logger.error( exception, currentPerson, request, null);
		}
		if(check ){
			try {		
				EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Business business = new Business(emc);
				
				//查询出ID对应的记录的sequence
				Object sequence = null;
				//logger.debug( "传入的ID=" + id );
				if( id == null || "(0)".equals(id) || id.isEmpty() ){
					//logger.debug( "第一页查询，没有id传入" );
				}else{
					if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
						sequence = PropertyUtils.getProperty(emc.find(id, AttendanceDetail.class ), "sequence");
					}
				}		
				
				//处理一下公司，查询下级公司
				if( wrapIn.getQ_companyName() != null && !wrapIn.getQ_companyName().isEmpty() ){
					companyNames.add( wrapIn.getQ_companyName() );
					try{
						companyList = business.organization().company().listSubNested( wrapIn.getQ_companyName() );
					}catch(Exception e){
						Exception exception = new ListCompanyNameByParentNameException( e, wrapIn.getQ_companyName() );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
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
						Exception exception = new ListDepartmentNameByParentNameException( e, wrapIn.getQ_departmentName() );
						result.error( exception );
						logger.error( exception, currentPerson, request, null);
					}
					if( departments != null && departments.size() > 0 ){
						for( WrapDepartment department : departments){
							departmentNames.add( department.getName() );
						}
					}
					wrapIn.setDepartmentNames(departmentNames);
				}
				
				//从数据库中查询符合条件的一页数据对象
				detailList = business.getAttendanceDetailFactory().listIdsPrevWithFilter( id, count, sequence, wrapIn );
				//从数据库中查询符合条件的对象总数
				total = business.getAttendanceDetailFactory().getCountWithFilter( wrapIn );
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
	/**
	 * 打卡信息接入
	 * 1-员工姓名 EmployeeName	
	   2-员工号   EmployeeNo
	   3-日期	RecordDateString
	   4-签到时间  OnDutyTime
	   5-签退时间  OffDutyTime
	 * @author liyi_
	 */
	@HttpMethodDescribe(value = "接入完成的上下班打卡信息记录，接入完成后直接分析.", request = JsonElement.class, response = WrapOutId.class)
	@Path("recive")
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response recive(@Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapInAttendanceDetailRecive wrapIn = null;
		WrapOutId wrapOutId = null;
		Date datetime = null;
		DateOperation dateOperation = new DateOperation();
		AttendanceDetail attendanceDetail = new AttendanceDetail();
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceDetailRecive.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		if( check ){
			if( wrapIn.getRecordDateString() == null || wrapIn.getRecordDateString().isEmpty() ){
				check = false;
				Exception exception = new AttendanceDetailRecordDateEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getEmpName() == null || wrapIn.getEmpName().isEmpty() ){
				check = false;
				Exception exception = new AttendanceDetailEmployeeNameEmptyException();
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try{
				datetime = dateOperation.getDateFromString( wrapIn.getRecordDateString() );
				attendanceDetail.setRecordDate( datetime );
				attendanceDetail.setRecordDateString( dateOperation.getDateStringFromDate( datetime, "YYYY-MM-DD") );
				attendanceDetail.setYearString( dateOperation.getYear( datetime ) );
				attendanceDetail.setMonthString( dateOperation.getMonth(datetime) );
			}catch( Exception e ){
				check = false;
				Exception exception = new AttendanceDetailRecordDateFormatException( e, wrapIn.getRecordDateString() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getOnDutyTime() != null && wrapIn.getOnDutyTime().trim().length() > 0 ){
				try{
					datetime = dateOperation.getDateFromString( wrapIn.getOnDutyTime() );
					attendanceDetail.setOnDutyTime( dateOperation.getDateStringFromDate( datetime, "HH:mm:ss") ); //上班打卡时间
				}catch( Exception e ){
					check = false;
					Exception exception = new AttendanceDetailOnDutyTimeFormatException( e, wrapIn.getOnDutyTime() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check ){
			if( wrapIn.getOffDutyTime() != null && wrapIn.getOffDutyTime().trim().length() > 0 ){
				try{
					datetime = dateOperation.getDateFromString( wrapIn.getOffDutyTime() );
					attendanceDetail.setOffDutyTime( dateOperation.getDateStringFromDate( datetime, "HH:mm:ss") ); //上班打卡时间
				}catch( Exception e ){
					check = false;
					Exception exception = new AttendanceDetailOffDutyTimeFormatException( e, wrapIn.getOffDutyTime() );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			}
		}
		if( check ){
			try {
				attendanceDetail = attendanceDetailServiceAdv.save( attendanceDetail );
				wrapOutId = new WrapOutId( attendanceDetail.getId() );
				result.setData( wrapOutId );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceDetailSaveException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceWorkDayConfigList = attendanceWorkDayConfigServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceWorkDayConfigListAllException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				companyAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles();
			} catch (Exception e) {
				check = false;
				Exception exception = new GetCycleMapFromAllCyclesException( e );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			try{
				attendanceDetailAnalyseServiceAdv.analyseAttendanceDetail( attendanceDetail, attendanceWorkDayConfigList, companyAttendanceStatisticalCycleMap);
				logger.info( "打卡信息保存并且分析完成。" );
			}catch(Exception e){
				check = false;
				Exception exception = new AttendanceDetailAnalyseException( e, attendanceDetail.getId() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceDetail数据对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("user " + currentPerson.getName() + "try to delete attendanceDetail......" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			AttendanceDetail attendanceDetail = emc.find(id, AttendanceDetail.class);
			if ( null == attendanceDetail ) {
				Exception exception = new AttendanceDetailNotExistsException( id );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}else{
				//进行数据库持久化操作				
				emc.beginTransaction( AttendanceDetail.class );
				emc.remove( attendanceDetail, CheckRemoveType.all );
				emc.commit();
				result.setData( new WrapOutId(id) );
				logger.info( "成功删除打卡数据信息。id=" + id );
			}			
		} catch ( Exception e ) {
			Exception exception = new AttendanceDetailDeleteException( e, id );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}