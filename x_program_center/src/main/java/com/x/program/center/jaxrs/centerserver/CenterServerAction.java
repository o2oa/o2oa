package com.x.program.center.jaxrs.centerserver;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.ApplicationServer;
import com.x.base.core.project.server.CenterServer;

@Path("centerserver")
public class CenterServerAction extends AbstractJaxrsAction {

	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "获取CenterServer配置.", response = ApplicationServer.class)
	public Response get(@Context HttpServletRequest request) {
		ActionResult<CenterServer> result = new ActionResult<>();
		CenterServer wrap = null;
		try {
			wrap = new ActionGet().execute();
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "更新CenterServer配置.", request = CenterServer.class, response = CenterServer.class)
	public Response update(@Context HttpServletRequest request, CenterServer wrapIn) {
		ActionResult<CenterServer> result = new ActionResult<>();
		CenterServer wrap = null;
		try {
			wrap = new ActionUpdate().execute(wrapIn);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}