package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.categoryinfo.exception.CategoryInfoProcessException;
import com.x.cms.core.entity.CategoryInfo;

import net.sf.ehcache.Element;

public class ExcuteListAll extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListAll.class );

	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutCategoryInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutCategoryInfo>> result = new ActionResult<>();
		List<WrapOutCategoryInfo> wraps = null;
		List<CategoryInfo> categoryInfoList = null;
		Boolean check = true;		
		
		String cacheKey = ApplicationCache.concreteCacheKey( "all" );
		Element element = cache.get( cacheKey );
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = ( List<WrapOutCategoryInfo> ) element.getObjectValue();
			result.setData(wraps);
		} else {
			try {
				categoryInfoList = categoryInfoServiceAdv.listAll();
			} catch ( Exception e ) {
				check = false;
				Exception exception = new CategoryInfoProcessException( e, "查询所有分类信息对象时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if( check ){
				if( categoryInfoList != null && !categoryInfoList.isEmpty() ){
					try {
						wraps = WrapTools.category_wrapout_copier.copy( categoryInfoList );
						SortTools.desc( wraps, "categorySeq");
						cache.put(new Element( cacheKey, wraps ));
						result.setData(wraps);
					} catch ( Exception e ) {
						check = false;
						Exception exception = new CategoryInfoProcessException( e, "将查询出来的分类信息对象转换为可输出的数据信息时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}
		
		return result;
	}
	
}