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
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.ReadCompleted;
import com.x.processplatform.core.entity.content.ReadCompleted_;
import com.x.processplatform.core.entity.element.Application;

class ActionListCountWithApplication extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListCountWithApplication.class);

	ActionResult<List<NameValueCountPair>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<NameValueCountPair>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<NameValueCountPair> wraps = listApplicationPair(business, effectivePerson);
			for (NameValueCountPair o : wraps) {
				o.setCount(this.countWithApplication(business, effectivePerson, Objects.toString(o.getValue())));
			}
			result.setData(wraps);
			return result;
		}
	}

	private List<NameValueCountPair> listApplicationPair(Business business, EffectivePerson effectivePerson)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(ReadCompleted.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ReadCompleted> root = cq.from(ReadCompleted.class);
		Predicate p = cb.equal(root.get(ReadCompleted_.person), effectivePerson.getDistinguishedName());
		cq.select(root.get(ReadCompleted_.application)).where(p);
		List<String> os = em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
		List<NameValueCountPair> wos = new ArrayList<>();
		for (String str : os) {
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
		SortTools.asc(wos, "name");
		return wos;
	}
}