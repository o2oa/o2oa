package com.x.okr.assemble.control.jaxrs.mind;

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
import javax.ws.rs.core.Response;

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
 * 具体工作项有短期工作还长期工作，短期工作不需要自动启动定期汇报，由人工撰稿汇报即可
 */

@Path("mind")
@JaxrsDescribe("工作信息脑图展现服务")
public class OkrWorkMindAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OkrWorkMindAction.class);

	/**
	 * 展示与用户可视的工作有关联的工作部署线（可配置为全部显示） 用户可视的工作，可以被访问详情
	 * 
	 * @param request
	 * @param centerId
	 * @return
	 */
	@JaxrsMethodDescribe(value = "根据中心工作ID获取用户需要在脑图里展现的所有工作信息列表", action = ActionListMindForCenterId.class)
	@GET
	@Path("center/{centerId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listMindWorkByCenterId(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("中心工作ID") @PathParam("centerId") String centerId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionListMindForCenterId.Wo> result = new ActionResult<>();
		try {
			result = new ActionListMindForCenterId().execute(request, effectivePerson, centerId);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}