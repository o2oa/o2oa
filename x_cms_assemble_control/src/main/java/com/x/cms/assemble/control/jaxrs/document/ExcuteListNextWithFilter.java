package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.document.entity.DocumentCacheForFilter;
import com.x.cms.assemble.control.jaxrs.document.exception.DocumentInfoProcessException;
import com.x.cms.assemble.control.jaxrs.documentpermission.WrapInDocumentSearchFilter;
import com.x.cms.core.entity.Document;

import net.sf.ehcache.Element;

public class ExcuteListNextWithFilter extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListNextWithFilter.class );
	
	protected ActionResult<List<WrapOutDocumentSimple>> execute( HttpServletRequest request, String id, Integer count, WrapInFilter wrapIn, EffectivePerson effectivePerson ) {
		ActionResult<List<WrapOutDocumentSimple>> result = new ActionResult<>();
		WrapInDocumentSearchFilter wrapInDocumentSearchFilter = new WrapInDocumentSearchFilter();
		Long total = 0L;
		List<WrapOutDocumentSimple> wraps = null;
		List<String> manageableCatagories = null;
		List<String> viewAbleDocIds = null;
		List<String> allViewAbleCategoryIds = new ArrayList<>();
		List<String> permissionObjectCode = new ArrayList<>();
		List<Document> documentList = null;
		DocumentCacheForFilter documentCacheForFilter = null;
		Boolean isXAdmin = false;
		Boolean check = true;
		
		if( wrapIn == null ){
			wrapIn = new WrapInFilter();
		}		
		if( count == 0 ){
			count = 20;
		}
		try {
			isXAdmin = effectivePerson.isManager();
		} catch (Exception e) {
			check = false;
			Exception exception = new DocumentInfoProcessException( e, "系统在检查用户是否是平台管理员时发生异常。Name:" + effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		String cacheKey = getCacheKeyFormWrapInFilter( effectivePerson.getName(), isXAdmin, id, count, wrapIn );
		Element element_filter = cache.get( cacheKey );
		
		if ( (null != element_filter) && ( null != element_filter.getObjectValue()) ) {
			documentCacheForFilter = ( DocumentCacheForFilter ) element_filter.getObjectValue();
			result.setCount( documentCacheForFilter.getTotal() );
			result.setData( documentCacheForFilter.getDocumentList() );
		} else {			
			//计算当前用户管理的所有栏目和分类
			if( check ){
				//根据权限，把用户传入的AppId和categoryId进行过滤，最终形成一个可以访问的allViewAbleCategoryIds
				try {
					manageableCatagories = appCategoryAdminServiceAdv.getManagerableCatagories( effectivePerson.getName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentInfoProcessException( e, "系统在根据登录人员获取能管理的分类信息ID列表时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			if( check ){
				//根据权限，把用户传入的AppId和categoryId进行过滤，最终形成一个可以访问的allViewAbleCategoryIds
				try {
					allViewAbleCategoryIds = getAllViewAbleCategoryIds( wrapIn.getAppIdList(), wrapIn.getAppAliasList(), wrapIn.getCategoryIdList(), wrapIn.getCategoryAliasList(), effectivePerson.getName(), isXAdmin );
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentInfoProcessException( e, "系统在根据应用栏目列表和分类列表计算可访问的分类ID列表时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			
			if( check ){
				if( allViewAbleCategoryIds == null || allViewAbleCategoryIds.isEmpty() ){
					if( allViewAbleCategoryIds == null ){
						allViewAbleCategoryIds = new ArrayList<>();
					}
					//看看是不是从所有的栏目和分类里取，如果不是，则添加无可见分类，如果是，则整体排序
					if( ( wrapIn.getAppIdList()== null || wrapIn.getAppIdList().isEmpty() )&&
						( wrapIn.getAppAliasList()== null || wrapIn.getAppAliasList().isEmpty() )&&
						( wrapIn.getCategoryIdList()== null || wrapIn.getCategoryIdList().isEmpty() )&&
						( wrapIn.getCategoryAliasList()== null || wrapIn.getCategoryAliasList().isEmpty() )
					){
						//手机办公首页，是从所有文档里直接查询的，没有带查询条件
					}else{
						allViewAbleCategoryIds.add( "无可见分类" );
					}
				}
				wrapInDocumentSearchFilter.setCategoryIdList( allViewAbleCategoryIds );
				wrapInDocumentSearchFilter.setTitle( wrapIn.getTitle() );
				wrapInDocumentSearchFilter.setCreateDateList( wrapIn.getCreateDateList() );
				wrapInDocumentSearchFilter.setCreatorList( wrapIn.getCreatorList() );
				wrapInDocumentSearchFilter.setPublisherList( wrapIn.getPublisherList() );
				wrapInDocumentSearchFilter.setPublishDateList( wrapIn.getPublishDateList() );
				wrapInDocumentSearchFilter.setStatusList( wrapIn.getStatusList( ));
				wrapInDocumentSearchFilter.setOrderField( wrapIn.getOrderField() );
				wrapInDocumentSearchFilter.setOrderType( wrapIn.getOrderType() );
			}
			
			if( check ){
				//在查询 之前先圈定用户所在的部门，群组，角色和自己的姓名，放到一个List里，进行权限控制 permissionObjectCode
				try {
					permissionObjectCode = userManagerService.composeUserPermission( effectivePerson.getName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentInfoProcessException( e, "系统在根据登录用户姓名获取用户拥有的所有组织，角色，群组信息列表时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				if( isXAdmin ){
					try {
						viewAbleDocIds = documentServiceAdv.lisViewableDocIdsWithFilter( wrapInDocumentSearchFilter, 500 );
					} catch (Exception e) {
						check = false;
						Exception exception = new DocumentInfoProcessException( e, "系统在根据过滤条件查询用户可访问的文档ID列表时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}else{
					//然后到documentPermission表里根据要求进行条件查询并且排序后，输出下一页的ID列表
					try {
						viewAbleDocIds = documentPermissionServiceAdv.lisViewableDocIdsWithFilter( wrapInDocumentSearchFilter, permissionObjectCode, manageableCatagories, 500 );
					} catch (Exception e) {
						check = false;
						Exception exception = new DocumentInfoProcessException( e, "系统在根据过滤条件查询用户可访问的文档ID列表时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
			if( check ){
				//从数据库中查询符合条件的对象总数
				try {
					total = documentServiceAdv.countWithDocIds( viewAbleDocIds );
				} catch (Exception e) {
					check = false;
					Exception exception = new DocumentInfoProcessException( e, "系统在获取用户可查询到的文档数据条目数量时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				try {
					documentList = documentServiceAdv.listNextWithDocIds( id, count, viewAbleDocIds,  wrapInDocumentSearchFilter.getOrderField(),  wrapInDocumentSearchFilter.getOrderType() );
				} catch ( Exception e ) {
					check = false;
					Exception exception = new DocumentInfoProcessException( e, "系统在根据用户可访问的文档ID列表对文档进行分页查询时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( check ){
				if( documentList != null ){
					try {
						wraps = WrapTools.documentSimple_wrapout_copier.copy( documentList );
						documentCacheForFilter = new DocumentCacheForFilter();
						documentCacheForFilter.setDocumentList( wraps );
						documentCacheForFilter.setTotal( total );
						cache.put( new Element( cacheKey, documentCacheForFilter ) );
						result.setCount( total );
						result.setData( wraps );
					} catch (Exception e) {
						Exception exception = new DocumentInfoProcessException( e, "系统在将分页查询结果转换为可输出的数据信息时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}
		return result;
	}	

	/**
	 * 组织一个缓存的KEY
	 * 
	 * @param personName
	 * @param isXAdmin
	 * @param id
	 * @param count
	 * @param wrapIn
	 * @return
	 */
	private String getCacheKeyFormWrapInFilter( String personName, Boolean isXAdmin, String id, Integer count, WrapInFilter wrapIn ) {
		
		String cacheKey = ApplicationCache.concreteCacheKey( personName, id, count, isXAdmin );
		
		if( wrapIn.getTitle() != null && !wrapIn.getTitle().isEmpty() ){
			cacheKey = ApplicationCache.concreteCacheKey( cacheKey, wrapIn.getTitle() );
		}
		if( wrapIn.getOrderType() != null && !wrapIn.getOrderType().isEmpty() ){
			cacheKey = ApplicationCache.concreteCacheKey( cacheKey, wrapIn.getOrderType() );
		}
		if( wrapIn.getOrderField()!= null && !wrapIn.getOrderField().isEmpty() ){
			cacheKey = ApplicationCache.concreteCacheKey( cacheKey, wrapIn.getOrderField() );
		}
		if( wrapIn.getAppIdList() != null && !wrapIn.getAppIdList().isEmpty() ){
			for( String key : wrapIn.getAppIdList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		if( wrapIn.getAppAliasList() != null && !wrapIn.getAppAliasList().isEmpty() ){
			for( String key : wrapIn.getAppAliasList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		if( wrapIn.getCategoryIdList() != null && !wrapIn.getCategoryIdList().isEmpty() ){
			for( String key : wrapIn.getCategoryIdList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		if( wrapIn.getCategoryAliasList() != null && !wrapIn.getCategoryAliasList().isEmpty() ){
			for( String key : wrapIn.getCategoryAliasList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		if( wrapIn.getCreateDateList() != null && !wrapIn.getCreateDateList().isEmpty() ){
			for( String key : wrapIn.getCreateDateList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		
		if( wrapIn.getPublishDateList() != null && !wrapIn.getPublishDateList().isEmpty() ){
			for( String key : wrapIn.getPublishDateList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		if( wrapIn.getPublisherList() != null && !wrapIn.getPublisherList().isEmpty() ){
			for( String key : wrapIn.getPublisherList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		if( wrapIn.getStatusList() != null && !wrapIn.getStatusList().isEmpty() ){
			for( String key : wrapIn.getStatusList() ){
				cacheKey = ApplicationCache.concreteCacheKey( cacheKey, key );
			}
		}
		cacheKey = ApplicationCache.concreteCacheKey( cacheKey, "filterNext" );
		return cacheKey;
	}
}