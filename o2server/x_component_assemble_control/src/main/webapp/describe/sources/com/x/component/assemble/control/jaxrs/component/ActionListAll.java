package com.x.component.assemble.control.jaxrs.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Components;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.component.core.entity.Component;

import net.sf.ehcache.Element;

class ActionListAll extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionListAll.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass());
			Element element = cache.get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				final List<Wo> wos = new ArrayList<>();
				List<Component> os = emc.listAll(Component.class);
				os.stream().filter(o -> ListTools.contains(Components.SYSTEM_NAME_NAMES, o.getName()))
						.sorted(Comparator.comparing(Component::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
								.thenComparing(Component::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.forEach(o -> {
							try {
								wos.add(Wo.copier.copy(o));
							} catch (Exception e) {
								logger.error(e);
							}
						});
				os.stream().filter(o -> !ListTools.contains(Components.SYSTEM_NAME_NAMES, o.getName()))
						.sorted(Comparator.comparing(Component::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
								.thenComparing(Component::getCreateTime, Comparator.nullsLast(Date::compareTo)))
						.forEach(o -> {
							try {
								wos.add(Wo.copier.copy(o));
							} catch (Exception e) {
								logger.error(e);
							}
						});
				cache.put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			return result;
		}
	}

	public static class Wo extends Component {

		private static final long serialVersionUID = -340611438251489741L;

		static WrapCopier<Component, Wo> copier = WrapCopierFactory.wo(Component.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
