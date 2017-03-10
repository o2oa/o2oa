package com.x.cms.assemble.control.jaxrs.form;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.factory.FormFactory;
import com.x.cms.core.entity.element.Form;

import net.sf.ehcache.Element;

public class ExcuteListAll extends ExcuteBase {
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutSimpleForm>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutSimpleForm>> result = new ActionResult<>();
		List<WrapOutSimpleForm> wraps = null;
		
		String cacheKey = ApplicationCache.concreteCacheKey( "all" );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = (List<WrapOutSimpleForm>) element.getObjectValue();
			result.setData(wraps);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				FormFactory formFactory = business.getFormFactory();
				List<String> ids = formFactory.listAll();// 获取所有表单模板列表
				List<Form> formList = formFactory.list(ids);// 查询ID IN ids 的所有表单模板信息列表
				wraps = WrapTools.formsimple_wrapout_copier.copy( formList );// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
				
				SortTools.desc(wraps, "createTime" );
				cache.put(new Element( cacheKey, wraps ));
				result.setData(wraps);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return result;
	}
	
}