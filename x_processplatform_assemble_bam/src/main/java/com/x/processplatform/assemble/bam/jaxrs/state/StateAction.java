package com.x.processplatform.assemble.bam.jaxrs.state;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.http.annotation.HttpMethodDescribe;

@Path("state")
public class StateAction extends AbstractJaxrsAction {

	@HttpMethodDescribe(value = "获取全局统计.", response = WrapOutMap.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("summary")
	public Response getSummary(@Context HttpServletRequest request) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionSummary().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取运行情况.", response = WrapOutMap.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("running")
	public Response getRunning(@Context HttpServletRequest request) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionRunning().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取根据公司,部门和个人的统计.", response = WrapOutMap.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("organization")
	public Response getOrganization(@Context HttpServletRequest request) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionOrganization().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取根据应用的统计.", response = WrapOutMap.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("category")
	public Response getCategory(@Context HttpServletRequest request) {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		try {
			result = new ActionCategory().execute();
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}