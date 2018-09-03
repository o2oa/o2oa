package com.x.collaboration.assemble.websocket.jaxrs.message;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("message")
public class MessageAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(MessageAction.class);

	// @HttpMethodDescribe(value = "发送WebSocket消息接口,同时呼叫其他模块进行发送,如果不能发送则保存到本地.",
	// response = WrapOutString.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response send(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = null;
		try {
			logger.debug("receive message:{}", jsonElement);
			wrap = new ActionSend().execute(jsonElement);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	// @HttpMethodDescribe(value = "转发WebSocket消息")
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response forward(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = new WrapOutBoolean();
		try {
			wrap = new ActionForward().execute(jsonElement);
			result.setData(wrap);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}