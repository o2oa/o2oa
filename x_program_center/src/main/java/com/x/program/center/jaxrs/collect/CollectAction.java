package com.x.program.center.jaxrs.collect;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.WrapOutInteger;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.program.center.jaxrs.collect.wrapin.WrapInCollect;
import com.x.program.center.jaxrs.collect.wrapout.WrapOutCollect;

@Path("collect")
public class CollectAction extends AbstractJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(CollectAction.class);

	@HttpMethodDescribe(value = "向 collect 服务器转发一个短信验证码申请.", response = WrapOutBoolean.class)
	@GET
	@Path("code/mobile/{mobile}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registCode(@Context HttpServletRequest request, @PathParam("mobile") String mobile) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCode().execute(effectivePerson, mobile);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "更新Collect配置.", response = WrapOutBoolean.class)
	public Response update(@Context HttpServletRequest request, WrapInCollect wrapIn) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdate().execute(effectivePerson, wrapIn);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "获取collect配置", response = WrapOutCollect.class)
	public Response get(@Context HttpServletRequest request) {
		ActionResult<WrapOutCollect> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute();
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@GET
	@Path("connect")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "测试是否可以连接到collect服务器", response = WrapOutBoolean.class)
	public Response connect(@Context HttpServletRequest request) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionConnect().execute();
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@GET
	@Path("validate")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "测试collect.json中的用户名密码是否正确.", response = WrapOutBoolean.class)
	public Response validate(@Context HttpServletRequest request) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionValidate().execute();
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@PUT
	@Path("validate/direct")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "直接用输入的用户名密码进行验证.", response = WrapOutBoolean.class)
	public Response validateDirect(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionValidateDirect().execute(jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@PUT
	@Path("validate/codeanswer")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "验证短信验证码是否正确.", response = WrapOutBoolean.class)
	public Response validateCodeAnswer(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionValidateCodeAnswer().execute(jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@PUT
	@Path("validate/password")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "验证密码复杂程度.", response = WrapOutInteger.class)
	public Response validatePassword(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutInteger> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionValidatePassword().execute(jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@GET
	@Path("name/{name}/exist")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "测试用户名是否可用.", response = WrapOutBoolean.class)
	public Response exist(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionExist().execute(name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@GET
	@Path("controllermobile/name/{name}/mobile/{mobile}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "测试手机号码是否在管理手机列表中", response = WrapOutBoolean.class)
	public Response controllerMobile(@Context HttpServletRequest request, @PathParam("name") String name,
			@PathParam("mobile") String mobile) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionControllerMobile().execute(name, mobile);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "在collect服务器上注册一个新的unit.", request = WrapInCollect.class, response = WrapOutBoolean.class)
	public Response regist(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionRegist().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@PUT
	@Path("resetpassword")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "重置在collect服务器上的的密码.", request = WrapInCollect.class, response = WrapOutBoolean.class)
	public Response resetPassword(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionResetPassword().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}