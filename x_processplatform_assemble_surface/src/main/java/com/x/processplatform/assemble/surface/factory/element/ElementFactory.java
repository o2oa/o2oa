package com.x.processplatform.assemble.surface.factory.element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Process;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public abstract class ElementFactory extends AbstractFactory {

	public ElementFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> T pick(String flag, Class<T> clz, ExceptionWhen exceptionWhen, String... attributes)
			throws Exception {
		Ehcache cache = ApplicationCache.instance().getCache(clz);
		T t = null;
		Element element = cache.get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				t = (T) element.getObjectValue();
			}
		} else {
			t = this.entityManagerContainer().flag(flag, clz, ExceptionWhen.none, false, attributes);
			if (t != null) {
				this.entityManagerContainer().get(clz).detach(t);
			}
			cache.put(new Element(flag, t));
		}
		if (null == t) {
			if ((null != exceptionWhen) && (exceptionWhen.equals(ExceptionWhen.not_found))) {
				throw new Exception("can not find entity{class:" + clz.getName() + ", flag:" + flag + "}");
			}
		}
		return t;
	}

	/* 取得属于指定Process 的设计元素 */
	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> List<T> listWithProcess(Class<T> clz, Process process) throws Exception {
		List<T> list = new ArrayList<>();
		Ehcache cache = ApplicationCache.instance().getCache(clz);
		String cacheKey = "listWithProcess#" + process.getId() + "#" + clz.getName();
		Element element = cache.get(cacheKey);
		if (null != element) {
			Object obj = element.getObjectValue();
			if (null != obj) {
				list = (List<T>) obj;
			}
		} else {
			EntityManager em = this.entityManagerContainer().get(clz);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(clz);
			Root<T> root = cq.from(clz);
			Predicate p = cb.equal(root.get("process"), process.getId());
			cq.select(root).where(p);
			List<T> os = em.createQuery(cq).getResultList();
			for (T t : os) {
				em.detach(t);
				list.add(t);
			}
			/* 将object改为unmodifiable */
			list = Collections.unmodifiableList(list);
			cache.put(new Element(cacheKey, list));
		}
		return list;
	}

}