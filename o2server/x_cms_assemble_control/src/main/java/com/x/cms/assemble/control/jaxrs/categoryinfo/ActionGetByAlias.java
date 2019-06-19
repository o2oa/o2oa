package com.x.cms.assemble.control.jaxrs.categoryinfo;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.CategoryInfo;

import net.sf.ehcache.Element;

public class ActionGetByAlias extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGetByAlias.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, String alias, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		List<String> ids = null;
		CategoryInfo categoryInfo = null;
		Boolean check = true;
		
		if( StringUtils.isEmpty(alias) ){
			check = false;
			Exception exception = new ExceptionIdEmpty();
			result.error( exception );
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( "alias", alias );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wo = ( Wo ) element.getObjectValue();
			result.setData(wo);
		} else {
			if( check ){
				try {
					ids = categoryInfoServiceAdv.listByAlias( alias );
					if( ListTools.isEmpty(ids) ){
						check = false;
						Exception exception = new ExceptionCategoryInfoNotExists( alias );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionCategoryInfoProcess( e, "根据标识查询分类信息对象时发生异常。ALIAS:"+ alias );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				try {
					categoryInfo = categoryInfoServiceAdv.get( ids.get( 0 ) );
					if( categoryInfo == null ){
						check = false;
						Exception exception = new ExceptionCategoryInfoNotExists( ids.get( 0 ) );
						result.error( exception );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionCategoryInfoProcess( e, "根据ID查询分类信息对象时发生异常。ID:" + ids.get( 0 ) );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				try {
					wo = Wo.copier.copy( categoryInfo );
					wo.setExtContent( categoryInfoServiceAdv.getExtContentWithId( wo.getId() ));
					cache.put(new Element( cacheKey, wo ));
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

		static WrapCopier<CategoryInfo, Wo> copier = WrapCopierFactory.wo( CategoryInfo.class, Wo.class, null, ListTools.toList(JpaObject.FieldsInvisible));
		
		@FieldDescribe("扩展信息JSON内容")
		private String extContent = null;

		public String getExtContent() {
			return extContent;
		}

		public void setExtContent(String extContent) {
			this.extContent = extContent;
		}
	}
}