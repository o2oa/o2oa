package com.x.processplatform.assemble.designer.jaxrs.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.role.RoleDefinition;
import com.x.base.core.utils.SortTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapin.WrapInApplication;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplication;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplicationSummary;
import com.x.processplatform.assemble.designer.wrapout.WrapOutForm;
import com.x.processplatform.assemble.designer.wrapout.WrapOutProcess;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.DataItem;
import com.x.processplatform.core.entity.content.Read;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.SerialNumber;
import com.x.processplatform.core.entity.content.Task;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.content.WorkLog;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;
import com.x.processplatform.core.entity.element.Begin;
import com.x.processplatform.core.entity.element.Cancel;
import com.x.processplatform.core.entity.element.Choice;
import com.x.processplatform.core.entity.element.Delay;
import com.x.processplatform.core.entity.element.Embed;
import com.x.processplatform.core.entity.element.End;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.QueryView;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;

@Path("application")
public class ApplicationAction extends StandardJaxrsAction {

	private BeanCopyTools<Application, WrapOutApplication> outCopier = BeanCopyToolsBuilder.create(Application.class,
			WrapOutApplication.class, null, WrapOutApplication.Excludes);

	private BeanCopyTools<Application, WrapOutApplicationSummary> summaryOutCopier = BeanCopyToolsBuilder
			.create(Application.class, WrapOutApplicationSummary.class, null, WrapOutApplicationSummary.Excludes);

	private BeanCopyTools<WrapInApplication, Application> inCopier = BeanCopyToolsBuilder
			.create(WrapInApplication.class, Application.class, null, WrapInApplication.Excludes);

