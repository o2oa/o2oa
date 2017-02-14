package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.assemble.authentication.wrap.in.WrapInAuthentication;
import com.x.organization.assemble.authentication.wrap.out.WrapOutAuthentication;

@Path("authentication")
public class AuthenticationAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "用户登录.credential=xxxx,password=xxxx", response = WrapOutAuthentication.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(@Context HttpServletRequest request, @Context HttpServletResponse response,
			WrapInAuthentication wrapIn) {
		ActionResult<WrapOutAuthentication> result = new ActionResult<>();
		try {
			result = new ActionLogin().execute(request, response, wrapIn);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "用户注销.", response = WrapOutAuthentication.class)
	@DELETE
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response logout(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		ActionResult<WrapOutAuthentication> result = new ActionResult<>();
		WrapOutAuthentication wrap = new WrapOutAuthentication();
		try {
			wrap = new ActionLogout().execute(request, response);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取当前用户.如果返回的Json数据不包含Data,那么当前用户未登陆.", response = WrapOutAuthentication.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request) {
		ActionResult<WrapOutAuthentication> result = new ActionResult<>();
		WrapOutAuthentication wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			wrap = new ActionGet().execute(business, effectivePerson);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}