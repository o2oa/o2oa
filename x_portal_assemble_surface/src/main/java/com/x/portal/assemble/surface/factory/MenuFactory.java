package com.x.portal.assemble.surface.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.cache.ApplicationCache;
import com.x.portal.assemble.surface.AbstractFactory;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Menu;
import com.x.portal.core.entity.Menu_;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class MenuFactory extends AbstractFactory {

	static Ehcache menuCache = ApplicationCache.instance().getCache(Menu.class);

	public MenuFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Menu pick(String id) throws Exception {
		String cacheKey = ApplicationCache.concreteCacheKey(id);
		Element element = menuCache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			return (Menu) element.getObjectValue();
		} else {
			Menu o = this.business().entityManagerContainer().find(id, Menu.class);
			if (null != o) {
				this.business().entityManagerContainer().get(Menu.class).detach(o);
				menuCache.put(new Element(id, o));
				return o;
			}
			return null;
		}
	}

	public List<String> listWithPortal(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Menu.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Menu> root = cq.from(Menu.class);
		Predicate p = cb.equal(root.get(Menu_.portal), portalId);
		List<String> list = em.createQuery(cq.select(root.get(Menu_.id)).where(p)).getResultList();
		return list;
	}
}