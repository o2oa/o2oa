package com.x.message.assemble.communicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.ApplicationCache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public abstract class AbstractFactory {

	protected Business business;

	protected Ehcache cache;

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
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
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

	@SuppressWarnings("unchecked")
	protected <T extends JpaObject> List<T> pick(List<String> flags, Class<T> clz) throws Exception {
		List<T> list = new ArrayList<>();
		if (null == flags || flags.isEmpty()) {
			return list;
		}
		Ehcache cache = ApplicationCache.instance().getCache(clz);
		Map<Object, Element> map = cache.getAll(flags);
		if (map.size() == flags.size()) {
			map.values().stream().forEach(o -> {
				list.add((T) o.getObjectValue());
			});
		} else {
			List<T> os = this.entityManagerContainer().flag(flags, clz);
			EntityManager em = this.entityManagerContainer().get(clz);
			os.stream().forEach(o -> {
				em.detach(o);
				list.add(o);
				cache.put(new Element(o.getId(), o));
			});
		}
		return list;
	}

}