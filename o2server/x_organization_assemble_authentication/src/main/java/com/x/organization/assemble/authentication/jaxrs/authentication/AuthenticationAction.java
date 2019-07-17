package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.wrapin.WrapInAuthentication;

@Path("authentication")
@JaxrsDescribe("认证")
public class AuthenticationAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AuthenticationAction.class);

	@JaxrsMethodDescribe(value = "获取当前可用的登录模式.", action = ActionMode.class)
	@GET
	@Path("mode")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void mode(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionMode.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionMode().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "检查用户名是否存在.", action = ActionCheckCredential.class)
	@GET
	@Path("check/credential/{credential}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void checkCredential(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("凭证") @PathParam("credential") String credential) {
		ActionResult<ActionCheckCredential.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCheckCredential().execute(effectivePerson, credential);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "用户登录.credential=xxxx,password=xxxx", action = ActionLogin.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void login(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response, JsonElement jsonElement) {
		ActionResult<ActionLogin.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionLogin().execute(request, response, effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "用户注销.", action = ActionLogout.class)
	@DELETE
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void logout(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		ActionResult<ActionLogout.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionLogout().execute(request, response, effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取当前用户,如果是未登录用户返回anonymous", action = ActionWho.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void who(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionWho.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionWho().execute(request, effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "用户登录.credential=xxxx,password=xxxx,使用图片验证码验证.", action = ActionCaptchaLogin.class)
	@POST
	@Path("captcha")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void captchaLogin(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response, JsonElement jsonElement) {
		ActionResult<ActionCaptchaLogin.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCaptchaLogin().execute(request, response, effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取图片验证码.", action = ActionCaptcha.class)
	@GET
	@Path("captcha/width/{width}/height/{height}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void captcha(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("图片验证码宽度") @PathParam("width") Integer width,
			@JaxrsParameterDescribe("图片验证码高度") @PathParam("height") Integer height) {
		ActionResult<ActionCaptcha.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCaptcha().execute(effectivePerson, width, height);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "用户登录.credential=xxxx,codeAnswer=xxxx,使用短信验证码登录.", action = ActionCodeLogin.class)
	@POST
	@Path("code")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void codeLogin(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response, WrapInAuthentication wrapIn) {
		ActionResult<ActionCodeLogin.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCodeLogin().execute(request, response, effectivePerson, wrapIn);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取短信验证码.", action = ActionCode.class)
	@GET
	@Path("code/credential/{credential}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void code(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("凭证") @PathParam("credential") String credential) {
		ActionResult<ActionCode.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCode().execute(effectivePerson, credential);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "获取登录二维码.", action = ActionBind.class)
	@GET
	@Path("bind")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void bind(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionBind.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionBind().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "通过二维码进行登录.", action = ActionBindLogin.class)
	@GET
	@Path("bind/meta/{meta}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void bindLogin(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response, @JaxrsParameterDescribe("认证序列号") @PathParam("meta") String meta) {
		ActionResult<ActionBindLogin.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionBindLogin().execute(request, response, effectivePerson, meta);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "二维码用户录入.", action = ActionBindMeta.class)
	@POST
	@Path("bind/meta/{meta}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void bindMeta(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("认证序列号") @PathParam("meta") String meta) {
		ActionResult<ActionBindMeta.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionBindMeta().execute(effectivePerson, meta);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "oauth登录认证地址", action = ActionOauthList.class)
	@GET
	@Path("oauth/list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void oauthList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<ActionOauthList.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionOauthList().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "oauth登录认证地址", action = ActionOauthGet.class)
	@GET
	@Path("oauth/name/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void oauth(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("oauthClient名称") @PathParam("name") String name) {
		ActionResult<ActionOauthGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionOauthGet().execute(effectivePerson, name);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "oauth登录认证", action = ActionOauthLogin.class)
	@GET
	@Path("oauth/login/name/{name}/code/{code}/redirecturi/{redirectUri}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void oauthLogin(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@JaxrsParameterDescribe("oauthClient名称") @PathParam("name") String name,
			@JaxrsParameterDescribe("code") @PathParam("code") String code,
			@JaxrsParameterDescribe("redirectUri") @PathParam("redirectUri") String redirectUri) {
		ActionResult<ActionOauthLogin.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionOauthLogin().execute(request, response, effectivePerson, name, code, redirectUri);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "oauth账户绑定.", action = ActionOauthBind.class)
	@GET
	@Path("oauth/bind/name/{name}/code/{code}/redirecturi/{redirectUri}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void oauthBind(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@JaxrsParameterDescribe("oauthClient名称") @PathParam("name") String name,
			@JaxrsParameterDescribe("code") @PathParam("code") String code,
			@JaxrsParameterDescribe("redirectUri") @PathParam("redirectUri") String redirectUri) {
		ActionResult<ActionOauthBind.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionOauthBind().execute(request, response, effectivePerson, name, code, redirectUri);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}