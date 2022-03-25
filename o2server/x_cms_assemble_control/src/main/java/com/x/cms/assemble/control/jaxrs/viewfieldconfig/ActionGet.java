package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ActionGet extends BaseAction {

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wrap = (Wo) optional.get();
			result.setData(wrap);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				ViewFieldConfig viewFieldConfig = business.getViewFieldConfigFactory().get(id);
				if ( null == viewFieldConfig ) {
					throw new Exception("需要查询的列表展现列信息不存在，请联系管理员。ID:" + id );
				}
				//如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
				wrap = Wo.copier.copy( viewFieldConfig );

				CacheManager.put(cacheCategory, cacheKey, wrap );
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		return result;
	}

	public static class Wo extends ViewFieldConfig {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<String>();

		public static final WrapCopier<ViewFieldConfig, Wo> copier = WrapCopierFactory.wo( ViewFieldConfig.class, Wo.class, null,JpaObject.FieldsInvisible);

	}

}
