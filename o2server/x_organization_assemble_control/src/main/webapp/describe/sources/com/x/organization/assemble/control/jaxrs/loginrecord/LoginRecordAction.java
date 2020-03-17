package com.x.organization.assemble.control.jaxrs.loginrecord;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("loginrecord")
@JaxrsDescribe("登录日志")
public class LoginRecordAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(LoginRecordAction.class);

	@JaxrsMethodDescribe(value = "获取用户登录记录的Excel.", action = ActionLoginRecord.class)
	@GET
	@Path("{stream}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void loginRecord(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("是否采用流格式直接作为附件下载") @PathParam("stream") Boolean stream) {
		ActionResult<ActionLoginRecord.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionLoginRecord().execute(effectivePerson, stream);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}