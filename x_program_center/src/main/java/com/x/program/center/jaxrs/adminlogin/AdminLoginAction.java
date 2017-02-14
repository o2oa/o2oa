package com.x.program.center.jaxrs.adminlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;

@Path("adminlogin")
public class AdminLoginAction extends AbstractJaxrsAction {

	@HttpMethodDescribe(value = "管理员登录.credential=xxxx,password=xxxx", response = WrapOutAdminLogin.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(@Context HttpServletRequest request, @Context HttpServletResponse response,
			WrapInAdminLogin wrapIn) {
		ActionResult<WrapOutAdminLogin> result = new ActionResult<>();
		WrapOutAdminLogin wrap = new WrapOutAdminLogin();
		try {
			if (StringUtils.isEmpty(wrapIn.getPassword())) {
				throw new Exception("password can not be empty.");
			}
			wrap = new ActionLogin().execute(request, response, wrapIn.getCredential(), wrapIn.getPassword());

			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "注销.", response = WrapOutAdminLogin.class)
	@DELETE
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response logout(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		ActionResult<WrapOutAdminLogin> result = new ActionResult<>();
		WrapOutAdminLogin wrap = new WrapOutAdminLogin();
		try {
			wrap = new ActionLogout().execute(request, response);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}