package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
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
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoProcess;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionForumIdEmpty;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionIdEmpty;

@Path("user/section")
@JaxrsDescribe("版块管理服务")
public class SectionInfoManagerUserAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(SectionInfoManagerUserAction.class);

	@JaxrsMethodDescribe(value = "获取所有版块的信息列表.", action = ActionAllSections.class)
	@GET
	@Path("all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAllSection(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<ActionAllSections.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			try {
				result = new ActionAllSections().execute(request, effectivePerson);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "获取所有版块的信息列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据论坛ID获取所有主版块的信息列表(管理).", action = ActionListWithForum.class)
	@GET
	@Path("forum/{forumId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithForum(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("论坛信息ID") @PathParam("forumId") String forumId) {
		ActionResult<List<ActionListWithForum.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			if (forumId == null || forumId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionForumIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionListWithForum().execute(request, effectivePerson, forumId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "根据论坛ID获取所有主版块的信息列表(管理)时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据主版块ID查询所有的子版块信息列表(管理).", action = ActionAllListSubSectionByMainSectionId.class)
	@GET
	@Path("sub/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAllSubSectionByMainSectionId(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("主版块信息ID") @PathParam("sectionId") String sectionId) {
		ActionResult<List<ActionAllListSubSectionByMainSectionId.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			if (sectionId == null || sectionId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSectionIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionAllListSubSectionByMainSectionId().execute(request, effectivePerson, sectionId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "根据主版块ID查询所有的子版块信息列表(管理)时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "创建新的版块信息或者更新版块信息.", action = ActionSave.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void post(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		Boolean check = true;
		EffectivePerson effectivePerson = this.effectivePerson(request);

		if (check) {
			try {
				result = new ActionSave().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "创建新的版块信息或者更新版块信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID删除指定的版块信息，如果版块里有贴子，则不允许删除.", action = ActionDelete.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("版块信息ID") @PathParam("id") String id) {
		ActionResult<ActionDelete.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDelete().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionRoleInfoProcess(e, "根据ID删除指定的版块信息时发生异常！");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据ID删除指定的版块信息，如果版块里有贴子，则全部删除.", action = ActionDeleteForce.class)
	@DELETE
	@Path("force/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteForce(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("版块信息ID") @PathParam("id") String id) {
		ActionResult<ActionDeleteForce.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionDeleteForce().execute(request, effectivePerson, id);
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ExceptionRoleInfoProcess(e, "根据ID删除指定的版块信息时发生异常！");
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}