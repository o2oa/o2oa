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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.WrapOutMessage;
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
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpAttribute;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
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
		WrapOutAttendanceDetail wrap = null;
		AttendanceDetail attendanceDetail = null;
		Boolean check = true;
		
		if( check ){
			if( id == null ){
				check = false;
				result.error( new Exception("需要查询的打卡详细记录ID为空，无法进行数据查询。") );
				result.setUserMessage( "需要查询的打卡详细记录ID为空，无法进行数据查询。" );
			}
		}		
		if( check ){
			try {
				attendanceDetail = attendanceDetailServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error( new Exception("系统在根据用户传入的ID查询打卡详细信息记录时发生异常。") );
				result.setUserMessage( "系统在根据用户传入的ID查询打卡详细信息记录时发生异常。" );
				logger.error( "system get attendance detail info with id:"+ id +" got an exception.", e );
			}
		}		
		if( check ){
			if( attendanceDetail == null ){
				check = false;
				result.error( new Exception("系统在根据用户传入的ID未能查询到任何打卡详细信息记录常。" ) );
				result.setUserMessage( "系统在根据用户传入的ID未能查询到任何打卡详细信息记录常。" );
			}
		}		
		if( check ){
			try {
				wrap = wrapout_copier.copy( attendanceDetail );
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				result.error( new Exception("系统在转换数据库对象attendanceDetail为输出对象时发生异常。") );
				result.setUserMessage( "系统在转换数据库对象为输出对象时发生异常。" );
				logger.error( "system copy attendanceDetail to wrap got an exception.", e );
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
		List<WrapOutAttendanceDetail> wraps = null;
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		Boolean check = true;
		
		if( check ){
			if( file_id == null ){
				check = false;
				result.error( new Exception("需要查询的打卡导入文件ID为空，无法进行详细数据查询。") );
				result.setUserMessage( "需要查询的打卡导入文件ID为空，无法进行详细数据查询。" );
			}
		}
		if( check ){
			try {
				ids = attendanceDetailServiceAdv.listByBatchName( file_id );
			} catch (Exception e) {
				check = false;
				result.error( new Exception("系统根据批次号/导入文件ID查询打卡详细信息ID列表时发生异常。") );
				result.setUserMessage( "系统根据批次号/导入文件ID查询打卡详细信息ID列表时发生异常。" );
				logger.error( "system list detail with batch id/import file id{'id':'"+ file_id +"'} got an exception.", e );
			}
		}
		
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					attendanceDetailList = attendanceDetailServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( new Exception("系统根据ID列表查询打卡详细信息列表时发生异常。") );
					result.setUserMessage( "系统根据ID列表查询打卡详细信息列表时发生异常。" );
					logger.error( "system list detail with {'ids':'"+ ids +"'} got an exception.", e );
				}
			}
		}
		if( check ){
			if( attendanceDetailList != null && !attendanceDetailList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( attendanceDetailList );
				} catch (Exception e) {
					check = false;
					result.error( new Exception("系统在转换数据库对象attendanceDetailList为输出对象时发生异常。") );
					result.setUserMessage( "系统在转换数据库对象为输出对象时发生异常。" );
					logger.error( "system copy attendanceDetailList to wrap got an exception.", e );
				}
			}
		}
		result.setData( wraps );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定年月的打卡数据列表", response = WrapOutAttendanceDetail.class, request = WrapInFilter.class)
	@PUT
	@Path("filter/list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAttendanceDetail( @Context HttpServletRequest request, WrapInFilter wrapIn ) {		
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
		List<WrapOutAttendanceDetail> wraps = null;		
		String q_empName = null;
		String q_year = null;
		String q_month = null;
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		Date maxRecordDate = null;
		String maxRecordDateString = null;
		DateOperation dateOperation = new DateOperation();
		Boolean check = true;
		
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
				result.error( new Exception( "系统在查询打卡信息记录最大日期时发生异常！" ) );
				result.setUserMessage( "系统在查询打卡信息记录最大日期时发生异常！" );
				logger.error( "system query attendance detail max record date got an exception.", e );
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
				result.error( new Exception( "系统在根据员工姓名，年份月份查询打卡详细信息ID列表时发生异常！" ) );
				result.setUserMessage( "系统在根据员工姓名，年份月份查询打卡详细信息ID列表时发生异常！" );
				logger.error( "system query attendance detail ids with empName, year and month got an exception.", e );
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					attendanceDetailList = attendanceDetailServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统在根据ID列表查询打卡详细信息列表时发生异常！" ) );
					result.setUserMessage( "系统在根据ID列表查询打卡详细信息ID列表时发生异常！" );
					logger.error( "system query attendance detail ids with ids got an exception.", e );
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
					result.error( new Exception( "系统转换数据库对象列表为输出列表时发生异常！" ) );
					result.setUserMessage( "系统转换数据库对象列表为输出列表时发生异常！" );
					logger.error( "system copy attendance detail list to wrap got an exception.", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户指定年月的打卡数据列表", response = WrapOutAttendanceDetail.class, request = WrapInFilter.class)
	@PUT
	@Path("filter/list/user")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listUserAttendanceDetail(@Context HttpServletRequest request, WrapInFilter wrapIn ) {		
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
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
				result.error( new Exception( "系统在查询打卡信息记录最大日期时发生异常！" ) );
				result.setUserMessage( "系统在查询打卡信息记录最大日期时发生异常！" );
				logger.error( "system query attendance detail max record date got an exception.", e );
			}
			//logger.info( ">>>>>>>>>>>>>getmaxrecordDate:" + ( new Date().getTime() -_start.getTime() ) );
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
					result.error( new Exception( "系统在根据员工姓名，考勤周期查询员工考勤信息ID列表时发生异常！" ) );
					result.setUserMessage( "系统在根据员工姓名，考勤周期查询员工考勤信息ID列表时发生异常！" );
					logger.error( "system query attendance detail with empName, cycleYear and cycleMonth got an exception.", e );
				}
				//logger.info( ">>>>>>>>>>>>>listAttendanceDetailIds with cycle:" + ( new Date().getTime() -_start.getTime() ) );
			}else if( q_year != null && q_month != null && !q_year.isEmpty() && !q_month.isEmpty()){
				try {
					ids = attendanceDetailServiceAdv.listUserAttendanceDetailByYearAndMonth( q_empName, q_year, q_month );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统在根据员工姓名，考勤周期查询员工考勤信息ID列表时发生异常！" ) );
					result.setUserMessage( "系统在根据员工姓名，考勤周期查询员工考勤信息ID列表时发生异常！" );
					logger.error( "system query attendance detail with empName, cycleYear and cycleMonth got an exception.", e );
				}
				//logger.info( ">>>>>>>>>>>>>listAttendanceDetailIds:" + ( new Date().getTime() -_start.getTime() ) );
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					attendanceDetailList = attendanceDetailServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统在根据ID列表查询员工考勤信息列表时发生异常！" ) );
					result.setUserMessage( "系统在根据ID列表查询员工考勤信息列表时发生异常！" );
					logger.error( "system query attendance detail with ids got an exception.", e );
				}
			}
			//logger.info( ">>>>>>>>>>>>>listAttendanceDetail:" + ( new Date().getTime() -_start.getTime() ) );
		}
		if( check ){
			if( attendanceDetailList != null ){
				try {
					wraps = wrapout_copier.copy( attendanceDetailList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统转换数据库对象列表为输出列表时发生异常！" ) );
					result.setUserMessage( "系统转换数据库对象列表为输出列表时发生异常！" );
					logger.error( "system copy attendance detail list to wrap got an exception.", e );
				}	
			}
			//logger.info( ">>>>>>>>>>>>>wrapout_copier:" + ( new Date().getTime() -_start.getTime() ) );
		}
		//logger.info( ">>>>>>>>>>>>>total time:" + ( new Date().getTime() - start.getTime() ) );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取公司指定年月的打卡数据列表", response = WrapOutAttendanceDetail.class, request = WrapInFilter.class)
	@PUT
	@Path("filter/list/company")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCompanyAttendanceDetail(@Context HttpServletRequest request, WrapInFilter wrapIn ) {		
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
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
		Boolean check = true;

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
				result.error( new Exception( "系统在查询打卡信息记录最大日期时发生异常！" ) );
				result.setUserMessage( "系统在查询打卡信息记录最大日期时发生异常！" );
				logger.error( "system query attendance detail max record date got an exception.", e );
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
					logger.error("系统在根据公司名称获取下级公司的时候发生异常", e);
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
				result.error( new Exception( "系统在根据公司名称，打卡年份和打卡月份查询公司员工打卡信息ID列表时发生异常！" ) );
				result.setUserMessage( "系统在根据公司名称，打卡年份和打卡月份查询公司员工打卡信息ID列表时发生异常！" );
				logger.error( "system query attendance detail max record date got an exception.", e );
			}	
		}
		if( check ){
			try {
				attendanceDetailList = attendanceDetailServiceAdv.list( ids );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统在根据ID列表查询员工考勤信息列表时发生异常！" ) );
				result.setUserMessage( "系统在根据ID列表查询员工考勤信息列表时发生异常！" );
				logger.error( "system query attendance detail with ids got an exception.", e );
			}
		}
		if( check ){
			if( attendanceDetailList != null ){
				try {
					wraps = wrapout_copier.copy( attendanceDetailList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统转换数据库对象列表为输出列表时发生异常！" ) );
					result.setUserMessage( "系统转换数据库对象列表为输出列表时发生异常！" );
					logger.error( "system copy attendance detail list to wrap got an exception.", e );
				}	
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取部门指定年月的打卡数据列表", response = WrapOutAttendanceDetail.class, request = WrapInFilter.class)
	@PUT
	@Path("filter/list/department")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDepartmentAttendanceDetail(@Context HttpServletRequest request, WrapInFilter wrapIn ) {		
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
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
		Boolean check = true;
		
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
				result.error( new Exception( "系统在查询打卡信息记录最大日期时发生异常！" ) );
				result.setUserMessage( "系统在查询打卡信息记录最大日期时发生异常！" );
				logger.error( "system query attendance detail max record date got an exception.", e );
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
					logger.error("系统在根据公司名称获取下级公司的时候发生异常", e);
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
				result.error( new Exception( "系统在根据公司名称，打卡年份和打卡月份查询公司员工打卡信息ID列表时发生异常！" ) );
				result.setUserMessage( "系统在根据公司名称，打卡年份和打卡月份查询公司员工打卡信息ID列表时发生异常！" );
				logger.error( "system query attendance detail max record date got an exception.", e );
			}	
		}
		if( check ){
			try {
				attendanceDetailList = attendanceDetailServiceAdv.list( ids );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统在根据ID列表查询员工考勤信息列表时发生异常！" ) );
				result.setUserMessage( "系统在根据ID列表查询员工考勤信息列表时发生异常！" );
				logger.error( "system query attendance detail with ids got an exception.", e );
			}
		}
		if( check ){
			if( attendanceDetailList != null ){
				try {
					wraps = wrapout_copier.copy( attendanceDetailList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统转换数据库对象列表为输出列表时发生异常！" ) );
					result.setUserMessage( "系统转换数据库对象列表为输出列表时发生异常！" );
					logger.error( "system copy attendance detail list to wrap got an exception.", e );
				}	
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}	
	
	@HttpMethodDescribe(value = "根据周期的年份月份，以及需要考勤人员的名单，检查人员在周期内每天的考核数据是否存在，如果不存在，则进行补齐", response = WrapOutMessage.class )
	@GET
	@Path("checkDetailWithPersonByCycle/{cycleYear}/{cycleMonth}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkDetailWithPersonByCycle(@Context HttpServletRequest request, @PathParam("cycleYear") String cycleYear , @PathParam("cycleMonth") String cycleMonth ) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		AttendanceStatisticalCycle attendanceStatisticalCycle  = null;		
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		List<String> ids = null;
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigList = null;
		Boolean check = true;
		if( check ){
			if( cycleYear == null || cycleYear.isEmpty() ){
				check = false;
				result.error( new Exception( "参数统计周期年份为空，无法继续进行操作！" ) );
				result.setUserMessage( "参数统计周期年份为空，无法继续进行操作！" );
			}
		}
		if( check ){
			if( cycleMonth == null || cycleMonth.isEmpty() ){
				check = false;
				result.error( new Exception( "参数统计周期月份为空，无法继续进行操作！" ) );
				result.setUserMessage( "参数统计周期月份为空，无法继续进行操作！" );
			}
		}
		if( check ){
			try {
				ids = attendanceEmployeeConfigServiceAdv.listByConfigType( "REQUIRED" );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统在查询需要考勤的人员配置列表时发生异常！" ) );
				result.setUserMessage( "系统在查询需要考勤的人员配置列表时发生异常！" );
				logger.error( "system list employee config ids with employee config require type got an exception！", e );
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					attendanceEmployeeConfigList = attendanceEmployeeConfigServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统在查询需要考勤的人员配置列表时发生异常！" ) );
					result.setUserMessage( "系统在查询需要考勤的人员配置列表时发生异常！" );
					logger.error( "system list employee config with ids got an exception.", e );
				}
			}
		}
		if( check && attendanceEmployeeConfigList != null && attendanceEmployeeConfigList.size() > 0){
			if( check ){
				try {//查询所有的周期配置，组织成Map
					companyAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles();
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统在查询并且组织所有的统计周期时发生异常！" ) );
					result.setUserMessage( "系统在查询并且组织所有的统计周期时发生异常！" );
					logger.error( "system query and compose statistic cycle to map got an exception.", e );
				}
			}
			if( check ){
				Boolean globalCheck = true;
				Boolean subCheck = true;
				for( AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigList ){
					subCheck = true;
					try {
						attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.checkAttendanceEmployeeConfig( attendanceEmployeeConfig );
					} catch (Exception e ) {
						globalCheck = subCheck = false;
						result.error( new Exception( "系统检查需要考勤员工配置信息时发生异常！" ) );
						result.setUserMessage( "系统检查需要考勤员工配置信息时发生异常！" );
						logger.error( "system check attendance employee config info got an exception.", e );
					}
					if( subCheck ){
						try {//根据公司部门，年月获取到一个适合的统计周期，如果没有则新建一个新的配置
							attendanceStatisticalCycle = attendanceStatisticCycleServiceAdv.getAttendanceDetailStatisticCycle( attendanceEmployeeConfig.getCompanyName(), attendanceEmployeeConfig.getOrganizationName(), cycleYear, cycleMonth, companyAttendanceStatisticalCycleMap );
						} catch (Exception e) {
							globalCheck = subCheck = false;
							result.error( new Exception( "系统在根据员工的公司和部门查询指定的统计周期时发生异常！" ) );
							result.setUserMessage( "系统在根据员工的公司和部门查询指定的统计周期时发生异常！" );
							logger.error( "system query statistic cycle with department, company and year, month got an exception.", e );
						}
					}
					if( subCheck ){
						if( attendanceStatisticalCycle != null ){
							try {
								attendanceDetailServiceAdv.checkAndReplenish( attendanceStatisticalCycle.getCycleStartDate(), attendanceStatisticalCycle.getCycleEndDate(), attendanceEmployeeConfig );
							} catch (Exception e) {
								globalCheck = subCheck = false;
								result.error( new Exception( "系统根据时间列表核对和补充员工打卡信息时发生异常！" ) );
								result.setUserMessage( "系统根据时间列表核对和补充员工打卡信息时发生异常！" );
								logger.error( "system check and replenish attendance detail info got an exception.", e );
							}
						}
					}
				}
				if( globalCheck ){
					result.setUserMessage( "系统根据时间列表核对和补充员工打卡信息成功完成！" );
				}else{
					result.warn( "系统根据时间列表核对和补充员工打卡信息成完成，但部分数据未补充录成功。" );
					result.setUserMessage( "系统根据时间列表核对和补充员工打卡信息成完成，但部分数据未补充录成功！" );
				}
			}
		}
		logger.info( "系统对统计周期["+cycleYear+"-"+cycleMonth+"]的打卡数据核对完成！");
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "分析打卡数据", response = WrapOutMessage.class)
	@GET
	@Path("analyse/{startDate}/{endDate}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response analyseAttendanceDetails(@Context HttpServletRequest request, @PathParam("startDate") String startDate, @PathParam("endDate") String endDate ) {
		ActionResult< WrapOutMessage > result = new ActionResult<>();
		List<String> ids = null;
		List<AttendanceDetail> attendanceDetailList = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		Boolean check = true;
		
		if( check ){
			try {
				ids = attendanceDetailServiceAdv.getAllAnalysenessDetails( startDate, endDate );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统根据开始时间和结束时间查询需要分析的员工打卡信息ID列表时发生异常！" ) );
				result.setUserMessage( "系统根据开始时间和结束时间查询需要分析的员工打卡信息ID列表时发生异常！" );
				logger.error( "system query all analyseness detail info with start and end date{'startDate':'"+ startDate +"','endDate':'"+ endDate +"'} got an exception.", e );
			}
		}
		if( check && ids != null && !ids.isEmpty()  ){
			try {
				attendanceDetailList = attendanceDetailServiceAdv.list( ids );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统在根据ID列表查询员工考勤信息列表时发生异常！" ) );
				result.setUserMessage( "系统在根据ID列表查询员工考勤信息列表时发生异常！" );
				logger.error( "system query attendance detail with ids got an exception.", e );
			}	
		}
		if( check && attendanceDetailList != null && !attendanceDetailList.isEmpty() ){
			try {
				attendanceWorkDayConfigList = attendanceWorkDayConfigServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统在根据ID列表查询工作节假日配置信息列表时发生异常！" ) );
				result.setUserMessage( "系统在根据ID列表查询工作节假日配置信息列表时发生异常！" );
				logger.error( "system query attendance work day config info with ids got an exception.", e );
			}	
		}
		if( check && attendanceDetailList != null && !attendanceDetailList.isEmpty() ){
			try {//查询所有的周期配置，组织成Map
				companyAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles();
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统在查询并且组织所有的统计周期时发生异常！" ) );
				result.setUserMessage( "系统在查询并且组织所有的统计周期时发生异常！" );
				logger.error( "system query and compose statistic cycle to map got an exception.", e );
			}
		}
		if( check ){
			if( attendanceDetailList != null && !attendanceDetailList.isEmpty() ){
				int i = 0;
				int count = attendanceDetailList.size();
				for( AttendanceDetail detail : attendanceDetailList ){
					i++;
					if( i % 100 == 0){
						logger.debug("已经分析["+i+"/"+count+"]条数据，请继续等待分析过程完成......");
					}
					try{
						attendanceDetailAnalyseServiceAdv.analyseAttendanceDetail( detail, attendanceWorkDayConfigList, companyAttendanceStatisticalCycleMap );	
					}catch(Exception e){
						check = false;
						result.error( new Exception( "系统分析员工打卡信息时发生异常！" ) );
						result.setUserMessage( "系统分析员工打卡信息时发生异常！" );
						logger.error( "system analyse employee attendance detail info got an exception.", e );
					}
				}
			}else{
				result.setUserMessage( "没有需要分析的员工打卡信息！" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "分析打卡数据", response = WrapOutMessage.class)
	@GET
	@Path("analyse/id/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response analyseAttendanceDetails(@Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult< WrapOutMessage > result = new ActionResult<>();
		AttendanceDetail detail = null;
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception( "用户传入的需要分析的打卡记录ID为空，无法进行数据分析！" ) );
				result.setUserMessage( "用户传入的需要分析的打卡记录ID为空，无法进行数据分析！" );
			}
		}
		if( check ){
			try {
				detail = attendanceDetailServiceAdv.get( id );
				if( detail == null ){
					check = false;
					result.error( new Exception( "打卡记录不存在，无法进行数据分析操作！" ) );
					result.setUserMessage( "打卡记录不存在，无法进行数据分析操作！" );
					logger.error( "system attendance detail{'id':'"+id+"'} is not exists." );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据ID查询打卡信息对象时发生异常！" );
				logger.error( "system get attendance detail with id got an exception.id:" + id, e );
			}
		}
		if( check ){
			try {
				attendanceWorkDayConfigList = attendanceWorkDayConfigServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统查询所有的工作节假日配置信息列表时发生异常！" );
				logger.error( "system query all work day config list got an exception", e );
			}
		}
		if( check ){
			try {
				companyAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles();
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在查询并且组织所有的统计周期时发生异常！" );
				logger.error( "system query and compose statistic cycle to map got an exception.", e );
			}
		}
		if( check ){
			try{
				attendanceDetailAnalyseServiceAdv.analyseAttendanceDetail( detail, attendanceWorkDayConfigList, companyAttendanceStatisticalCycleMap);
				result.setUserMessage( "打卡信息分析完成。" );
			}catch(Exception e){
				check = false;
				result.error( e );
				result.setUserMessage( "系统分析指定打卡信息对象时发生异常！" );
				logger.error( "system analyse attendance detail got an exception.", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "将指定的打卡记录归档", response = WrapOutMessage.class )
	@GET
	@Path("archive/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response archiveAttendanceDetail( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		Boolean check = true;
		
		if( check ){
			if( id != null && !id.isEmpty() ){
				try{
					attendanceDetailServiceAdv.archive( id );
					result.setUserMessage( "指定打卡信息归档完成。" );
				}catch(Exception e){
					check = false;
					result.error( e );
					result.setUserMessage( "系统归档指定打卡信息对象时发生异常！" );
					logger.error( "system archive attendance detail got an exception.id:" + id, e );
				}
			}else{
				try{
					attendanceDetailServiceAdv.archiveAll();
					result.setUserMessage( "所有打卡信息归档完成。" );
				}catch(Exception e){
					check = false;
					result.error( e );
					result.setUserMessage( "系统归档所有定打卡信息对象时发生异常！" );
					logger.error( "system archive all attendance detail got an exception.", e );
				}
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的AttendanceDetail,下一页.", response = WrapOutAttendanceDetail.class, request = WrapInFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
		List<WrapOutAttendanceDetail> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<AttendanceDetail> detailList = null;
		List<String> companyNames = new ArrayList<String>();
		List<String> departmentNames = new ArrayList<String>();
		List<WrapCompany> companyList = null;
		List<WrapDepartment> departments = null;
		logger.debug("user[" + currentPerson.getName() + "] try to list attendanceDetail for nextpage, last id=" + id );
		try {		
			EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
			Business business = new Business(emc);
			
			//查询出ID对应的记录的sequence
			Object sequence = null;
			if( id == null || "(0)".equals(id) || id.isEmpty() ){
				//logger.debug( "第一页查询，没有id传入" );
			}else{
				if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
					sequence = PropertyUtils.getProperty(emc.find( id, AttendanceDetail.class, ExceptionWhen.not_found), "sequence");
				}
			}
			
			//处理一下公司，查询下级公司
			if( wrapIn.getQ_companyName() != null && !wrapIn.getQ_companyName().isEmpty() ){
				companyNames.add( wrapIn.getQ_companyName() );
				try{
					companyList = business.organization().company().listSubNested( wrapIn.getQ_companyName() );
				}catch(Exception e){
					logger.error("系统在根据公司名称获取下级公司的时候发生异常", e);
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
					logger.error("系统在根据部门名称查询下级部门的时候发生异常", e);
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
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的AttendanceDetail,上一页.", response = WrapOutAttendanceDetail.class, request = WrapInFilter.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutAttendanceDetail>> result = new ActionResult<>();
		List<WrapOutAttendanceDetail> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<AttendanceDetail> detailList = null;
		List<String> companyNames = new ArrayList<String>();
		List<String> departmentNames = new ArrayList<String>();
		List<WrapCompany> companyList = null;
		List<WrapDepartment> departments = null;
		logger.debug("user[" + currentPerson.getName() + "] try to list attendanceDetail for nextpage, last id=" + id );
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
					sequence = PropertyUtils.getProperty(emc.find(id, AttendanceDetail.class, ExceptionWhen.not_found), "sequence");
				}
			}		
			
			//处理一下公司，查询下级公司
			if( wrapIn.getQ_companyName() != null && !wrapIn.getQ_companyName().isEmpty() ){
				companyNames.add( wrapIn.getQ_companyName() );
				try{
					companyList = business.organization().company().listSubNested( wrapIn.getQ_companyName() );
				}catch(Exception e){
					logger.error("系统在根据公司名称获取下级公司的时候发生异常", e);
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
					logger.error("系统在根据部门名称查询下级部门的时候发生异常", e);
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
	@HttpMethodDescribe(value = "接入完成的上下班打卡信息记录，接入完成后直接分析.", request = WrapInAttendanceDetailRecive.class, response = WrapOutMessage.class)
	@Path("recive")
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response recive(@Context HttpServletRequest request, WrapInAttendanceDetailRecive wrapIn ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrapOutId = null;
		Date datetime = null;
		DateOperation dateOperation = new DateOperation();
		AttendanceDetail attendanceDetail = new AttendanceDetail();
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> companyAttendanceStatisticalCycleMap = null;
		Boolean check = true;
		
		if( wrapIn == null ){
			check = false;
			result.error( new Exception("系统未获取到需要保存的数据！") );
			result.setUserMessage( "系统未获取到需要保存的数据！" );
		}
		if( check ){
			if( wrapIn.getRecordDateString() == null || wrapIn.getRecordDateString().isEmpty() ){
				check = false;
				result.error( new Exception("打卡信息中打卡日期不能为空，格式: yyyy-mm-dd！") );
				result.setUserMessage( "打卡信息中打卡日期不能为空，格式: yyyy-mm-dd！" );
			}
		}
		if( check ){
			if( wrapIn.getEmpName() == null || wrapIn.getEmpName().isEmpty() ){
				check = false;
				result.error( new Exception("打卡信息中打卡员工姓名不能为空！") );
				result.setUserMessage( "打卡信息中打卡员工姓名不能为空！" );
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
				result.error( new Exception("打卡日期格式异常，时间：" + wrapIn.getRecordDateString() ) );
				result.setUserMessage( "打卡日期格式异常，时间：" + wrapIn.getRecordDateString() );
				logger.error("record date string error:" + wrapIn.getRecordDateString(), e);
			}
		}
		if( check ){
			if( wrapIn.getOnDutyTime() != null && wrapIn.getOnDutyTime().trim().length() > 0 ){
				try{
					datetime = dateOperation.getDateFromString( wrapIn.getOnDutyTime() );
					attendanceDetail.setOnDutyTime( dateOperation.getDateStringFromDate( datetime, "HH:mm:ss") ); //上班打卡时间
				}catch( Exception e ){
					check = false;
					result.error( new Exception("上班打卡时间格式异常，时间：" + wrapIn.getOnDutyTime() ) );
					result.setUserMessage( "上班打卡时间格式异常，时间：" + wrapIn.getOnDutyTime() );
					logger.error("on duty time string error:" + wrapIn.getOnDutyTime(), e);
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
					result.error( new Exception("下班打卡时间格式异常，时间：" + wrapIn.getOffDutyTime() ) );
					result.setUserMessage( "下班打卡时间格式异常，时间：" + wrapIn.getOffDutyTime() );
					logger.error("off duty time string error:" + wrapIn.getOffDutyTime(), e);
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
				result.error( new Exception("系统在保存打卡数据信息时发生异常。" ) );
				result.setUserMessage( "系统在保存打卡数据信息时发生异常。" );
				logger.error("system save attendanceDetail got an exception.", e);
			}
		}
		if( check ){
			try {
				attendanceWorkDayConfigList = attendanceWorkDayConfigServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统查询所有的工作节假日配置信息列表时发生异常！" );
				logger.error( "system query all work day config list got an exception", e );
			}
		}
		if( check ){
			try {
				companyAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles();
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在查询并且组织所有的统计周期时发生异常！" );
				logger.error( "system query and compose statistic cycle to map got an exception.", e );
			}
		}
		if( check ){
			try{
				attendanceDetailAnalyseServiceAdv.analyseAttendanceDetail( attendanceDetail, attendanceWorkDayConfigList, companyAttendanceStatisticalCycleMap);
				result.setUserMessage( "打卡信息保存并且分析完成。" );
			}catch(Exception e){
				check = false;
				result.error( e );
				result.setUserMessage( "系统分析指定打卡信息对象时发生异常！" );
				logger.error( "system analyse attendance detail got an exception.", e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceDetail数据对象.", response = WrapOutMessage.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		WrapOutMessage wrapOutMessage = new WrapOutMessage();
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("user " + currentPerson.getName() + "try to delete attendanceDetail......" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			AttendanceDetail attendanceDetail = emc.find(id, AttendanceDetail.class);
			if (null == attendanceDetail) {
				wrapOutMessage.setStatus("ERROR");
				wrapOutMessage.setMessage( "需要删除的打卡数据信息不存在。id=" + id );
			}else{
				//进行数据库持久化操作				
				emc.beginTransaction( AttendanceDetail.class );
				emc.remove( attendanceDetail, CheckRemoveType.all );
				emc.commit();			
				wrapOutMessage.setStatus("SUCCESS");
				wrapOutMessage.setMessage( "成功删除打卡数据信息。id=" + id );
			}			
		} catch ( Exception e ) {
			e.printStackTrace();
			wrapOutMessage.setStatus("ERROR");
			wrapOutMessage.setMessage( "删除打卡数据过程中发生异常。" );
			wrapOutMessage.setExceptionMessage( e.getMessage() );
		}
		result.setData( wrapOutMessage );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}