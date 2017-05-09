package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception.AppCategoryAdminCategoryIdEmptyException;
import com.x.cms.assemble.control.jaxrs.appcategoryadmin.exception.AppCategoryAdminProcessException;
import com.x.cms.core.entity.AppCategoryAdmin;

import net.sf.ehcache.Element;

public class ExcuteListByCategoryId extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListByCategoryId.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutAppCategoryAdmin>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String categoryId ) throws Exception {
		ActionResult<List<WrapOutAppCategoryAdmin>> result = new ActionResult<>();
		List<WrapOutAppCategoryAdmin> wraps = null;
		List<String> ids = null;
		List<AppCategoryAdmin> appCategoryAdminList = null;
		Boolean check = true;
		
		String cacheKey = ApplicationCache.concreteCacheKey( "category", categoryId );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = ( List<WrapOutAppCategoryAdmin> ) element.getObjectValue();
			result.setData(wraps);
		} else {
			if( check ){
				if( categoryId == null || categoryId.isEmpty() ){
					check = false;
					Exception exception = new AppCategoryAdminCategoryIdEmptyException();
					result.error( exception );
				}
			}
			if( check ){
				try {
					ids = appCategoryAdminServiceAdv.listAppCategoryIdByCategoryId( categoryId );
				} catch (Exception e) {
					check = false;
					Exception exception = new AppCategoryAdminProcessException( e, "根据应用栏目ID查询所有对应的应用栏目分类管理员配置信息列表时发生异常。AppId:" + categoryId );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				if( ids != null && !ids.isEmpty() ){
					try {
						appCategoryAdminList = appCategoryAdminServiceAdv.list( ids );
					} catch (Exception e) {
						check = false;
						Exception exception = new AppCategoryAdminProcessException( e, "根据指定的ID列表查询应用栏目分类管理员配置信息时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			if( check ){
				if( appCategoryAdminList != null && !appCategoryAdminList.isEmpty() ){
					try {
						wraps = WrapTools.appCategoryAdmin_wrapout_copier.copy( appCategoryAdminList );
						SortTools.desc( wraps, "sequence");
						cache.put(new Element( cacheKey, wraps ));
						result.setData(wraps);
					} catch (Exception e) {
						Exception exception = new AppCategoryAdminProcessException( e, "系统将查询出来的应用栏目分类管理员信息转换为输出格式时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}		
		return result;
	}
	
}