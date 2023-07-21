package com.x.program.center.jaxrs.qiyeweixin;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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

@Path("qiyeweixin")
@JaxrsDescribe("企业微信接口")
public class QiyeweixinAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(QiyeweixinAction.class);

	@JaxrsMethodDescribe(value = "发送一个拉入同步请求.", action = ActionRequestPullSync.class)
	@POST
	@Path("request/pull/sync")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void requestPullSync(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionRequestPullSync.Wo> result = new ActionResult<>();
		try {
			result = new ActionRequestPullSync().execute(effectivePerson, jsonElement);
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

	@JaxrsMethodDescribe(value = "随机生成一个企业微信回调的EncodingAESKey.", action = ActionCallbackAESKey.class)
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

	@JaxrsMethodDescribe(value = "接收企业微信通讯录变更回调的验证请求.", action = ActionSyncOrganizationCallbackGet.class)
	@GET
	public void syncOrganizationCallbackGet(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("msg_signature") @QueryParam("msg_signature") String msg_signature,
			@JaxrsParameterDescribe("timestamp") @QueryParam("timestamp") String timestamp,
			@JaxrsParameterDescribe("nonce") @QueryParam("nonce") String nonce,
			@JaxrsParameterDescribe("echostr") @QueryParam("echostr") String echostr) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSyncOrganizationCallbackGet.Wo> result = new ActionResult<>();
		try {
			result = new ActionSyncOrganizationCallbackGet().execute(effectivePerson, msg_signature, timestamp, nonce,
					echostr);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "接收企业微信通讯录变更回调.", action = ActionSyncOrganizationCallbackPost.class)
	@POST
	public void syncOrganizationCallbackPost(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("msg_signature") @QueryParam("msg_signature") String msg_signature,
			@JaxrsParameterDescribe("timestamp") @QueryParam("timestamp") String timestamp,
			@JaxrsParameterDescribe("nonce") @QueryParam("nonce") String nonce, String body) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSyncOrganizationCallbackPost.Wo> result = new ActionResult<>();
		try {
			result = new ActionSyncOrganizationCallbackPost().execute(effectivePerson, msg_signature, timestamp, nonce,
					body);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}


	@JaxrsMethodDescribe(value = "发送获取隐私信息请求的消息.", action = ActionSendGetPrivateInfoMessage.class)
	@POST
	@Path("send/getprivateinfo/message")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getPrivateInfoMessage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSendGetPrivateInfoMessage.Wo> result = new ActionResult<>();
		try {
			result = new ActionSendGetPrivateInfoMessage().execute(jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}
}