package com.x.pan.assemble.control.jaxrs.zone;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Folder3;
import com.x.pan.core.entity.ZoneRoleEnum;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

class ActionList extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger( ActionList.class );
	private static final Collator COLLATOR = Collator.getInstance(Locale.getDefault());

	ActionResult<List<Wo>> execute(final EffectivePerson effectivePerson) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), effectivePerson.getDistinguishedName());
			Optional<?> optional = CacheManager.get(zoneCacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((List<Wo>)optional.get());
				return result;
			}

			final Business business = new Business(emc);
			List<Folder3> zoneList = business.folder3().listZoneObject(effectivePerson.getDistinguishedName(), false);
			List<Wo> wos = Wo.copier.copy(zoneList);
			final boolean isManager = business.controlAble(effectivePerson);
			wos.stream().forEach(o -> {
				if(isManager){
					o.setIsAdmin(true);
					o.setIsEditor(true);
				}else{
					try {
						boolean isAdmin = business.folder3().isZoneAdmin(o.getId(), effectivePerson.getDistinguishedName());
						if(isAdmin){
							o.setIsAdmin(true);
							o.setIsEditor(true);
						}else{
							o.setIsAdmin(false);
							o.setIsEditor(false);
							boolean isEditor = business.folder3().isZoneEditor(o.getId(), effectivePerson.getDistinguishedName());
							if(isEditor){
								o.setIsEditor(true);
							}
						}
					} catch (Exception e) {
						logger.debug(e.getMessage());
					}
				}
			});
			wos = wos.stream().sorted(Comparator.comparing(Folder3::getName, COLLATOR))
					.collect(Collectors.toList());
			CacheManager.put(zoneCacheCategory, cacheKey, wos);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Zone {

		protected static WrapCopier<Folder3, Wo> copier = WrapCopierFactory.wo(Folder3.class, Wo.class,
				JpaObject.singularAttributeField(Folder3.class, true, true), null);

	}

}
