package com.x.teamwork.assemble.control.jaxrs.extfield;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.CustomExtFieldRele;


public class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		CustomExtFieldRele customExtFieldRele = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( id ) ) {
			check = false;
			Exception exception = new CustomExtFieldReleFlagForQueryEmptyException();
			result.error( exception );
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey("CustomExtFieldRele.Get." , id );
		Optional<?> optional = CacheManager.get(customExtFieldReleCache, cacheKey);

		if (optional.isPresent()) {
			wo = (Wo) optional.get();
			
		} else {
			if( Boolean.TRUE.equals( check ) ){
				try {
					customExtFieldRele = customExtFieldReleQueryService.get(id);
					if ( customExtFieldRele == null) {
						check = false;
						Exception exception = new CustomExtFieldReleNotExistsException(id);
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new CustomExtFieldReleQueryException( e, "根据指定id查询扩展属性关联信息对象时发生异常。flag:" + id );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if( Boolean.TRUE.equals( check ) ){
				try {
					wo = Wo.copier.copy( customExtFieldRele );
				} catch (Exception e) {
					Exception exception = new CustomExtFieldReleQueryException(e, "将查询出来的扩展属性关联信息对象转换为可输出的数据信息时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		CacheManager.put(customExtFieldReleCache,cacheKey,wo);
		result.setData( wo );
		return result;
	}

	public static class Wo extends CustomExtFieldRele {
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		static WrapCopier<CustomExtFieldRele, Wo> copier = WrapCopierFactory.wo( CustomExtFieldRele.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));		

	}
}