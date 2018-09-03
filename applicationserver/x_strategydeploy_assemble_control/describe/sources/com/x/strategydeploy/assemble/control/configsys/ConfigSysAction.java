package com.x.strategydeploy.assemble.control.configsys;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import com.x.strategydeploy.assemble.control.configsys.BaseAction.Wo;

@Path("configsys")
@JaxrsDescribe("战略管理平台配置")
public class ConfigSysAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(ConfigSysAction.class);

	@JaxrsMethodDescribe(value = "新建或者更新配置", action = ActionSave.class)
	@POST
	@Path("save")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("配置数据") JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		if (check) {
			try {
				result = new ActionSave().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "列出所有配置", action = ActionListAll.class)
	@GET
	@Path("listall")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listall(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<Wo>> result = new ActionResult<>();
		Boolean check = true;
		if (check) {
			try {
				List<Wo> wos = new ArrayList<Wo>();
				wos = new ActionListAll().execute(request, effectivePerson);
				result.setData(wos);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
				result.error(e);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "根据别名列出配置", action = ActionListAll.class)
	@GET
	@Path("listbyalias/{alias}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listbyalias(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request, @JaxrsParameterDescribe("配置别名") @PathParam("alias") String alias) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<Wo>> result = new ActionResult<>();
		Boolean check = true;
		if (check) {
			try {
				List<Wo> wos = new ArrayList<Wo>();
				wos = new ActionGetConfigByAlias().execute(request, effectivePerson, alias);
				result.setData(wos);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.error(e, effectivePerson, request, null);
				result.error(e);
			}
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}
