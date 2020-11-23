package com.x.general.assemble.control.jaxrs.file;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("generalfile")
@JaxrsDescribe("获取附件")
public class GeneralFileAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(GeneralFileAction.class);

	@JaxrsMethodDescribe(value = "获取附件.", action = ActionGeneralFile.class)
	@GET
	@Path("flag/{flag}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getResult(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
						  @JaxrsParameterDescribe("附件标记") @PathParam("flag") String flag) {
		ActionResult<ActionGeneralFile.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGeneralFile().execute(effectivePerson, flag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}