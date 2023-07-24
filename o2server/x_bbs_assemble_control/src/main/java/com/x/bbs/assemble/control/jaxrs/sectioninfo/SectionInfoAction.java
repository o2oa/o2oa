package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

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

@Path("section")
@JaxrsDescribe("版块查询服务")
public class SectionInfoAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(SectionInfoAction.class);

	@JaxrsMethodDescribe(value = "根据论坛ID获取所有版块的信息列表.", action = ActionViewWithForum.class)
	@GET
	@Path("viewforum/{forumId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void viewWithForum(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("论坛信息ID") @PathParam("forumId") String forumId) {
		ActionResult<List<ActionViewWithForum.Wo>> result = new ActionResult<>();
		Boolean check = true;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		if (check) {
			if (forumId == null || forumId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionForumIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionViewWithForum().execute(request, effectivePerson, forumId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "根据主版块ID查询所有的子版块信息列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据主版块ID查询所有的子版块信息列表.", action = ActionListSubSectionByMainSectionId.class)
	@GET
	@Path("viewsub/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listSubSectionByMainSectionId(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("主版块信息ID") @PathParam("sectionId") String sectionId) {
		ActionResult<List<ActionListSubSectionByMainSectionId.Wo>> result = new ActionResult<>();
		Boolean check = true;
		EffectivePerson effectivePerson = this.effectivePerson(request);
		if (check) {
			if (sectionId == null || sectionId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSectionIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionListSubSectionByMainSectionId().execute(request, effectivePerson, sectionId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "根据主版块ID查询所有的子版块信息列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据指定ID获取版块信息.", action = ActionGet.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("版块信息ID") @PathParam("id") String id) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSectionIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionGet().execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "根据指定ID获取版块信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "从应用市场同步所有的应用名称，存入论坛板块应用市场字段subjectTypeList.", action = ActionSynApplicationsFromMarket.class)
	@GET
	@Path("syn")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void syn(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionSynApplicationsFromMarket.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
			try {
				result = new ActionSynApplicationsFromMarket().execute(request, effectivePerson);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "根据版块名称获取版块信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}