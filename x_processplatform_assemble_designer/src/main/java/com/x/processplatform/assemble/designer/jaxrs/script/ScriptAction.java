package com.x.processplatform.assemble.designer.jaxrs.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInScript;
import com.x.processplatform.assemble.designer.wrapout.WrapOutScript;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Script;

@Path("script")
public class ScriptAction extends StandardJaxrsAction {

	BeanCopyTools<Script, WrapOutScript> outCopier = BeanCopyToolsBuilder.create(Script.class, WrapOutScript.class,
			null, WrapOutScript.Excludes);
	BeanCopyTools<WrapInScript, Script> inCopier = BeanCopyToolsBuilder.create(WrapInScript.class, Script.class, null,
			WrapInScript.Excludes);

	@HttpMethodDescribe(value = "列示Script对象,下一页.", response = WrapOutId.class)
	@GET
	@Path("list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListNext(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutScript>> result = new ActionResult<>();
		try {
			result = this.standardListNext(outCopier, id, count, "sequence", null, null, null, null, null, null, null,
					true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示Script对象,上一页.", response = WrapOutId.class)
	@GET
	@Path("list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response standardListPrev(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count) {
		ActionResult<List<WrapOutScript>> result = new ActionResult<>();
		try {
			result = this.standardListPrev(outCopier, id, count, "sequence", null, null, null, null, null, null, null,
					true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定的脚本信息.", response = WrapOutScript.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutScript> result = new ActionResult<>();
		WrapOutScript wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			Script script = emc.find(id, Script.class, ExceptionWhen.not_found);
			Application application = emc.find(script.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			wrap = outCopier.copy(script);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建脚本.", response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInScript wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			Application application = emc.find(wrapIn.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			emc.beginTransaction(Script.class);
			Script script = new Script();
			inCopier.copy(wrapIn, script);
			script.setCreatorPerson(effectivePerson.getName());
			script.setLastUpdatePerson(effectivePerson.getName());
			script.setLastUpdateTime(new Date());
			emc.persist(script, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Script.class);
			wrap = new WrapOutId(script.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新脚本.", response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInScript wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			Script script = emc.find(id, Script.class, ExceptionWhen.not_found);
			Application application = emc.find(script.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			emc.beginTransaction(Script.class);
			inCopier.copy(wrapIn, script);
			script.setLastUpdatePerson(effectivePerson.getName());
			script.setLastUpdateTime(new Date());
			emc.commit();
			ApplicationCache.notify(Script.class);
			wrap = new WrapOutId(script.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除脚本.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			Script script = emc.find(id, Script.class, ExceptionWhen.not_found);
			Application application = emc.find(script.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			emc.beginTransaction(Script.class);
			emc.remove(script, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Script.class);
			result.setData(new WrapOutId(script.getId()));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示应用所有脚本.", response = WrapOutScript.class)
	@GET
	@Path("application/{applicationId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithApplication(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId) {
		ActionResult<List<WrapOutScript>> result = new ActionResult<>();
		List<WrapOutScript> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			List<String> ids = business.script().listWithApplication(application.getId());
			for (Script o : emc.list(Script.class, ids)) {
				wraps.add(outCopier.copy(o));
			}
			Collections.sort(wraps, new Comparator<WrapOutScript>() {
				public int compare(WrapOutScript o1, WrapOutScript o2) {
					return ObjectUtils.compare(o1.getName(), o2.getName(), true);
				}
			});
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据应用ID和脚本名称获取脚本.", response = WrapOutScript.class)
	@GET
	@Path("application/{applicationId}/name/{name}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithApplicationWithName(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId, @PathParam("name") String name) {
		ActionResult<WrapOutScript> result = new ActionResult<>();
		WrapOutScript wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			String id = business.script().getWithApplicationWithName(application.getId(), name);
			if (StringUtils.isNotEmpty(id)) {
				Script script = emc.find(id, Script.class);
				wrap = outCopier.copy(script);
			} else {
				throw new Exception("script not existed with name or alias : " + name + ".");
			}
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}