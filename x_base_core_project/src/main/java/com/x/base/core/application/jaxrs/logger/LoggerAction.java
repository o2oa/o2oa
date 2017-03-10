package com.x.base.core.application.jaxrs.logger;

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
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutBoolean;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.AbstractThisApplication;

@Path("logger")
public class LoggerAction extends AbstractJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(LoggerAction.class);

	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "获取logger状态.", response = WrapOutString.class)
	public Response get(@Context HttpServletRequest request) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			wrap.setValue(LoggerFactory.getLevel());

			result.setData(wrap);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@GET
	@Path("trace")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "将logger level 调整为 trace", response = WrapOutBoolean.class)
	public Response trace(@Context HttpServletRequest request) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = new WrapOutBoolean();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			logger.info("{} change logger level to TRACE.", AbstractThisApplication.clazz.getName());
			LoggerFactory.setLevelTrace();
			wrap.setValue(true);
			result.setData(wrap);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@GET
	@Path("debug")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "将logger level 调整为 debug", response = WrapOutBoolean.class)
	public Response debug(@Context HttpServletRequest request) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = new WrapOutBoolean();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			logger.info("{} change logger level to DEBUG.", AbstractThisApplication.clazz.getName());
			LoggerFactory.setLevelDebug();
			wrap.setValue(true);
			result.setData(wrap);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@GET
	@Path("info")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "将logger level 调整为 info", response = WrapOutBoolean.class)
	public Response info(@Context HttpServletRequest request) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = new WrapOutBoolean();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			logger.info("{} change logger level to INFO.", AbstractThisApplication.clazz.getName());
			LoggerFactory.setLevelInfo();
			wrap.setValue(true);
			result.setData(wrap);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@GET
	@Path("warn")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	@HttpMethodDescribe(value = "将logger level 调整为 warn", response = WrapOutBoolean.class)
	public Response enable(@Context HttpServletRequest request) {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = new WrapOutBoolean();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			logger.info("{} change logger level to WARN.", AbstractThisApplication.clazz.getName());
			LoggerFactory.setLevelWarn();
			wrap.setValue(true);
			result.setData(wrap);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}