package com.x.processplatform.assemble.surface.jaxrs.job;

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

@Path("job")
@JaxrsDescribe("任务操作")
public class JobAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(JobAction.class);

	@JaxrsMethodDescribe(value = "根据job查找属于这个job的work和workCompleted.", action = ActionFindWorkWorkCompleted.class)
	@GET
	@Path("{job}/find/work/workcompleted")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void findWorkWorkCompleted(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("任务标识") @PathParam("job") String job) {
		ActionResult<ActionFindWorkWorkCompleted.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionFindWorkWorkCompleted().execute(effectivePerson, job);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据serial查找work,并返回最新创建的一个.", action = ActionLatestWorkWorkCompletedWithSerial.class)
	@GET
	@Path("latest/work/workcompleted/serial/{serial}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void latestWorkWorkCompletedWithSerial(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request, @JaxrsParameterDescribe("编号") @PathParam("serial") String serial) {
		ActionResult<ActionLatestWorkWorkCompletedWithSerial.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionLatestWorkWorkCompletedWithSerial().execute(effectivePerson, serial);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}