	@HttpMethodDescribe(value = "列示所有应用，同时附带流程简要信息和表单简要信息.返回值按名称进行排序", response = WrapOutApplicationSummary.class)
	@GET
	@Path("list/summary")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSummary(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutApplicationSummary>> result = new ActionResult<>();
		List<WrapOutApplicationSummary> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			List<String> ids = business.application().listWithPerson(effectivePerson);
			/* 由于有多值字段所以需要全部取出 */
			for (Application o : emc.list(Application.class, ids)) {
				WrapOutApplicationSummary wrap = summaryOutCopier.copy(o);
				wrap.setProcessList(this.wrapOutProcessWithApplication(business, o.getId()));
				wrap.setFormList(this.wrapOutFormWithApplication(business, o.getId()));
				wraps.add(wrap);
			}
			this.sortWrapOutApplicationSummary(wraps);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取单个应用信息.", response = WrapOutApplication.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutApplication> result = new ActionResult<>();
		WrapOutApplication wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			Application application = emc.find(id, Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			wrap = outCopier.copy(application);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示所有应用", response = WrapOutApplication.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutApplication>> result = new ActionResult<>();
		List<WrapOutApplication> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			List<String> ids = business.application().listWithPerson(effectivePerson);
			/* 由于有多值字段所以需要全部取出 */
			wraps = outCopier.copy(emc.list(Application.class, ids));
			SortTools.asc(wraps, false, "name");
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据应用分类获取应用,应用分类不为null.", response = WrapOutApplication.class)
	@GET
	@Path("list/applicationcategory/{applicationCategory}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWithApplicationCategory(@Context HttpServletRequest request,
			@PathParam("applicationCategory") String applicationCategory) {
		ActionResult<List<WrapOutApplication>> result = new ActionResult<>();
		List<WrapOutApplication> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			List<String> ids = business.application().listWithPersonWithApplicationCategory(effectivePerson,
					applicationCategory);
			/* 由于有多值字段所以需要全部取出 */
			for (Application o : emc.list(Application.class, ids)) {
				WrapOutApplication wrap = outCopier.copy(o);
				wraps.add(wrap);
			}
			Collections.sort(wraps, new Comparator<WrapOutApplication>() {
				public int compare(WrapOutApplication o1, WrapOutApplication o2) {
					/* ASC */
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

	@HttpMethodDescribe(value = "根据应用分类获取应用,应用分类不为null.", response = WrapOutApplication.class)
	@GET
	@Path("list/summary/applicationcategory/{applicationCategory}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSummaryWithApplicationCategory(@Context HttpServletRequest request,
			@PathParam("applicationCategory") String applicationCategory) {
		ActionResult<List<WrapOutApplicationSummary>> result = new ActionResult<>();
		List<WrapOutApplicationSummary> wraps = new ArrayList<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			List<String> ids = business.application().listWithPersonWithApplicationCategory(effectivePerson,
					applicationCategory);
			for (Application o : emc.list(Application.class, ids)) {
				WrapOutApplicationSummary wrap = summaryOutCopier.copy(o);
				wrap.setProcessList(this.wrapOutProcessWithApplication(business, o.getId()));
				wrap.setFormList(this.wrapOutFormWithApplication(business, o.getId()));
				wraps.add(wrap);
			}
			this.sortWrapOutApplicationSummary(wraps);
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建应用.", request = WrapInApplication.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInApplication wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			if ((!business.organization().role().hasAny(effectivePerson.getName(),
					RoleDefinition.ProcessPlatformCreator, RoleDefinition.ProcessPlatformManager,
					RoleDefinition.Manager)) & (!effectivePerson.isManager())) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} not have sufficient permissions.");
			}
			emc.beginTransaction(Application.class);
			Application application = new Application();
			wrapIn.copyTo(application);
			application.setCreatorPerson(effectivePerson.getName());
			application.setLastUpdatePerson(effectivePerson.getName());
			application.setLastUpdateTime(new Date());
			emc.persist(application, CheckPersistType.all);
			emc.commit();
			ApplicationCache.notify(Application.class);
			wrap = new WrapOutId(application.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新应用.", request = WrapInApplication.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInApplication wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			emc.beginTransaction(Application.class);
			Application application = emc.find(id, Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			inCopier.copy(wrapIn, application);
			application.setLastUpdatePerson(effectivePerson.getName());
			application.setLastUpdateTime(new Date());
			emc.commit();
			ApplicationCache.notify(Application.class);
			wrap = new WrapOutId(application.getId());
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "删除应用,将同时删除流程和表单和脚本.", response = WrapOutId.class)
	@DELETE
	@Path("{id}/{onlyRemoveNotCompleted}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("onlyRemoveNotCompleted") boolean onlyRemoveNotCompleted) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			EffectivePerson effectivePerson = this.effectivePerson(request);
			Business business = new Business(emc);
			Application application = emc.find(id, Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			/* 先删除content内容 */
			this.delete_task(business, application);
			this.delete_taskCompleted(business, application, onlyRemoveNotCompleted);
			this.delete_read(business, application);
			this.delete_readCompleted(business, application, onlyRemoveNotCompleted);
			this.delete_review(business, application, onlyRemoveNotCompleted);
			this.delete_attachment(business, application, onlyRemoveNotCompleted);
			// this.delete_attachmentLog(business, application,
			// onlyRemoveNotCompleted);
			this.delete_dataItem(business, application, onlyRemoveNotCompleted);
			this.delete_serialNumber(business, application);
			this.delete_work(business, application);
			if (!onlyRemoveNotCompleted) {
				this.delete_workCompleted(business, application);
			}
			this.delete_workLog(business, application, onlyRemoveNotCompleted);
			/* 删除数据字典和数据字典数据 */
			this.delete_applicationDictItem(business, application);
			this.delete_applicationDict(business, application);
			/* 再删除设计 */
			emc.beginTransaction(Application.class);
			emc.beginTransaction(Process.class);
			emc.beginTransaction(Agent.class);
			emc.beginTransaction(Begin.class);
			emc.beginTransaction(Cancel.class);
			emc.beginTransaction(Choice.class);
			emc.beginTransaction(Delay.class);
			emc.beginTransaction(Embed.class);
			emc.beginTransaction(End.class);
			emc.beginTransaction(Invoke.class);
			emc.beginTransaction(Manual.class);
			emc.beginTransaction(Merge.class);
			emc.beginTransaction(Message.class);
			emc.beginTransaction(Parallel.class);
			emc.beginTransaction(Service.class);
			emc.beginTransaction(Split.class);
			emc.beginTransaction(Route.class);
			emc.beginTransaction(Form.class);
			emc.beginTransaction(Script.class);
			for (String str : business.process().listWithApplication(id)) {
				Process process = emc.find(str, Process.class);
				this.delete_agent(business, process);
				this.delete_begin(business, process);
				this.delete_cancel(business, process);
				this.delete_choice(business, process);
				this.delete_delay(business, process);
				this.delete_embed(business, process);
				this.delete_end(business, process);
				this.delete_invoke(business, process);
				this.delete_manual(business, process);
				this.delete_merge(business, process);
				this.delete_message(business, process);
				this.delete_parallel(business, process);
				this.delete_route(business, process);
				this.delete_service(business, process);
				this.delete_split(business, process);
				emc.remove(process);
			}
			this.delete_form(business, application);
			this.delete_script(business, application);
			this.delete_serialNumber(business, application);
			this.delete_queryView(business, application);
			emc.remove(application);
			emc.commit();
			ApplicationCache.notify(Application.class);
			ApplicationCache.notify(Process.class);
			ApplicationCache.notify(Agent.class);
			ApplicationCache.notify(Begin.class);
			ApplicationCache.notify(Cancel.class);
			ApplicationCache.notify(Choice.class);
			ApplicationCache.notify(Delay.class);
			ApplicationCache.notify(Embed.class);
			ApplicationCache.notify(End.class);
			ApplicationCache.notify(Invoke.class);
			ApplicationCache.notify(Manual.class);
			ApplicationCache.notify(Merge.class);
			ApplicationCache.notify(Message.class);
			ApplicationCache.notify(Parallel.class);
			ApplicationCache.notify(Service.class);
			ApplicationCache.notify(Split.class);
			ApplicationCache.notify(Route.class);
			ApplicationCache.notify(Form.class);
			ApplicationCache.notify(Script.class);
			ApplicationCache.notify(SerialNumber.class);
			result.setData(new WrapOutId(application.getId()));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	/* 用于拼装applicationSummary下的Process信息 */
	private List<WrapOutProcess> wrapOutProcessWithApplication(Business business, String application) throws Exception {
		List<WrapOutProcess> list = new ArrayList<>();
		List<String> ids = business.process().listWithApplication(application);
		for (Process o : business.entityManagerContainer().fetchAttribute(ids, Process.class, "name", "updateTime")) {
			WrapOutProcess wrap = new WrapOutProcess();
			o.copyTo(wrap);
			list.add(wrap);
		}
		this.sortWrapOutProcess(list);
		return list;
	}

	/* 用于拼装applicationSummary下的Form信息 */
	private List<WrapOutForm> wrapOutFormWithApplication(Business business, String application) throws Exception {
		List<WrapOutForm> list = new ArrayList<>();
		List<String> ids = business.form().listWithApplication(application);
		for (Form o : business.entityManagerContainer().fetchAttribute(ids, Form.class, "name", "updateTime")) {
			WrapOutForm wrap = new WrapOutForm();
			o.copyTo(wrap);
			list.add(wrap);
		}
		this.sortWrapOutForm(list);
		return list;
	}

	private void sortWrapOutApplicationSummary(List<WrapOutApplicationSummary> list) {
		Collections.sort(list, new Comparator<WrapOutApplicationSummary>() {
			public int compare(WrapOutApplicationSummary o1, WrapOutApplicationSummary o2) {
				/* ASC */
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}

	private void sortWrapOutProcess(List<WrapOutProcess> list) {
		Collections.sort(list, new Comparator<WrapOutProcess>() {
			public int compare(WrapOutProcess o1, WrapOutProcess o2) {
				/* ASC */
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}

	private void sortWrapOutForm(List<WrapOutForm> list) {
		Collections.sort(list, new Comparator<WrapOutForm>() {
			public int compare(WrapOutForm o1, WrapOutForm o2) {
				/* ASC */
				return ObjectUtils.compare(o1.getName(), o2.getName(), true);
			}
		});
	}

	private void delete_agent(Business business, Process process) throws Exception {
		for (String str : business.agent().listWithProcess(process.getId())) {
			Agent o = business.entityManagerContainer().find(str, Agent.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_begin(Business business, Process process) throws Exception {
		String str = business.begin().getWithProcess(process.getId());
		if (StringUtils.isNoneEmpty(str)) {
			Begin o = business.entityManagerContainer().find(str, Begin.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_cancel(Business business, Process process) throws Exception {
		for (String str : business.cancel().listWithProcess(process.getId())) {
			Cancel o = business.entityManagerContainer().find(str, Cancel.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_choice(Business business, Process process) throws Exception {
		for (String str : business.choice().listWithProcess(process.getId())) {
			Choice o = business.entityManagerContainer().find(str, Choice.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_delay(Business business, Process process) throws Exception {
		for (String str : business.delay().listWithProcess(process.getId())) {
			Delay o = business.entityManagerContainer().find(str, Delay.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_embed(Business business, Process process) throws Exception {
		for (String str : business.embed().listWithProcess(process.getId())) {
			Embed o = business.entityManagerContainer().find(str, Embed.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_end(Business business, Process process) throws Exception {
		for (String str : business.end().listWithProcess(process.getId())) {
			End o = business.entityManagerContainer().find(str, End.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_invoke(Business business, Process process) throws Exception {
		for (String str : business.invoke().listWithProcess(process.getId())) {
			Invoke o = business.entityManagerContainer().find(str, Invoke.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_manual(Business business, Process process) throws Exception {
		for (String str : business.manual().listWithProcess(process.getId())) {
			Manual o = business.entityManagerContainer().find(str, Manual.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_merge(Business business, Process process) throws Exception {
		for (String str : business.merge().listWithProcess(process.getId())) {
			Merge o = business.entityManagerContainer().find(str, Merge.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_message(Business business, Process process) throws Exception {
		for (String str : business.message().listWithProcess(process.getId())) {
			Message o = business.entityManagerContainer().find(str, Message.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_parallel(Business business, Process process) throws Exception {
		for (String str : business.parallel().listWithProcess(process.getId())) {
			Parallel o = business.entityManagerContainer().find(str, Parallel.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_route(Business business, Process process) throws Exception {
		for (String str : business.route().listWithProcess(process.getId())) {
			Route o = business.entityManagerContainer().find(str, Route.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_service(Business business, Process process) throws Exception {
		for (String str : business.service().listWithProcess(process.getId())) {
			Service o = business.entityManagerContainer().find(str, Service.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_split(Business business, Process process) throws Exception {
		for (String str : business.split().listWithProcess(process.getId())) {
			Split o = business.entityManagerContainer().find(str, Split.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_form(Business business, Application application) throws Exception {
		for (String str : business.form().listWithApplication(application.getId())) {
			Form o = business.entityManagerContainer().find(str, Form.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_script(Business business, Application application) throws Exception {
		for (String str : business.script().listWithApplication(application.getId())) {
			Script o = business.entityManagerContainer().find(str, Script.class);
			business.entityManagerContainer().remove(o);
		}
	}

	private void delete_applicationDict(Business business, Application application) throws Exception {
		List<String> ids = business.applicationDict().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), ApplicationDict.class, ids);
	}

	private void delete_applicationDictItem(Business business, Application application) throws Exception {
		List<String> ids = business.applicationDictItem().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), ApplicationDictItem.class, ids);
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

	private void delete_attachment(Business business, Application application, boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.attachment().listWithApplicationWithCompleted(application.getId(), false)
				: business.attachment().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), Attachment.class, ids);
	}

	// private void delete_attachmentLog(Business business, Application
	// application, boolean onlyRemoveNotCompleted)
	// throws Exception {
	// List<String> ids = onlyRemoveNotCompleted
	// ?
	// business.attachmentLog().listWithApplicationWithCompleted(application.getId(),
	// false)
	// : business.attachmentLog().listWithApplication(application.getId());
	// this.delete_batch(business.entityManagerContainer(), AttachmentLog.class,
	// ids);
	// }

	private void delete_dataItem(Business business, Application application, boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.dataItem().listWithApplicationWithCompleted(application.getId(), false)
				: business.dataItem().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), DataItem.class, ids);
	}

	private void delete_serialNumber(Business business, Application application) throws Exception {
		List<String> ids = business.serialNumber().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), SerialNumber.class, ids);
	}

	private void delete_queryView(Business business, Application application) throws Exception {
		List<String> ids = business.queryView().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), QueryView.class, ids);
	}

	private void delete_task(Business business, Application application) throws Exception {
		List<String> ids = business.task().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), Task.class, ids);
	}

	private void delete_work(Business business, Application application) throws Exception {
		List<String> ids = business.work().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), Work.class, ids);
	}

	private void delete_workCompleted(Business business, Application application) throws Exception {
		List<String> ids = business.workCompleted().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), WorkCompleted.class, ids);
	}

	private void delete_workLog(Business business, Application application, Boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.workLog().listWithApplicationWithCompleted(application.getId(), false)
				: business.workLog().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), WorkLog.class, ids);
	}

	private void delete_taskCompleted(Business business, Application application, Boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.taskCompleted().listWithApplicationWithCompleted(application.getId(), false)
				: business.taskCompleted().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), TaskCompleted.class, ids);
	}

	private void delete_read(Business business, Application application) throws Exception {
		List<String> ids = business.read().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), Read.class, ids);
	}

	private void delete_readCompleted(Business business, Application application, Boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.readCompleted().listWithApplicationWithCompleted(application.getId(), false)
				: business.readCompleted().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), ReadCompleted.class, ids);
	}

	private void delete_review(Business business, Application application, Boolean onlyRemoveNotCompleted)
			throws Exception {
		List<String> ids = onlyRemoveNotCompleted
				? business.review().listWithApplicationWithCompleted(application.getId(), false)
				: business.review().listWithApplication(application.getId());
		this.delete_batch(business.entityManagerContainer(), Review.class, ids);
	}
}