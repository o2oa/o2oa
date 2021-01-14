package com.x.base.core.project.jaxrs.sysresource;

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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("sysresource")
@JaxrsDescribe("系统资源")
public class SysResourceAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(SysResourceAction.class);

	@JaxrsMethodDescribe(value = "获取静态资源信息.", action = ActionListResource.class)
	@GET
	@Path("filePath/{filePath}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listResource(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
					@JaxrsParameterDescribe("查找路径(根路径:(0))") @PathParam("filePath") String filePath) {
		ActionResult<ActionListResource.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListResource().execute(effectivePerson, filePath);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

}
