package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.ReadCompleted_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted.ActionFilterAttributeFilterWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.readcompleted.ActionFilterAttributeFilterWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionFilterAttributeFilter extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionFilterAttributeFilter.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Predicate p = predicate(effectivePerson, business, wi);
			Wo wo = new Wo();
			wo.getApplicationList().addAll(this.listApplicationPair(business, p));
			wo.getProcessList().addAll(this.listProcessPair(business, p));
			wo.getCreatorUnitList().addAll(this.listCreatorUnitPair(business, p));
			wo.getStartTimeMonthList().addAll(this.listStartTimeMonthPair(business, p));
			wo.getCompletedTimeMonthList().addAll(this.listCompletedTimeMonthPair(business, p));
			wo.getActivityNameList().addAll(this.listActivityNamePair(business, p));
			wo.getCompletedList().addAll(this.listCompletedPair(business, p));
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.ActionFilterAttributeFilter$Wi")
	public class Wi extends ActionFilterAttributeFilterWi {

		private static final long serialVersionUID = -8042234611305053649L;

	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.readcompleted.ActionFilterAttributeFilter$Wo")
	public static class Wo extends ActionFilterAttributeFilterWo {

		private static final long serialVersionUID = 2822821335310558473L;

	}

	private Predicate predicate(EffectivePerson effectivePerson, Business business, Wi wi) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getDistinguishedName());
		if (ListTools.isNotEmpty(wi.getApplicationList())) {
			p = cb.and(p, root.get(ReadCompleted_.application).in(wi.getApplicationList()));
		}
		if (ListTools.isNotEmpty(wi.getProcessList())) {
			p = cb.and(p, root.get(ReadCompleted_.process).in(wi.getProcessList()));
		}
		if (ListTools.isNotEmpty(wi.getCreatorUnitList())) {
			p = cb.and(p, root.get(ReadCompleted_.creatorUnit).in(wi.getCreatorUnitList()));
		}
		if (ListTools.isNotEmpty(wi.getStartTimeMonthList())) {
			p = cb.and(p, root.get(ReadCompleted_.startTimeMonth).in(wi.getStartTimeMonthList()));
		}
		if (ListTools.isNotEmpty(wi.getCompletedTimeMonthList())) {
			p = cb.and(p, root.get(ReadCompleted_.completedTimeMonth).in(wi.getCompletedTimeMonthList()));
		}
		if (ListTools.isNotEmpty(wi.getActivityNameList())) {
			p = cb.and(p, root.get(ReadCompleted_.activityName).in(wi.getActivityNameList()));
		}
		// completed对象按单值处理
		if (ListTools.isNotEmpty(wi.getCompletedList())) {
			boolean value = BooleanUtils.isTrue(wi.getCompletedList().get(0));
			if (value) {
				p = cb.and(p, cb.isTrue(root.get(ReadCompleted_.completed)));
			} else {
				p = cb.and(p, cb.or(cb.isNull(root.get(ReadCompleted_.completed)),
						cb.isFalse(root.get(ReadCompleted_.completed))));
			}
		}
		return p;
	}

	private List<NameValueCountPair> listApplicationPair(Business business, Predicate p) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		cq.select(root.get(ReadCompleted_.application)).where(p);
		List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : os) {
			if (StringUtils.isNotEmpty(str)) {
				NameValueCountPair o = new NameValueCountPair();
				Application application = business.application().pick(str);
				if (null != application) {
					o.setValue(application.getId());
					o.setName(application.getName());
				} else {
					o.setValue(str);
					o.setName(str);
				}
				wos.add(o);
			}
		}
		wos = wos.stream().sorted(Comparator.comparing(NameValueCountPair::getName, (s1, s2) -> {
			return Objects.toString(s1, "").compareTo(Objects.toString(s2, ""));
		})).collect(Collectors.toList());
		return wos;
	}

	private List<NameValueCountPair> listProcessPair(Business business, Predicate p) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		cq.select(root.get(ReadCompleted_.process)).where(p);
		List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : os) {
			if (StringUtils.isNotEmpty(str)) {
				NameValueCountPair o = new NameValueCountPair();
				Process process = business.process().pick(str);
				if (null != process) {
					o.setValue(process.getId());
					o.setName(process.getName());
				} else {
					o.setValue(str);
					o.setName(str);
				}
				wos.add(o);
			}
		}
		wos = wos.stream().sorted(Comparator.comparing(NameValueCountPair::getName, (s1, s2) -> {
			return Objects.toString(s1, "").compareTo(Objects.toString(s2, ""));
		})).collect(Collectors.toList());
		return wos;
	}

	private List<NameValueCountPair> listCreatorUnitPair(Business business, Predicate p) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		cq.select(root.get(ReadCompleted_.creatorUnit)).where(p);
		List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : os) {
			if (StringUtils.isNotEmpty(str)) {
				NameValueCountPair o = new NameValueCountPair();
				o.setValue(str);
				o.setName(StringUtils.defaultString(StringUtils.substringBefore(str, "@"), str));
				wos.add(o);
			}
		}
		wos = wos.stream().sorted(Comparator.comparing(NameValueCountPair::getName, (s1, s2) -> {
			return Objects.toString(s1, "").compareTo(Objects.toString(s2, ""));
		})).collect(Collectors.toList());
		return wos;
	}

	private List<NameValueCountPair> listActivityNamePair(Business business, Predicate p) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		cq.select(root.get(ReadCompleted_.activityName)).where(p);
		List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : os) {
			if (StringUtils.isNotEmpty(str)) {
				NameValueCountPair o = new NameValueCountPair();
				o.setValue(str);
				o.setName(str);
				wos.add(o);
			}
		}
		wos = wos.stream().sorted(Comparator.comparing(NameValueCountPair::getName, (s1, s2) -> {
			return Objects.toString(s1, "").compareTo(Objects.toString(s2, ""));
		})).collect(Collectors.toList());
		return wos;
	}

	private List<NameValueCountPair> listStartTimeMonthPair(Business business, Predicate p) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		cq.select(root.get(ReadCompleted_.startTimeMonth)).where(p);
		List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : os) {
			if (StringUtils.isNotEmpty(str)) {
				NameValueCountPair o = new NameValueCountPair();
				o.setValue(str);
				o.setName(str);
				wos.add(o);
			}
		}
		wos = wos.stream().sorted(Comparator.comparing(NameValueCountPair::getName, (s1, s2) -> {
			return Objects.toString(s1, "").compareTo(Objects.toString(s2, ""));
		})).collect(Collectors.toList());
		return wos;
	}

	private List<NameValueCountPair> listCompletedTimeMonthPair(Business business, Predicate p) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		cq.select(root.get(ReadCompleted_.completedTimeMonth)).where(p);
		List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : os) {
			if (StringUtils.isNotEmpty(str)) {
				NameValueCountPair o = new NameValueCountPair();
				o.setValue(str);
				o.setName(str);
				wos.add(o);
			}
		}
		wos = wos.stream().sorted(Comparator.comparing(NameValueCountPair::getName, (s1, s2) -> {
			return Objects.toString(s1, "").compareTo(Objects.toString(s2, ""));
		})).collect(Collectors.toList());
		return wos;
	}

	private List<NameValueCountPair> listCompletedPair(Business business, Predicate p) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		cq.select(root.get(ReadCompleted_.completed)).where(p);
		List<Boolean> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		List<NameValueCountPair> wos = new ArrayList<>();
		for (Boolean value : os) {
			NameValueCountPair o = new NameValueCountPair();
			if (BooleanUtils.isTrue(value)) {
				o.setValue(Boolean.TRUE);
				o.setName("completed");
			} else {
				o.setValue(Boolean.FALSE);
				o.setName("not completed");
			}
			wos.add(o);
		}
		return wos;
	}
}