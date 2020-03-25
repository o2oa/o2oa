package com.x.okr.assemble.control.jaxrs.okrauthorize;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.OkrAttachmentFileInfoAction;

@Path("okrauthorize")
@JaxrsDescribe("工作授权管理服务")
public class OkrWorkAuthorizeAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(OkrAttachmentFileInfoAction.class);

	@JaxrsMethodDescribe(value = "工作处理授权服务", action = ActionWorkAuthorize.class)
	@PUT
	@Path("work")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void workAuthorize(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionWorkAuthorize.Wo> result = new ActionResult<>();
		Boolean check = true;

		if (check) {
			try {
				result = new ActionWorkAuthorize().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("系统进行工作授权执行过程发生异常。");
				logger.error(e, effectivePerson, request, null);

			}
		}

		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	/**
	 * 工作授权收回服务
	 * 
	 * PUT PARAMETER : workId
	 * 
	 * @param request
	 * @return
	 */
	@JaxrsMethodDescribe(value = "工作授权收回服务", action = ActionWorkTackback.class)
	@PUT
	@Path("takeback")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void takeback(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionWorkTackback.Wo> result = new ActionResult<>();
		Boolean check = true;

		if (check) {
			try {
				result = new ActionWorkTackback().execute(request, effectivePerson, jsonElement);
			} catch (Exception e) {
				result = new ActionResult<>();
				logger.warn("系统对已经授权的工作进行收回操作过程发生异常。");
				logger.error(e, effectivePerson, request, null);
			}
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}