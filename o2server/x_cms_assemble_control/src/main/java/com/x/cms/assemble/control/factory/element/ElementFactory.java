package com.x.cms.assemble.control.factory.element;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public abstract class ElementFactory extends AbstractFactory {

	public ElementFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> T pick(String flag, Class<T> clz) throws Exception {
		Ehcache cache = ApplicationCache.instance().getCache(clz);
		T t = null;
		Element element = cache.get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				t = (T) element.getObjectValue();
			}
		} else {
			t = this.entityManagerContainer().flag(flag, clz);
			if (t != null) {
				this.entityManagerContainer().get(clz).detach(t);
			}
			cache.put(new Element(flag, t));
		}
		return t;
	}

}