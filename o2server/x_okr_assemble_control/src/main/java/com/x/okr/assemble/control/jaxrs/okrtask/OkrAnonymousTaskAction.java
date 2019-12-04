package com.x.okr.assemble.control.jaxrs.okrtask;

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

/**
 * 待办信息管理服务（匿名）
 * 
 * @author O2LEE
 *
 */
@Path("task")
@JaxrsDescribe("待办信息管理服务（匿名）")
public class OkrAnonymousTaskAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OkrAnonymousTaskAction.class);

	@JaxrsMethodDescribe(value = "查询指定用户的待办数量, 需要加上指定的流程的待办数量", action = ActionCountMyTask.class)
	@GET
	@Path("count/{flag}/callback/{funcName}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void countMyTask(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("用户信息标识") @PathParam("flag") String flag,
			@JaxrsParameterDescribe("回调方法名") @PathParam("funcName") String funcName) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionCountMyTask.Wo> result = new ActionResult<>();
		try {
			result = new ActionCountMyTask().execute(request, effectivePerson, flag, funcName);
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error(e);
			logger.warn("system excute ExcuteCountMyTask got an exception.flag:" + flag);
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
