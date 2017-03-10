package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.x.attendance.assemble.control.service.AttendanceStatisticServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.StatisticCompanyForDay;
import com.x.attendance.entity.StatisticCompanyForMonth;
import com.x.attendance.entity.StatisticDepartmentForDay;
import com.x.attendance.entity.StatisticDepartmentForMonth;
import com.x.attendance.entity.StatisticPersonForMonth;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpAttribute;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;

@Path("statisticshow")
public class AttendanceStatisticShowAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( AttendanceStatisticShowAction.class );
	private UserManagerService userManagerService = new UserManagerService();
	private AttendanceStatisticServiceAdv attendanceStatisticServiceAdv = new AttendanceStatisticServiceAdv();
	private BeanCopyTools<StatisticPersonForMonth, WrapOutAttendanceStatisticPersonForMonth> wrapout_copier_person_forMonth = BeanCopyToolsBuilder
			.create(StatisticPersonForMonth.class, WrapOutAttendanceStatisticPersonForMonth.class, null, WrapOutAttendanceStatisticPersonForMonth.Excludes);
	private BeanCopyTools<StatisticDepartmentForMonth, WrapOutAttendanceStatisticDepartmentForMonth> wrapout_copier_department_forMonth = BeanCopyToolsBuilder
			.create(StatisticDepartmentForMonth.class, WrapOutAttendanceStatisticDepartmentForMonth.class, null, WrapOutAttendanceStatisticDepartmentForMonth.Excludes);
	private BeanCopyTools<StatisticCompanyForMonth, WrapOutAttendanceStatisticCompanyForMonth> wrapout_copier_company_forMonth = BeanCopyToolsBuilder
			.create(StatisticCompanyForMonth.class, WrapOutAttendanceStatisticCompanyForMonth.class, null, WrapOutAttendanceStatisticCompanyForMonth.Excludes);
	private BeanCopyTools<StatisticDepartmentForDay, WrapOutAttendanceStatisticDepartmentForDay> wrapout_copier_department_forDay = BeanCopyToolsBuilder
			.create(StatisticDepartmentForDay.class, WrapOutAttendanceStatisticDepartmentForDay.class, null, WrapOutAttendanceStatisticDepartmentForDay.Excludes);
	private BeanCopyTools<StatisticCompanyForDay, WrapOutAttendanceStatisticCompanyForDay> wrapout_copier_company_forDay = BeanCopyToolsBuilder
			.create(StatisticCompanyForDay.class, WrapOutAttendanceStatisticCompanyForDay.class, null, WrapOutAttendanceStatisticCompanyForDay.Excludes);

	@HttpMethodDescribe(value = "查询员工指定月份的统计数据", response = WrapOutAttendanceStatisticPersonForMonth.class)
	@GET
	@Path("person/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showPersonStatistic(@Context HttpServletRequest request, @PathParam("name") String name,
			@PathParam("year") String year, @PathParam("month") String month) {
		ActionResult<List<WrapOutAttendanceStatisticPersonForMonth>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceStatisticPersonForMonth> wraps = null;
		List<String> ids = null;
		List<StatisticPersonForMonth> statisticPersonForMonth_list = null;
		Boolean check = true;
		
		if ("(0)".equals(year)) {
			year = null;
		}
		if ("(0)".equals(month)) {
			month = null;
		}
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new QueryStatisticPersonNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				ids = attendanceStatisticServiceAdv.listPersonForMonthByUserYearAndMonth(name, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new PersonStatisticForMonthListByUserException( e, name, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticPersonForMonth_list = attendanceStatisticServiceAdv.listPersonForMonth(ids);
				} catch (Exception e) {
					check = false;
					Exception exception = new PersonStatisticForMonthListByIdsException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticPersonForMonth_list != null ){
				try {
					wraps = wrapout_copier_person_forMonth.copy( statisticPersonForMonth_list );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					Exception exception = new PersonStatisticForMonthWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "查询指定部门所有员工指定月份的统计数据", response = WrapOutAttendanceStatisticPersonForMonth.class)
	@GET
	@Path("persons/department/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showPersonsInDepartmentStatistic( @Context HttpServletRequest request, @PathParam("name") String name, @PathParam("year") String year, @PathParam("month") String month) {
		ActionResult<List<WrapOutAttendanceStatisticPersonForMonth>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceStatisticPersonForMonth> wraps = null;
		List<String> ids = null;
		List<StatisticPersonForMonth> statisticPersonForMonth_list = null;
		List<String> departmentNameList = new ArrayList<String>();
		Boolean check = true;
		if ("(0)".equals(year) ) {
			year = null;
		}
		if ("(0)".equals(month) ) {
			month = null;
		}
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new QueryStatisticDepartmentNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}else{
				departmentNameList.add( name );
			}
		}
		if( check ){
			try {
				ids = attendanceStatisticServiceAdv.listPersonForMonthByDepartmentYearAndMonth( departmentNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new PersonStatisticForMonthListByDepartmentsException(e, departmentNameList, year, month);
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticPersonForMonth_list = attendanceStatisticServiceAdv.listPersonForMonth(ids);
				} catch (Exception e) {
					check = false;
					Exception exception = new PersonStatisticForMonthListByIdsException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticPersonForMonth_list != null ){
				try {
					wraps = wrapout_copier_person_forMonth.copy( statisticPersonForMonth_list );
					result.setData(wraps);
				} catch ( Exception e ) {
					check = false;
					Exception exception = new PersonStatisticForMonthWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "查询指定部门所有员工指定月份的统计数据", response = WrapOutAttendanceStatisticPersonForMonth.class)
	@GET
	@Path( "persons/department/subnested/{name}/{year}/{month}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showPersonsInDepartmentAndSubNested( @Context HttpServletRequest request, @PathParam("name") String name, @PathParam("year") String year, @PathParam("month") String month) {
		ActionResult<List<WrapOutAttendanceStatisticPersonForMonth>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceStatisticPersonForMonth> wraps = null;
		List<String> ids = null;
		List<String> departmentNameList = new ArrayList<String>();
		List<StatisticPersonForMonth> statisticPersonForMonth_list = null;
		Boolean check = true;
		if ( "(0)".equals( year ) ) {
			year = null;
		}
		if ( "(0)".equals( month ) ) {
			month = null;
		}
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new QueryStatisticDepartmentNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				departmentNameList = userManagerService.listSubOrganizationNameList( name );
			} catch (Exception e) {
				check = false;
				Exception exception = new ListDepartmentNameByParentNameException( e, name );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( departmentNameList == null ){
				departmentNameList = new ArrayList<>();
			}
			departmentNameList.add( name );
		}
		if( check ){
			try {
				ids = attendanceStatisticServiceAdv.listPersonForMonthByDepartmentYearAndMonth( departmentNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new PersonStatisticForMonthListByDepartmentsException(e, departmentNameList, year, month);
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticPersonForMonth_list = attendanceStatisticServiceAdv.listPersonForMonth( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new PersonStatisticForMonthListByIdsException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticPersonForMonth_list != null ){
				try {
					wraps = wrapout_copier_person_forMonth.copy( statisticPersonForMonth_list );
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new PersonStatisticForMonthWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe( value = "查询部门指定月份的统计数据", response = WrapOutAttendanceStatisticDepartmentForMonth.class)
	@GET
	@Path("department/subnested/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showDepartmentAndSubnestedStatistic(@Context HttpServletRequest request, @PathParam("name") String name, @PathParam("year") String year, @PathParam("month") String month) {
		ActionResult<List<WrapOutAttendanceStatisticDepartmentForMonth>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceStatisticDepartmentForMonth> wraps = null;
		List<String> ids = null;
		List<StatisticDepartmentForMonth> statisticDepartmentForMonth_list = null;
		List<String> departmentNameList = null;
		Boolean check = true;
		
		if ("(0)".equals(year)) {
			year = null;
		}
		if ("(0)".equals(month)) {
			month = null;
		}
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new QueryStatisticDepartmentNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				departmentNameList = userManagerService.listSubOrganizationNameList( name );
			} catch (Exception e) {
				check = false;
				Exception exception = new ListDepartmentNameByParentNameException( e, name );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( departmentNameList == null ){
				departmentNameList = new ArrayList<>();
			}
			departmentNameList.add( name );
		}
		if( check ){
			try {
				ids = attendanceStatisticServiceAdv.listDepartmentForMonthByDepartmentYearAndMonth( departmentNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForMonthListByDepartmentsException(e, departmentNameList, year, month);
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticDepartmentForMonth_list = attendanceStatisticServiceAdv.listDepartmentForMonth( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new DepartmentStatisticForMonthListByIdsException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticDepartmentForMonth_list != null ){
				try {
					wraps = wrapout_copier_department_forMonth.copy( statisticDepartmentForMonth_list );
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new DepartmentStatisticForMonthWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "查询公司指定月份的统计数据", response = WrapOutAttendanceStatisticDepartmentForMonth.class)
	@GET
	@Path( "department/company/{name}/{year}/{month}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showDepartmentStatisticWithCompanyName(@Context HttpServletRequest request, @PathParam("name") String name,
			@PathParam("year") String year, @PathParam("month") String month) {
		ActionResult<List<WrapOutAttendanceStatisticDepartmentForMonth>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceStatisticDepartmentForMonth> wraps = null;
		List<String> ids = null;
		List<StatisticDepartmentForMonth> statisticDepartmentForMonth_list = null;
		List<String> departmentNames = new ArrayList<String>();
		Boolean check = true;
		
		if ("(0)".equals(year)) {
			year = null;
		}
		if ("(0)".equals(month)) {
			month = null;
		}
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new QueryStatisticCompanyNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				//根据公司递归查询下级公司经及公司的顶级部门
				departmentNames = getTopDepartmentNameList( name, departmentNames );
			} catch (Exception e) {
				check = false;
				Exception exception = new GetTopDepartmentNamesByOrganNameException( e, name, departmentNames );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( departmentNames == null ){
				departmentNames = new ArrayList<>();
			}
			departmentNames.add( name );
		}
		if( check ){
			try {
				ids = attendanceStatisticServiceAdv.listDepartmentForMonthByDepartmentYearAndMonth( departmentNames, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForMonthListByDepartmentsException(e, departmentNames, year, month);
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticDepartmentForMonth_list = attendanceStatisticServiceAdv.listDepartmentForMonth( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new DepartmentStatisticForMonthListByIdsException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticDepartmentForMonth_list != null ){
				try {
					wraps = wrapout_copier_department_forMonth.copy( statisticDepartmentForMonth_list );
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new DepartmentStatisticForMonthWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "查询部门指定月份的统计数据", response = WrapOutAttendanceStatisticDepartmentForMonth.class)
	@GET
	@Path("department/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showDepartmentStatistic(@Context HttpServletRequest request, @PathParam("name") String name, @PathParam("year") String year, @PathParam("month") String month) {
		ActionResult<List<WrapOutAttendanceStatisticDepartmentForMonth>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceStatisticDepartmentForMonth> wraps = null;
		List<String> ids = null;
		List<StatisticDepartmentForMonth> statisticDepartmentForMonth_list = null;
		Boolean check = true;
		
		if ("(0)".equals( year )) {
			year = null;
		}
		if ("(0)".equals( month )) {
			month = null;
		}
		
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new QueryStatisticDepartmentNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				ids = attendanceStatisticServiceAdv.listDepartmentForMonthByDepartmentYearAndMonth( name, year, month);
			} catch (Exception e) {
				check = false;
				result.error( e );
				Exception exception = new DepartmentStatisticForMonthListByDepartmentsException(e, name, year, month);
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticDepartmentForMonth_list = attendanceStatisticServiceAdv.listDepartmentForMonth( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new DepartmentStatisticForMonthListByIdsException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticDepartmentForMonth_list != null ){
				try {
					wraps = wrapout_copier_department_forMonth.copy( statisticDepartmentForMonth_list );
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new DepartmentStatisticForMonthWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "查询部门指定月份的统计数据", response = WrapOutAttendanceStatisticDepartmentForMonth.class)
	@GET
	@Path( "department/sum/{name}/{year}/{month}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sumDepartmentStatistic( @Context HttpServletRequest request, @PathParam("name") String name, @PathParam("year") String year, @PathParam("month") String month) {
		ActionResult<WrapOutAttendanceStatisticDepartmentForMonth> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutAttendanceStatisticDepartmentForMonth wraps = null;
		StatisticDepartmentForMonth statisticDepartmentForMonth = null;
		List<String> departmentNameList = new ArrayList<String>();
		Object employeeCount = null;
		Object onDutyEmployeeCount = null;
		Object absenceDayCount = null;
		Object onSelfHolidayCount = null;
		Object lateCount = null;
		Object leaveEarlyCount = null;
		Object offDutyCount = null;
		Object onDutyCount = null;
		Object abNormalDutyCount = null;
		Object lackOfTimeCount = null;
		Boolean check = true;
		
		if ( "(0)".equals(year)) {
			year = null;
		}
		if ( "(0)".equals(month)) {
			month = null;
		}
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new QueryStatisticDepartmentNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				departmentNameList = userManagerService.listSubOrganizationNameList( name );
			} catch (Exception e) {
				check = false;
				Exception exception = new ListDepartmentNameByParentNameException( e, name );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( departmentNameList == null ){
				departmentNameList = new ArrayList<>();
			}
			departmentNameList.add( name );
		}
		if( check ){
			try {
				absenceDayCount = attendanceStatisticServiceAdv.sumDepartmentForMonth_AbsenceDayCount_ByDepartmentYearAndMonth( departmentNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForMonthSumAbsenceDayException( e, departmentNameList, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				onSelfHolidayCount = attendanceStatisticServiceAdv.sumDepartmentForMonth_OnSelfHolidayCount_ByDepartmentYearAndMonth( departmentNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForMonthSumSelfHolidayException( e, departmentNameList, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				lateCount = attendanceStatisticServiceAdv.sumDepartmentForMonth_LateCount_ByDepartmentYearAndMonth( departmentNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForMonthSumLateCountException( e, departmentNameList, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				leaveEarlyCount = attendanceStatisticServiceAdv.sumDepartmentForMonth_LeaveEarlyCount_ByDepartmentYearAndMonth( departmentNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForMonthSumLeaveEarlyDayException( e, departmentNameList, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				onDutyCount = attendanceStatisticServiceAdv.sumDepartmentForMonth_OnDutyCount_ByDepartmentYearAndMonth( departmentNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForMonthSumOnDutyException( e, departmentNameList, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				offDutyCount = attendanceStatisticServiceAdv.sumDepartmentForMonth_OffDutyCount_ByDepartmentYearAndMonth( departmentNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForMonthSumOffDutyException( e, departmentNameList, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				abNormalDutyCount = attendanceStatisticServiceAdv.sumDepartmentForMonth_AbNormalDutyCount_ByDepartmentYearAndMonth( departmentNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForMonthSumAbsenceDayException( e, departmentNameList, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				lackOfTimeCount = attendanceStatisticServiceAdv.sumDepartmentForMonth_LackOfTimeCount_ByDepartmentYearAndMonth( departmentNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForMonthSumLackOfTimeException( e, departmentNameList, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}		
		}
		if( check ){
			try {
				onDutyEmployeeCount = attendanceStatisticServiceAdv.sumDepartmentForMonth_AttendanceDayCount_ByDepartmentYearAndMonth( departmentNameList, year, month );
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForMonthSumAbsenceDayException( e, departmentNameList, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			double count = 0.0;
			statisticDepartmentForMonth = new StatisticDepartmentForMonth(
					lateCount == null?0:(long)lateCount, 
					leaveEarlyCount == null?0:(long)leaveEarlyCount, 
					offDutyCount == null?0:(long)offDutyCount, 
					onDutyCount == null?0:(long)onDutyCount, 
					absenceDayCount == null?0:(double)absenceDayCount, 
					employeeCount == null?0:(double)employeeCount,  
					onDutyEmployeeCount == null?0:(double)onDutyEmployeeCount,  
					onSelfHolidayCount == null?0:(double)onSelfHolidayCount,  
					lackOfTimeCount == null?0:(long)lackOfTimeCount, 
					abNormalDutyCount == null?0:(long)abNormalDutyCount 
			);
			if( onDutyEmployeeCount == null ){
				count = 0.0;
			}else{
				count = (double)onDutyEmployeeCount;
			}
			if( statisticDepartmentForMonth != null ){
				statisticDepartmentForMonth.setOnDutyEmployeeCount( count );
			}
		}
		if( check ){
			try {
				wraps = wrapout_copier_department_forMonth.copy( statisticDepartmentForMonth );
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForMonthWrapOutException( e );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "查询公司指定月份的统计数据", response = WrapOutAttendanceStatisticCompanyForMonth.class)
	@GET
	@Path("company/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showCompanyStatistic(@Context HttpServletRequest request, @PathParam("name") String name, @PathParam("year") String year, @PathParam("month") String month) {
		ActionResult<List<WrapOutAttendanceStatisticCompanyForMonth>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceStatisticCompanyForMonth> wraps = null;
		List<String> ids = null;
		List<StatisticCompanyForMonth> statisticCompanyForMonth_list = null;
		Boolean check = true;
		
		if ("(0)".equals(year)) {
			year = null;
		}
		if ("(0)".equals(month)) {
			month = null;
		}
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new QueryStatisticCompanyNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				ids = attendanceStatisticServiceAdv.listStatisticCompanyForMonth_ByCompanyYearAndMonth( name, year, month );
			} catch (Exception e) {
				check = false;
				Exception exception = new CompanyStatisticForMonthListByCompanyException( e, name, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticCompanyForMonth_list = attendanceStatisticServiceAdv.listCompanyForMonth( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new CompanyStatisticForMonthListByIdsException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticCompanyForMonth_list != null ){
				try {
					wraps = wrapout_copier_company_forMonth.copy( statisticCompanyForMonth_list );
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new CompanyStatisticForMonthWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "查询部门指定月份每日的统计数据", response = WrapOutAttendanceStatisticDepartmentForDay.class)
	@GET
	@Path("department/day/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showDepartmentDayStatistic(@Context HttpServletRequest request, @PathParam("name") String name, @PathParam("year") String year, @PathParam("month") String month) {
		ActionResult<List<WrapOutAttendanceStatisticDepartmentForDay>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceStatisticDepartmentForDay> wraps = null;
		List<String> ids = null;
		List<StatisticDepartmentForDay> statisticDepartmentForDay_list = null;
		List<String> departmentNames = null;
		Boolean check = true;
		
		if ("(0)".equals(year)) {
			year = null;
		}
		if ("(0)".equals(month)) {
			month = null;
		}
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new QueryStatisticDepartmentNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				departmentNames = userManagerService.listSubOrganizationNameList( name );
			} catch (Exception e) {
				check = false;
				Exception exception = new ListDepartmentNameByParentNameException( e, name );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( departmentNames == null ){
				departmentNames = new ArrayList<>();
			}
			departmentNames.add( name );
		}
		if( check ){
			try {
				ids = attendanceStatisticServiceAdv.listStatisticDepartmentForDay_ByDepartmentDayYearAndMonth( departmentNames, year, month );
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentStatisticForDayListByDepartmentsException( e, departmentNames, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticDepartmentForDay_list = attendanceStatisticServiceAdv.listDepartmentForDay( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new DepartmentStatisticForDayListByIdsException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticDepartmentForDay_list != null ){
				try {
					wraps = wrapout_copier_department_forDay.copy( statisticDepartmentForDay_list );
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new DepartmentStatisticForDayWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "查询公司指定月份每日的统计数据", response = WrapOutAttendanceStatisticCompanyForDay.class)
	@GET
	@Path("company/day/{name}/{year}/{month}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showCompanyDayStatistic(@Context HttpServletRequest request, @PathParam("name") String name, @PathParam("year") String year, @PathParam("month") String month) {
		ActionResult<List<WrapOutAttendanceStatisticCompanyForDay>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceStatisticCompanyForDay> wraps = null;
		List<String> ids = null;
		List<StatisticCompanyForDay> statisticCompanyForDay_list = null;
		Boolean check = true;
		
		if ("(0)".equals(year)) {
			year = null;
		}
		if ("(0)".equals(month)) {
			month = null;
		}
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new QueryStatisticCompanyNameEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				ids = attendanceStatisticServiceAdv.listStatisticCompanyForDay_ByNameYearAndMonth( name, year, month );
			} catch (Exception e) {
				check = false;
				Exception exception = new CompanyStatisticForDayListByCompanyException( e, name, year, month );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticCompanyForDay_list = attendanceStatisticServiceAdv.listCompanyForDay( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new CompanyStatisticForDayListByIdsException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticCompanyForDay_list != null ){
				try {
					wraps = wrapout_copier_company_forDay.copy( statisticCompanyForDay_list );
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new CompanyStatisticForDayWrapOutException( e );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "查询部门指定日期的统计数据", response = WrapOutAttendanceStatisticDepartmentForDay.class)
	@GET
	@Path("department/day/{name}/{date}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showDepartmentStatisticForDay(@Context HttpServletRequest request, @PathParam("name") String name, @PathParam("date") String date) {
		ActionResult<List<WrapOutAttendanceStatisticDepartmentForDay>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceStatisticDepartmentForDay> wraps = null;
		Business business = null;
		List<String> ids = null;
		List<StatisticDepartmentForDay> statisticDepartmentForDay_list = null;
		List<String> departmentNames = new ArrayList<String>();
		List<WrapDepartment> departments = null;
		
		if ("(0)".equals(name)) {
			name = null;
		}
		if ("(0)".equals(date)) {
			date = null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( name != null && !name.isEmpty()){
				departmentNames.add(name);
				try{
					departments = business.organization().department().listSubNested( name );
				}catch(Exception e){
					Exception exception = new ListDepartmentNameByParentNameException( e, name );
					logger.error( exception, effectivePerson, request, null);
				}
				if( departments != null && departments.size() > 0 ){
					for( WrapDepartment department : departments){
						departmentNames.add( department.getName() );
					}
				}
			}
			try{
				ids = business.getStatisticDepartmentForDayFactory().listByDepartmentDayDate( departmentNames, date );
			}catch(Exception e){
				Exception exception = new DepartmentStatisticForDayListByDateException( e, departmentNames, date );
				logger.error( exception, effectivePerson, request, null);
			}
			try{
				if( ids != null && !ids.isEmpty() ){
					statisticDepartmentForDay_list = business.getStatisticDepartmentForDayFactory().list(ids);
				}
			}catch(Exception e){
				Exception exception = new DepartmentStatisticForDayListByIdsException( e );
				logger.error( exception, effectivePerson, request, null);
			}
			try{
				if( statisticDepartmentForDay_list != null && !statisticDepartmentForDay_list.isEmpty() ){
					wraps = wrapout_copier_department_forDay.copy(statisticDepartmentForDay_list);
					result.setData(wraps);
				}
			}catch(Exception e){
				Exception exception = new DepartmentStatisticForDayWrapOutException( e );
				logger.error( exception, effectivePerson, request, null);
			}		
		} catch (Exception e) {
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "查询部门指定日期的统计数据", response = WrapOutAttendanceStatisticDepartmentForDay.class)
	@GET
	@Path("department/day/company/{name}/{date}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showDepartmentStatisticForDayWithCompanyName(@Context HttpServletRequest request,
			@PathParam("name") String name, @PathParam("date") String date) {
		ActionResult<List<WrapOutAttendanceStatisticDepartmentForDay>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceStatisticDepartmentForDay> wraps = null;
		Business business = null;
		List<String> ids = null;
		List<StatisticDepartmentForDay> statisticDepartmentForDay_list = null;
		List<String> departmentNames = new ArrayList<String>();
		
		if ("(0)".equals(name)) {
			name = null;
		}
		if ("(0)".equals(date)) {
			date = null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			
			if( name != null && !name.isEmpty()){
				getTopDepartmentNameList( name, departmentNames);
			}
			
			try{
				ids = business.getStatisticDepartmentForDayFactory().listByDepartmentDayDate( departmentNames, date );
			}catch(Exception e){
				Exception exception = new DepartmentStatisticForDayListByDateException( e, departmentNames, date );
				logger.error( exception, effectivePerson, request, null);
			}
			try{
				if( ids != null && !ids.isEmpty() ){
					statisticDepartmentForDay_list = business.getStatisticDepartmentForDayFactory().list(ids);
				}
			}catch(Exception e){
				Exception exception = new DepartmentStatisticForDayListByIdsException( e );
				logger.error( exception, effectivePerson, request, null);
			}
			try{
				if( statisticDepartmentForDay_list != null && !statisticDepartmentForDay_list.isEmpty() ){
					wraps = wrapout_copier_department_forDay.copy(statisticDepartmentForDay_list);
					result.setData(wraps);
				}
			}catch(Exception e){
				Exception exception = new DepartmentStatisticForDayWrapOutException( e );
				logger.error( exception, effectivePerson, request, null);
			}
		} catch (Exception e) {
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的StatisticPersonForMonth,下一页.", response = WrapOutAttendanceStatisticPersonForMonth.class, request = JsonElement.class)
	@PUT
	@Path("filter/personMonth/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStatisticPersonForMonthNextPageWithFilter(@Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceStatisticPersonForMonth>> result = new ActionResult<>();
		List<WrapOutAttendanceStatisticPersonForMonth> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<StatisticPersonForMonth> statisticList = null;
		WrapInFilterStatisticPersonForMonth wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterStatisticPersonForMonth.class );
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

				// 查询出ID对应的记录的sequence
				Object sequence = null;
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					// logger.debug( "第一页查询，没有id传入" );
				} else {
					if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
						sequence = PropertyUtils.getProperty(
								emc.find(id, StatisticPersonForMonth.class ), "sequence");
					}
				}

				//将下级组织的数据纳入组织统计数据查询范围
				List<String> organizationNameList = getDepartmentNameList(wrapIn.getCompanyName(), wrapIn.getOrganizationName());			
				wrapIn.setOrganizationName(organizationNameList);
				// 从数据库中查询符合条件的一页数据对象
				statisticList = business.getStatisticPersonForMonthFactory().listIdsNextWithFilter(id, count, sequence, wrapIn);

				// 从数据库中查询符合条件的对象总数
				total = business.getStatisticPersonForMonthFactory().getCountWithFilter(wrapIn);

				// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier_person_forMonth.copy(statisticList);

				// 对查询的列表进行排序
				result.setCount(total);
				result.setData(wraps);

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的StatisticPersonForMonth,上一页.", response = WrapOutAttendanceStatisticPersonForMonth.class, request = JsonElement.class)
	@PUT
	@Path("filter/personMonth/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStatisticPersonForMonthPrevPageWithFilter(@Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceStatisticPersonForMonth>> result = new ActionResult<>();
		List<WrapOutAttendanceStatisticPersonForMonth> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<StatisticPersonForMonth> statisticList = null;
		WrapInFilterStatisticPersonForMonth wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterStatisticPersonForMonth.class );
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

				// 查询出ID对应的记录的sequence
				Object sequence = null;
				logger.debug("传入的ID=" + id);
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					logger.debug("第一页查询，没有id传入");
				} else {
					if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
						sequence = PropertyUtils.getProperty(
								emc.find(id, StatisticPersonForMonth.class ), "sequence");
					}
				}

				//将下级组织的数据纳入组织统计数据查询范围
				List<String> organizationNameList = getDepartmentNameList(wrapIn.getCompanyName(), wrapIn.getOrganizationName());			
				wrapIn.setOrganizationName(organizationNameList);
				// 从数据库中查询符合条件的一页数据对象
				statisticList = business.getStatisticPersonForMonthFactory().listIdsPrevWithFilter(id, count, sequence,
						wrapIn);

				// 从数据库中查询符合条件的对象总数
				total = business.getStatisticPersonForMonthFactory().getCountWithFilter(wrapIn);

				// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier_person_forMonth.copy(statisticList);

				result.setCount(total);
				result.setData(wraps);

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的StatisticDepartmentForMonth,下一页.", response = WrapOutAttendanceStatisticDepartmentForMonth.class, request = JsonElement.class)
	@PUT
	@Path("filter/departmentMonth/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStatisticDepartmentForMonthNextPageWithFilter(@Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("count") Integer count,
			JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceStatisticDepartmentForMonth>> result = new ActionResult<>();
		List<WrapOutAttendanceStatisticDepartmentForMonth> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<StatisticDepartmentForMonth> statisticList = null;
		WrapInFilterStatisticDepartmentForMonth wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterStatisticDepartmentForMonth.class );
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

				// 查询出ID对应的记录的sequence
				Object sequence = null;
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					// logger.debug( "第一页查询，没有id传入" );
				} else {
					if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
						sequence = PropertyUtils.getProperty(
								emc.find(id, StatisticDepartmentForMonth.class ), "sequence");
					}
				}

				//将下级组织的数据纳入组织统计数据查询范围
				List<String> organizationNameList = getDepartmentNameList(wrapIn.getCompanyName(), wrapIn.getOrganizationName());			
				wrapIn.setOrganizationName(organizationNameList);
				// 从数据库中查询符合条件的一页数据对象
				statisticList = business.getStatisticDepartmentForMonthFactory().listIdsNextWithFilter(id, count, sequence,
						wrapIn);

				// 从数据库中查询符合条件的对象总数
				total = business.getStatisticDepartmentForMonthFactory().getCountWithFilter(wrapIn);

				// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier_department_forMonth.copy(statisticList);

				// 对查询的列表进行排序
				result.setCount(total);
				result.setData(wraps);

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的StatisticDepartmentForMonth,上一页.", response = WrapOutAttendanceStatisticDepartmentForMonth.class, request = JsonElement.class)
	@PUT
	@Path("filter/departmentMonth/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStatisticDepartmentForMonthPrevPageWithFilter(@Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("count") Integer count,
			JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceStatisticDepartmentForMonth>> result = new ActionResult<>();
		List<WrapOutAttendanceStatisticDepartmentForMonth> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<StatisticDepartmentForMonth> statisticList = null;
		WrapInFilterStatisticDepartmentForMonth wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterStatisticDepartmentForMonth.class );
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

				// 查询出ID对应的记录的sequence
				Object sequence = null;
				logger.debug("传入的ID=" + id);
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					logger.debug("第一页查询，没有id传入");
				} else {
					if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
						sequence = PropertyUtils.getProperty(
								emc.find(id, StatisticDepartmentForMonth.class ), "sequence");
					}
				}

				//将下级组织的数据纳入组织统计数据查询范围
				List<String> organizationNameList = getDepartmentNameList(wrapIn.getCompanyName(), wrapIn.getOrganizationName());			
				wrapIn.setOrganizationName(organizationNameList);
				// 从数据库中查询符合条件的一页数据对象
				statisticList = business.getStatisticDepartmentForMonthFactory().listIdsPrevWithFilter(id, count, sequence,
						wrapIn);

				// 从数据库中查询符合条件的对象总数
				total = business.getStatisticDepartmentForMonthFactory().getCountWithFilter(wrapIn);

				// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier_department_forMonth.copy(statisticList);

				result.setCount(total);
				result.setData(wraps);

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的StatisticCompanyForMonth,下一页.", response = WrapOutAttendanceStatisticCompanyForMonth.class, request = JsonElement.class)
	@PUT
	@Path("filter/companyMonth/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStatisticCompanyForMonthNextPageWithFilter(@Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("count") Integer count,
			JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceStatisticCompanyForMonth>> result = new ActionResult<>();
		List<WrapOutAttendanceStatisticCompanyForMonth> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<StatisticCompanyForMonth> statisticList = null;
		WrapInFilterStatisticCompanyForMonth wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterStatisticCompanyForMonth.class );
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

				// 查询出ID对应的记录的sequence
				Object sequence = null;
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					// logger.debug( "第一页查询，没有id传入" );
				} else {
					if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
						sequence = PropertyUtils.getProperty(
								emc.find(id, StatisticCompanyForMonth.class ), "sequence");
					}
				}

				//将下级组织的数据纳入组织统计数据查询范围
				List<String> organizationNameList = getDepartmentNameList(wrapIn.getCompanyName(), wrapIn.getOrganizationName());			
				wrapIn.setOrganizationName(organizationNameList);
				// 从数据库中查询符合条件的一页数据对象
				statisticList = business.getStatisticCompanyForMonthFactory().listIdsNextWithFilter(id, count, sequence,
						wrapIn);

				// 从数据库中查询符合条件的对象总数
				total = business.getStatisticCompanyForMonthFactory().getCountWithFilter(wrapIn);

				// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier_company_forMonth.copy(statisticList);

				// 对查询的列表进行排序
				result.setCount(total);
				result.setData(wraps);

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的StatisticCompanyForMonth,上一页.", response = WrapOutAttendanceStatisticCompanyForMonth.class, request = JsonElement.class)
	@PUT
	@Path( "filter/companyMonth/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStatisticCompanyForMonthPrevPageWithFilter(@Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceStatisticCompanyForMonth>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutAttendanceStatisticCompanyForMonth> wraps = null;
		long total = 0;
		List<StatisticCompanyForMonth> statisticList = null;
		WrapInFilterStatisticCompanyForMonth wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterStatisticCompanyForMonth.class );
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

				// 查询出ID对应的记录的sequence
				Object sequence = null;

				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					logger.debug("第一页查询，没有id传入");
				} else {
					if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
						sequence = PropertyUtils.getProperty(
								emc.find(id, StatisticCompanyForMonth.class ), "sequence");
					}
				}

				//将下级组织的数据纳入组织统计数据查询范围
				List<String> organizationNameList = getDepartmentNameList(wrapIn.getCompanyName(), wrapIn.getOrganizationName());			
				wrapIn.setOrganizationName(organizationNameList);
				// 从数据库中查询符合条件的一页数据对象
				statisticList = business.getStatisticCompanyForMonthFactory().listIdsPrevWithFilter(id, count, sequence,
						wrapIn);

				// 从数据库中查询符合条件的对象总数
				total = business.getStatisticCompanyForMonthFactory().getCountWithFilter(wrapIn);

				// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier_company_forMonth.copy(statisticList);

				result.setCount(total);
				result.setData(wraps);

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的StatisticDepartmentForDay,下一页.", response = WrapOutAttendanceStatisticDepartmentForDay.class, request = JsonElement.class)
	@PUT
	@Path("filter/departmentDay/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStatisticDepartmentForDayNextPageWithFilter(@Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("count") Integer count,
			JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceStatisticDepartmentForDay>> result = new ActionResult<>();
		List<WrapOutAttendanceStatisticDepartmentForDay> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<StatisticDepartmentForDay> statisticList = null;
		WrapInFilterStatisticDepartmentForDay wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterStatisticDepartmentForDay.class );
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

				// 查询出ID对应的记录的sequence
				Object sequence = null;
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					// logger.debug( "第一页查询，没有id传入" );
				} else {
					if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
						sequence = PropertyUtils.getProperty(
								emc.find(id, StatisticDepartmentForDay.class ), "sequence");
					}
				}

				//将下级组织的数据纳入组织统计数据查询范围
				List<String> organizationNameList = getDepartmentNameList(wrapIn.getCompanyName(), wrapIn.getOrganizationName());			
				wrapIn.setOrganizationName(organizationNameList);
				// 从数据库中查询符合条件的一页数据对象
				statisticList = business.getStatisticDepartmentForDayFactory().listIdsNextWithFilter(id, count, sequence,
						wrapIn);

				// 从数据库中查询符合条件的对象总数
				total = business.getStatisticDepartmentForDayFactory().getCountWithFilter(wrapIn);

				// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier_department_forDay.copy(statisticList);

				// 对查询的列表进行排序
				result.setCount(total);
				result.setData(wraps);

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的StatisticDepartmentForDay,上一页.", response = WrapOutAttendanceStatisticDepartmentForDay.class, request = JsonElement.class)
	@PUT
	@Path("filter/departmentDay/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStatisticDepartmentForDayPrevPageWithFilter(@Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("count") Integer count,
			JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceStatisticDepartmentForDay>> result = new ActionResult<>();
		List<WrapOutAttendanceStatisticDepartmentForDay> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<StatisticDepartmentForDay> statisticList = null;
		WrapInFilterStatisticDepartmentForDay wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterStatisticDepartmentForDay.class );
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

				// 查询出ID对应的记录的sequence
				Object sequence = null;

				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					logger.debug("第一页查询，没有id传入");
				} else {
					if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
						sequence = PropertyUtils.getProperty(
								emc.find(id, StatisticDepartmentForDay.class ), "sequence");
					}
				}

				//将下级组织的数据纳入组织统计数据查询范围
				List<String> organizationNameList = getDepartmentNameList(wrapIn.getCompanyName(), wrapIn.getOrganizationName());			
				wrapIn.setOrganizationName(organizationNameList);
				// 从数据库中查询符合条件的一页数据对象
				statisticList = business.getStatisticDepartmentForDayFactory().listIdsPrevWithFilter(id, count, sequence,
						wrapIn);

				// 从数据库中查询符合条件的对象总数
				total = business.getStatisticDepartmentForDayFactory().getCountWithFilter(wrapIn);

				// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier_department_forDay.copy(statisticList);

				result.setCount(total);
				result.setData(wraps);

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的StatisticCompanyForDay,下一页.", response = WrapOutAttendanceStatisticCompanyForDay.class, request = JsonElement.class)
	@PUT
	@Path("filter/companyDay/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStatisticCompanyForDayNextPageWithFilter(@Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceStatisticCompanyForDay>> result = new ActionResult<>();
		List<WrapOutAttendanceStatisticCompanyForDay> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<StatisticCompanyForDay> statisticList = null;
		WrapInFilterStatisticCompanyForDay wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterStatisticCompanyForDay.class );
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

				// 查询出ID对应的记录的sequence
				Object sequence = null;
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					// logger.debug( "第一页查询，没有id传入" );
				} else {
					if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
						sequence = PropertyUtils.getProperty(
								emc.find(id, StatisticCompanyForDay.class ), "sequence");
					}
				}

				//将下级组织的数据纳入组织统计数据查询范围
				List<String> organizationNameList = getDepartmentNameList(wrapIn.getCompanyName(), wrapIn.getOrganizationName());			
				wrapIn.setOrganizationName(organizationNameList);
				// 从数据库中查询符合条件的一页数据对象
				statisticList = business.getStatisticCompanyForDayFactory().listIdsNextWithFilter(id, count, sequence,
						wrapIn);

				// 从数据库中查询符合条件的对象总数
				total = business.getStatisticCompanyForDayFactory().getCountWithFilter(wrapIn);

				// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier_company_forDay.copy(statisticList);

				// 对查询的列表进行排序
				result.setCount(total);
				result.setData(wraps);

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的StatisticCompanyForDay,上一页.", response = WrapInFilterStatisticCompanyForDay.class, request = JsonElement.class)
	@PUT
	@Path("filter/companyDay/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listStatisticCompanyForDayPrevPageWithFilter(@Context HttpServletRequest request,
			@PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAttendanceStatisticCompanyForDay>> result = new ActionResult<>();
		List<WrapOutAttendanceStatisticCompanyForDay> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<StatisticCompanyForDay> statisticList = null;
		WrapInFilterStatisticCompanyForDay wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterStatisticCompanyForDay.class );
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

				// 查询出ID对应的记录的sequence
				Object sequence = null;
				logger.debug("传入的ID=" + id);
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					logger.debug("第一页查询，没有id传入");
				} else {
					if (!StringUtils.equalsIgnoreCase(id, HttpAttribute.x_empty_symbol)) {
						sequence = PropertyUtils.getProperty(
								emc.find(id, StatisticCompanyForDay.class ), "sequence");
					}
				}
				
				//将下级组织的数据纳入组织统计数据查询范围
				List<String> organizationNameList = getDepartmentNameList(wrapIn.getCompanyName(), wrapIn.getOrganizationName());			
				wrapIn.setOrganizationName(organizationNameList);
				
				// 从数据库中查询符合条件的一页数据对象
				statisticList = business.getStatisticCompanyForDayFactory().listIdsPrevWithFilter(id, count, sequence, wrapIn);

				// 从数据库中查询符合条件的对象总数
				total = business.getStatisticCompanyForDayFactory().getCountWithFilter(wrapIn);

				// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				wraps = wrapout_copier_company_forDay.copy(statisticList);

				result.setCount(total);
				result.setData(wraps);

			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	// 根据部门递归查询下级部门
	private List<String> getDepartmentNameList(String departmentName, List<String> organizationNameList) throws Exception {
		if (organizationNameList == null) {
			organizationNameList = new ArrayList<String>();
		}
		if (departmentName != null && !organizationNameList.contains(departmentName.trim())) {
			organizationNameList.add(departmentName.trim());
			
			// 查询该部门的下级部门
			List<WrapDepartment> departmentList = null;
			Business business = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();) {
				business = new Business(emc);
				// 对查询的department进行解析，如果有下级部门的，全部解析出来
				departmentList = business.organization().department().listSubNested(departmentName);
				if (departmentList != null && departmentList.size() > 0) {
					for (WrapDepartment department : departmentList) {
						getDepartmentNameList(department.getName(), organizationNameList);
					}
				}
			} catch (Exception e) {
				throw e;
			}
		}		
		return organizationNameList;
	}

	// 根据公司递归查询下级公司经及公司的顶级部门
	private List<String> getTopDepartmentNameList( String companyName, List<String> organizationNameList ) throws Exception {
		if (organizationNameList == null) {
			organizationNameList = new ArrayList<String>();
		}
		if ( companyName!= null && !organizationNameList.contains( companyName.trim() )) {
			organizationNameList.add( companyName.trim() );
			
			// 查询该部门的下级部门
			List<WrapCompany> companyList = null;
			List<WrapDepartment> departmentList = null;
			Business business = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();) {
				business = new Business(emc);
				//查询所有的Top部门
				departmentList = business.organization().department().listTopWithCompany( companyName );
				if( departmentList != null && departmentList.size() > 0 ){
					//logger.debug("根据公司名称["+companyName+"]查询到"+departmentList.size()+"个顶级部门。");
					for( WrapDepartment department : departmentList ){
						if( organizationNameList.contains( department.getName() )){
							organizationNameList.add(department.getName());
						}
						getDepartmentNameList(department.getName(), organizationNameList);
					}
				}else{
					//logger.debug("根据公司名称["+companyName+"]未查询到任何顶级部门。");
				}
				
				// 对查询的company进行解析，如果有下级公司的，全部解析出来
				companyList = business.organization().company().listSubNested(companyName);
				if (companyList != null && companyList.size() > 0) {
					//logger.debug("根据公司名称["+companyName+"]查询到"+companyList.size()+"个子公司。");
					for (WrapCompany company : companyList) {
						if( organizationNameList.contains( company.getName() )){
							organizationNameList.add(company.getName());
						}
						getTopDepartmentNameList( company.getName(), organizationNameList);
					}
				}else{
					//logger.debug("根据公司名称["+companyName+"]未查询到任何子公司。");
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return organizationNameList;
	}

	/**
	 * 根据List<String> companyNameList, List<String> organizationNameList
	 * 查询所有符合查询范围所部门以及下级组织名称列表
	 * @param companyNameList
	 * @param organizationNameList
	 * @return
	 * @throws Exception 
	 */
	private List<String> getDepartmentNameList( List<String> companyNameList, List<String> organizationNameList ) throws Exception{
		
		if( organizationNameList == null ){
			organizationNameList = new ArrayList<String>();
		}
		//先查询公司所有的下属部门，全部加入List中
		//对查询的department进行解析，如果有下级部门的，全部解析出来
		if( companyNameList != null && companyNameList.size() > 0 ){
			for( String companyName : companyNameList ){
				//再递归查询所有的下级公司以及所有部门
				getTopDepartmentNameList( companyName, organizationNameList);
			}
		}
		//对查询的department进行解析，如果有下级部门的，全部解析出来
		if( organizationNameList != null && organizationNameList.size() > 0 ){
			for( String departmentName : organizationNameList ){
				getDepartmentNameList( departmentName, organizationNameList);
			}
		}
		return organizationNameList;
	}
}