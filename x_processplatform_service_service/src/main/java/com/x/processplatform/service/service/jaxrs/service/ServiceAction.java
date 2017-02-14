package com.x.processplatform.service.service.jaxrs.service;

import java.net.URLEncoder;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.exception.JaxrsBusinessLogicException;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.temporary.ServiceValue;
import com.x.processplatform.service.service.Business;
import com.x.processplatform.service.service.ThisApplication;

@Path("service")
public class ServiceAction extends StandardJaxrsAction {
	@HttpMethodDescribe(value = "对于等待调用的work写入回写数据", response = WrapOutId.class)
	@GET
	@Path("work/{workId}/{value}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("workId") String workId,
			@PathParam("value") String value) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				EffectivePerson effectivePerson = this.effectivePerson(request);
				Work work = emc.find(workId, Work.class, ExceptionWhen.not_found);
				if (!Objects.equals(ActivityType.service, work.getActivityType())) {
					throw new JaxrsBusinessLogicException(
							"invalid work{id:" + workId + ", activityType:" + work.getActivityType() + "} ");
				}
				Service service = emc.find(work.getActivity(), Service.class);
				business.checkServicePermission(request, business, effectivePerson, work, service);
				emc.beginTransaction(Work.class);
				emc.beginTransaction(ServiceValue.class);
				ServiceValue serviceValue = new ServiceValue();
				work.copyTo(serviceValue);
				serviceValue.setValue(value);
				work.setServiceValue(serviceValue.getId());
				emc.check(work, CheckPersistType.all);
				emc.persist(serviceValue, CheckPersistType.all);
				emc.commit();
			}
			ThisApplication.applications.putQuery(x_processplatform_service_processing.class,
					"work/" + URLEncoder.encode(workId, "UTF-8") + "/processing", null);
			wrap = new WrapOutId(workId);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对于等待调用的work写入回写数据", response = WrapOutId.class)
	@POST
	@Path("work/{workId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, @PathParam("workId") String workId,
			@FormParam("value") String value) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				EffectivePerson effectivePerson = this.effectivePerson(request);
				Work work = emc.find(workId, Work.class, ExceptionWhen.not_found);
				if (!Objects.equals(ActivityType.service, work.getActivityType())) {
					throw new JaxrsBusinessLogicException(
							"invalid work{id:" + workId + ", activityType:" + work.getActivityType() + "} ");
				}
				Service service = emc.find(work.getActivity(), Service.class);
				business.checkServicePermission(request, business, effectivePerson, work, service);
				emc.beginTransaction(Work.class);
				emc.beginTransaction(ServiceValue.class);
				ServiceValue serviceValue = new ServiceValue();
				work.copyTo(serviceValue);
				serviceValue.setValue(value);
				work.setServiceValue(serviceValue.getId());
				emc.check(work, CheckPersistType.all);
				emc.persist(serviceValue, CheckPersistType.all);
				emc.commit();
			}
			ThisApplication.applications.putQuery(x_processplatform_service_processing.class,
					"work/" + URLEncoder.encode(workId, "UTF-8") + "/processing", null);
			wrap = new WrapOutId(workId);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

}