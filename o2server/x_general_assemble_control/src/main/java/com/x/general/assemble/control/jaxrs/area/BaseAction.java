package com.x.general.assemble.control.jaxrs.area;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.general.assemble.control.Business;
import com.x.general.core.entity.area.District;
import com.x.general.core.entity.area.District_;

abstract class BaseAction extends StandardJaxrsAction {

	protected CacheCategory cacheCategory = new CacheCategory(District.class);

	protected District getProvince(Business business, String name) throws Exception {
		List<District> os = business.entityManagerContainer().listEqualAndEqual(District.class,
				District.level_FIELDNAME, District.LEVEL_PROVINCE, District.name_FIELDNAME, name);
		if (os.size() == 1) {
			return os.get(0);
		}
		return null;
	}

	protected District getCity(Business business, District province, String name) throws Exception {
		EntityManager em = business.entityManagerContainer().get(District.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<District> cq = cb.createQuery(District.class);
		Root<District> root = cq.from(District.class);
		Predicate p = cb.equal(root.get(District_.name), name);
		p = cb.and(p, cb.equal(root.get(District_.level), District.LEVEL_CITY));
		p = cb.and(p, cb.equal(root.get(District_.province), province.getId()));
		cq.select(root).where(p);
		List<District> os = em.createQuery(cq).getResultList();
		if (os.size() == 1) {
			return os.get(0);
		}
		return null;
	}

	protected District getDistrict(Business business, District city, String name) throws Exception {
		EntityManager em = business.entityManagerContainer().get(District.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<District> cq = cb.createQuery(District.class);
		Root<District> root = cq.from(District.class);
		Predicate p = cb.equal(root.get(District_.name), name);
		p = cb.and(p, cb.equal(root.get(District_.level), District.LEVEL_DISTRICT));
		p = cb.and(p, cb.equal(root.get(District_.city), city.getId()));
		cq.select(root).where(p);
		List<District> os = em.createQuery(cq).getResultList();
		if (os.size() == 1) {
			return os.get(0);
		}
		return null;
	}

	protected List<District> listCity(Business business, District province) throws Exception {
		List<District> os = business.entityManagerContainer().listEqualAndEqual(District.class,
				District.level_FIELDNAME, District.LEVEL_CITY, District.province_FIELDNAME, province.getId());
		return os;
	}

	protected List<District> listDistrict(Business business, District city) throws Exception {
		List<District> os = business.entityManagerContainer().listEqualAndEqual(District.class,
				District.level_FIELDNAME, District.LEVEL_DISTRICT, District.city_FIELDNAME, city.getId());
		return os;
	}

	protected List<District> listStreet(Business business, District district) throws Exception {
		List<District> os = business.entityManagerContainer().listEqualAndEqual(District.class,
				District.level_FIELDNAME, District.LEVEL_STREET, District.district_FIELDNAME, district.getId());
		return os;
	}

}
