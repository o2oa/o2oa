package com.x.program.center.jaxrs.dataserver;

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
import com.x.base.core.project.server.DataServer;

@Path("dataserver")
public class DataServerAction extends AbstractJaxrsAction {

	@GET
	@Path("name/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "获取指定序号的Web服务器.")
	public Response get(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<DataServer> result = new ActionResult<>();
		DataServer wrap = null;
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
	@HttpMethodDescribe(value = "创建服务器配置.", request = DataServer.class, response = DataServer.class)
	public Response create(@Context HttpServletRequest request, DataServer wrapIn) {
		ActionResult<DataServer> result = new ActionResult<>();
		DataServer wrap = null;
//		try {
//			if (StringUtils.isEmpty(wrapIn.getName())) {
//				throw new Exception("server name can not be empty.");
//			}
//			wrap = new ActionCreate().execute(wrapIn);
//			result.setData(wrap);
//		} catch (Throwable th) {
//			th.printStackTrace();
//			result.error(th);
//		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("name/{name}")
	@HttpMethodDescribe(value = "更新指定排序号的服务器配置.", request = DataServer.class, response = DataServer.class)
	public Response update(@Context HttpServletRequest request, @PathParam("name") String name, DataServer wrapIn) {
		ActionResult<DataServer> result = new ActionResult<>();
		DataServer wrap = null;
//		try {
//			if (StringUtils.isEmpty(wrapIn.getName())) {
//				throw new Exception("server name can not be empty.");
//			}
//			wrap = new ActionUpdate().execute(name, wrapIn);
//			result.setData(wrap);
//		} catch (Throwable th) {
//			th.printStackTrace();
//			result.error(th);
//		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@DELETE
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("name/{name}")
	@HttpMethodDescribe(value = "删除指定排序号的服务器配置.", response = DataServer.class)
	public Response delete(@Context HttpServletRequest request, @PathParam("name") String name) {
		ActionResult<DataServer> result = new ActionResult<>();
		DataServer wrap = null;
		try {
			wrap = new ActionDelete().execute(name);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}