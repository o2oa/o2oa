package com.x.processplatform.assemble.designer.jaxrs.applicationdict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;
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

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInApplicationDict;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplicationDict;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;

@Path("applicationdict")
public class ApplicationDictAction extends AbstractJaxrsAction {

	private BeanCopyTools<ApplicationDict, WrapOutApplicationDict> outCopier = BeanCopyToolsBuilder
			.create(ApplicationDict.class, WrapOutApplicationDict.class, null, WrapOutApplicationDict.Excludes);

	private BeanCopyTools<WrapInApplicationDict, ApplicationDict> inCopier = BeanCopyToolsBuilder
			.create(WrapInApplicationDict.class, ApplicationDict.class, null, WrapInApplicationDict.Excludes);

	@HttpMethodDescribe(value = "获取Application的数据字典列表.", response = WrapOutApplicationDict.class)
	@GET
	@Path("list/application/{applicationId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithApplication(@Context HttpServletRequest request,
			@PathParam("applicationId") String applicationId) {
		ActionResult<List<WrapOutApplicationDict>> result = new ActionResult<>();
		List<WrapOutApplicationDict> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Application application = emc.find(applicationId, Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			List<String> ids = business.applicationDict().listWithApplication(applicationId);
			for (ApplicationDict o : emc.list(ApplicationDict.class, ids)) {
				wraps.add(outCopier.copy(o));
			}
			Collections.sort(wraps, new Comparator<WrapOutApplicationDict>() {
				public int compare(WrapOutApplicationDict o1, WrapOutApplicationDict o2) {
					/* asc */
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

	@HttpMethodDescribe(value = "获取单个数据字典以及数据字典数据.", response = WrapOutApplicationDict.class)
	@GET
	@Path("{applicationDict}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("applicationDict") String applicationDict) {
		ActionResult<WrapOutApplicationDict> result = new ActionResult<>();
		WrapOutApplicationDict wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			ApplicationDict dict = emc.find(applicationDict, ApplicationDict.class);
			if (null == dict) {
				throw new Exception("applicationDict{id:" + applicationDict + "} not existed.");
			}
			Application application = emc.find(dict.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			wrap = outCopier.copy(dict);
			List<ApplicationDictItem> items = business.applicationDictItem()
					.listEntityWithApplicationDict(applicationDict);
			/* 由于需要排序重新生成可排序List */
			items = new ArrayList<>(items);
			ItemConverter<ApplicationDictItem> converter = new ItemConverter<>(ApplicationDictItem.class);
			JsonElement json = converter.assemble(items);
			wrap.setData(json);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建数据字典以及数据.", request = WrapInApplicationDict.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInApplicationDict wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Application application = emc.find(wrapIn.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			emc.beginTransaction(ApplicationDict.class);
			emc.beginTransaction(ApplicationDictItem.class);
			ApplicationDict applicationDict = new ApplicationDict();
			inCopier.copy(wrapIn, applicationDict);
			applicationDict.setApplication(application.getId());
			emc.persist(applicationDict, CheckPersistType.all);
			ItemConverter<ApplicationDictItem> converter = new ItemConverter<>(ApplicationDictItem.class);
			List<ApplicationDictItem> list = converter.disassemble(wrapIn.getData());
			for (ApplicationDictItem o : list) {
				o.setApplicationDict(applicationDict.getId());
				o.setApplication(application.getId());
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			ApplicationCache.notify(ApplicationDict.class);
			wrap = new WrapOutId(applicationDict.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新数据字典以及数据.", request = WrapInApplicationDict.class, response = WrapOutId.class)
	@PUT
	@Path("{applicationDict}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("applicationDict") String applicationDict,
			WrapInApplicationDict wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			ApplicationDict dict = emc.find(applicationDict, ApplicationDict.class, ExceptionWhen.not_found);
			Application application = emc.find(wrapIn.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			emc.beginTransaction(ApplicationDict.class);
			emc.beginTransaction(ApplicationDictItem.class);
			inCopier.copy(wrapIn, dict);
			dict.setApplication(application.getId());
			emc.check(dict, CheckPersistType.all);
			ItemConverter<ApplicationDictItem> converter = new ItemConverter<>(ApplicationDictItem.class);
			List<ApplicationDictItem> exists = business.applicationDictItem()
					.listEntityWithApplicationDict(applicationDict);
			List<ApplicationDictItem> currents = converter.disassemble(wrapIn.getData());
			List<ApplicationDictItem> removes = converter.subtract(exists, currents);
			List<ApplicationDictItem> adds = converter.subtract(currents, exists);
			for (ApplicationDictItem o : removes) {
				emc.remove(o);
			}
			for (ApplicationDictItem o : adds) {
				o.setApplicationDict(dict.getId());
				o.setApplication(application.getId());
				emc.persist(o, CheckPersistType.all);
			}
			emc.commit();
			ApplicationCache.notify(ApplicationDict.class);
			wrap = new WrapOutId(dict.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除指定的数据字典以及数据字典数据.", response = WrapOutId.class)
	@DELETE
	@Path("{applicationDict}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("applicationDict") String applicationDict) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			EffectivePerson effectivePerson = this.effectivePerson(request);
			ApplicationDict dict = emc.find(applicationDict, ApplicationDict.class, ExceptionWhen.not_found);
			Application application = emc.find(dict.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			List<String> ids = business.applicationDictItem().listWithApplicationDict(applicationDict);
			this.delete_batch(emc, ApplicationDictItem.class, ids);
			emc.beginTransaction(ApplicationDict.class);
			emc.remove(dict, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(ApplicationDict.class);
			wrap = new WrapOutId(dict.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private void delete_batch(EntityManagerContainer emc, Class<? extends JpaObject> clz, List<String> ids)
			throws Exception {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < ids.size(); i++) {
			list.add(ids.get(i));
			if ((list.size() == 1000) || (i == (ids.size() - 1))) {
				EntityManager em = emc.beginTransaction(clz);
				for (String str : list) {
					em.remove(em.find(clz, str));
				}
				em.getTransaction().commit();
				list.clear();
			}
		}
	}

}