package com.x.processplatform.assemble.surface.jaxrs.readcompleted;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.bean.NameValueCountPair;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.ReadCompleted_;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;

class ActionListCountWithProcess extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListCountWithProcess.class);

	ActionResult<List<NameValueCountPair>> execute(EffectivePerson effectivePerson, String applicationFlag)
			throws Exception {

		LOGGER.debug("execute:{}, applicationFlag:{}.", effectivePerson::getDistinguishedName, () -> applicationFlag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			List<NameValueCountPair> wos = this.list(business, effectivePerson, application);
			for (NameValueCountPair o : wos) {
				o.setCount(countWithProcess(business, effectivePerson, Objects.toString(o.getValue())));
			}
			result.setData(wos);
			return result;
		}
	}

	private List<NameValueCountPair> list(Business business, EffectivePerson effectivePerson, Application application)
			throws Exception {
		List<NameValueCountPair> wos = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getDistinguishedName());
		p = cb.and(p, cb.equal(root.get(ReadCompleted_.application), application.getId()));
		cq.select(root.get(ReadCompleted_.process)).where(p);
		List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		for (String str : os) {
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
		SortTools.asc(wos, "name");
		return wos;
	}

}