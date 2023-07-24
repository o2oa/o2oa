package com.x.program.center.jaxrs.dingding;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

@Path("dingding")
@JaxrsDescribe("钉钉接口")
public class DingdingAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(DingdingAction.class);

	@JaxrsMethodDescribe(value = "发送一个拉入同步请求.", action = ActionSyncOrgnaizationCallback.class)
	@POST
	@Path("request/pull/sync")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void requestPullSync(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSyncOrgnaizationCallback.Wo> result = new ActionResult<>();
		try {
			result = new ActionSyncOrgnaizationCallback().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "立即同步.", action = ActionPullSync.class)
	@GET
	@Path("pull/sync")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void pullSync(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionPullSync.Wo> result = new ActionResult<>();
		try {
			result = new ActionPullSync().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "随机生成一个钉钉回调的EncodingAESKey.", action = ActionCallbackAESKey.class)
	@GET
	@Path("get/callback/aes")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void syncOrganizationCallbackEncodingAESKeyGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionCallbackAESKey.Wo> result = new ActionResult<>();
		try {
			result = new ActionCallbackAESKey().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "到钉钉注册回调地址", action = ActionSyncOrganizationCallbackUrlRegister.class)
	@GET
	@Path("sync/organization/register/callback/{enable}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void registerSyncOrgCallbackUrl(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("注册回调地址还是更新") @PathParam("enable") boolean enable) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSyncOrganizationCallbackUrlRegister.Wo> result = new ActionResult<>();
		try {
			result = new ActionSyncOrganizationCallbackUrlRegister().execute(effectivePerson, enable);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/**
	 * 钉钉回调用的post接口 需要返回固定格式的json字符串
	 * 
	 * @param asyncResponse
	 * @param request
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param jsonElement
	 */
	@JaxrsMethodDescribe(value = "接收钉钉通讯录变更回调的接口", action = ActionSyncOrganizationCallbackPost.class)
	@POST
	@Path("sync/organization/callback")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void syncOrganizationCallback(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("signature") @QueryParam("signature") String signature,
			@JaxrsParameterDescribe("timestamp") @QueryParam("timestamp") String timestamp,
			@JaxrsParameterDescribe("nonce") @QueryParam("nonce") String nonce, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			Map<String, String> json = new ActionSyncOrganizationCallbackPost().execute(effectivePerson, signature,
					timestamp, nonce, jsonElement);
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			asyncResponse.resume(Response.ok(gson.toJson(json)).build());
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			asyncResponse.resume(Response.serverError().entity("fail").build());
		}

//		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}