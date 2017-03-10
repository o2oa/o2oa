package com.x.cms.assemble.control.jaxrs.view;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.View;

import net.sf.ehcache.Element;

public class ExcuteGet extends ExcuteBase {
	
	protected ActionResult<WrapOutView> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutView> result = new ActionResult<>();
		WrapOutView wrap = null;
		String cacheKey = ApplicationCache.concreteCacheKey( id );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wrap = (WrapOutView) element.getObjectValue();
			result.setData(wrap);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				View view = business.getViewFactory().get(id);
				if ( null == view ) {
					throw new Exception("view{id:" + id + "} 信息不存在.");
				}
				//如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
				BeanCopyTools<View, WrapOutView> copier = BeanCopyToolsBuilder.create(View.class, WrapOutView.class, null, WrapOutView.Excludes);
				wrap = new WrapOutView();
				copier.copy(view, wrap);
				cache.put(new Element( cacheKey, wrap ));
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		
		return result;
	}
	
}