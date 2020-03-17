package com.x.cms.assemble.control.jaxrs.appinfo;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.proxy.StandardJaxrsActionProxy;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.ThisApplication;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@JaxrsDescribe("匿名栏目信息访问")
@Path("anonymous/appinfo")
public class AppInfoAnonymousAction extends StandardJaxrsAction {

	private StandardJaxrsActionProxy proxy = new StandardJaxrsActionProxy(ThisApplication.context());
	private static Logger logger = LoggerFactory.getLogger(AppInfoAnonymousAction.class);

	@JaxrsMethodDescribe(value = "根据标识获取信息栏目信息对象.", action = ActionGetAnonymous.class)
	@GET
	@Path("{flag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("栏目ID") @PathParam("flag") String flag) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<BaseAction.Wo> result = new ActionResult<>();
		try {
			result = ((ActionGetAnonymous)proxy.getProxy(ActionGetAnonymous.class)).execute( request, effectivePerson, flag );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "根据指定ID查询应用栏目信息对象时发生异常。flag:" + flag );
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件的信息栏目信息,下一页.", action = ActionListNextWithFilterAnonymous.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listNextWithFilter( @Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, 
			@JaxrsParameterDescribe("最后一条信息ID，如果是第一页，则可以用(0)代替") @PathParam("id") String id, 
			@JaxrsParameterDescribe("每页显示的条目数量") @PathParam("count") Integer count, 
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListNextWithFilterAnonymous.Wo>> result = new ActionResult<>();
		try {
			result = ((ActionListNextWithFilterAnonymous)proxy.getProxy(ActionListNextWithFilterAnonymous.class))
					.execute(request, effectivePerson, id, count, jsonElement );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionAppInfoProcess(e, "查询栏目信息对象时发生异常");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}