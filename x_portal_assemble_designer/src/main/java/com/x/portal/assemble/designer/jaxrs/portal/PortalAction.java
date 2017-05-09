package com.x.portal.assemble.designer.jaxrs.portal;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.assemble.designer.wrapout.WrapOutPortal;
import com.x.portal.assemble.designer.wrapout.WrapOutPortalSummary;

@Path("portal")
public class PortalAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(PortalAction.class);

	@HttpMethodDescribe(value = "获取单个Portal配置信息,检查是否有editable权限.", response = WrapOutPortal.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutPortal> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建新的Portal.需要对Portal模块的管理权限", response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCreate().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定的Portal,需要对此portal的管理权限", response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(@Context HttpServletRequest request, @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionUpdate().execute(effectivePerson, id, jsonElement);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除指定的Portal,需要指定权限.", response = WrapOutBoolean.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(effectivePerson, id);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示当前用户可以管理的Portal.", response = WrapOutPortal.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutPortal>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionList().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示当前用户在指定分类下可以管理的Portal.", response = WrapOutPortal.class)
	@GET
	@Path("list/portalcategory/{portalCategory}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithPortalCategory(@Context HttpServletRequest request,
			@PathParam("portalCategory") String portalCategory) {
		ActionResult<List<WrapOutPortal>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithPortalCategory().execute(effectivePerson, portalCategory);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示当前用户可以管理的Portal的简要信息包含page.", response = WrapOutPortal.class)
	@GET
	@Path("list/summary")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSummary(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutPortalSummary>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListSummary().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示当前用户在指定分类下可以管理的Portal的简要信息包含page.", response = WrapOutPortal.class)
	@GET
	@Path("list/summary/portalcategory/{portalCategory}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSummaryWithPortalCategory(@Context HttpServletRequest request,
			@PathParam("portalCategory") String portalCategory) {
		ActionResult<List<WrapOutPortalSummary>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListSummaryWithPortalCategory().execute(effectivePerson, portalCategory);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}