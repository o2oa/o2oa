package com.x.processplatform.assemble.surface.factory.element;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

public class ProcessFactory extends ElementFactory {

	public ProcessFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Process pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	public Process pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Process.class, exceptionWhen, Process.FLAGS);
	}

	/* 获取Application下的所有流程 */
	public List<String> listWithApplication(Application application) throws Exception {
		List<String> list = new ArrayList<>();
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.equal(root.get(Process_.application), application.getId());
		cq.select(root.get(Process_.id)).where(p).distinct(true);
		list = em.createQuery(cq).getResultList();
		return list;
	}

	/* 获取用户可启动的流程，如果applicationId 为空则取到所有可启动流程 */
	public List<String> listStartableWithApplication(EffectivePerson effectivePerson, List<String> identities,
			List<String> departments, List<String> companies, Application application) throws Exception {
		List<String> list = new ArrayList<>();
		EntityManager em = this.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.conjunction();
		if (!effectivePerson.isManager()) {
			p = cb.and(cb.isEmpty(root.get(Process_.startableIdentityList)),
					cb.isEmpty(root.get(Process_.startableDepartmentList)),
					cb.isEmpty(root.get(Process_.startableCompanyList)));
			p = cb.or(p, cb.equal(root.get(Process_.creatorPerson), effectivePerson.getName()));
			if (ListTools.isNotEmpty(identities)) {
				p = cb.or(p, root.get(Process_.startableIdentityList).in(identities));
			}
			if (ListTools.isNotEmpty(departments)) {
				p = cb.or(p, root.get(Process_.startableDepartmentList).in(departments));
			}
			if (ListTools.isNotEmpty(companies)) {
				p = cb.or(p, root.get(Process_.startableCompanyList).in(companies));
			}
		}
		p = cb.and(p, cb.equal(root.get(Process_.application), application.getId()));
		cq.select(root.get(Process_.id)).where(p).distinct(true);
		list = em.createQuery(cq).getResultList();
		return list;
	}

	public List<String> listControlableProcess(EffectivePerson effectivePerson, Application application)
			throws Exception {
		Business business = this.business();
		List<String> ids = this.listWithApplication(application);
		List<String> list = new ArrayList<>();
		for (String str : ids) {
			Process o = business.process().pick(str);
			if (null != o) {
				if (effectivePerson.isUser(o.getControllerList())) {
					list.add(str);
				}
			}
		}
		return list;
	}

	public <T extends JpaObject> String pickName(String id, Class<T> clz, String person) throws Exception {
		Process o = this.pick(id);
		if (null != o) {
			return o.getName();
		} else {
			EntityManager em = this.entityManagerContainer().get(clz);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<T> root = cq.from(clz);
			Predicate p = cb.equal(root.get("process"), id);
			if (StringUtils.isNotEmpty(person)) {
				p = cb.and(p, cb.equal(root.get("person"), person));
			}
			cq.select(root.get("processName")).where(p);
			List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
			if (!list.isEmpty()) {
				return list.get(0);
			}
		}
		return null;
	}

	/* 判断用户是否有管理权限 */
	public boolean allowControl(EffectivePerson effectivePerson, Process process) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (null != process) {
			if (effectivePerson.isUser(process.getControllerList())) {
				return true;
			}
			Application application = this.business().application().pick(process.getApplication());
			if (null != application) {
				if (effectivePerson.isUser(application.getControllerList())) {
					return true;
				}
				if (effectivePerson.isUser(application.getCreatorPerson())) {
					return true;
				}
			}
		}
		return false;
	}
}