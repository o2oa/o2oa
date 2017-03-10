package com.x.organization.assemble.personal.jaxrs.reset;

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

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.WrapOutInteger;
import com.x.base.core.http.annotation.HttpMethodDescribe;

@Path("reset")
public class ResetAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "验证人员是否存在.", response = WrapOutBoolean.class)
	@GET
	@Path("check/credential/{credential}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkCredential(@Context HttpServletRequest request, @PathParam("credential") String credential) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			result = new ActionCheckCredential().execute(credential);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "验证密码强度等级.", response = WrapOutInteger.class)
	@GET
	@Path("check/password/{password}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkPassword(@Context HttpServletRequest request, @PathParam("password") String password) {
		ActionResult<WrapOutInteger> result = new ActionResult<>();
		try {
			result = new ActionCheckPassword().execute(password);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取短信验证码", response = WrapOutBoolean.class)
	@GET
	@Path("code/credential/{credential}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response code(@Context HttpServletRequest request, @PathParam("credential") String credential) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			result = new ActionCode().execute(credential);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "重置密码.", request = WrapInReset.class, response = WrapOutBoolean.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response reset(@Context HttpServletRequest request, WrapInReset wrapIn) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			result = new ActionReset().execute(wrapIn);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}