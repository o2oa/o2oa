package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.instrument.assemble.express.wrap.WrapCaptcha;
import com.x.organization.assemble.authentication.wrap.in.WrapInAuthentication;
import com.x.organization.assemble.authentication.wrap.out.WrapOutAuthentication;
import com.x.organization.assemble.authentication.wrap.out.WrapOutBind;
import com.x.organization.assemble.authentication.wrap.out.WrapOutInitManagerCredential;

@Path("authentication")
public class AuthenticationAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AuthenticationAction.class);

	@HttpMethodDescribe(value = "获取当前可用的登录模式.", response = WrapOutMap.class)
	@GET
	@Path("mode")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response mode(@Context HttpServletRequest request) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionMode().execute();
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "检查用户名是否存在.", response = WrapOutBoolean.class)
	@GET
	@Path("check/credential/{credential}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkCredential(@Context HttpServletRequest request, @PathParam("credential") String credential) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCheckCredential().execute(credential);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "用户登录.credential=xxxx,password=xxxx", response = WrapOutAuthentication.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(@Context HttpServletRequest request, @Context HttpServletResponse response,
			WrapInAuthentication wrapIn) {
		ActionResult<WrapOutAuthentication> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionLogin().execute(request, response, wrapIn);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "用户注销.", response = WrapOutAuthentication.class)
	@DELETE
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response logout(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		ActionResult<WrapOutAuthentication> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionLogout().execute(request, response);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取当前用户,如果是未登录用户返回anonymous", response = WrapOutAuthentication.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response who(@Context HttpServletRequest request) {
		ActionResult<WrapOutAuthentication> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionWho().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "用户登录.credential=xxxx,password=xxxx,使用图片验证码验证.", response = WrapOutAuthentication.class)
	@POST
	@Path("captcha")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response captchaLogin(@Context HttpServletRequest request, @Context HttpServletResponse response,
			WrapInAuthentication wrapIn) {
		ActionResult<WrapOutAuthentication> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCaptchaLogin().execute(request, response, wrapIn);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取图片验证码.", response = WrapCaptcha.class)
	@GET
	@Path("captcha/width/{width}/height/{height}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response captcha(@Context HttpServletRequest request, @PathParam("width") Integer width,
			@PathParam("height") Integer height) {
		ActionResult<WrapCaptcha> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCaptcha().execute(width, height);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "用户登录.credential=xxxx,codeAnswer=xxxx,使用短信验证码登录.", response = WrapOutAuthentication.class)
	@POST
	@Path("code")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response codeLogin(@Context HttpServletRequest request, @Context HttpServletResponse response,
			WrapInAuthentication wrapIn) {
		ActionResult<WrapOutAuthentication> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCodeLogin().execute(request, response, wrapIn);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取短信验证码.")
	@GET
	@Path("code/credential/{credential}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response code(@Context HttpServletRequest request, @PathParam("credential") String credential) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCode().execute(credential);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取登录二维码.")
	@GET
	@Path("bind")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response bind(@Context HttpServletRequest request) {
		ActionResult<WrapOutBind> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionBind().execute();
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "通过二维码进行登录.")
	@GET
	@Path("bind/meta/{meta}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response bindLogin(@Context HttpServletRequest request, @Context HttpServletResponse response,
			@PathParam("meta") String meta) {
		ActionResult<WrapOutAuthentication> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionBindLogin().execute(request, response, meta);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "二维码用户录入.")
	@POST
	@Path("bind/meta/{meta}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response bindMeta(@Context HttpServletRequest request, @PathParam("meta") String meta) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionBindMeta().execute(effectivePerson, meta);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}