package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.ThisApplication;
import com.x.processplatform.core.entity.element.Activity;
import com.x.processplatform.core.entity.element.Agent;
import com.x.processplatform.core.entity.element.Application;
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
import com.x.processplatform.core.entity.element.Parallel;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.Service;
import com.x.processplatform.core.entity.element.Split;

class ActionListWithApplication extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithApplication.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationId) throws Exception {

		LOGGER.debug("execute:{}, applicationId:{}.", effectivePerson::getDistinguishedName, () -> applicationId);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			Business business = new Business(emc);
			Application application = emc.find(applicationId, Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(applicationId);
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			List<String> ids = business.form().listWithApplication(applicationId);

			wos = emc.fetch(ids, Wo.copier);
			wos = business.form().sort(wos);

			LinkedHashMap<String, Set<String>> formRelatedProcess = this.formRelatedProcess(business,
					wos.stream().map(Wo::getId).collect(Collectors.toList()));

			Map<String, WoProcess> processes = emc
					.fetch(formRelatedProcess.entrySet().stream().flatMap(o -> o.getValue().stream()).distinct()
							.collect(Collectors.toList()), WoProcess.copier)
					.stream().collect(Collectors.toMap(WoProcess::getId, Function.identity()));

			wos.stream().forEach(o -> {
				Set<String> set = formRelatedProcess.get(o.getId());
				if (null != set) {
					o.setProcessList(
							set.stream().map(processes::get).filter(Objects::nonNull).collect(Collectors.toList()));
				}
			});

			result.setData(wos);
			return result;
		}
	}

	/**
	 * 通过活动的form设置得到那些活动使用了表单,再通过活动的process得到processId,生成form->processId的一对多map
	 * 
	 * @param business
	 * @param formIds
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private LinkedHashMap<String, Set<String>> formRelatedProcess(Business business, List<String> formIds)
			throws InterruptedException, ExecutionException {
		return ThisApplication.forkJoinPool().submit(() -> Stream
				.of(Agent.class, Begin.class, Cancel.class, Choice.class, Delay.class, Embed.class, End.class,
						Invoke.class, Manual.class, Merge.class, Parallel.class, Publish.class, Service.class,
						Split.class)
				.parallel().<List<Tuple>>map(o -> listProcessIdsWhichFormUsedByActivity(business, formIds, o))
				.filter(o -> (!o.isEmpty())).flatMap(List::stream)
				.collect(LinkedHashMap<String, Set<String>>::new, (m, o) -> m.compute(o.get(0).toString(), (k, v) -> {
					v = (null == v ? new HashSet<>() : v);
					v.add(o.get(1).toString());
					return v;
				}), (l, r) -> r.entrySet().stream().forEach(en -> l.compute(en.getKey(), (k, v) -> {
					if (null == v) {
						return en.getValue();
					} else {
						v.addAll(en.getValue());
						return v;
					}
				})))).get();
	}

	private List<Tuple> listProcessIdsWhichFormUsedByActivity(Business business, List<String> formIds,
			Class<? extends Activity> o) {
		try {
			EntityManager em = business.entityManagerContainer().get(o);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			Root<?> root = cq.from(o);
			Predicate p = root.get(Activity.form_FIELDNAME).in(formIds);
			return em.createQuery(
					cq.multiselect(root.get(Activity.form_FIELDNAME), root.get(Activity.process_FIELDNAME)).where(p))
					.getResultList();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return new ArrayList<>();
	}

	public static class Wo extends Form {

		private static final long serialVersionUID = -7495725325510376323L;

		private List<WoProcess> processList;

		public List<WoProcess> getProcessList() {
			return processList;
		}

		public void setProcessList(List<WoProcess> processList) {
			this.processList = processList;
		}

		public static final WrapCopier<Form, Wo> copier = WrapCopierFactory.wo(Form.class, Wo.class,
				JpaObject.singularAttributeField(Form.class, true, true),
				ListTools.toList(JpaObject.FieldsInvisible, Form.data_FIELDNAME, Form.mobileData_FIELDNAME));

	}

	public static class WoProcess extends Process {

		private static final long serialVersionUID = -3529711319985696566L;

		public static final WrapCopier<Process, WoProcess> copier = WrapCopierFactory.wo(Process.class, WoProcess.class,
				Arrays.asList(JpaObject.id_FIELDNAME, Process.name_FIELDNAME), null);

	}

}
