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
import com.x.cms.assemble.control.jaxrs.categoryinfo.exception.CategoryInfoAppIdEmptyException;
import com.x.cms.assemble.control.jaxrs.categoryinfo.exception.CategoryInfoProcessException;
import com.x.cms.core.entity.CategoryInfo;

import net.sf.ehcache.Element;

public class ExcuteListWhatICanPublish extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListWhatICanPublish.class );
	
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
				Exception exception = new CategoryInfoProcessException( e, "系统在检查用户是否是平台管理员时发生异常。Name:" + effectivePerson.getName() );
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
				Exception exception = new CategoryInfoProcessException( e, "系统在检查用户是否是平台管理员时发生异常。Name:" + effectivePerson.getName() );
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
				Exception exception = new CategoryInfoProcessException( e, "系统在检查用户是否是平台管理员时发生异常。Name:" + effectivePerson.getName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		String cacheKey = ApplicationCache.concreteCacheKey( effectivePerson.getName(), appId, "publish", isXAdmin, appManager, appPublisher );
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
						Exception exception = new CategoryInfoProcessException( e, "根据应用栏目ID查询分类信息对象时发生异常。AppId:" + appId );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					if( check ){
						try {
							ids = categoryInfoServiceAdv.listPublishByAppIdAndUserPermission( appId, effectivePerson.getName(), "PUBLISH" );
						} catch (Exception e) {
							check = false;
							Exception exception = new CategoryInfoProcessException( e, "系统在检查用户是否是平台管理员时发生异常。Name:" + effectivePerson.getName() );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
				}
			}
			
			if( check ){
				if( ids != null && !ids.isEmpty() ){
					try {
						categoryInfoList = categoryInfoServiceAdv.list( ids );
					} catch (Exception e) {
						check = false;
						Exception exception = new CategoryInfoProcessException( e, "根据ID列表查询分类信息对象时发生异常。" );
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