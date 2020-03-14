package com.x.query.service.processing.jaxrs.segment;

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

@Path("segment")
@JaxrsDescribe("分词")
public class SegmentAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(SegmentAction.class);

	@JaxrsMethodDescribe(value = "清空所有分词内容.", action = ActionClean.class)
	@GET
	@Path("clean/work/{cleanWork}/workCompleted/{cleanWorkCompleted}/cms/{cleanCms}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void clean(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("是否清空工作索引") @PathParam("cleanWork") Boolean cleanWork,
			@JaxrsParameterDescribe("是否清空已完成工作索引") @PathParam("cleanWorkCompleted") Boolean cleanWorkCompleted,
			@JaxrsParameterDescribe("是否清空内容管理索引") @PathParam("cleanCms") Boolean cleanCms) {
		ActionResult<ActionClean.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionClean().execute(effectivePerson, cleanWork, cleanWorkCompleted, cleanCms);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对内容管理文档进行索引.", action = ActionCrawlCms.class)
	@GET
	@Path("crawl/cms/{documentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void crawlCms(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("文档标识") @PathParam("documentId") String documentId) {
		ActionResult<ActionCrawlCms.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCrawlCms().execute(effectivePerson, documentId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对工作进行索引.", action = ActionCrawlWork.class)
	@GET
	@Path("crawl/work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void crawlWork(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("工作标识") @PathParam("workId") String workId) {
		ActionResult<ActionCrawlWork.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCrawlWork().execute(effectivePerson, workId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "对已完成工作进行索引.", action = ActionCrawlWorkCompleted.class)
	@GET
	@Path("crawl/workcompleted/{workCompletedId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void crawlWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("已完成工作标识") @PathParam("workCompletedId") String workCompletedId) {
		ActionResult<ActionCrawlWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionCrawlWorkCompleted().execute(effectivePerson, workCompletedId);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}