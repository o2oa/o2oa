package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.cms.core.entity.enums.DocumentStatus;
import com.x.cms.core.entity.message.DocumentEvent;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryExt;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.query.core.entity.Item;

public class CategoryInfoService {

	public CategoryInfo get( EntityManagerContainer emc, String id ) throws Exception {
		if( StringUtils.isEmpty(id ) ){
			return null;
		}
		return emc.find(id, CategoryInfo.class );
	}

	public CategoryInfo getWithFlag( EntityManagerContainer emc, String flag ) throws Exception {
		if( StringUtils.isEmpty( flag ) ){
			return null;
		}
		return emc.flag( flag, CategoryInfo.class );
	}

	public List<String> listByAppId( EntityManagerContainer emc, String appId ) throws Exception {
		if( StringUtils.isEmpty(appId)){
			return null;
		}
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listByAppId( appId );
	}

	public List<String> listByAppId( String appId ) throws Exception {
		if( StringUtils.isEmpty(appId ) ){
			return null;
		}
		Business business = new Business( EntityManagerContainerFactory.instance().create() );
		return business.getCategoryInfoFactory().listByAppId( appId );
	}

	/**
	 * 根据需要过滤的栏目列表和信息类型以及返回最大条目数量列示栏目信息ID列表
	 * @param appIds
	 * @param documentType
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listByAppIds( EntityManagerContainer emc, List<String> appIds, String documentType, Integer maxCount ) throws Exception {
		if( ListTools.isEmpty( appIds ) ){
			return listAllIds(emc);
		}
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listByAppIds( appIds, documentType, maxCount );
	}

	public List<String> listAppPeopleViewableCategoryInfoIds( EntityManagerContainer emc, List<String> inAppInfoIds, List<String> inCategoryIds,
			List<String> excludCategoryIds, String documentType, Integer maxCount ) throws Exception {
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listAllPeopleViewableCategoryInfoIds(inAppInfoIds, inCategoryIds, excludCategoryIds,
				documentType, maxCount);
	}

	public List<String> listAppPeoplePublishableCategoryInfoIds(EntityManagerContainer emc, List<String> inAppInfoIds, List<String> inCategoryIds,
			List<String> excludCategoryIds, String documentType, Integer maxCount ) throws Exception {
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listAllPeoplePublishableCategoryInfoIds(inAppInfoIds, inCategoryIds, excludCategoryIds,
				documentType, maxCount);
	}

	public List<CategoryInfo> listAll(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listAll();
	}

	public List<String> listAllIds(EntityManagerContainer emc) throws Exception {
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listAllIds();
	}

	public CategoryInfo saveBaseInfo( EntityManagerContainer emc, CategoryInfo categoryInfo ) throws Exception {
		AppInfo appInfo = null;
		CategoryInfo categoryInfo_tmp = null;
		if( categoryInfo.getId() == null ){
			categoryInfo.setId( CategoryInfo.createId() );
		}
		appInfo = emc.find( categoryInfo.getAppId(), AppInfo.class );
		categoryInfo_tmp = emc.find( categoryInfo.getId(), CategoryInfo.class );
		emc.beginTransaction( AppInfo.class );
		emc.beginTransaction( CategoryInfo.class );
		if( categoryInfo_tmp == null ){
			categoryInfo.setAppName( appInfo.getAppName() );
			emc.persist( categoryInfo, CheckPersistType.all);
		}else{
			categoryInfo_tmp.setAppId( appInfo.getId() );
			categoryInfo_tmp.setAppName( appInfo.getAppName() );
			categoryInfo_tmp.setCategoryAlias( categoryInfo.getCategoryAlias() );
			categoryInfo_tmp.setCategoryMemo( categoryInfo.getCategoryMemo() );
			categoryInfo_tmp.setCategoryName( categoryInfo.getCategoryName() );
			categoryInfo_tmp.setFormId( categoryInfo.getFormId() );
			categoryInfo_tmp.setFormName( categoryInfo.getFormName() );
			categoryInfo_tmp.setParentId( categoryInfo.getParentId() );
			categoryInfo_tmp.setReadFormId( categoryInfo.getReadFormId() );
			categoryInfo_tmp.setReadFormName( categoryInfo.getReadFormName() );
			emc.check( categoryInfo_tmp, CheckPersistType.all );
		}
		if ( appInfo.getCategoryList() == null ){
			appInfo.setCategoryList( new ArrayList<String>());
		}
		if( !appInfo.getCategoryList().contains( categoryInfo.getId() ) ){
			appInfo.getCategoryList().add( categoryInfo.getId() );
		}
		emc.commit();
		return categoryInfo;
	}

	public CategoryInfo save( EntityManagerContainer emc, CategoryInfo object, String extContent ) throws Exception {
		CategoryInfo categoryInfo = null;
		CategoryExt categoryExt = null;
		AppInfo appInfo = null;

		if( object.getId() == null ){
			object.setId( CategoryInfo.createId() );
		}
		categoryInfo = emc.find( object.getId(), CategoryInfo.class );
		categoryExt = emc.find( object.getId(), CategoryExt.class );
		appInfo = emc.find( object.getAppId(), AppInfo.class );

		if( appInfo == null ){ throw new Exception("应用栏目信息不存在！"); }

		//补全默认列表名称
		if ( StringUtils.isNotEmpty( object.getDefaultViewId() ) ) {
			View view = emc.find( object.getDefaultViewId(), View.class );
			if (view != null) {
				object.setDefaultViewName( view.getName() );
			} else {
				throw new Exception("category default view not exits. view id:" + object.getDefaultViewId() );
			}
		}

		if( categoryInfo == null ){
			categoryInfo = new CategoryInfo();
			object.copyTo( categoryInfo );
			if(StringUtils.isBlank(categoryInfo.getReadFormId()) && StringUtils.isNotBlank(appInfo.getDefaultReadForm())){
				Form form = emc.find( appInfo.getDefaultReadForm(), Form.class );
				if(form != null){
					categoryInfo.setReadFormId(form.getId());
					categoryInfo.setReadFormName(form.getName());
				}
			}
			if(StringUtils.isBlank(categoryInfo.getFormId()) && StringUtils.isNotBlank(appInfo.getDefaultEditForm())){
				Form form = emc.find( appInfo.getDefaultEditForm(), Form.class );
				if(form != null){
					categoryInfo.setFormId(form.getId());
					categoryInfo.setFormName(form.getName());
				}
			}
			categoryInfo.setAppName( appInfo.getAppName() );
			if( StringUtils.isEmpty( categoryInfo.getCategoryAlias() )) {
				categoryInfo.setCategoryAlias( categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName() );
			}
			if( StringUtils.isEmpty( categoryInfo.getDocumentType() ) ) {
				categoryInfo.setDocumentType( appInfo.getDocumentType() );
			}
			if( !"信息".equals(categoryInfo.getDocumentType()) && !"数据".equals( categoryInfo.getDocumentType() )) {
				categoryInfo.setDocumentType( "信息" );
			}
			if( categoryInfo.getCreateTime() == null ) {
				categoryInfo.setCreateTime(new Date());
			}
			emc.beginTransaction( CategoryInfo.class );
			emc.persist( categoryInfo, CheckPersistType.all);
			emc.commit();
		}else{
			object.copyTo( categoryInfo, JpaObject.FieldsUnmodify  );
			categoryInfo.setAppName( appInfo.getAppName() );
			if( StringUtils.isEmpty( categoryInfo.getCategoryAlias() )) {
				categoryInfo.setCategoryAlias( categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName() );
			}
			if( categoryInfo.getCreateTime() == null ) {
				categoryInfo.setCreateTime(new Date());
			}
			if( StringUtils.isEmpty( categoryInfo.getDocumentType() ) ) {
				categoryInfo.setDocumentType( appInfo.getDocumentType() );
			}
			if( !"信息".equals(categoryInfo.getDocumentType()) && !"数据".equals( categoryInfo.getDocumentType() )) {
				categoryInfo.setDocumentType( "信息" );
			}

			emc.beginTransaction( CategoryInfo.class );
			emc.check( categoryInfo, CheckPersistType.all );
			emc.commit();
		}

		if( categoryExt == null ){
			categoryExt = new CategoryExt();
			categoryExt.setId(categoryInfo.getId());
			categoryExt.setContent(extContent);
			if( categoryExt.getCreateTime() == null ) {
				categoryExt.setCreateTime(new Date());
			}
			emc.beginTransaction( CategoryExt.class );
			emc.persist( categoryExt, CheckPersistType.all);
			emc.commit();
		}else{
			categoryExt.setContent(extContent);
			if( categoryExt.getCreateTime() == null ) {
				categoryExt.setCreateTime(new Date());
			}
			emc.beginTransaction( CategoryExt.class );
			emc.check( categoryExt, CheckPersistType.all );
			emc.commit();
		}

		if ( appInfo.getCategoryList() == null ){
			appInfo.setCategoryList( new ArrayList<String>());
		}
		if( !appInfo.getCategoryList().contains( categoryInfo.getId() ) ){
			appInfo.getCategoryList().add( categoryInfo.getId() );
		}
		emc.beginTransaction( AppInfo.class );
		emc.check( appInfo, CheckPersistType.all );
		emc.commit();
		return categoryInfo;
	}

//	public CategoryInfo save( EntityManagerContainer emc, CategoryInfo temp_categoryInfo, String extContent ) throws Exception {
//		CategoryInfo categoryInfo = null;
//		CategoryExt categoryExt = null;
//		AppInfo appInfo = null;
//		String oldCategoryName = null;
//		List<String> document_ids = null;
//		Business business = new Business(emc);
//		Long docCount = 0L;
//		Integer totalWhileCount = 0;
//		Integer currenteWhileCount = 0;
//		Integer queryMaxCount = 1000;
//
//		if( temp_categoryInfo.getId() == null ){
//			temp_categoryInfo.setId( CategoryInfo.createId() );
//		}
//		categoryInfo = emc.find( temp_categoryInfo.getId(), CategoryInfo.class );
//		categoryExt = emc.find( temp_categoryInfo.getId(), CategoryExt.class );
//		appInfo = emc.find( temp_categoryInfo.getAppId(), AppInfo.class );
//
//		if( appInfo == null ){
//			throw new Exception("应用栏目信息不存在！");
//		}
//
//		if( categoryInfo == null ){
//			categoryInfo = new CategoryInfo();
//			temp_categoryInfo.copyTo( categoryInfo );
//			categoryInfo.setAppName( appInfo.getAppName() );
//			categoryInfo.setCategoryAlias( categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName() );
//			if( StringUtils.isEmpty( categoryInfo.getDocumentType() ) ) {
//				categoryInfo.setDocumentType( appInfo.getDocumentType() );
//			}
//			if( !"信息".equals(categoryInfo.getDocumentType()) && !"数据".equals( categoryInfo.getDocumentType() )) {
//				categoryInfo.setDocumentType( "信息" );
//			}
//			if( categoryInfo.getCreateTime() == null ) {
//				categoryInfo.setCreateTime(new Date());
//			}
//			emc.beginTransaction( CategoryInfo.class );
//			emc.persist( categoryInfo, CheckPersistType.all);
//			emc.commit();
//		}else{
//			oldCategoryName = categoryInfo.getCategoryName();
//			temp_categoryInfo.copyTo( categoryInfo, JpaObject.FieldsUnmodify  );
//			categoryInfo.setAppName( appInfo.getAppName() );
//			categoryInfo.setCategoryAlias( categoryInfo.getAppName() + "-" + categoryInfo.getCategoryName() );
//
//			if( categoryInfo.getCreateTime() == null ) {
//				categoryInfo.setCreateTime(new Date());
//			}
//			if( StringUtils.isEmpty( categoryInfo.getDocumentType() ) ) {
//				categoryInfo.setDocumentType( appInfo.getDocumentType() );
//			}
//			if( !"信息".equals(categoryInfo.getDocumentType()) && !"数据".equals( categoryInfo.getDocumentType() )) {
//				categoryInfo.setDocumentType( "信息" );
//			}
//
//			//查询是否修改了名称，如果修改了名称，那么所有的文档相应的名称也都需要修改过来
//			if( !oldCategoryName.equals( categoryInfo.getCategoryName() )){
//				//对该目录下所有的文档的栏目名称和分类别名进行调整
//				docCount = business.getDocumentFactory().countByCategoryId( categoryInfo.getId() );
//				if( docCount > 0 ) {
//					totalWhileCount = (int) (docCount/queryMaxCount) + 1;
//					if( totalWhileCount > 0 ) {
//						while( docCount > 0 && currenteWhileCount <= totalWhileCount ) {
//							//查询1000个文档进行操作
//							document_ids = business.getDocumentFactory().listByCategoryId( categoryInfo.getId(), queryMaxCount );
//							changeDocumentInfoWithCategory( emc, document_ids, categoryInfo );
//							//当前循环次数+1
//							currenteWhileCount ++;
//							//重新查询剩余的文档数量
//							docCount = business.getDocumentFactory().countByCategoryId( categoryInfo.getId() );
//						}
//					}
//				}
//			}
//
//			emc.beginTransaction( CategoryInfo.class );
//			emc.check( categoryInfo, CheckPersistType.all );
//			emc.commit();
//		}
//
//		if( categoryExt == null ){
//			categoryExt = new CategoryExt();
//			categoryExt.setId(categoryInfo.getId());
//			categoryExt.setContent(extContent);
//			if( categoryExt.getCreateTime() == null ) {
//				categoryExt.setCreateTime(new Date());
//			}
//			emc.beginTransaction( CategoryExt.class );
//			emc.persist( categoryExt, CheckPersistType.all);
//			emc.commit();
//		}else{
//			categoryExt.setContent(extContent);
//			if( categoryExt.getCreateTime() == null ) {
//				categoryExt.setCreateTime(new Date());
//			}
//			emc.beginTransaction( CategoryExt.class );
//			emc.check( categoryExt, CheckPersistType.all );
//			emc.commit();
//		}
//
//		if ( appInfo.getCategoryList() == null ){
//			appInfo.setCategoryList( new ArrayList<String>());
//		}
//		if( !appInfo.getCategoryList().contains( categoryInfo.getId() ) ){
//			appInfo.getCategoryList().add( categoryInfo.getId() );
//		}
//		emc.beginTransaction( AppInfo.class );
//		emc.check( appInfo, CheckPersistType.all );
//		emc.commit();
//		return categoryInfo;
//	}

//	private void changeDocumentInfoWithCategory( EntityManagerContainer emc, List<String> document_ids, CategoryInfo categoryInfo ) throws Exception {
//		if( ListTools.isNotEmpty( document_ids ) ){
//			emc.beginTransaction( Document.class );
//			Document document = null;
//			for( String docId : document_ids ){
//				try {
//					document = emc.find( docId, Document.class );
//					document.setAppName( categoryInfo.getAppName() );
//					document.setCategoryAlias( categoryInfo.getCategoryAlias() );
//					if( document.getHasIndexPic() == null ){
//						document.setHasIndexPic( false );
//					}
//					emc.check( document, CheckPersistType.all );
//				}catch( Exception e ) {
//					e.printStackTrace();
//				}
//			}
//			emc.commit();
//		}
//	}

	public CategoryExt saveExtContent( EntityManagerContainer emc, String categoryId, String extContent ) throws Exception {
		CategoryExt categoryExt = null;

		categoryExt = emc.find( categoryId, CategoryExt.class );

		emc.beginTransaction( CategoryExt.class );

		if( categoryExt == null ){
			categoryExt = new CategoryExt();
			categoryExt.setId(categoryId);
			categoryExt.setContent(extContent);
			if( categoryExt.getCreateTime() == null ) {
				categoryExt.setCreateTime(new Date());
			}
			emc.persist( categoryExt, CheckPersistType.all);
		}else{
			categoryExt.setContent(extContent);
			if( categoryExt.getCreateTime() == null ) {
				categoryExt.setCreateTime(new Date());
			}
			emc.check( categoryExt, CheckPersistType.all );
		}
		emc.commit();
		return categoryExt;
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		List<String> ids = null;
		CategoryInfo categoryInfo = null;
		CategoryExt categoryExt = null;
		Document document = null;
		AppInfo appInfo = null;
		ViewCategory viewCategory = null;
		List<Item> dataItems = null;
		Long docCount = 0L;
		Integer totalWhileCount = 0;
		Integer currenteWhileCount = 0;
		Integer queryMaxCount = 1000;

		Business business = new Business( emc );
		emc.beginTransaction( AppInfo.class );
		emc.beginTransaction( CategoryInfo.class );
		emc.beginTransaction( CategoryExt.class );
		emc.beginTransaction( ViewCategory.class );
		emc.beginTransaction( Document.class );
		emc.beginTransaction( Item.class );

		categoryInfo = emc.find( id, CategoryInfo.class );
		categoryExt = emc.find( id, CategoryExt.class );

		ids = business.getViewCategoryFactory().listByCategoryId(id);
		if( ids != null && !ids.isEmpty()  ){
			for( String del_id : ids ){
				viewCategory = emc.find( del_id, ViewCategory.class );
				emc.remove( viewCategory );
			}
		}

		if( categoryInfo != null ){
			appInfo = emc.find( categoryInfo.getFormId(), AppInfo.class );
			if( appInfo != null ){
				if( appInfo.getCategoryList() != null ){
					appInfo.getCategoryList().remove( id );
				}
				emc.check( appInfo, CheckPersistType.all );
			}
		}

		//还有文档以及文档权限需要删除
		docCount = business.getDocumentFactory().countByCategoryId( id );
		if( docCount > 0 ) {
			totalWhileCount = (int) (docCount/queryMaxCount) + 1;
			if( totalWhileCount > 0 ) {
				while( docCount > 0 && currenteWhileCount <= totalWhileCount ) {
					//查询1000个文档进行操作
					ids = business.getDocumentFactory().listByCategoryId( id, queryMaxCount );
					if( ids != null && !ids.isEmpty()  ){
						for( String del_id : ids ){
							document = emc.find( del_id, Document.class );
							if( document != null ){
								try {
									//删除与该文档有关的所有数据信息
									dataItems = business.itemFactory().listWithDocmentWithPath( del_id );
									if ((!dataItems.isEmpty())) {
										emc.beginTransaction( Item.class );
										for ( Item o : dataItems ) {
											emc.remove(o);
										}
									}
								}catch( Exception e ) {
									e.printStackTrace();
								}

								//检查是否需要删除热点图片
								try {
									ThisApplication.queueDocumentDelete.send( document.getId() );
								} catch ( Exception e1 ) {
									e1.printStackTrace();
								}
								emc.beginTransaction(DocumentEvent.class);
								DocumentEvent documentEvent = DocumentEvent.deleteEventInstance(document);
								emc.persist(documentEvent);
								emc.remove( document );
							}
						}
					}
					//当前循环次数+1
					currenteWhileCount ++;
					//重新查询剩余的文档数量
					docCount = business.getDocumentFactory().countByCategoryId( id );
				}
			}
		}

		if( categoryExt != null ) {
			emc.remove( categoryExt, CheckRemoveType.all );
		}

		if( categoryInfo != null ) {
			emc.remove( categoryInfo, CheckRemoveType.all );
		}

		emc.commit();
	}

	public List<String> listByAlias(EntityManagerContainer emc, String cataggoryAlias) throws Exception {
		if( StringUtils.isEmpty(cataggoryAlias) ){
			return null;
		}
		Business business = new Business( emc );
		List<String> cataggoryAliases = new ArrayList<>();
		cataggoryAliases.add(cataggoryAlias);
		return business.getCategoryInfoFactory().listByAlias( cataggoryAliases );
	}

	public List<CategoryInfo> listByAliases(EntityManagerContainer emc, List<String> cataggoryAliases) throws Exception {
		if( ListTools.isEmpty( cataggoryAliases ) ){
			return null;
		}
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listByAliases( cataggoryAliases );
	}

	public String getExtContentWithId(EntityManagerContainer emc, String id) throws Exception {
		if( StringUtils.isEmpty(id ) ){
			return "{}";
		}
		Business business = new Business( emc );
		return business.categoryExtFactory().getContent(id);
	}

	/**
	 * 查询所有用户都可以发布的分类ID列表
	 * @param emc
	 * @param inAppInfoIds
	 * @param inCategoryIds
	 * @param excludCategoryIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllPeoplePublishableCategoryInfoIds( EntityManagerContainer emc, List<String> inAppInfoIds, List<String> inCategoryIds,
			List<String> excludCategoryIds, String documentType, Integer maxCount ) throws Exception {
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listAllPeoplePublishableCategoryInfoIds( inAppInfoIds, inCategoryIds, excludCategoryIds,
				documentType, maxCount);
	}

	/**
	 * 查询所有用户都可以查看的分类ID列表
	 * @param emc
	 * @param inAppInfoIds
	 * @param inCategoryIds
	 * @param excludCategoryIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllPeopleViewableCategoryInfoIds( EntityManagerContainer emc, List<String> inAppInfoIds, List<String> inCategoryIds,
			List<String> excludCategoryIds, String documentType, Integer maxCount ) throws Exception {
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listAllPeopleViewableCategoryInfoIds( inAppInfoIds, inCategoryIds, excludCategoryIds,
				documentType, maxCount );
	}

	/**
	 * 根据权限查询用户可以发布文档的分类ID列表(根据权限，不包含未配置发布权限的全员可发布的分类)
	 * @param emc
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param inCategoryIds
	 * @param excludCategoryIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listPublishableCategoryInfoIdsWithPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, List<String> inCategoryIds,
			List<String> excludCategoryIds, String documentType, Integer maxCount ) throws Exception {
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listPublishableCategoryInfoIdsWithPermission( personName, unitNames, groupNames,
				inAppInfoIds, inCategoryIds, excludCategoryIds,
				documentType, maxCount);
	}

	/**
	 * 根据权限查询用户可以访问文档的分类ID列表(根据权限，不包含未配置访问权限的全员可见的分类)
	 * @param emc
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @param inCategoryIds
	 * @param excludCategoryIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listViewableCategoryInfoIdsWithPermission(EntityManagerContainer emc, String personName,
			List<String> unitNames, List<String> groupNames, List<String> inAppInfoIds, List<String> inCategoryIds,
			List<String> excludCategoryIds, String documentType, Integer maxCount ) throws Exception {
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listViewableCategoryInfoIdsWithPermission( personName, unitNames, groupNames,
				inAppInfoIds, inCategoryIds, excludCategoryIds,
				documentType, maxCount );
	}

	/**
	 * 查询用户可以管理的分类列表
	 * @param emc
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param inAppInfoIds
	 * @return
	 * @throws Exception
	 */
	public List<String> listManageableCategoryIds(EntityManagerContainer emc, String personName, List<String> unitNames,
			List<String> groupNames, List<String> inAppInfoIds, String documentType, Integer maxCount ) throws Exception {
		if (StringUtils.isEmpty( personName )) {
			throw new Exception("personName is null!");
		}
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().listManageableCategoryIds( personName, unitNames, groupNames, inAppInfoIds,
				documentType, maxCount );
	}
}
