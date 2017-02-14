package com.x.bbs.assemble.control.jaxrs.login;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;


@Path( "logout" )
public class LogoutAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( LogoutAction.class );
	private UserManagerService userManagerService = new UserManagerService();
	
	@HttpMethodDescribe(value = "退出系统.", request = WrapInLoginInfo.class, response = RoleAndPermission.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(@Context HttpServletRequest request, WrapInLoginInfo wrapIn ) {
		ActionResult<RoleAndPermission> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson( request );
		try {
			userManagerService.logout( currentPerson.getName() );
		} catch (Exception e) {
			logger.error( "system logout got an exception", e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
