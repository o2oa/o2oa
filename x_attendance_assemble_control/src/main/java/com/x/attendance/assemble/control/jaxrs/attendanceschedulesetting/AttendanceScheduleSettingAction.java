package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.control.jaxrs.WrapOutMessage;
import com.x.attendance.assemble.control.service.AttendanceScheduleSettingServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;


@Path("attendanceschedulesetting")
public class AttendanceScheduleSettingAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceScheduleSettingAction.class );
	private UserManagerService userManagerService = new UserManagerService();
	private BeanCopyTools<WrapInAttendanceScheduleSetting, AttendanceScheduleSetting> wrapin_copier = BeanCopyToolsBuilder.create( WrapInAttendanceScheduleSetting.class, AttendanceScheduleSetting.class, null, WrapInAttendanceScheduleSetting.Excludes );
	private BeanCopyTools<AttendanceScheduleSetting, WrapOutAttendanceScheduleSetting> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceScheduleSetting.class, WrapOutAttendanceScheduleSetting.class, null, WrapOutAttendanceScheduleSetting.Excludes);
	private AttendanceScheduleSettingServiceAdv attendanceScheduleSettingServiceAdv = new AttendanceScheduleSettingServiceAdv();
	
	@HttpMethodDescribe(value = "获取所有系统配置列表", response = WrapOutAttendanceScheduleSetting.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAttendanceScheduleSetting(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAttendanceScheduleSetting>> result = new ActionResult<>();
		List<WrapOutAttendanceScheduleSetting> wraps = null;
		List<AttendanceScheduleSetting> attendanceScheduleSettingList = null;
		Boolean check = true;
		
		if( check ){
			try {
				attendanceScheduleSettingList = attendanceScheduleSettingServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统查询所有组织排班信息列表时发生异常。");
				logger.error( "system query all schedule setting list got an exception.", e);
			}
		}
		if( check && attendanceScheduleSettingList != null ){
			try {
				wraps = wrapout_copier.copy( attendanceScheduleSettingList );
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常。");
				logger.error( "system copy schedule setting list to wrap got an exception.", e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "按部门获取所有系统配置列表", response = WrapOutAttendanceScheduleSetting.class)
	@GET
	@Path("list/department/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAttendanceScheduleSettingByDepartment(@Context HttpServletRequest request, @PathParam("name") String name ) {
		ActionResult<List<WrapOutAttendanceScheduleSetting>> result = new ActionResult<>();
		List<WrapOutAttendanceScheduleSetting> wraps = null;
		List<String> ids = null;
		List<AttendanceScheduleSetting> attendanceScheduleSettingList = null;
		Boolean check = true;
		
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				result.error( new Exception("系统传入的参数name为空。") );
				result.setUserMessage("系统传入的参数name为空。");
			}
		}
		if( check ){
			try {
				ids = attendanceScheduleSettingServiceAdv.listByDepartmentName( name );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据部门名称查询指定组织排班信息列表时发生异常。");
				logger.error( "system query schedule setting id list with department name got an exception.name：" + name, e);
			}
		}
		if( check && ids != null && !ids.isEmpty() ){
			try {
				attendanceScheduleSettingList = attendanceScheduleSettingServiceAdv.list( ids );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据ID列表查询指定组织排班信息列表时发生异常。");
				logger.error( "system query schedule setting with ids got an exception.", e);
			}
		}
		if( check && attendanceScheduleSettingList != null ){
			try {
				wraps = wrapout_copier.copy( attendanceScheduleSettingList );
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常。");
				logger.error( "system copy schedule setting list to wrap got an exception.", e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "按公司获取所有系统配置列表", response = WrapOutAttendanceScheduleSetting.class)
	@GET
	@Path("list/company/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAttendanceScheduleSettingByCompany(@Context HttpServletRequest request, @PathParam("name") String name ) {
		ActionResult<List<WrapOutAttendanceScheduleSetting>> result = new ActionResult<>();
		List<WrapOutAttendanceScheduleSetting> wraps = null;
		List<String> ids = null;
		List<AttendanceScheduleSetting> attendanceScheduleSettingList = null;
		Boolean check = true;
		
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				result.error( new Exception("系统传入的参数name为空。") );
				result.setUserMessage("系统传入的参数name为空。");
			}
		}
		if( check ){
			try {
				ids = attendanceScheduleSettingServiceAdv.listByCompanyName( name );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据公司名称查询指定组织排班信息列表时发生异常。");
				logger.error( "system query schedule setting id list with company name got an exception.name：" + name, e);
			}
		}
		if( check && ids != null && !ids.isEmpty() ){
			try {
				attendanceScheduleSettingList = attendanceScheduleSettingServiceAdv.list( ids );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据ID列表查询指定组织排班信息列表时发生异常。");
				logger.error( "system query schedule setting with ids got an exception.", e);
			}
		}
		if( check && attendanceScheduleSettingList != null ){
			try {
				wraps = wrapout_copier.copy( attendanceScheduleSettingList );
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常。");
				logger.error( "system copy schedule setting list to wrap got an exception.", e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取AttendanceScheduleSetting对象.", response = WrapOutAttendanceScheduleSetting.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutAttendanceScheduleSetting> result = new ActionResult<>();
		WrapOutAttendanceScheduleSetting wrap = null;
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("系统传入的参数id为空。") );
				result.setUserMessage("系统传入的参数id为空。");
			}
		}
		if( check ){
			try {
				attendanceScheduleSetting = attendanceScheduleSettingServiceAdv.get( id );
				if( attendanceScheduleSetting == null ){
					check = false;
					result.error( new Exception("根据ID未能查询到任何信息。") );
					result.setUserMessage("根据ID未能查询到任何信息。");
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据ID查询指定组织排班信息列表时发生异常。");
				logger.error( "system query schedule setting with id got an exception.name：" + id, e);
			}
		}
		if( check ){
			try {
				wrap = wrapout_copier.copy( attendanceScheduleSetting );
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象时发生异常。");
				logger.error( "system copy schedule setting to wrap got an exception.", e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceScheduleSetting系统设置对象.", request = WrapInAttendanceScheduleSetting.class, response = WrapOutMessage.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInAttendanceScheduleSetting wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrapOutId = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		WrapCompany company = null;
		WrapDepartment department = null;
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		String companyName = null;
		String identity = null;
		Boolean check = true;
		
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("系统未获取到需要保存的数据，无法进行数据保存操作。") );
				result.setUserMessage( "系统未获取到需要保存的数据，无法进行数据保存操作。" );
			}
		}
		if( check ){
			identity = wrapIn.getIdentity();
			if( wrapIn.getOrganizationName() == null ){
				//未传入组织名称，使用专入的身份来获取组织信息，如果未传入身份，那么取当前登录用户的身份
				if( identity != null && !identity.isEmpty() ){
					try {
						companyName = userManagerService.getCompanyNameByIdentity(identity);
					} catch (Exception e) {
						check = false;
						result.error( e );
						result.setUserMessage( "系统未能根据参数，身份和登录人信息查询到公司信息，无法进行数据保存。" );
						logger.error( "system query company name by identity got an exception.identity:" + identity, e );
					}
				}
				if( companyName == null || companyName.isEmpty() ){
					try {
						companyName = userManagerService.getCompanyNameByEmployeeName( currentPerson.getName() );
					} catch (Exception e) {
						check = false;
						result.error( e );
						result.setUserMessage( "系统未能根据参数，身份和登录人信息查询到公司信息，无法进行数据保存。" );
						logger.error( "system query company name by employee name got an exception.employee:" + currentPerson.getName(), e );
					}
				}
				if( companyName == null || companyName.isEmpty() ){
					check = false;
					result.error( new Exception("系统未能根据参数，身份和登录人信息查询到公司信息，无法进行数据保存。") );
					result.setUserMessage( "系统未能根据参数，身份和登录人信息查询到公司信息，无法进行数据保存。" );
				}else{
					wrapIn.setOrganizationName( companyName );
				}
			}else{
				try {
					department = userManagerService.getDepartmentByName( wrapIn.getOrganizationName() );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统未能根据参数，身份和登录人信息查询到部门信息，无法进行数据保存。" );
					logger.error( "system query department name by parameter organization name got an exception.organization:" + wrapIn.getOrganizationName(), e );
				}
				if( department == null ){
					try {
						company = userManagerService.getCompanyByName( wrapIn.getOrganizationName() );
					} catch (Exception e) {
						check = false;
						result.error( e );
						result.setUserMessage( "系统未能根据参数，身份和登录人信息查询到部门信息，无法进行数据保存。" );
						logger.error( "system query company name by parameter organization name got an exception.organization:" + wrapIn.getOrganizationName(), e );
					}
					if( company != null ){
						wrapIn.setCompanyName( company.getName() );
					}else{
						check = false;
						result.error( new Exception("系统未能根据传入的组织名称查询到部门或者公司信息，无法进行数据保存。名称：" + wrapIn.getOrganizationName() ) );
						result.setUserMessage( "系统未能根据传入的组织名称查询到部门或者公司信息，无法进行数据保存。名称：" + wrapIn.getOrganizationName() );
					}
				}else{
					wrapIn.setOrganizationName( department.getName() );
					wrapIn.setCompanyName( department.getCompany() );
				}
			}
		}
		if( check ){
			attendanceScheduleSetting = new AttendanceScheduleSetting();
			try {
				wrapin_copier.copy( wrapIn, attendanceScheduleSetting );
				if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
					attendanceScheduleSetting.setId( wrapIn.getId() );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据传入的参数组织排班信息对象发生异常。" );
				logger.error( "system copy wrapin to attendanceScheduleSetting", e );
			}
		}
		if( check ){
			try {
				attendanceScheduleSetting = attendanceScheduleSettingServiceAdv.save( attendanceScheduleSetting );
				wrapOutId = new WrapOutId( attendanceScheduleSetting.getId() );
				result.setData( wrapOutId );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统保存排班信息对象发生异常。" );
				logger.error( "system save attendanceScheduleSetting got an exception", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceScheduleSetting系统设置对象.", response = WrapOutMessage.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrapOutId = null;
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到需要删除的排班配置信息数据ID。" ) );
				result.setUserMessage( "系统未获取到需要删除的排班配置信息数据ID。" );
			}
		}
		if( check ){
			try {
				attendanceScheduleSetting = attendanceScheduleSettingServiceAdv.get( id );
				if( attendanceScheduleSetting == null ){
					check = false;
					result.error( new Exception("根据ID未能查询到任何信息。") );
					result.setUserMessage("根据ID未能查询到任何信息。");
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据ID查询指定组织排班信息列表时发生异常。");
				logger.error( "system query schedule setting with id got an exception.name：" + id, e);
			}
		}
		if( check ){
			try {
				attendanceScheduleSettingServiceAdv.delete(id);
				wrapOutId = new WrapOutId( id );
				result.setData( wrapOutId );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统根据ID删除排班配置数据时发生异常。");
				logger.error( "system delete attendance schedule setting info got an exception.name：" + id, e);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}