package com.x.base.core.container.checker;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainerBasic;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.NotEqual;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.tools.ListTools;

public class StringValueListRemoveChecker extends AbstractChecker {
	public StringValueListRemoveChecker(EntityManagerContainerBasic emc) {
		super(emc);
	}

	public void check(Field field, List<String> values, JpaObject jpa, CheckRemove checkRemove,
			CheckRemoveType checkRemoveType) throws Exception {
		if (Objects.equals(checkRemoveType, CheckRemoveType.all)) {
			this.citationExists(this.emc, field, values, jpa, checkRemove, checkRemoveType);
			this.citationNotExists(this.emc, field, values, jpa, checkRemove, checkRemoveType);
		}
	}

	@SuppressWarnings("unchecked")
	private void citationExists(EntityManagerContainerBasic emc, Field field, List<String> values, JpaObject jpa,
			CheckRemove checkRemove, CheckRemoveType checkRemoveType) throws Exception {
		next: for (String value : ListTools.nullToEmpty(values)) {
			if (ArrayUtils.contains(checkRemove.excludes(), value)) {
				continue next;
			}
			for (CitationExist citationExist : checkRemove.citationExists()) {
				EntityManager em = emc.get(citationExist.type());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> cq = cb.createQuery(Long.class);
				Root<? extends JpaObject> root = cq.from(citationExist.type());
				Predicate p = cb.disjunction();
				for (String str : citationExist.fields()) {
					Path<?> path = root.get(str);
					if (JpaObjectTools.isList(path)) {
						p = cb.or(p, cb.isMember(value, (Path<List<String>>) path));
					} else {
						p = cb.or(p, cb.equal(path, value));
					}
				}
				p = cb.and(p, cb.notEqual(root.get("id"), jpa.getId()));
				for (Equal o : citationExist.equals()) {
					p = cb.and(p, cb.equal(root.get(o.field()), jpa.get(o.property())));
				}
				for (NotEqual o : citationExist.notEquals()) {
					p = cb.and(p, cb.notEqual(root.get(o.field()), jpa.get(o.property())));
				}
				cq.select(cb.count(root)).where(p);
				Long count = em.createQuery(cq).getSingleResult();
				if (count == 0) {
					throw new Exception("check remove stringValueList citationExists error, class:"
							+ jpa.getClass().getName() + ", field:" + field.getName() + ", values: "
							+ StringUtils.join(values, ",") + ", value:" + value + ", must be a existed in class:"
							+ citationExist.type() + ", fields:" + StringUtils.join(citationExist.fields(), ",") + ".");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void citationNotExists(EntityManagerContainerBasic emc, Field field, List<String> values, JpaObject jpa,
			CheckRemove checkRemove, CheckRemoveType checkRemoveType) throws Exception {
		next: for (String value : ListTools.nullToEmpty(values)) {
			if (ArrayUtils.contains(checkRemove.excludes(), value)) {
				continue next;
			}
			for (CitationNotExist citationNotExist : checkRemove.citationNotExists()) {
				EntityManager em = emc.get(citationNotExist.type());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> cq = cb.createQuery(Long.class);
				Root<? extends JpaObject> root = cq.from(citationNotExist.type());
				Predicate p = cb.disjunction();
				for (String str : citationNotExist.fields()) {
					Path<?> path = root.get(str);
					if (JpaObjectTools.isList(path)) {
						p = cb.or(p, cb.isMember(value, (Path<List<String>>) path));
					} else {
						p = cb.or(p, cb.equal(path, value));
					}
				}
				p = cb.and(p, cb.notEqual(root.get("id"), jpa.getId()));
				for (Equal o : citationNotExist.equals()) {
					p = cb.and(p, cb.equal(root.get(o.field()), jpa.get(o.property())));
				}
				for (NotEqual o : citationNotExist.notEquals()) {
					p = cb.and(p, cb.notEqual(root.get(o.field()), jpa.get(o.property())));
				}
				cq.select(cb.count(root)).where(p);
				Long count = em.createQuery(cq).getSingleResult();
				if (count != 0) {
					throw new Exception(
							"check remove stringValueList citationNotExists error, class:" + jpa.getClass().getName()
									+ ", field:" + field.getName() + ", values: " + StringUtils.join(values, ",")
									+ ", value:" + value + ", already existed in class:" + citationNotExist.type()
									+ ", fields:" + StringUtils.join(citationNotExist.fields(), ",") + ".");
				}
			}
		}
	}
}