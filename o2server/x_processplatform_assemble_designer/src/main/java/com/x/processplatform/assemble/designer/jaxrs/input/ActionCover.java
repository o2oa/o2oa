package com.x.processplatform.assemble.designer.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.assemble.designer.Business;
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
import com.x.processplatform.core.entity.element.File;
import com.x.processplatform.core.entity.element.Form;
import com.x.processplatform.core.entity.element.FormField;
import com.x.processplatform.core.entity.element.Invoke;
import com.x.processplatform.core.entity.element.Manual;
import com.x.processplatform.core.entity.element.Merge;
import com.x.processplatform.core.entity.element.Message;
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.element.Script;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;
import com.x.processplatform.core.entity.element.wrap.WrapAgent;
import com.x.processplatform.core.entity.element.wrap.WrapApplicationDict;
import com.x.processplatform.core.entity.element.wrap.WrapBegin;
import com.x.processplatform.core.entity.element.wrap.WrapCancel;
import com.x.processplatform.core.entity.element.wrap.WrapChoice;
import com.x.processplatform.core.entity.element.wrap.WrapDelay;
import com.x.processplatform.core.entity.element.wrap.WrapEmbed;
import com.x.processplatform.core.entity.element.wrap.WrapEnd;
import com.x.processplatform.core.entity.element.wrap.WrapFile;
import com.x.processplatform.core.entity.element.wrap.WrapForm;
import com.x.processplatform.core.entity.element.wrap.WrapFormField;
import com.x.processplatform.core.entity.element.wrap.WrapInvoke;
import com.x.processplatform.core.entity.element.wrap.WrapManual;
import com.x.processplatform.core.entity.element.wrap.WrapMerge;
import com.x.processplatform.core.entity.element.wrap.WrapMessage;
import com.x.processplatform.core.entity.element.wrap.WrapParallel;
import com.x.processplatform.core.entity.element.wrap.WrapProcess;
import com.x.processplatform.core.entity.element.wrap.WrapProcessPlatform;
import com.x.processplatform.core.entity.element.wrap.WrapRoute;
import com.x.processplatform.core.entity.element.wrap.WrapScript;
import com.x.processplatform.core.entity.element.wrap.WrapService;
import com.x.processplatform.core.entity.element.wrap.WrapSplit;

