package com.x.organization.assemble.control.jaxrs.function;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.http.WrapInString;
import com.x.base.core.project.http.WrapInStringList;
import com.x.base.core.project.http.WrapOutCount;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.control.Business;

@Path("function")
public class FunctionAction extends StandardJaxrsAction {

	// @HttpMethodDescribe(value = "获取人员指定信息.", response = WrapOutStringList.class)
	@POST
	@Path("list/person")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listAllPersonName(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			WrapInStringList wrapIn) {
		ActionResult<List<Tuple>> result = new ActionResult<>();
		List<Tuple> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wraps = new ActionListAllPersonName().execute(business, wrapIn);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	// @HttpMethodDescribe(value = "更新指定Person的Password.", request =
	// WrapInString.class, response = WrapOutId.class)
	@PUT
	@Path("person/{name}/set/password")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setPassword(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("name") String name, WrapInString wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			wrap = new ActionSetPassword().execute(business, name, wrapIn);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	// @HttpMethodDescribe(response = WrapOutCount.class, value = "set text")
	@GET
	@Path("person/set/text/{attribute}/{key}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setText(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("attribute") String attribute, @PathParam("key") String key) {
		ActionResult<WrapOutCount> result = new ActionResult<>();
		try {
			result = new ActionSetText().execute(attribute, key);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	// @HttpMethodDescribe(response = WrapOutCount.class, value = "create password")
	@GET
	@Path("person/create/password/{attribute}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void createPassword(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@PathParam("attribute") String attribute) {
		ActionResult<WrapOutCount> result = new ActionResult<>();
		try {
			result = new ActionCreatePassword().execute(attribute);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}