package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectIdEmpty;

@Path("subject")
@JaxrsDescribe("主贴查询服务")
public class SubjectInfoAction extends StandardJaxrsAction {
	private static Logger logger = LoggerFactory.getLogger(SubjectInfoAction.class);

	@JaxrsMethodDescribe(value = "列示根据过滤条件的推荐主题列表.", action = ActionSubjectListRecommendedForPages.class)
	@PUT
	@Path("recommended/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listRecommendedSubjectForPage(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("展示页码") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页显示条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionSubjectListRecommendedForPages.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubjectListRecommendedForPages().execute(request, effectivePerson, page, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "列示根据过滤条件的推荐主题列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取所有推荐到BBS首页的主题列表.", action = ActionSubjectListRecommendedForBBSIndex.class)
	@GET
	@Path("recommended/index/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listRecommendedSubjectForBBSIndex(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("查询的最大条目数量") @PathParam("count") Integer count) {
		ActionResult<List<ActionSubjectListRecommendedForBBSIndex.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			if (count == null || count <= 0) {
				count = 10;
			}
		}
		if (check) {
			try {
				result = new ActionSubjectListRecommendedForBBSIndex().execute(request, effectivePerson, count);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "获取所有推荐到BBS首页的主题列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	// 所有的置顶贴应该全部取出
	@JaxrsMethodDescribe(value = "获取所有可以取到的置顶贴列表.", action = ActionSubjectListTop.class)
	@GET
	@Path("top/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listTopSubject(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("版块信息ID") @PathParam("sectionId") String sectionId) {
		ActionResult<List<ActionSubjectListTop.Wo>> result = new ActionResult<>();
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
				result = new ActionSubjectListTop().execute(request, effectivePerson, sectionId);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "获取所有可以取到的置顶贴列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件的精华主题列表.", action = ActionSubjectListCreamedForPages.class)
	@PUT
	@Path("creamed/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCreamedSubjectForPage(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("显示页码") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页显示条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionSubjectListCreamedForPages.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			if (page == null) {
				page = 1;
			}
		}
		if (check) {
			if (count == null) {
				count = 20;
			}
		}
		if (check) {
			try {
				result = new ActionSubjectListCreamedForPages().execute(request, effectivePerson, page, count,
						jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "列示根据过滤条件的精华主题列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件的SubjectInfo,下一页.", action = ActionSubjectListForPage.class)
	@PUT
	@Path("filter/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listSubjectForPage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("显示页码") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页显示条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionSubjectListForPage.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubjectListForPage().execute(request, effectivePerson, page, count, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "列示根据过滤条件的推荐主题列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件(版块名称，标题类别)的SubjectInfo,下一页.", action = ActionSubjectListWithSubjectTypeForPage.class)
	@POST
	@Path("filter/listsubjectinfo/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listSubjectWithSubjectTypeForPage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
								   @JaxrsParameterDescribe("显示页码") @PathParam("page") Integer page,
								   @JaxrsParameterDescribe("每页显示条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionSubjectListWithSubjectTypeForPage.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubjectListWithSubjectTypeForPage().execute(request, effectivePerson, page, count, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "列示根据过滤条件的推荐主题列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "统计根据过滤条件(版块名称，标题类别)的SubjectInfo的评分", action = ActionSubjectStatGradeWithSubjectType.class)
	@GET
	@Path("statgrade/sectionName/{sectionName}/subjectType/{subjectType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void statSubjectGrade(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
												  @JaxrsParameterDescribe("版块名称") @PathParam("sectionName") String sectionName,
												  @JaxrsParameterDescribe("主题类别") @PathParam("subjectType") String subjectType) {
		ActionResult<List<ActionSubjectStatGradeWithSubjectType.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubjectStatGradeWithSubjectType().execute(effectivePerson, sectionName, subjectType);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "统计根据过滤条件(版块名称，标题类别)的SubjectInfo的评分时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件的SubjectInfo,为首页准备的服务.", action = ActionSubjectListForBBSIndex.class)
	@PUT
	@Path("index/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listSubjectForBBSIndex(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("显示页码") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页显示条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionSubjectListForBBSIndex.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubjectListForBBSIndex().execute(request, effectivePerson, page, count, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "列示根据过滤条件的推荐主题列表时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "列示根据过滤条件的SubjectInfo,下一页.", action = ActionSubjectSearchForPage.class)
	@PUT
	@Path("search/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void searchSubjectForPage(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("显示页码") @PathParam("page") Integer page,
			@JaxrsParameterDescribe("每页显示条目数量") @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<ActionSubjectSearchForPage.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;

		if (check) {
			try {
				result = new ActionSubjectSearchForPage().execute(request, effectivePerson, page, count, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "列示根据过滤条件的SubjectInfo时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据指定ID查看主贴具体信息，需要记录查询次数和热度的.", action = ActionSubjectView.class)
	@GET
	@Path("view/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void viewSubject(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("主贴信息ID") @PathParam("id") String id) {
		ActionResult<ActionSubjectView.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				result = new ActionSubjectView().execute(request, effectivePerson, id);
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionRoleInfoProcess(e, "根据指定ID查看主题具体信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}