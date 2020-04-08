package com.x.query.assemble.surface.jaxrs.segment;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.segment.Word;
import com.x.query.core.entity.segment.Word_;

class ActionSearch extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String key) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = new Wo();
			List<String> keys = this.keys(key);
			if (!keys.isEmpty()) {
				List<String> entries = this.entries(business, keys);
				wo.setValueList(entries);
				wo.setCount(this.count(business, keys));
			}
			result.setData(wo);
			return result;
		}
	}

	private List<String> entries(Business business, List<String> keys) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Word.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Word> root = cq.from(Word.class);
		Expression<Long> express_count = cb.count(root.get(Word_.entry));
		Expression<String> express_entry = root.get(Word_.entry);
		cq.multiselect(express_entry, express_count).where(cb.isMember(root.get(Word_.value), cb.literal(keys)))
				.groupBy(express_entry).orderBy(cb.desc(express_count));
		List<Tuple> os = em.createQuery(cq).setMaxResults(500).getResultList();
		List<String> list = new ArrayList<>();
		for (Tuple t : os) {
			list.add(t.get(express_entry));
		}
		return list;
	}

	private Integer count(Business business, List<String> keys) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Word.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Word> root = cq.from(Word.class);
		Expression<String> express_entry = root.get(Word_.entry);
		cq.select(cb.count(express_entry)).where(cb.isMember(root.get(Word_.value), cb.literal(keys)))
				.groupBy(express_entry);
		List<Long> os = em.createQuery(cq).getResultList();
		return os.size();
	}

	public static class Wo extends WrapStringList {
		private Integer count;

		public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

	}
}