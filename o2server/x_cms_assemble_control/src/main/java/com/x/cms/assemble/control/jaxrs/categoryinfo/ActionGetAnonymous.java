package com.x.cms.assemble.control.jaxrs.categoryinfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.CategoryInfo;

public class ActionGetAnonymous extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGetAnonymous.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, String flag, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		CategoryInfo categoryInfo = null;
		Boolean check = true;

		if( StringUtils.isEmpty( flag ) ){
			check = false;
			Exception exception = new ExceptionIdEmpty();
			result.error( exception );
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), flag );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			result.setData((Wo)optional.get());
		} else {
			if( check ){
				try {
					categoryInfo = categoryInfoServiceAdv.getWithFlag( flag );
					if( categoryInfo == null ){
						check = false;
						Exception exception = new ExceptionCategoryInfoNotExists( flag );
						result.error( exception );
					}else {
						if( !categoryInfo.getAnonymousAble() ){
							check = false;
							Exception exception = new ExceptionCategoryInfoAccessDenied();
							result.error( exception );
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionCategoryInfoProcess( e, "根据ID查询分类信息对象时发生异常。Flag:" + flag );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}

			if( check ){
				try {
					wo = Wo.copier.copy( categoryInfo );
					wo.setExtContent( categoryInfoServiceAdv.getExtContentWithId( wo.getId() ));
					CacheManager.put(cacheCategory, cacheKey, wo);
					result.setData( wo );
				} catch ( Exception e ) {
					check = false;
					Exception exception = new ExceptionCategoryInfoProcess( e, "将查询出来的分类信息对象转换为可输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends CategoryInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		@FieldDescribe("扩展信息JSON内容")
		private String extContent = null;

		static WrapCopier<CategoryInfo, Wo> copier = WrapCopierFactory.wo( CategoryInfo.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));

		public String getExtContent() {
			return extContent;
		}

		public void setExtContent(String extContent) {
			this.extContent = extContent;
		}
	}

}
