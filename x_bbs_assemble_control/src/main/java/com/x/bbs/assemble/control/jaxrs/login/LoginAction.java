package com.x.bbs.assemble.control.jaxrs.login;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;


@Path( "login" )
public class LoginAction extends StandardJaxrsAction{

	private UserManagerService userManagerService = new UserManagerService();
	private BBSOperationRecordService BBSOperationRecordService = new BBSOperationRecordService();
	
	@HttpMethodDescribe(value = "用户进入系统，获取并且更新用户权限角色信息.", request = WrapInLoginInfo.class, response = RoleAndPermission.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(@Context HttpServletRequest request, WrapInLoginInfo wrapIn ) {
		ActionResult<RoleAndPermission> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson( request );
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		if( "anonymous".equalsIgnoreCase( currentPerson.getTokenType().name() )){
			try {
				BBSOperationRecordService.loginOperation( "anonymous", hostIp, hostName );
				result.setUserMessage("登录成功！");
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统根据用户获取权限信息时发生异常！" );
			}
		}else{
			try {
				BBSOperationRecordService.loginOperation( currentPerson.getName(), hostIp, hostName );
				RoleAndPermission roleAndPermission = userManagerService.getUserRoleAndPermissionForLogin( currentPerson.getName() );			
				result.setData( roleAndPermission );
				result.setUserMessage("登录成功！");
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统根据用户获取权限信息时发生异常！" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
}
