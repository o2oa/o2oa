package com.x.cms.assemble.control.jaxrs.categoryinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.core.entity.CategoryInfo;

import net.sf.ehcache.Element;

public class ExcuteListWhatICanView extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListWhatICanView.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<WrapOutCategoryInfo>> execute( HttpServletRequest request, String appId, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutCategoryInfo>> result = new ActionResult<>();
		List<WrapOutCategoryInfo> wraps = null;
		List<String> ids = null;
		List<String> ids_tmp = null;
		List<CategoryInfo> categoryInfoList = null;	
		Boolean check = true;
		Boolean isXAdmin = false;
		Boolean appManager = false;
		Boolean appPublisher = false;
		
		if( appId == null || appId.isEmpty() ){
			check = false;
			Exception exception = new CategoryInfoAppIdEmptyException();
			result.error( exception );
		}
		
		if( check ){//判断用户是否系统管理员
			try {
				isXAdmin = effectivePerson.isManager();
			} catch (Exception e) {
				check = false;
				Exception exception = new UserManagerCheckException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){//判断用户是否该栏目的管理者
			try {
				ids_tmp = appCategoryAdminServiceAdv.listAppCategoryIdByCondition( "APPINFO" , appId, effectivePerson.getName() );
				if( ids_tmp != null && !ids_tmp.isEmpty() ){
					appManager = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new UserManagerCheckException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){//判断用户是否该栏目的发布者
			try {
				ids_tmp = appCategoryPermissionServiceAdv.listAppCategoryIdByCondition( "APPINFO", appId, effectivePerson.getName(), "PUBLISH" );
				if( ids_tmp != null && !ids_tmp.isEmpty() ){
					appPublisher = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new UserManagerCheckException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( "view", effectivePerson.getName(), appId, isXAdmin, appManager,appPublisher );
		Element element = cache.get(cacheKey);
		
		if ((null != element) && ( null != element.getObjectValue()) ) {
			wraps = ( List<WrapOutCategoryInfo> ) element.getObjectValue();
			result.setData(wraps);
		} else {			
			if( check ){			
				if( isXAdmin || appManager || appPublisher ){
					try {
						ids = categoryInfoServiceAdv.listByAppId( appId );
					} catch (Exception e) {
						check = false;
						Exception exception = new CategoryInfoListByAppIdException( e, appId );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					ids = new ArrayList<>();
					
					//登录者可以管理的分类
					if( check ){//判断用户是否该栏目的管理者
						try {
							ids_tmp = appCategoryAdminServiceAdv.listAppCategoryIdByCondition( "CATEGORY" , null, effectivePerson.getName() );
							for( String id : ids_tmp ){
								if( !ids.contains( id ) ){
									ids.add( id );
								}
							}
						} catch (Exception e) {
							check = false;
							Exception exception = new UserManagerCheckException( e, effectivePerson.getName() );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
					
					//登录者可以发布或者访问的分类, 需要按部门角色等进行搜索
					try {
						ids_tmp = categoryInfoServiceAdv.listViewableByAppIdAndUserPermission( appId, effectivePerson.getName(), null );
						for( String id : ids_tmp ){
							if( !ids.contains( id ) ){
								ids.add( id );
							}
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new CategoryInfoListViewableInPermissionException( e, effectivePerson.getName() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			
			if( check ){
				if( ids != null && !ids.isEmpty() ){
					try {
						categoryInfoList = categoryInfoServiceAdv.list( ids );
					} catch (Exception e) {
						check = false;
						Exception exception = new CategoryInfoListByIdsException( e );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			
			if( check ){
				if( categoryInfoList != null && !categoryInfoList.isEmpty() ){
					try {
						wraps = WrapTools.category_wrapout_copier.copy( categoryInfoList );
						SortTools.desc( wraps, "categorySeq");
						cache.put(new Element( cacheKey, wraps ));
						result.setData(wraps);		
					} catch (Exception e) {
						check = false;
						Exception exception = new CategoryInfoWrapOutException( e );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}		
		return result;
	}
	
}