package com.x.cms.assemble.control.jaxrs.view;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.View;

public class ActionGet extends BaseAction {

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String flag ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), flag );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wrap = (Wo) optional.get();
			result.setData(wrap);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				View view = business.getViewFactory().flag( flag );
				if ( null == view ) {
					throw new Exception("需要查询的列表信息不存在，请联系管理员。ID:" + flag );
				}
				wrap = Wo.copier.copy( view );
				//根据FormId补充FormName
				if(StringUtils.isNotEmpty( wrap.getFormId() )) {
					wrap.setFormName( formServiceAdv.getNameWithId( wrap.getFormId() ) );
				}
				CacheManager.put(cacheCategory, cacheKey, wrap );
				result.setData(wrap);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}

	public static class Wo extends View {

		private static final long serialVersionUID = -5076990764713538973L;

		@FieldDescribe("绑定的表单名称.")
		private String formName = null;

		public static final WrapCopier<View, Wo> copier = WrapCopierFactory.wo( View.class, Wo.class, null,JpaObject.FieldsInvisible);

		public String getFormName() {
			return formName;
		}

		public void setFormName(String formName) {
			this.formName = formName;
		}
	}
}
