package o2.collect.assemble.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.tools.JpaObjectTools;

import o2.collect.assemble.AbstractFactory;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Unit;
import o2.collect.core.entity.Unit_;

public class UnitFactory extends AbstractFactory {

	public UnitFactory(Business business) throws Exception {
		super(business);
	}

	public String flag(String flag) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.name), flag);
		if (JpaObjectTools.withinDefinedLength(flag, Unit.class, Unit_.id.getName())) {
			p = cb.or(p, cb.equal(root.get(Unit_.id), flag));
		}
		if (JpaObjectTools.withinDefinedLength(flag, Unit.class, Unit_.controllerMobileList.getName())) {
			p = cb.or(p, cb.isMember(flag, root.get(Unit_.controllerMobileList)));
		}
		cq.select(root.get(Unit_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	public String getWithName(String name, String excludeId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.name), name);
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Unit_.id), excludeId));
		}
		cq.select(root.get(Unit_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	public List<String> listWithControllerPhone(String mobile) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.isMember(mobile, root.get(Unit_.controllerMobileList));
		cq.select(root.get(Unit_.id)).where(p);
		List<String> list = em.createQuery(cq).getResultList();
		return list;
	}

}