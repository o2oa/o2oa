package com.x.bbs.assemble.control.jaxrs.login;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.bbs.assemble.control.jaxrs.login.exception.InsufficientPermissionsException;
import com.x.bbs.assemble.control.jaxrs.login.exception.UserLoginException;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;


@Path( "login" )
public class LoginAction extends StandardJaxrsAction{

	private Logger logger = LoggerFactory.getLogger( LoginAction.class );
	private UserManagerService userManagerService = new UserManagerService();
	private BBSOperationRecordService BBSOperationRecordService = new BBSOperationRecordService();
	
	@HttpMethodDescribe(value = "用户进入系统，获取并且更新用户权限角色信息.", request = WrapInLoginInfo.class, response = RoleAndPermission.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(@Context HttpServletRequest request, WrapInLoginInfo wrapIn ) {
		ActionResult<RoleAndPermission> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson( request );
		Boolean isBBSSystemAdmin = false;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		
		if( "anonymous".equalsIgnoreCase( currentPerson.getTokenType().name() )){
			try {
				BBSOperationRecordService.loginOperation( "anonymous", hostIp, hostName );
				result.setData( new RoleAndPermission() );
			} catch (Exception e) {
				Exception exception = new UserLoginException( e, "anonymous" );
				result.error( exception );
				logger.error(exception);
			}
		}else{
			RoleAndPermission roleAndPermission = null;
			try {
				BBSOperationRecordService.loginOperation( currentPerson.getName(), hostIp, hostName );
				roleAndPermission = userManagerService.getUserRoleAndPermissionForLogin( currentPerson.getName() );	
			} catch (Exception e) {
				Exception exception = new UserLoginException( e, currentPerson.getName() );
				result.error( exception );
				logger.error(exception);
			}
			try {
				isBBSSystemAdmin = userManagerService.isHasRole( currentPerson.getName(), "BBSSystemAdmin");
			} catch (Exception e ) {
				Exception exception = new InsufficientPermissionsException( currentPerson.getName(), "BBSSystemAdmin" );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
			if( roleAndPermission != null ){
				roleAndPermission.setIsBBSSystemAdmin(isBBSSystemAdmin);
			}
			result.setData( roleAndPermission );
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
}
