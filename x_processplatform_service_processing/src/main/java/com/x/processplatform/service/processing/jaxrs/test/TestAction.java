package com.x.processplatform.service.processing.jaxrs.test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.organization.core.express.Organization;
import com.x.organization.core.express.wrap.WrapIdentity;
import com.x.processplatform.service.processing.ThisApplication;

@Path("test")
public class TestAction extends AbstractJaxrsAction {

	@HttpMethodDescribe(value = "流转一个流程实例.", response = WrapOutId.class)
	@GET
	@Path("{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("name") String name) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try {
			Organization org = new Organization(ThisApplication.context());
			WrapIdentity o = org.identity().getWithName(name);
			System.out.println("##########################################");
			System.out.println(o);
			System.out.println("##########################################");
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}