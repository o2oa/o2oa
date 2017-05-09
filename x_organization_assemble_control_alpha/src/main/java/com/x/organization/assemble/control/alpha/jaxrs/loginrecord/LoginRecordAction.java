package com.x.organization.assemble.control.alpha.jaxrs.loginrecord;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.FileWo;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

@Path("loginrecord")
public class LoginRecordAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(LoginRecordAction.class);

	// @HttpMethodDescribe(value = "获取用户登录记录的xslx.", response = FileWo.class)
	// @GET
	// @Path("{stream}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// public Response loginRecord(@Context HttpServletRequest request,
	// @PathParam("stream") Boolean stream) {
	// ActionResult<FileWo> result = new ActionResult<>();
	// EffectivePerson effectivePerson = this.effectivePerson(request);
	// try {
	// result = new ActionLoginRecord().execute(effectivePerson, stream);
	// } catch (Exception e) {
	// logger.error(e, effectivePerson, request, null);
	// result.error(e);
	// }
	// return ResponseFactory.getDefaultActionResultResponse(result);
	// }

	@HttpMethodDescribe(value = "获取用户登录记录的xls.", response = FileWo.class)
	@GET
	@Path("{stream}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void loginRecord(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("stream") Boolean stream) {
		ActionResult<FileWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionLoginRecord().execute(effectivePerson, stream);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}