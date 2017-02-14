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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.control.jaxrs.WrapOutMessage;
import com.x.attendance.assemble.control.service.AttendanceAdminServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceAdmin;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;


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
		Boolean check = true;
		if( check ){
			try {
				attendanceAdminList = attendanceAdminServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage("系统在获取所有管理员信息时发生异常！");
				logger.error( "system list all attendance admin info got an exception.", e );
			}
		}
		if( check ){
			if( attendanceAdminList != null && !attendanceAdminList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( attendanceAdminList );
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage("系统在转换所有管理员信息为输出对象时发生异常！");
					logger.error( "system copy all attendance admin info to wrap out got an exception.", e );
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
		WrapOutAttendanceAdmin wrap = null;
		AttendanceAdmin attendanceAdmin = null;
		Boolean check = true;
		if( check ){
			try {
				attendanceAdmin = attendanceAdminServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage("系统在根据ID获取管理员信息时发生异常！");
				logger.error( "system get attendance admin info by id got an exception.", e );
			}
		}
		if( check ){
			if (attendanceAdmin != null) {
				try {
					wrap = wrapout_copier.copy( attendanceAdmin );
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage("系统在转换管理员信息为输出对象时发生异常！");
					logger.error( "system copy attendance admin info to wrap out got an exception.", e );
				}
				
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新AttendanceAdmin对象.", request = WrapInAttendanceAdmin.class, response = WrapOutMessage.class)
	@POST
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response post(@Context HttpServletRequest request, WrapInAttendanceAdmin wrapIn) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		WrapOutMessage wrapOutMessage = new WrapOutMessage();
		EffectivePerson currentPerson = this.effectivePerson(request);
		AttendanceAdmin attendanceAdmin = null;
		String companyName = null;
		Boolean check = true;
		
		if( wrapIn == null){
			check = false;
			result.error( new Exception("系统未获取到传入的参数，无法继续保存信息！") );
			result.setUserMessage("系统未获取到传入的参数，无法继续保存信息！");
			logger.error( "wrapIn object is null." );
		}		
		if( check ){
			if( wrapIn.getOrganizationName() == null  || wrapIn.getOrganizationName().isEmpty() ){
				try {
					companyName = userManagerService.getCompanyNameByEmployeeName( currentPerson.getName() );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage("系统获取登录用户所属公司时发生异常！");
					logger.error( "system get company name by user name got an exception{'name':'"+ currentPerson.getName() +"'}." );
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
			} catch (Exception e) {
				check = false;
				wrapOutMessage.setStatus( "ERROR");
				wrapOutMessage.setMessage( "系统在COPY传入信息到考勤员对象时发生异常." );
				wrapOutMessage.setExceptionMessage( e.getMessage() );
				logger.error( "system copy wrap in to attendance admin object got an exception.", e );
			}
		}
		if( check ){
			try {
				attendanceAdmin = attendanceAdminServiceAdv.save( attendanceAdmin );
				wrapOutMessage.setStatus( "SUCCESS");
				wrapOutMessage.setMessage( attendanceAdmin.getId() );
			} catch (Exception e) {
				check = false;
				wrapOutMessage.setStatus( "ERROR");
				wrapOutMessage.setMessage( "保存AttendanceAdmin过程中发生异常." );
				wrapOutMessage.setExceptionMessage( e.getMessage() );
				logger.error( "system save attendance admin info got an exception.", e );
			}
		}
		result.setData( wrapOutMessage );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除AttendanceAdminAttendanceAdmin对象.", response = WrapOutMessage.class)
	@DELETE
	@Path("{id}")
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response delete( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutMessage> result = new ActionResult<>();
		WrapOutMessage wrapOutMessage = new WrapOutMessage();
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
    			wrapOutMessage.setStatus("SUCCESS");
    			wrapOutMessage.setMessage( "成功删除AttendanceAdmin信息。id=" + id );
    		} catch (Exception e) {
    			wrapOutMessage.setStatus("ERROR");
    			wrapOutMessage.setMessage( "删除AttendanceAdmin过程中发生异常。" );
    			wrapOutMessage.setExceptionMessage( e.getMessage() );
    			logger.error( "system delete attendance admin info got an exception.", e );
    		}
        }
        result.setData( wrapOutMessage );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}