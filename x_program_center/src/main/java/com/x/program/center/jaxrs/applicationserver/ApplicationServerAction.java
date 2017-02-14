package com.x.program.center.jaxrs.applicationserver;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

@Path("applicationserver")
public class ApplicationServerAction extends AbstractJaxrsAction {

	@GET
	@Path("name/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "获取指定名称的Web服务器.", response = ApplicationServer.class)
	public Response getWithName(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<ApplicationServer> result = new ActionResult<>();
		ApplicationServer wrap = null;
		try {
			wrap = new ActionGet().execute(name);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "创建服务器配置.", request = ApplicationServer.class, response = ApplicationServer.class)
	public Response create(@Context HttpServletRequest request, ApplicationServer wrapIn) {
		ActionResult<ApplicationServer> result = new ActionResult<>();
		ApplicationServer wrap = null;
		// try {
		// if (StringUtils.isEmpty(wrapIn.getName())) {
		// throw new Exception("server name can not be empty.");
		// }
		// wrap = new ActionCreate().execute(wrapIn);
		// result.setData(wrap);
		// } catch (Throwable th) {
		// th.printStackTrace();
		// result.error(th);
		// }
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("name/{name}")
	@HttpMethodDescribe(value = "更新指定名称的服务器配置.", request = ApplicationServer.class, response = ApplicationServer.class)
	public Response update(@Context HttpServletRequest request, @PathParam("name") String name,
			ApplicationServer wrapIn) {
		ActionResult<ApplicationServer> result = new ActionResult<>();
		ApplicationServer wrap = null;
		try {
			wrap = new ActionUpdate().execute(name, wrapIn);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@DELETE
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("name/{name}")
	@HttpMethodDescribe(value = "删除指定排序号的服务器配置.", response = ApplicationServer.class)
	public Response delete(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<ApplicationServer> result = new ActionResult<>();
		ApplicationServer wrap = null;
		try {
			wrap = new ActionDelete().execute(name);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("list/depolyable")
	@HttpMethodDescribe(value = "列示可部署的Assemble和Service.", response = WrapOutDeployable.class)
	public Response listDepolyable(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutDeployable>> result = new ActionResult<>();
		List<WrapOutDeployable> wraps = new ArrayList<>();
		try {
			wraps = new ActionListDepolyable().execute();
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("name/{name}/deploy/{forceRedeploy}")
	@HttpMethodDescribe(value = "在指定的服务器上进行应用部署.", response = ApplicationServer.class)
	public Response deploy(@Context HttpServletRequest request, @PathParam("name") String name,
			@PathParam("forceRedeploy") Boolean forceRedeploy) {
		ActionResult<ApplicationServer> result = new ActionResult<>();
		ApplicationServer wrap = null;
		try {
			wrap = new ActionDeploy().execute(name, forceRedeploy);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	// @GET
	// @Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Path("name/{name}/deploy/{forceRedeploy}")
	// @HttpMethodDescribe(value = "在指定的服务器上进行应用部署.", response =
	// ApplicationServer.class)
	// public void deploy(@Suspended final AsyncResponse asyncResponse, @Context
	// HttpServletRequest request,
	// final @PathParam("name") String name, final @PathParam("forceRedeploy")
	// Boolean forceRedeploy) {
	// final ActionResult<ApplicationServer> result = new ActionResult<>();
	// asyncResponse.register(new CompletionCallback() {
	// @Override
	// public void onComplete(Throwable throwable) {
	// if (throwable != null) {
	// result.error(throwable);
	// } else {
	// asyncResponse.resume(result.toJson());
	// }
	// }
	// });
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// try {
	// ApplicationServer wrap = new ActionDeploy().execute(name, forceRedeploy);
	// result.setData(wrap);
	// } catch (Exception e) {
	// result.error(e);
	// }
	// }
	// }).start();
	// }

}