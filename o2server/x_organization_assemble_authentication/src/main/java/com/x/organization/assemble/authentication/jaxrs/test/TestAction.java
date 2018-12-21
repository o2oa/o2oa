package com.x.organization.assemble.authentication.jaxrs.test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;

import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("test")
@JaxrsDescribe("测试")
public class TestAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(TestAction.class);

	@JaxrsMethodDescribe(value = "Test1", action = ActionTest1.class)
	@GET
	@Path("1")
	public void test1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		ActionResult<ActionTest1.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionTest1().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "Test2", action = ActionTest2.class)
	@GET
	@Path("2")
	public void test2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		ActionResult<ActionTest2.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionTest2().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "Test3", action = ActionTest3.class)
	@GET
	@Path("3")
	public void test3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		ActionResult<ActionTest3.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionTest3().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "Test4", action = ActionTest4.class)
	@GET
	@Path("4")
	public void test4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		ActionResult<ActionTest4.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionTest4().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "Test5", action = ActionTest5.class)
	@GET
	@Path("5")
	public void test5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		ActionResult<ActionTest5.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionTest5().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "Test6", action = ActionTest6.class)
	@GET
	@Path("6")
	public void test6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		ActionResult<ActionTest6.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionTest6().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@JaxrsMethodDescribe(value = "Test7", action = ActionTest6.class)
	@GET
	@Path("7")
	public void test7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@Context HttpServletResponse response) {
		ActionResult<ActionTest7.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionTest7().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}