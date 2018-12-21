package com.x.cms.assemble.control.jaxrs.viewcategory;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.ViewCategory;

import net.sf.ehcache.Element;

public class ActionGet extends BaseAction {

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		String cacheKey = ApplicationCache.concreteCacheKey(id);
		Element element = cache.get(cacheKey);

		if ((null != element) && (null != element.getObjectValue())) {
			wrap = (Wo) element.getObjectValue();
			result.setData(wrap);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				ViewCategory view = business.getViewCategoryFactory().get(id);
				if (null == view) {
					throw new Exception("view{id:" + id + "} 信息不存在.");
				}

				wrap = Wo.copier.copy(view);
				cache.put(new Element(cacheKey, wrap));

				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		return result;
	}

	public static class Wo extends ViewCategory {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<ViewCategory, Wo> copier = WrapCopierFactory.wo(ViewCategory.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}

}