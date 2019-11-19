package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

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
import com.x.cms.assemble.control.factory.ViewFieldConfigFactory;
import com.x.cms.core.entity.element.ViewFieldConfig;

import net.sf.ehcache.Element;

public class ActionListAll extends BaseAction {

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		String cacheKey = ApplicationCache.concreteCacheKey( "all" );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = (List<Wo>) element.getObjectValue();
			result.setData(wraps);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
				Business business = new Business(emc);			
				//如判断用户是否有查看所有展示列配置信息的权限，如果没权限不允许继续操作
				if (!business.viewEditAvailable( effectivePerson )) {
					throw new Exception("person{name:" + effectivePerson.getDistinguishedName() + "} 用户没有查询全部展示列配置信息配置的权限！");
				}			
				//如果有权限，继续操作
				ViewFieldConfigFactory viewFieldConfigFactory  = business.getViewFieldConfigFactory();
				List<String> ids = viewFieldConfigFactory.listAll();//获取所有展示列配置信息列表
				List<ViewFieldConfig> viewFieldConfigList = emc.list( ViewFieldConfig.class, ids );//查询ID IN ids 的所有展示列配置信息信息列表
				
				wraps = Wo.copier.copy( viewFieldConfigList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				
				cache.put(new Element( cacheKey, wraps ));	
				result.setData(wraps);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return result;
	}
	
	public static class Wo extends ViewFieldConfig {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier<ViewFieldConfig, Wo> copier = WrapCopierFactory.wo( ViewFieldConfig.class, Wo.class, null, JpaObject.FieldsInvisible);

	}
	
}