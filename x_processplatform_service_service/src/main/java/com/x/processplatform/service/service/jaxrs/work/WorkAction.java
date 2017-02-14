package com.x.processplatform.service.service.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.ListTools;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.service.service.Business;
import com.x.processplatform.service.service.WrapTools;
import com.x.processplatform.service.service.wrapout.WrapOutWork;

@Path("work")
public class WorkAction extends StandardJaxrsAction {

	@HttpMethodDescribe(value = "列示所有处于Service状态的work对象,下一页.仅管理员可用", response = WrapOutWork.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListNext(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			business.isManager(effectivePerson, ExceptionWhen.not_allow);
			EqualsTerms equals = new EqualsTerms();
			equals.put("activityType", ActivityType.service);
			result = this.standardListNext(WrapTools.workOutCopier, id, count, "sequence", equals, null, null, null,
					null, null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示所有处于Service状态的work对象,上一页.仅管理员可用", response = WrapOutWork.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListPrev(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			business.isManager(effectivePerson, ExceptionWhen.not_allow);
			EqualsTerms equals = new EqualsTerms();
			equals.put("activityType", ActivityType.service);
			result = this.standardListPrev(WrapTools.workOutCopier, id, count, "sequence", equals, null, null, null,
					null, null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定应用中所有处于Service状态的work对象,下一页.仅管理员可使用", response = WrapOutWork.class)
	@GET
	@Path("list/{id}/next/{count}/application/{application}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ListNextApplication(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("application") String application) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			business.isManager(effectivePerson, ExceptionWhen.not_allow);
			EqualsTerms equals = new EqualsTerms();
			equals.put("activityType", ActivityType.service);
			equals.put("application", application);
			result = this.standardListNext(WrapTools.workOutCopier, id, count, "sequence", equals, null, null, null,
					null, null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定应用中所有处于Service状态的work对象,上一页.仅管理员可使用", response = WrapOutWork.class)
	@GET
	@Path("list/{id}/prev/{count}/application/{application}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevApplication(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("application") String application) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			business.isManager(effectivePerson, ExceptionWhen.not_allow);
			EqualsTerms equals = new EqualsTerms();
			equals.put("activityType", ActivityType.service);
			equals.put("application", application);
			result = this.standardListPrev(WrapTools.workOutCopier, id, count, "sequence", equals, null, null, null,
					null, null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定流程中所有处于Service状态的work对象,下一页.仅管理员可使用", response = WrapOutWork.class)
	@GET
	@Path("list/{id}/next/{count}/process/{process}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ListNextProcess(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("process") String process) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			business.isManager(effectivePerson, ExceptionWhen.not_allow);
			EqualsTerms equals = new EqualsTerms();
			equals.put("activityType", ActivityType.service);
			equals.put("process", process);
			result = this.standardListNext(WrapTools.workOutCopier, id, count, "sequence", equals, null, null, null,
					null, null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示指定流程中所有处于Service状态的work对象,上一页.仅管理员可使用", response = WrapOutWork.class)
	@GET
	@Path("list/{id}/prev/{count}/process/{process}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevProcess(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("process") String process) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			business.isManager(effectivePerson, ExceptionWhen.not_allow);
			EqualsTerms equals = new EqualsTerms();
			equals.put("activityType", ActivityType.service);
			equals.put("process", process);
			result = this.standardListPrev(WrapTools.workOutCopier, id, count, "sequence", equals, null, null, null,
					null, null, null, true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示所有处于指定Service的work对象，仅管理员和可信的IP可用", response = WrapOutWork.class)
	@GET
	@Path("list/service/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithService(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<List<WrapOutWork>> result = new ActionResult<>();
		List<WrapOutWork> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Service service = emc.find(id, Service.class, ExceptionWhen.not_found);
			for (;;) {
				if ((null == service.getTrustAddressList()) || (service.getTrustAddressList().isEmpty())) {
					break;
				}
				if (ListTools.contains(service.getTrustAddressList(), request.getRemoteAddr())) {
					break;
				}
				if (business.isManager(effectivePerson)) {
					break;
				}
				throw new Exception("not trust address:" + request.getRemoteHost() + ", or " + effectivePerson.getName()
						+ " has sufficient permissions.");
			}
			List<String> ids = business.work().listWithActivity(service.getId());
			wraps = WrapTools.workOutCopier.copy(emc.list(Work.class, ids));
			SortTools.asc(wraps, false, "sequence");
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}