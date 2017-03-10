package com.x.cms.assemble.control.jaxrs.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.WrapTools;
import com.x.cms.assemble.control.jaxrs.document.WrapOutDocumentComplex;
import com.x.cms.assemble.control.jaxrs.documentpermission.WrapInDocumentSearchFilter;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.View;

public class ExcuteListNextPageViewData extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteListNextPageViewData.class );
	
	protected ActionResult<List<WrapOutDocumentComplex>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, Integer count, WrapInFilter wrapIn ) throws Exception {
		ActionResult<List<WrapOutDocumentComplex>> result = new ActionResult<>();
		List<WrapOutDocumentComplex> wraps = new ArrayList<WrapOutDocumentComplex>();
		WrapInDocumentSearchFilter wrapInDocumentSearchFilter = new WrapInDocumentSearchFilter();
		WrapOutDocumentComplex wrap = null;
		List<String> manageableCatagories = null;
		List<String> viewAbleCategoryIdList = new ArrayList<>();
		List<String> documentStatusList = new ArrayList<>();
		List<String> permissionObjectCode = null;
		List<String> viewAbleDocIds = null;
		List<Document> documentList = null;
		Long documentCount = 0L;
		Map<String, Object> condition = new HashMap<String, Object>();
		View view = null;
		Boolean check = true;
		
		
		//1、进行输入的信息验证
		if( count <= 0){
			count = 12;
		}
		if( "(0)".equals(id) || "".equals(id) ){
			id = null;
		}
		if( StringUtils.isEmpty( wrapIn.getOrderField() ) ){
			condition.put( "orderField", "title" );
		}else{
			condition.put( "orderField", wrapIn.getOrderField() );
		}
		if( wrapIn.getOrderType() != null ){
			condition.put( "orderType", wrapIn.getOrderType() );
		}else{
			condition.put( "orderType", "DESC" );
		}
		if( wrapIn.getSearchDocStatus() != null ){
			condition.put( "searchDocStatus", wrapIn.getSearchDocStatus() );
			documentStatusList.add( wrapIn.getSearchDocStatus() );
		}else{
			condition.put( "searchDocStatus", "published" );
			documentStatusList.add( "published" );
		}
		if( check ){
			if( wrapIn.getViewId() != null ){
				condition.put( "viewId", wrapIn.getViewId() );
			}else{
				check = false;
				Exception exception = new ViewIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}		
		if( check ){
			if( wrapIn.getCategoryId() != null ){
				condition.put( "categoryId", wrapIn.getCategoryId() );
				viewAbleCategoryIdList.add( wrapIn.getCategoryId() );
			}else{
				check = false;
				Exception exception = new CategoryIdEmptyException();
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			try{
				view = viewServiceAdv.get( wrapIn.getViewId() );
				if( view == null ){
					check = false;
					Exception exception = new ViewNotExistsException( wrapIn.getViewId() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new ServiceLogicException( e, "系统在根据ID查询列表视图信息时发生异常。" );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			//在查询 之前先圈定用户所在的部门，群组，角色和自己的姓名，放到一个List里，进行权限控制 permissionObjectCode
			try {
				permissionObjectCode = userManagerService.composeUserPermission( effectivePerson.getName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ServiceLogicException( e, "系统在根据登录用户姓名获取用户拥有的所有组织，角色，群组信息列表时发生异常。" );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			wrapInDocumentSearchFilter.setAppIdList( null );
			wrapInDocumentSearchFilter.setTitle( null );
			wrapInDocumentSearchFilter.setCategoryIdList( viewAbleCategoryIdList );
			wrapInDocumentSearchFilter.setCreateDateList( null );
			wrapInDocumentSearchFilter.setCreatorList( null );
			wrapInDocumentSearchFilter.setPublisherList( null );
			wrapInDocumentSearchFilter.setPublishDateList( null );
			wrapInDocumentSearchFilter.setStatusList( documentStatusList );
		}
		if( check ){
			//计算当前用户管理的所有栏目和分类
			if( check ){
				//根据权限，把用户传入的AppId和categoryId进行过滤，最终形成一个可以访问的allViewAbleCategoryIds
				try {
					manageableCatagories = getManagerableCatagories( effectivePerson.getName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ServiceLogicException( e, "系统在根据登录人员获取能管理的分类信息ID列表时发生异常。" );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
			//然后到documentPermission表里根据要求进行条件查询并且排序后，输出下一页的ID列表
			try {
				viewAbleDocIds = documentPermissionServiceAdv.lisViewableDocIdsWithFilter( wrapInDocumentSearchFilter, permissionObjectCode, manageableCatagories, 500 );
				if( viewAbleDocIds == null ){
					viewAbleDocIds = new ArrayList<>();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ServiceLogicException( e, "系统在根据过滤条件查询用户可访问的文档ID列表时发生异常。" );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( viewAbleDocIds != null && !viewAbleDocIds.isEmpty() ){
				documentCount = Long.parseLong( viewAbleDocIds.size() + "" );
			}
		}
		if( check ){
			if( viewAbleDocIds != null && !viewAbleDocIds.isEmpty() ){
				try {
					documentList = viewServiceAdv.nextPageDocuemntView( id, count, viewAbleDocIds, condition );
				} catch (Exception e) {
					check = false;
					Exception exception = new ServiceLogicException( e, "系统在根据条件查询列表数据时发生异常。" );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}
		
		if( check ){
			if( documentList != null && documentList.size() > 0 ){
				for( Document document : documentList ){
					wrap = new WrapOutDocumentComplex();
					try {
						wrap.setDocument( WrapTools.document_wrapout_copier.copy( document ) );
					} catch (Exception e) {
						Exception exception = new ServiceLogicException( e, "系统在将文档信息转换为可输出的数据信息时发生异常。" );
						logger.error( exception, effectivePerson, request, null);
					}
					try {
						wrap.setData( documentInfoServiceAdv.getDocumentData(document) );
					} catch (Exception e) {
						Exception exception = new ServiceLogicException( e, "系统在根据文档信息获取文档数据信息时发生异常。" );
						logger.error( exception, effectivePerson, request, null);
					}
					wraps.add( wrap );
				}
			}
			result.setData( wraps );
			result.setCount( documentCount );
		}
		return result;
	}
	private List<String> getManagerableCatagories(String personName ) throws Exception {
		List<String> appInfo_ids = null;
		List<String> category_ids = null;
		List<String> result = new ArrayList<>();
				
		appInfo_ids = appCategoryAdminServiceAdv.listAppCategoryObjectIdByUser( personName, "APPINFO" );
		if( appInfo_ids != null && !appInfo_ids.isEmpty() ){
			for( String appId : appInfo_ids ){
				category_ids = categoryInfoServiceAdv.listByAppId(appId);
				if( category_ids != null && !category_ids.isEmpty() ){
					for( String categoryId : category_ids ){
						if( !result.contains( categoryId )){
							result.add( categoryId );
						}
					}
				}
			}
		}
		
		category_ids = appCategoryAdminServiceAdv.listAppCategoryObjectIdByUser( personName, "CATEGORY" );
		if( category_ids != null && !category_ids.isEmpty() ){
			for( String categoryId : category_ids ){
				if( !result.contains( categoryId )){
					result.add( categoryId );
				}
			}
		}
		
		return result;
	}
}