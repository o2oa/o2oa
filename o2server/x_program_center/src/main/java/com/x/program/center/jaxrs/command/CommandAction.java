package com.x.program.center.jaxrs.command;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@JaxrsDescribe("命令")
@Path("command")
public class CommandAction extends StandardJaxrsAction {
	private static final Logger logger = LoggerFactory.getLogger(CommandAction.class);

	@JaxrsMethodDescribe(value = "执行服务器命令", action = ActionCommand.class)
	@POST
	@Path("execute")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void executeCommand(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionCommand.Wo> result = new ActionResult<>();
		try {
			result = new ActionCommand().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "获取所有节点信息.", action = ActionListNode.class)
	@GET
	@Path("list/node")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getNodeInfoList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionListNode.Wo> result = new ActionResult<>();
		try {
			result = new ActionListNode().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