class ActionCover extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCover.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		// logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Application application = business.entityManagerContainer().find(wi.getId(), Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(wi.getId());
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			this.cover(business, wi, application);
			wo.setId(application.getId());
			result.setData(wo);
			return result;
		}
	}

	private void cover(Business business, Wi wi, Application application) throws Exception {
		List<JpaObject> persistObjects = new ArrayList<>();
		List<JpaObject> removeObjects = new ArrayList<>();
		for (WrapForm _o : wi.getFormList()) {
			Form form = business.entityManagerContainer().find(_o.getId(), Form.class);
			if (null != form) {
				WrapForm.inCopier.copy(_o, form);
			} else {
				form = WrapForm.inCopier.copy(_o);
				persistObjects.add(form);
			}
			if (StringUtils.isNotEmpty(form.getAlias())) {
				form.setAlias(this.idleAliasWithApplication(business, application.getId(), form.getAlias(), Form.class,
						form.getId()));
			}
			if (StringUtils.isNotEmpty(form.getName())) {
				form.setName(this.idleNameWithApplication(business, application.getId(), form.getName(), Form.class,
						form.getId()));
			}
			form.setApplication(application.getId());
			persistObjects.addAll(this.coverFormElement(business, form, WrapFormField.inCopier, _o.getFormFieldList(),
					FormField.class));
			removeObjects
					.addAll(this.orphanFormElement(business, _o.getFormFieldList(), FormField.class, form.getId()));
		}
		for (WrapScript _o : wi.getScriptList()) {
			Script obj = business.entityManagerContainer().find(_o.getId(), Script.class);
			if (null != obj) {
				WrapScript.inCopier.copy(_o, obj);
			} else {
				obj = WrapScript.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(this.idleAliasWithApplication(business, application.getId(), obj.getAlias(), Script.class,
						obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithApplication(business, application.getId(), obj.getName(), Script.class,
						obj.getId()));
			}
			obj.setApplication(application.getId());
		}
		for (WrapFile _o : wi.getFileList()) {
			File obj = business.entityManagerContainer().find(_o.getId(), File.class);
			if (null != obj) {
				WrapFile.inCopier.copy(_o, obj);
			} else {
				obj = WrapFile.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(this.idleAliasWithApplication(business, application.getId(), obj.getAlias(), File.class,
						obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithApplication(business, application.getId(), obj.getName(), File.class,
						obj.getId()));
			}
			obj.setApplication(application.getId());
		}
		for (WrapApplicationDict _o : wi.getApplicationDictList()) {
			ApplicationDict obj = business.entityManagerContainer().find(_o.getId(), ApplicationDict.class);
			if (null != obj) {
				for (ApplicationDictItem o : business.applicationDictItem()
						.listWithApplicationDictObject(obj.getId())) {
					removeObjects.add(o);
				}
				WrapApplicationDict.inCopier.copy(_o, obj);
			} else {
				obj = WrapApplicationDict.inCopier.copy(_o);
				persistObjects.add(obj);
			}
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			List<ApplicationDictItem> list = converter.disassemble(_o.getData());
			for (ApplicationDictItem o : list) {
				o.setBundle(obj.getId());
				/** 将数据字典和数据存放在同一个分区 */
				o.setDistributeFactor(obj.getDistributeFactor());
				o.setApplication(obj.getApplication());
				persistObjects.add(o);
			}
			if (StringUtils.isNotEmpty(obj.getAlias())) {
				obj.setAlias(this.idleAliasWithApplication(business, application.getId(), obj.getAlias(),
						ApplicationDict.class, obj.getId()));
			}
			if (StringUtils.isNotEmpty(obj.getName())) {
				obj.setName(this.idleNameWithApplication(business, application.getId(), obj.getName(),
						ApplicationDict.class, obj.getId()));
			}
			obj.setApplication(application.getId());
		}
		for (WrapProcess wrapProcess : wi.getProcessList()) {
			Process process = business.entityManagerContainer().find(wrapProcess.getId(), Process.class);
			if (null != process) {
				WrapProcess.inCopier.copy(wrapProcess, process);
			} else {
				process = WrapProcess.inCopier.copy(wrapProcess);
				persistObjects.add(process);
			}
			if (StringUtils.isNotEmpty(process.getAlias())) {
				process.setAlias(this.idleAliasWithApplication(business, application.getId(), process.getAlias(),
						Process.class, process.getId()));
			}
			if (StringUtils.isNotEmpty(process.getName())) {
				process.setName(this.idleNameWithApplication(business, application.getId(), process.getName(),
						Process.class, process.getId()));
			}
			process.setApplication(application.getId());
			persistObjects.addAll(this.coverProcessElement(business, process, WrapAgent.inCopier,
					wrapProcess.getAgentList(), Agent.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapBegin.inCopier,
					wrapProcess.getBegin(), Begin.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapCancel.inCopier,
					wrapProcess.getCancelList(), Cancel.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapChoice.inCopier,
					wrapProcess.getChoiceList(), Choice.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapDelay.inCopier,
					wrapProcess.getDelayList(), Delay.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapEmbed.inCopier,
					wrapProcess.getEmbedList(), Embed.class));
			persistObjects.addAll(
					this.coverProcessElement(business, process, WrapEnd.inCopier, wrapProcess.getEndList(), End.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapInvoke.inCopier,
					wrapProcess.getInvokeList(), Invoke.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapManual.inCopier,
					wrapProcess.getManualList(), Manual.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapMerge.inCopier,
					wrapProcess.getMergeList(), Merge.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapMessage.inCopier,
					wrapProcess.getMessageList(), Message.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapParallel.inCopier,
					wrapProcess.getParallelList(), Parallel.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapService.inCopier,
					wrapProcess.getServiceList(), Service.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapSplit.inCopier,
					wrapProcess.getSplitList(), Split.class));
			persistObjects.addAll(this.coverProcessElement(business, process, WrapRoute.inCopier,
					wrapProcess.getRouteList(), Route.class));
			removeObjects.addAll(
					this.orphanProcessElement(business, wrapProcess.getAgentList(), Agent.class, process.getId()));
			removeObjects
					.addAll(this.orphanProcessElement(business, wrapProcess.getBegin(), Begin.class, process.getId()));
			removeObjects.addAll(
					this.orphanProcessElement(business, wrapProcess.getCancelList(), Cancel.class, process.getId()));
			removeObjects.addAll(
					this.orphanProcessElement(business, wrapProcess.getChoiceList(), Choice.class, process.getId()));
			removeObjects.addAll(
					this.orphanProcessElement(business, wrapProcess.getDelayList(), Delay.class, process.getId()));
			removeObjects.addAll(
					this.orphanProcessElement(business, wrapProcess.getEmbedList(), Embed.class, process.getId()));
			removeObjects
					.addAll(this.orphanProcessElement(business, wrapProcess.getEndList(), End.class, process.getId()));
			removeObjects.addAll(
					this.orphanProcessElement(business, wrapProcess.getInvokeList(), Invoke.class, process.getId()));
			removeObjects.addAll(
					this.orphanProcessElement(business, wrapProcess.getManualList(), Manual.class, process.getId()));
			removeObjects.addAll(
					this.orphanProcessElement(business, wrapProcess.getMergeList(), Merge.class, process.getId()));
			removeObjects.addAll(
					this.orphanProcessElement(business, wrapProcess.getMessageList(), Message.class, process.getId()));
			removeObjects.addAll(this.orphanProcessElement(business, wrapProcess.getParallelList(), Parallel.class,
					process.getId()));
			removeObjects.addAll(
					this.orphanProcessElement(business, wrapProcess.getServiceList(), Service.class, process.getId()));
			removeObjects.addAll(
					this.orphanProcessElement(business, wrapProcess.getSplitList(), Split.class, process.getId()));
			removeObjects.addAll(
					this.orphanProcessElement(business, wrapProcess.getRouteList(), Route.class, process.getId()));
		}
		for (JpaObject o : persistObjects) {
			business.entityManagerContainer().persist(o);
		}
		for (JpaObject o : removeObjects) {
			business.entityManagerContainer().remove(o);
		}
		business.entityManagerContainer().beginTransaction(Application.class);
		business.entityManagerContainer().beginTransaction(File.class);
		business.entityManagerContainer().beginTransaction(Script.class);
		business.entityManagerContainer().beginTransaction(Form.class);
		business.entityManagerContainer().beginTransaction(FormField.class);
		business.entityManagerContainer().beginTransaction(ApplicationDict.class);
		business.entityManagerContainer().beginTransaction(ApplicationDictItem.class);
		// business.entityManagerContainer().beginTransaction(ApplicationDictLobItem.class);
		business.entityManagerContainer().beginTransaction(Process.class);
		business.entityManagerContainer().beginTransaction(Agent.class);
		business.entityManagerContainer().beginTransaction(Begin.class);
		business.entityManagerContainer().beginTransaction(Cancel.class);
		business.entityManagerContainer().beginTransaction(Choice.class);
		business.entityManagerContainer().beginTransaction(Delay.class);
		business.entityManagerContainer().beginTransaction(Embed.class);
		business.entityManagerContainer().beginTransaction(End.class);
		business.entityManagerContainer().beginTransaction(Invoke.class);
		business.entityManagerContainer().beginTransaction(Manual.class);
		business.entityManagerContainer().beginTransaction(Merge.class);
		business.entityManagerContainer().beginTransaction(Message.class);
		business.entityManagerContainer().beginTransaction(Parallel.class);
		business.entityManagerContainer().beginTransaction(Service.class);
		business.entityManagerContainer().beginTransaction(Split.class);
		business.entityManagerContainer().beginTransaction(Route.class);
		business.entityManagerContainer().commit();
	}

	private <T extends JpaObject, W extends JpaObject> List<T> orphanFormElement(Business business, List<W> list,
			Class<T> cls, String formId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		Predicate p = cb.not(root.get(JpaObject.id_FIELDNAME)
				.in(ListTools.extractProperty(list, JpaObject.id_FIELDNAME, String.class, true, true)));
		p = cb.and(p, cb.equal(root.get(FormField.form_FIELDNAME), formId));
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	private <T extends JpaObject, W extends JpaObject> List<T> orphanProcessElement(Business business, List<W> list,
			Class<T> cls, String processId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		Predicate p = cb.not(root.get(JpaObject.id_FIELDNAME)
				.in(ListTools.extractProperty(list, JpaObject.id_FIELDNAME, String.class, true, true)));
		p = cb.and(p, cb.equal(root.get("process"), processId));
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	private <W extends JpaObject, T extends JpaObject> List<T> orphanProcessElement(Business business, W w,
			Class<T> cls, String processId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		Predicate p = cb.notEqual(root.get(JpaObject.id_FIELDNAME), w.getId());
		p = cb.and(p, cb.equal(root.get("process"), processId));
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	private <T extends JpaObject, W extends JpaObject> List<T> coverFormElement(Business business, Form form,
			WrapCopier<W, T> copier, List<W> list, Class<T> cls) throws Exception {
		List<T> os = new ArrayList<>();
		for (W w : list) {
			os.addAll(this.coverFormElement(business, form, copier, w, cls));
		}
		return os;
	}

	private <W extends JpaObject, T extends JpaObject> List<T> coverFormElement(Business business, Form form,
			WrapCopier<W, T> copier, W w, Class<T> cls) throws Exception {
		List<T> os = new ArrayList<>();
		T _t = business.entityManagerContainer().find(w.getId(), cls);
		if (null != _t) {
			copier.copy(w, _t);
		} else {
			_t = copier.copy(w);
			os.add(_t);
		}
		FieldUtils.writeField(_t, FormField.form_FIELDNAME, form.getId(), true);
		return os;
	}

	private <T extends JpaObject, W extends JpaObject> List<T> coverProcessElement(Business business, Process process,
			WrapCopier<W, T> copier, List<W> list, Class<T> cls) throws Exception {
		List<T> os = new ArrayList<>();
		for (W w : list) {
			os.addAll(this.coverProcessElement(business, process, copier, w, cls));
		}
		return os;
	}

	private <W extends JpaObject, T extends JpaObject> List<T> coverProcessElement(Business business, Process process,
			WrapCopier<W, T> copier, W w, Class<T> cls) throws Exception {
		List<T> os = new ArrayList<>();
		T _t = business.entityManagerContainer().find(w.getId(), cls);
		if (null != _t) {
			copier.copy(w, _t);
		} else {
			_t = copier.copy(w);
			os.add(_t);
		}
		FieldUtils.writeField(_t, Agent.process_FIELDNAME, process.getId(), true);
		return os;
	}

	private <T extends JpaObject> String idleNameWithApplication(Business business, String applicationId, String name,
			Class<T> cls, String excludeId) throws Exception {
		if (StringUtils.isEmpty(name)) {
			return "";
		}
		List<String> list = new ArrayList<>();
		list.add(name);
		for (int i = 1; i < 99; i++) {
			list.add(name + String.format("%02d", i));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = root.get("name").in(list);
		p = cb.and(p, cb.equal(root.get("application"), applicationId));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get("name")).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	private <T extends JpaObject> String idleAliasWithApplication(Business business, String applicationId, String alias,
			Class<T> cls, String excludeId) throws Exception {
		if (StringUtils.isEmpty(alias)) {
			return "";
		}
		List<String> list = new ArrayList<>();
		list.add(alias);
		for (int i = 1; i < 99; i++) {
			list.add(alias + String.format("%02d", i));
		}
		list.add(StringTools.uniqueToken());
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<T> root = cq.from(cls);
		Predicate p = root.get("alias").in(list);
		p = cb.and(p, cb.equal(root.get("application"), applicationId));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(JpaObject.id_FIELDNAME), excludeId));
		}
		cq.select(root.get("alias")).where(p);
		List<String> os = em.createQuery(cq).getResultList();
		list = ListUtils.subtract(list, os);
		return list.get(0);
	}

	public static class Wi extends WrapProcessPlatform {

		private static final long serialVersionUID = -4612391443319365035L;

	}

	public static class Wo extends WoId {

	}

}