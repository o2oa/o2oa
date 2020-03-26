package com.x.query.assemble.surface.jaxrs.segment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.content.Review;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.segment.Entry;
import com.x.query.core.entity.segment.Word;
import com.x.query.core.entity.segment.Word_;

class ActionSearchAccessible extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String key) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<Wo> wos = new ArrayList<>();
			List<String> keys = this.keys(key);
			if (!keys.isEmpty()) {
				List<String> ids = this.match(business, keys);
				ids = this.sort(ids);
				if (effectivePerson.isNotManager()
						&& (!business.organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager,
								OrganizationDefinition.QueryManager, OrganizationDefinition.SearchPrivilege))) {
					ids = this.filter(business, effectivePerson, ids);
				}
				ids = ids.stream().limit(200).collect(Collectors.toList());
				List<Entry> os = emc.list(Entry.class, true, ids);
				wos = Wo.copier.copy(os);
			}
			result.setData(wos);
			return result;
		}
	}

	private List<String> filter(Business business, EffectivePerson effectivePerson, List<String> ids) throws Exception {
		List<String> list = new ArrayList<>();
		List<Entry> entries = this.listEntry(business, ids);
		list.addAll(this.filterWorkOrWorkCompleted(business, effectivePerson, entries));
		list.addAll(this.filterCms(business, effectivePerson, entries));
		Collections.sort(list, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return Integer.compare(ids.indexOf(o1), ids.indexOf(o2));
			}
		});
		return list;
	}

	private List<String> sort(List<String> ids) {
		List<String> os = new ArrayList<>();
		ids.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
				.sorted(Comparator.comparing(Map.Entry<String, Long>::getValue).reversed()).forEach(o -> {
					os.add(o.getKey());
				});
		return os;
	}

	private List<String> match(Business business, List<String> values) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Word.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Word> root = cq.from(Word.class);
		
		
		
		cq.select(root.get(Word_.entry)).where(cb.isMember(root.get(Word_.value), cb.literal(values)));
		List<String> os = em.createQuery(cq).getResultList();
		return os;
	}

	private List<Entry> listEntry(Business business, List<String> ids) throws Exception {
		List<Entry> os = new ArrayList<>();
		for (List<String> parts : ListTools.batch(ids, 1000)) {
			os.addAll(business.entityManagerContainer().fetch(parts, Entry.class,
					ListTools.toList(Entry.bundle_FIELDNAME, Entry.type_FIELDNAME)));
		}
		Collections.sort(os, new Comparator<Entry>() {
			public int compare(Entry o1, Entry o2) {
				return Integer.compare(ids.indexOf(o1.getId()), ids.indexOf(o2.getId()));
			}
		});
		return os;
	}

	private List<String> filterWorkOrWorkCompleted(Business business, EffectivePerson effectivePerson,
			List<Entry> entries) throws Exception {
		List<Entry> parts = entries.stream().filter(o -> StringUtils.equals(o.getType(), Entry.TYPE_WORKCOMPLETED)
				|| StringUtils.equals(o.getType(), Entry.TYPE_WORK)).collect(Collectors.toList());
		List<String> ids = new ArrayList<>();
		for (List<Entry> list : ListTools.batch(parts, 1000)) {
			ids.addAll(
					ListTools
							.extractProperty(
									business.entityManagerContainer().fetchEqualAndIn(
											Review.class, ListTools.toList(Review.job_FIELDNAME),
											Review.person_FIELDNAME, effectivePerson.getDistinguishedName(),
											Review.job_FIELDNAME, ListTools.extractProperty(list,
													Entry.bundle_FIELDNAME, String.class, true, true)),
									Review.job_FIELDNAME, String.class, true, true));
		}
		return ids;
	}

	private List<String> filterCms(Business business, EffectivePerson effectivePerson, List<Entry> entries)
			throws Exception {
		List<Entry> parts = entries.stream().filter(o -> StringUtils.equals(o.getType(), Entry.TYPE_WORKCOMPLETED))
				.collect(Collectors.toList());
		List<String> ids = new ArrayList<>();
		ids.addAll(ListTools.extractProperty(parts, Entry.bundle_FIELDNAME, String.class, true, true));
		return ids;
	}

	public static class Wo extends Entry {

		private static final long serialVersionUID = -8067704098385000667L;

		static WrapCopier<Entry, Wo> copier = WrapCopierFactory.wo(Entry.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}



}