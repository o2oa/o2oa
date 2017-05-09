package com.x.base.core.application.jaxrs.logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.AbstractJaxrsAction;
import com.x.base.core.project.jaxrs.BooleanWo;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StringWo;

@Path("logger")
public class LoggerAction extends AbstractJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(LoggerAction.class);

	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "获取logger状态.", response = StringWo.class)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<StringWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result.setData(new StringWo(LoggerFactory.getLevel()));
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@GET
	@Path("trace")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "将logger level 调整为 trace", response = BooleanWo.class)
	public void trace(@Suspended final AsyncResponse asyncResponse, @Context ServletContext servletContext,
			@Context HttpServletRequest request) {
		ActionResult<BooleanWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			com.x.base.core.project.Context ctx = (com.x.base.core.project.Context) servletContext
					.getAttribute(com.x.base.core.project.Context.class.getName());
			logger.info("{} change logger level to TRACE.", ctx.clazz().getName());
			LoggerFactory.setLevelTrace();
			result.setData(BooleanWo.trueInstance());
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@GET
	@Path("debug")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "将logger level 调整为 debug", response = BooleanWo.class)
	public void debug(@Suspended final AsyncResponse asyncResponse, @Context ServletContext servletContext,
			@Context HttpServletRequest request) {
		ActionResult<BooleanWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			com.x.base.core.project.Context ctx = (com.x.base.core.project.Context) servletContext
					.getAttribute(com.x.base.core.project.Context.class.getName());
			logger.info("{} change logger level to DEBUG.", ctx.clazz().getName());
			LoggerFactory.setLevelDebug();
			result.setData(BooleanWo.trueInstance());
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@GET
	@Path("info")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "将logger level 调整为 info", response = BooleanWo.class)
	public void info(@Suspended final AsyncResponse asyncResponse, @Context ServletContext servletContext,
			@Context HttpServletRequest request) {
		ActionResult<BooleanWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			com.x.base.core.project.Context ctx = (com.x.base.core.project.Context) servletContext
					.getAttribute(com.x.base.core.project.Context.class.getName());
			logger.info("{} change logger level to INFO.", ctx.clazz().getName());
			LoggerFactory.setLevelInfo();
			result.setData(BooleanWo.trueInstance());
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

	@GET
	@Path("warn")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "将logger level 调整为 warn", response = BooleanWo.class)
	public void warn(@Suspended final AsyncResponse asyncResponse, @Context ServletContext servletContext,
			@Context HttpServletRequest request) {
		ActionResult<BooleanWo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			com.x.base.core.project.Context ctx = (com.x.base.core.project.Context) servletContext
					.getAttribute(com.x.base.core.project.Context.class.getName());
			logger.info("{} change logger level to WARN.", ctx.clazz().getName());
			LoggerFactory.setLevelWarn();
			result.setData(BooleanWo.trueInstance());
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}

}