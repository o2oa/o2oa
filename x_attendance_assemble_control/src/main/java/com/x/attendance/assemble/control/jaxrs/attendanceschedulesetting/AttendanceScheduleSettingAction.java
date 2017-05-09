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

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.service.AttendanceScheduleSettingServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
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
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceScheduleSetting> wraps = null;
		List<AttendanceScheduleSetting> attendanceScheduleSettingList = null;
		Boolean check = true;
		
		if( check ){
			try {
				attendanceScheduleSettingList = attendanceScheduleSettingServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceScheduleListAllException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check && attendanceScheduleSettingList != null ){
			try {
				wraps = wrapout_copier.copy( attendanceScheduleSettingList );
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceScheduleWrapOutException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
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
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceScheduleSetting> wraps = null;
		List<String> ids = null;
		List<AttendanceScheduleSetting> attendanceScheduleSettingList = null;
		Boolean check = true;
		
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new AttendanceScheduleNameEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				ids = attendanceScheduleSettingServiceAdv.listByDepartmentName( name );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceScheduleListByDepartmentException( e, name );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check && ids != null && !ids.isEmpty() ){
			try {
				attendanceScheduleSettingList = attendanceScheduleSettingServiceAdv.list( ids );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceScheduleListByIdsException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check && attendanceScheduleSettingList != null ){
			try {
				wraps = wrapout_copier.copy( attendanceScheduleSettingList );
				result.setData(wraps);
			} catch ( Exception e ) {
				check = false;
				Exception exception = new AttendanceScheduleWrapOutException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
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
		EffectivePerson effectivePerson = this.effectivePerson(request);
		List<WrapOutAttendanceScheduleSetting> wraps = null;
		List<String> ids = null;
		List<AttendanceScheduleSetting> attendanceScheduleSettingList = null;
		Boolean check = true;
		
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new AttendanceScheduleNameEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				ids = attendanceScheduleSettingServiceAdv.listByCompanyName( name );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceScheduleListByCompanyException( e, name );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check && ids != null && !ids.isEmpty() ){
			try {
				attendanceScheduleSettingList = attendanceScheduleSettingServiceAdv.list( ids );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceScheduleListByIdsException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check && attendanceScheduleSettingList != null ){
			try {
				wraps = wrapout_copier.copy( attendanceScheduleSettingList );
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceScheduleWrapOutException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
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
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutAttendanceScheduleSetting wrap = null;
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new AttendanceScheduleIdEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceScheduleSetting = attendanceScheduleSettingServiceAdv.get( id );
				if( attendanceScheduleSetting == null ){
					check = false;
					Exception exception = new AttendanceScheduleNotExistsException( id );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceScheduleGetByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				wrap = wrapout_copier.copy( attendanceScheduleSetting );
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceScheduleWrapOutException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceScheduleSetting系统设置对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInAttendanceScheduleSetting wrapIn = null;
		WrapOutId wrapOutId = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapCompany company = null;
		WrapDepartment department = null;
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		String companyName = null;
		String identity = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceScheduleSetting.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
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
						Exception exception = new GetCompanyNameByUserIdentityException( e, identity );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
				if( companyName == null || companyName.isEmpty() ){
					try {
						companyName = userManagerService.getCompanyNameByEmployeeName( effectivePerson.getName() );
					} catch (Exception e) {
						check = false;
						Exception exception = new GetCompanyNameByUserIdentityException( e, effectivePerson.getName() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
				if( companyName == null || companyName.isEmpty() ){
					check = false;
					Exception exception = new CanNotFindCompanyWithPersonException( effectivePerson.getName() );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}else{
					wrapIn.setOrganizationName( companyName );
				}
			}else{
				try {
					department = userManagerService.getDepartmentByName( wrapIn.getOrganizationName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new GetDepartmentWithNameException( e, wrapIn.getOrganizationName() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
				if( department == null ){
					try {
						company = userManagerService.getCompanyByName( wrapIn.getOrganizationName() );
					} catch (Exception e) {
						check = false;
						Exception exception = new GetCompanyWithNameException( e, wrapIn.getOrganizationName() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					if( company != null ){
						wrapIn.setCompanyName( company.getName() );
					}else{
						check = false;
						Exception exception = new CanNotFindCompanyWithOrganNameException( wrapIn.getOrganizationName() );
						result.error( exception );
						//logger.error( e, effectivePerson, request, null);
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
				Exception exception = new AttendanceScheduleWrapOutException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceScheduleSetting = attendanceScheduleSettingServiceAdv.save( attendanceScheduleSetting );
				wrapOutId = new WrapOutId( attendanceScheduleSetting.getId() );
				result.setData( wrapOutId );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceScheduleSaveException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceScheduleSetting系统设置对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutId wrapOutId = null;
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new AttendanceScheduleIdEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceScheduleSetting = attendanceScheduleSettingServiceAdv.get( id );
				if( attendanceScheduleSetting == null ){
					check = false;
					Exception exception = new AttendanceScheduleNotExistsException( id );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new GetAttendanceScheduleByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceScheduleSettingServiceAdv.delete(id);
				wrapOutId = new WrapOutId( id );
				result.setData( wrapOutId );
			} catch (Exception e) {
				check = false;
				Exception exception = new AttendanceScheduleDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}