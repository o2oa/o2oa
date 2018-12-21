package com.x.collaboration.assemble.websocket.jaxrs.dialog;

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
import com.x.collaboration.core.entity.Dialog;
import com.x.collaboration.core.entity.Dialog_;

public class ActionListTalk {

	private Gson gson = XGsonBuilder.instance();

	protected List<JsonElement> execute(Business business, EffectivePerson effectivePerson, String person)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Dialog.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Dialog> cq = cb.createQuery(Dialog.class);
		Root<Dialog> root = cq.from(Dialog.class);
		Predicate p = cb.and(cb.equal(root.get(Dialog_.from), effectivePerson.getDistinguishedName()),
				cb.equal(root.get(Dialog_.person), person));
		p = cb.or(p, cb.and(cb.equal(root.get(Dialog_.person), effectivePerson.getDistinguishedName()),
				cb.equal(root.get(Dialog_.from), person)));
		cq.select(root).where(p).orderBy(cb.desc(root.get(Dialog_.createTime)));
		List<Dialog> list = em.createQuery(cq).setMaxResults(30).getResultList();
		List<JsonElement> jsonElements = new ArrayList<>();
		for (Dialog o : list) {
			jsonElements.add(gson.fromJson(o.getBody(), JsonElement.class));
		}
		return jsonElements;
	}

}
