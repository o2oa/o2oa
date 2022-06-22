package com.x.component.assemble.control.jaxrs.component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Components;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.component.core.entity.Component;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListAll extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListAll.class);

	@SuppressWarnings("unchecked")
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			CacheKey cacheKey = new CacheKey(this.getClass());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>) optional.get());
			} else {
				List<Component> os = emc.listAll(Component.class);
				final List<Wo> wos = os.stream()
						.filter(o -> ListTools.contains(Components.SYSTEM_NAME_NAMES, o.getName()))
						.sorted(Comparator.nullsLast(Comparator.comparing(Component::getOrderNumber))
								.thenComparing(Comparator.nullsLast(Comparator.comparing(Component::getCreateTime))))
						.map(o -> Wo.copier.copy(o)).collect(Collectors.toList());
				os.stream().filter(o -> !ListTools.contains(Components.SYSTEM_NAME_NAMES, o.getName()))
						.sorted(Comparator.nullsLast(Comparator.comparing(Component::getOrderNumber))
								.thenComparing(Comparator.nullsLast(Comparator.comparing(Component::getCreateTime))))
						.forEach(o -> wos.add(Wo.copier.copy(o)));
				CacheManager.put(cacheCategory, cacheKey, wos);
				result.setData(wos);
			}
			return result;
		}
	}

	@Schema(name = "com.x.component.assemble.control.jaxrs.component.ActionListAll$Wo")
	public static class Wo extends Component {

		private static final long serialVersionUID = -340611438251489741L;

		static WrapCopier<Component, Wo> copier = WrapCopierFactory.wo(Component.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
