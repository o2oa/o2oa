package com.x.collaboration.assemble.websocket.jaxrs.talk;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.collaboration.assemble.websocket.Business;
import com.x.collaboration.core.entity.Talk;
import com.x.collaboration.core.entity.Talk_;

public class ActionList {

	private Gson gson = XGsonBuilder.instance();

	protected List<JsonElement> execute(Business business, EffectivePerson effectivePerson) throws Exception {
		List<Talk> talks = this.listTalk(business, effectivePerson);
		List<JsonElement> list = new ArrayList<>();
		for (Talk o : talks) {
			list.add(gson.fromJson(o.getBody(), JsonElement.class));
		}
		return list;
	}

	private List<Talk> listTalk(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Talk.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Talk> cq = cb.createQuery(Talk.class);
		Root<Talk> root = cq.from(Talk.class);
		Predicate p = cb.equal(root.get(Talk_.from), effectivePerson.getDistinguishedName());
		p = cb.or(p, cb.equal(root.get(Talk_.person), effectivePerson.getDistinguishedName()));
		cq.select(root).where(p).orderBy(cb.desc(root.get(Talk_.updateTime)));
		return em.createQuery(cq).setMaxResults(50).getResultList();
	}

}
