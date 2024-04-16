package com.x.organization.assemble.personal.jaxrs.reset;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("reset")
@JaxrsDescribe("重置操作")
public class ResetAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ResetAction.class);

	@JaxrsMethodDescribe(value = "验证人员是否存在.", action = ActionCheckCredential.class)
	@GET
	@Path("check/credential/{credential}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void checkCredential(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("用户标识") @PathParam("credential") String credential) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCheckCredential().execute(credential);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "验证密码强度等级.", action = ActionCheckPassword.class)
	@GET
	@Path("check/password/{password}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void checkPassword(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("密码") @PathParam("password") String password) {
		ActionResult<ActionCheckPassword.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCheckPassword().execute(password);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取短信验证码", action = ActionCode.class)
	@GET
	@Path("code/credential/{credential}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void code(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("用户标识") @PathParam("credential") String credential) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCode().execute(credential);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "重置密码.", action = ActionReset.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void reset(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionReset.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionReset().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "重置密码MockPutToPost.", action = ActionReset.class)
	@POST
	@Path("mockputtopost")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void resetMockPutToPost(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionReset.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionReset().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "匿名更新个人的密码.", action = ActionSetPasswordAnonymous.class)
	@POST
	@Path("password/anonymous")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setPasswordAnonymous(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSetPasswordAnonymous.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetPasswordAnonymous().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

}
