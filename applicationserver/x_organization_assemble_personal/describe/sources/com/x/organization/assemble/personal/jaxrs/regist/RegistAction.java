package com.x.organization.assemble.personal.jaxrs.regist;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.http.WrapOutInteger;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.personal.wrapin.WrapInRegist;

@Path("regist")
@JaxrsDescribe("注册")
public class RegistAction extends StandardJaxrsAction {

	@JaxrsMethodDescribe(value = "当前允许的注册模式,disable,captcha,code", action = ActionMode.class)
	@GET
	@Path("mode")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response mode(@Context HttpServletRequest request) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		try {
			result = new ActionMode().execute();
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "生成一个captcha", action = ActionCaptcha.class)
	@GET
	@Path("captcha/width/{width}/height/{height}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response captcha(@Context HttpServletRequest request, @PathParam("width") Integer width,
			@PathParam("height") Integer height) {
		ActionResult<ActionCaptcha.Wo> result = new ActionResult<>();
		try {
			result = new ActionCaptcha().execute(width, height);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "生成一个code", action = ActionCode.class)
	@GET
	@Path("code/mobile/{mobile}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response code(@Context HttpServletRequest request, @PathParam("mobile") String mobile) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			result = new ActionCode().execute(mobile);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "校验mobile是否已经存在", action = ActionCheckMobile.class)
	@GET
	@Path("check/mobile/{mobile}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkMobile(@Context HttpServletRequest request, @PathParam("mobile") String mobile) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			result = new ActionCheckMobile().execute(mobile);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "校验name是否已经存在", action = ActionCheckName.class)
	@GET
	@Path("check/name/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkName(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			result = new ActionCheckName().execute(name);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@JaxrsMethodDescribe(value = "校验password密码等级.", action = ActionCheckPassword.class)
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

	@JaxrsMethodDescribe(value = "注册人员", action = ActionCreate.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest request, WrapInRegist wrapIn) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		try {
			result = new ActionCreate().execute(wrapIn);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}