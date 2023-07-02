package com.x.cms.assemble.control.jaxrs.commend;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.DocumentCommend;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			result.setData((Wo)optional.get());
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				DocumentCommend dc = emc.find(id, DocumentCommend.class);
				if (null == dc) {
					throw new ExceptionEntityNotExist(id, DocumentCommend.class);
				}
				wo = Wo.copier.copy( dc );
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData( wo );
			}
		}
		
		return result;
	}
	
	public static class Wo extends DocumentCommend {

		static WrapCopier<DocumentCommend, Wo> copier = WrapCopierFactory.wo( DocumentCommend.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

	}
}