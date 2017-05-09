package com.x.attendance.assemble.control.jaxrs.attendanceadmin;

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
import com.x.attendance.assemble.control.service.AttendanceAdminServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceAdmin;
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


@Path("attendanceadmin")
public class AttendanceAdminAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AttendanceAdminAction.class );
	
	private BeanCopyTools<WrapInAttendanceAdmin, AttendanceAdmin> wrapin_copier = BeanCopyToolsBuilder.create( WrapInAttendanceAdmin.class, AttendanceAdmin.class, null, WrapInAttendanceAdmin.Excludes );
	private BeanCopyTools<AttendanceAdmin, WrapOutAttendanceAdmin> wrapout_copier = BeanCopyToolsBuilder.create( AttendanceAdmin.class, WrapOutAttendanceAdmin.class, null, WrapOutAttendanceAdmin.Excludes);
	private AttendanceAdminServiceAdv attendanceAdminServiceAdv = new AttendanceAdminServiceAdv();
	private UserManagerService userManagerService = new UserManagerService();
	
	@HttpMethodDescribe(value = "获取所有AttendanceAdmin列表", response = WrapOutAttendanceAdmin.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAttendanceAdmin( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAttendanceAdmin>> result = new ActionResult<>();
		List<WrapOutAttendanceAdmin> wraps = null;
		List<AttendanceAdmin> attendanceAdminList = null;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( check ){
			try {
				attendanceAdminList = attendanceAdminServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.error( new AttendanceAdminListAllException(), effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( attendanceAdminList != null && !attendanceAdminList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( attendanceAdminList );
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceAdminWrapCopyException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取指定的AttendanceAdmin对象.", response = WrapOutAttendanceAdmin.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutAttendanceAdmin> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		WrapOutAttendanceAdmin wrap = null;
		AttendanceAdmin attendanceAdmin = null;
		Boolean check = true;
		if( check ){
			try {
				attendanceAdmin = attendanceAdminServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				Exception exception = new AttendanceAdminQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if (attendanceAdmin != null) {
				try {
					wrap = wrapout_copier.copy( attendanceAdmin );
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					Exception exception = new AttendanceAdminWrapCopyException( e );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceAdmin对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInAttendanceAdmin wrapIn = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		AttendanceAdmin attendanceAdmin = null;
		String companyName = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAttendanceAdmin.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if( check ){
			if( wrapIn.getOrganizationName() == null  || wrapIn.getOrganizationName().isEmpty() ){
				try {
					companyName = userManagerService.getCompanyNameByEmployeeName( currentPerson.getName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new GetCurrentPersonCompanyNameException( e, currentPerson.getName() );
					result.error( exception );
					logger.error( e, currentPerson, request, null);
				}
				wrapIn.setOrganizationName( companyName );
			}
		}
		if( check ){
			try {
				attendanceAdmin = new AttendanceAdmin();
				wrapin_copier.copy( wrapIn, attendanceAdmin );
				if(  wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
					attendanceAdmin.setId( wrapIn.getId() );
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new AttendanceAdminWrapCopyException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				attendanceAdmin = attendanceAdminServiceAdv.save( attendanceAdmin );
				result.setData( new WrapOutId( attendanceAdmin.getId() ) );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new AttendanceAdminSaveException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceAdminAttendanceAdmin对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response delete( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
        if( check ){
        	if( id == null || id.isEmpty() || "(0)".equals( id )){
        		check = false;
        		result.error( new Exception("传入的id为空，或者不合法，无法查询数据。") );
        	}
        }
        if( check ){
        	try {
        		attendanceAdminServiceAdv.delete( id );
        		result.setData( new WrapOutId(id) );
    		} catch (Exception e) {
    			check = false;
    			Exception exception = new AttendanceAdminDeleteException( e, id );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
    		}
        }
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}