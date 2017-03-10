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
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentPermissionServiceAdv;
import com.x.cms.assemble.control.service.DocumentViewRecordServiceAdv;
import com.x.cms.assemble.control.service.FormServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentPictureInfo;
import com.x.cms.core.entity.content.DataItem;
import com.x.organization.core.express.wrap.WrapIdentity;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	static Ehcache cache = ApplicationCache.instance().getCache( Document.class);
	static Ehcache pic_cache = ApplicationCache.instance().getCache( DocumentPictureInfo.class);
	
	protected LogService logService = new LogService();
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
	protected List<String> getAllViewAbleCategoryIds( List<String> wrapIn_viewAbleAppIds, List<String> wrapIn_viewAbleCategoryIds, String personName, Boolean xadmin ) throws Exception{
		List<String> categoryIds = null;
		List<String> allViewableCategoryIds = new ArrayList<>();
		
		if( wrapIn_viewAbleAppIds != null && !wrapIn_viewAbleAppIds.isEmpty() ){
			for( String appId : wrapIn_viewAbleAppIds ){
				if( xadmin ){
					categoryIds = categoryInfoServiceAdv.listByAppId( appId );
				}else{
					//根据自己的权限 获取在该栏目下面所有可见的分类ID
					categoryIds = categoryInfoServiceAdv.listViewableByAppIdAndUserPermission( appId, personName, null );
				}
				if( categoryIds != null && !categoryIds.isEmpty() ){
					for( String categoryId : categoryIds ){
						//看看分类列表是否为空
						if(  wrapIn_viewAbleCategoryIds!= null && !wrapIn_viewAbleCategoryIds.isEmpty()  ){
							for( String viewAbleCategoryId : wrapIn_viewAbleCategoryIds ){
								if( categoryId.equalsIgnoreCase( viewAbleCategoryId )){
									allViewableCategoryIds.add( categoryId );
								}
							}
						}else{
							allViewableCategoryIds.add( categoryId );
						}
					}
				}
			}
		}else{
			allViewableCategoryIds = wrapIn_viewAbleCategoryIds;
		}
		return allViewableCategoryIds;
	}
}
