package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.service.AppCategoryAdminServiceAdv;
import com.x.cms.assemble.control.service.AppCategoryPermissionService;
import com.x.cms.assemble.control.service.AppCategoryPermissionServiceAdv;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentPermissionServiceAdv;
import com.x.cms.assemble.control.service.DocumentViewRecordServiceAdv;
import com.x.cms.assemble.control.service.FormServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.content.DataItem;
import com.x.organization.core.express.wrap.WrapIdentity;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( Document.class);
	
	protected LogService logService = new LogService();
	protected AppCategoryPermissionServiceAdv appCategoryPermissionServiceAdv = new AppCategoryPermissionServiceAdv();
	protected DocumentViewRecordServiceAdv documentViewRecordServiceAdv = new DocumentViewRecordServiceAdv();
	protected DocumentInfoServiceAdv documentServiceAdv = new DocumentInfoServiceAdv();
	protected FormServiceAdv formServiceAdv = new FormServiceAdv();
	protected AppCategoryAdminServiceAdv appCategoryAdminServiceAdv = new AppCategoryAdminServiceAdv();
	protected AppCategoryPermissionService appCategoryPermissionService = new AppCategoryPermissionService();
	protected DocumentPermissionServiceAdv documentPermissionServiceAdv = new DocumentPermissionServiceAdv();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();
	
	protected WrapIdentity findIdentity( List<WrapIdentity> identities, String name ) throws Exception {
		if( name != null && !name.isEmpty() && !"null".equals(name)){
			if( identities != null && !identities.isEmpty() ){
				for (WrapIdentity o : identities) {
					if (StringUtils.equals(o.getName(), name)) {
						return o;
					}
				}
			}
		}else{
			if( identities != null && !identities.isEmpty() ){
				return identities.get(0);
			}
		}
		return null;
	}
	
	protected boolean modifyDocStatus( String id, String stauts, String personName ) throws Exception{
		List<DataItem> dataItemList = null;
		Business business = null;
		Document document = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);			
		
			//进行数据库持久化操作
			emc.beginTransaction( Document.class );
			emc.beginTransaction( DataItem.class );
			
			//删除与该文档有关的所有数据信息
			dataItemList = business.getDataItemFactory().listWithDocIdWithPath( id );
			if( dataItemList != null && !dataItemList.isEmpty() ){
				for( DataItem dataItem : dataItemList ){
					dataItem.setDocStatus( stauts );
					dataItem.setPublishTime( new Date() );
					emc.check( dataItem, CheckPersistType.all );
				}
			}
			
			document = business.getDocumentFactory().get(id);
			if (null != document) {
				//修改文档状态
				document.setDocStatus( stauts );
				document.setPublishTime( new Date() );
				//保存文档信息
				emc.check( document, CheckPersistType.all);
			}			
			emc.commit();
			return true;
		} catch (Exception th) {
			throw th;
		}
	}
	
	/**
	 * 根据要求的栏目ID列表，分类ID列表，查询用户在这些栏目下可以访问的所有分类ID
	 * @param wrapIn_viewAbleAppIds
	 * @param wrapIn_viewAbleCategoryIds
	 * @param personName
	 * @param xadmin
	 * @return
	 * @throws Exception
	 */
	protected List<String> getAllViewAbleCategoryIds( List<String> wrapIn_viewAbleAppIds, List<String> wrapIn_appAliasList, List<String> wrapIn_viewAbleCategoryIds, List<String> wrapIn_categoryAliasList, String personName, Boolean xadmin ) throws Exception{
	
		List<String> ids_tmp = null;
		List<String> appIds = null;
		List<String> categoryIds = null;
		List<String> categoryIds_tmp = null;
		List<String> viewAbleAppIds = new ArrayList<>();
		List<String> allViewableCategoryIds = new ArrayList<>();
		Boolean categoryIdsEmpty = false;
		Boolean categoryAliasEmpty = false;
		
		//根据栏目ID取
		if( wrapIn_viewAbleAppIds != null && !wrapIn_viewAbleAppIds.isEmpty() ){
			for( String appId : wrapIn_viewAbleAppIds ){
				if( !viewAbleAppIds.contains( appId )){
					//System.out.println("===============1 add appinfo:" + appId );
					viewAbleAppIds.add( appId );
				}
			}
		}
		
		//根据栏目别名取
		if( wrapIn_appAliasList != null && !wrapIn_appAliasList.isEmpty() ){
			for( String appAlias : wrapIn_appAliasList ){
				appIds = appInfoServiceAdv.getWithAlias(appAlias);
				if( appIds != null && !appIds.isEmpty() && !viewAbleAppIds.contains( appIds.get(0) )){
					//System.out.println("===============2 add appinfo:" + appIds.get(0) );
					viewAbleAppIds.add( appIds.get(0) );
				}
			}
		}
		
		if( viewAbleAppIds != null && !viewAbleAppIds.isEmpty() ){
			
			for( String appId : viewAbleAppIds ){
				if( xadmin ){
					categoryIds = categoryInfoServiceAdv.listByAppId( appId );
				}else{
					//如果该员工是该栏目的访问者，那么该员工可以访问该栏目下所有的分类
					ids_tmp = appCategoryPermissionServiceAdv.listAppCategoryIdByPermission( "APPINFO", appId, personName, null );
					if( ids_tmp != null && !ids_tmp.isEmpty() ){
						categoryIds = categoryInfoServiceAdv.listByAppId( appId );
					}else{
						//或者说这个栏目没有设置任何访问者
						ids_tmp = appCategoryPermissionServiceAdv.listPermissionByAppInfo( appId, "VIEW" );
						if( ids_tmp != null && !ids_tmp.isEmpty() ){
							//根据自己的权限 获取在该栏目下面所有可见的分类ID
							//System.out.println( "======================"+ appId +", personName:" + personName);
							categoryIds = categoryInfoServiceAdv.listViewableByAppIdAndUserPermission( appId, personName );
						}else{
							//没有设置任何访问者, 栏目下所有的分类都可以访问	
							categoryIds = categoryInfoServiceAdv.listByAppId( appId );
						}
					}
				}
				if( categoryIds != null && !categoryIds.isEmpty() ){
					for( String categoryId : categoryIds ){
						//看看分类列表是否为空
						if(  wrapIn_viewAbleCategoryIds!= null && !wrapIn_viewAbleCategoryIds.isEmpty()  ){
							for( String viewAbleCategoryId : wrapIn_viewAbleCategoryIds ){
								if( categoryId.equalsIgnoreCase( viewAbleCategoryId )){
									if( !allViewableCategoryIds.contains( categoryId )){
										//System.out.println( "======================3 add category:" + categoryId);
										allViewableCategoryIds.add( categoryId );
									}
								}
							}
						}else{
							categoryIdsEmpty = true;
						}
						
						if(  wrapIn_categoryAliasList!= null && !wrapIn_categoryAliasList.isEmpty()  ){
							for( String cataggoryAlias : wrapIn_categoryAliasList ){
								categoryIds_tmp = categoryInfoServiceAdv.listByAlias( cataggoryAlias );
								if( categoryId.equalsIgnoreCase( categoryIds_tmp.get( 0 ) )){
									if( !allViewableCategoryIds.contains( categoryIds_tmp.get( 0 ) )){
										//System.out.println( "======================4 add category:" + categoryIds_tmp.get( 0 ));
										allViewableCategoryIds.add( categoryIds_tmp.get( 0 ) );
									}
								}
							}
						}else{
							categoryAliasEmpty = true;
						}
						
						if( categoryIdsEmpty && categoryAliasEmpty ){
							if( !allViewableCategoryIds.contains( categoryId )){
								//System.out.println( "======================5 add category:" + categoryId);
								allViewableCategoryIds.add( categoryId );
							}
						}						
					}
				}
			}
		}else{
			allViewableCategoryIds = wrapIn_viewAbleCategoryIds;
			if(  wrapIn_categoryAliasList!= null && !wrapIn_categoryAliasList.isEmpty()  ){
				for( String cataggoryAlias : wrapIn_categoryAliasList ){
					categoryIds_tmp = categoryInfoServiceAdv.listByAlias( cataggoryAlias );
					if( !allViewableCategoryIds.contains( categoryIds_tmp.get( 0 ) )){
						//System.out.println( "======================6 add category:" + categoryIds_tmp.get( 0 ));
						allViewableCategoryIds.add( categoryIds_tmp.get( 0 ) );
					}
				}
			}
		}
		return allViewableCategoryIds;
	}
}
