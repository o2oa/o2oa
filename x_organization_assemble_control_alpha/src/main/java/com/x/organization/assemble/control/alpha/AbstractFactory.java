package com.x.organization.assemble.control.alpha;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.exception.ExceptionWhen;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public abstract class AbstractFactory {

	private Business business;

	public AbstractFactory(Business business) throws Exception {
		try {
			if (null == business) {
				throw new Exception("business can not be null.");
			}
			this.business = business;
		} catch (Exception e) {
			throw new Exception("can not instantiating factory.");
		}
	}

	public EntityManagerContainer entityManagerContainer() throws Exception {
		return this.business.entityManagerContainer();
	}

	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> T pick(String flag, Class<T> clz, String... attributes) throws Exception {
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
		return t;
	}

}