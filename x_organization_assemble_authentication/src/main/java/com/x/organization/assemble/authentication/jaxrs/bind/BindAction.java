package com.x.organization.assemble.authentication.jaxrs.bind;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.authentication.wrapout.WrapOutBind;

@Path("bind")
public class BindAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "列示所有Bind对象.", response = WrapOutBind.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNext(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutBind>> result = new ActionResult<>();
		try {
			result = new ActionList().execute();
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}