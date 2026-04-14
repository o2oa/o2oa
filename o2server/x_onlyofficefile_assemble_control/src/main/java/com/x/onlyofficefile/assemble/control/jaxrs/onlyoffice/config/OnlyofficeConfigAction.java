package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.config;

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

@Path("onlyofficeconfig")
@JaxrsDescribe("参数配置")
public class OnlyofficeConfigAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(OnlyofficeConfigAction.class);

    @JaxrsMethodDescribe(value = "获取配置信息", action =ActionGetConfig.class)
 	@GET
 	@Path("get")
 	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
 	public void getConfig(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
 		ActionResult<ActionGetConfig.Wo> result = new ActionResult<>();
 		EffectivePerson effectivePerson = this.effectivePerson(request);
 		try {
 			result = new ActionGetConfig().execute(request,effectivePerson);
 		} catch (Exception e) {
 			logger.error(e, effectivePerson, request, null);
 			result.error(e);
 		}
 		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
 	}


    @JaxrsMethodDescribe(value = "保存配置数据", action = ActionUpdateConfig.class)
	@POST
	@Path("save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void saveConfig(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,JsonElement jsonElement) {
		ActionResult<ActionUpdateConfig.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdateConfig().execute(request , effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "刷新配置信息", action =ActionRefreshConfig.class)
	@GET
	@Path("refresh")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void refresh(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionRefreshConfig.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionRefreshConfig().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}


}
