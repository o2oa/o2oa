package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.CmsBatchOperation;
import com.x.cms.core.entity.Document;

/**
 * 批处理操作执行
 */
public class CmsBatchOperationProcessService {
	
	public static String OPT_OBJ_DOCUMENT = "DOCUMENT";
	public static String OPT_OBJ_CATEGORY = "CATEGORY";
	public static String OPT_OBJ_APPINFO = "APPINFO";
	public static String OPT_TYPE_UPDATENAME = "UPDATENAME";
	public static String OPT_TYPE_DELETE = "DELETE";
	private static  Logger logger = LoggerFactory.getLogger( CmsBatchOperationProcessService.class );
	private DocumentInfoService documentInfoService = new DocumentInfoService();
	/**
	 * 批处理操作执行
	 * @param cmsBatchOperation
	 * @return
	 * @throws Exception 
	 */
	public String process( CmsBatchOperation cmsBatchOperation ) throws Exception {
		logger.info( "cms processing batch operation: " + cmsBatchOperation.toString() );
		//先把cmsBatchOperation状态修改为执行中
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			cmsBatchOperation = emc.find( cmsBatchOperation.getId(), CmsBatchOperation.class );
			cmsBatchOperation.setIsRunning( true );
			emc.beginTransaction( CmsBatchOperation.class );
			emc.check( cmsBatchOperation, CheckPersistType.all );
			logger.info( "cms change batch operation running......: " );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		
		if( "DOCUMENT".equalsIgnoreCase( cmsBatchOperation.getObjType() )) {
			//文档处理
		}else  if( "CATEGORY".equalsIgnoreCase( cmsBatchOperation.getObjType() )) {
			//分类处理：分类名称变更，分类删除
			if( "UPDATENAME".equalsIgnoreCase( cmsBatchOperation.getOptType() )) {
				if( StringUtils.isNotEmpty( cmsBatchOperation.getOldInfo() )) {
					//将categoryName是旧分类名称的所有文档的分类和栏目相关的信息更新掉，最后删除当前的批处理信息
					changeCategoryNameInDocument( cmsBatchOperation.getId(), cmsBatchOperation.getBundle(), cmsBatchOperation.getOldInfo() );
				}
			}else if( "DELETE".equalsIgnoreCase( cmsBatchOperation.getOptType()  )) {
				//删除文档，并且修改分类所属的栏目中的分类数量和文档数量
				deleteDocumentInCategory( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );
			}
		}else  if( "APPINFO".equalsIgnoreCase( cmsBatchOperation.getObjType() )) {
			//栏目处理：栏目名称变更，栏目删除
			if( "UPDATENAME".equalsIgnoreCase( cmsBatchOperation.getOptType() )) {
				if( StringUtils.isNotEmpty( cmsBatchOperation.getOldInfo() )) {
					//将AppName是旧栏目名称的所有分类以及文档的栏目相关的信息更新掉，最后删除当前的批处理信息
					changeAppNameInCategory( cmsBatchOperation.getId(), cmsBatchOperation.getBundle(), cmsBatchOperation.getOldInfo() );
				}
			}else if( "DELETE".equalsIgnoreCase( cmsBatchOperation.getOptType()  )) {
				//删除分类以及文档，并且修改分类所属的栏目中的分类数量和文档数量
				deleteDocumentInApp( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );
			}
		}
		logger.info( "cms batch operation process completed." );
		return "error";
	}

	/**
	 * 将栏目下所有的文档删除，最后删除当前的批处理信息
	 * @param id
	 * @param bundle
	 * @throws Exception 
	 */
	private void deleteDocumentInApp(String id, String bundle) throws Exception {
		Long docCount = 0L;
		Integer totalWhileCount = 0;
		Integer currenteWhileCount = 0;
		Integer queryMaxCount = 1000;
		List<String> document_ids = null;
		List<String> categoryIds = null;
		CmsBatchOperation cmsBatchOperation = null;
		CategoryInfo categoryInfo = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			categoryIds = business.getCategoryInfoFactory().listByAppId( bundle );
			
			if( ListTools.isNotEmpty( categoryIds )) {
				for( String categoryId : categoryIds ) {
					categoryInfo = emc.find( categoryId, CategoryInfo.class );
					if( categoryInfo != null ) {
						emc.beginTransaction( CategoryInfo.class );
						emc.remove( categoryInfo, CheckRemoveType.all );
						logger.info( "cms processing batch operation: remove category: " + categoryInfo.getId() + ", alias: " + categoryInfo.getCategoryAlias() );
						emc.commit();
					}
					
					//将该目录下所有的文档删除
					docCount = business.getDocumentFactory().countByCategoryId( categoryId );
					if( docCount > 0 ) {
						totalWhileCount = (int) (docCount/queryMaxCount) + 1;
						if( totalWhileCount > 0 ) {
							while( docCount > 0 && currenteWhileCount <= totalWhileCount ) {//查询1000个文档进行操作						
								document_ids = business.getDocumentFactory().listByCategoryId( bundle, queryMaxCount );														
								for( String docId : document_ids ){									
									try {
										documentInfoService.delete( emc, docId );
										logger.info( "cms processing batch operation: remove document("+ currenteWhileCount +"/" + totalWhileCount + "): " + docId );
									}catch( Exception e ) {
										e.printStackTrace();
									}
								}
								//当前循环次数+1
								currenteWhileCount ++;
								//重新查询剩余的文档数量
								docCount = business.getDocumentFactory().countByCategoryId( categoryInfo.getId() );
							}
						}
					}
				}
			}
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		
		ApplicationCache.notify( Document.class );
		ApplicationCache.notify( CategoryInfo.class );
	}

	/**
	 * 将AppName是旧栏目名称的所有分类以及文档的栏目相关的信息更新掉，最后删除当前的批处理信息
	 * @param id
	 * @param bundle
	 * @param oldInfo
	 * @throws Exception 
	 */
	private void changeAppNameInCategory( String id, String bundle, String oldName ) throws Exception {
		Long docCount = 0L;
		Integer totalWhileCount = 0;
		Integer currenteWhileCount = 0;
		Integer queryMaxCount = 1000;
		List<String> document_ids = null;
		List<String> categoryIds = null;
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		CmsBatchOperation cmsBatchOperation = null;
		
		//先查询该栏目下的所有分类信息列表
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			appInfo = emc.find( bundle, AppInfo.class );
			categoryIds = business.getCategoryInfoFactory().listByAppId( bundle );
			if( appInfo == null ) {
				throw new Exception("appinfo not exists.ID: " + bundle );
			}
			if( ListTools.isNotEmpty( categoryIds )) {
				for( String categoryId : categoryIds ) {
					categoryInfo = emc.find( categoryId, CategoryInfo.class );
					if( categoryInfo != null ) {
						categoryInfo.setAppName( appInfo.getAppName() );
						if( StringUtils.isEmpty( categoryInfo.getCategoryAlias() )) {
							categoryInfo.setCategoryAlias( appInfo.getAppName() + "-" + categoryInfo.getCategoryName() );
						}						
						emc.beginTransaction( CategoryInfo.class );
						emc.check( categoryInfo, CheckPersistType.all );
						logger.info( "cms processing batch operation: change category: " + categoryInfo.getId() + ", app_name: " + oldName + " -> " + categoryInfo.getAppName() );
						emc.commit();
					}
					//再处理该分类下所有的文档信息
					docCount = business.getDocumentFactory().countByCategoryId( categoryInfo.getId() );
					if( docCount > 0 ) {
						totalWhileCount = (int) (docCount/queryMaxCount) + 1;
						if( totalWhileCount > 0 ) {
							while( docCount > 0 && currenteWhileCount <= totalWhileCount ) {
								//查询1000个文档进行操作
								document_ids = business.getDocumentFactory().listByCategoryIdAndNotEqualAppName( categoryInfo.getId(), appInfo.getAppName(), queryMaxCount );							
								changeDocumentInfoWithCategory( emc, document_ids, categoryInfo );
								logger.info( "cms processing batch operation: update app and category info for document, batch("+ currenteWhileCount +"/" + totalWhileCount + ") " );
								//当前循环次数+1
								currenteWhileCount ++;
								//重新查询剩余未修改栏目名称的文档数量
								docCount = business.getDocumentFactory().countByCategoryIdAndNotEqualAppName( categoryInfo.getId(), appInfo.getAppName() );
							}
						}
					}
				}
			}
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		}
		ApplicationCache.notify( Document.class );
		ApplicationCache.notify( CategoryInfo.class );
	}

	/**
	 * 将分类下所有的文档删除，最后删除当前的批处理信息
	 * @param id
	 * @param bundle
	 * @throws Exception 
	 */
	private void deleteDocumentInCategory( String id, String bundle ) throws Exception {
		Long docCount = 0L;
		Integer totalWhileCount = 0;
		Integer currenteWhileCount = 0;
		Integer queryMaxCount = 1000;
		List<String> document_ids = null;
		CmsBatchOperation cmsBatchOperation = null;
		CategoryInfo categoryInfo = null;
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			categoryInfo = emc.find( bundle, CategoryInfo.class );
			
			if( categoryInfo != null ) {
				emc.beginTransaction( CategoryInfo.class );
				emc.remove( categoryInfo, CheckRemoveType.all );
				logger.info( "cms processing batch operation: remove category, id:"+ bundle );
				emc.commit();
			}
			
			//将该目录下所有的文档删除
			docCount = business.getDocumentFactory().countByCategoryId( bundle );
			if( docCount > 0 ) {
				totalWhileCount = (int) (docCount/queryMaxCount) + 1;
				if( totalWhileCount > 0 ) {
					while( docCount > 0 && currenteWhileCount <= totalWhileCount ) {//查询1000个文档进行操作						
						document_ids = business.getDocumentFactory().listByCategoryId( bundle, queryMaxCount );
						for( String docId : document_ids ){
							try {
								documentInfoService.delete( emc, docId );
								logger.info( "cms processing batch operation: remove document("+ currenteWhileCount +"/" + totalWhileCount + "): " + docId );
							}catch( Exception e ) {
								e.printStackTrace();
							}
						}
						//当前循环次数+1
						currenteWhileCount ++;
						//重新查询剩余的未修改分类名称的文档数量
						docCount = business.getDocumentFactory().countByCategoryId( categoryInfo.getId() );
					}
				}
			}			
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		ApplicationCache.notify( CategoryInfo.class );
		ApplicationCache.notify( Document.class );
	}

	/**
	 * 将categoryName是旧分类名称的所有文档的分类和栏目相关的信息更新掉，最后删除当前的批处理信息
	 * @param id 批处理信息ID
	 * @param bundle  绑定的分类信息ID
	 * @param oldName 分类使用的旧名称
	 * @throws Exception 
	 */
	private void changeCategoryNameInDocument( String id, String bundle, String oldName ) throws Exception {
		Long docCount = 0L;
		Integer totalWhileCount = 0;
		Integer currenteWhileCount = 0;
		Integer queryMaxCount = 1000;
		List<String> document_ids = null;
		CmsBatchOperation cmsBatchOperation = null;
		CategoryInfo categoryInfo = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			categoryInfo = emc.find( bundle, CategoryInfo.class );
			if( categoryInfo != null ) {
				//对该目录下所有的文档的栏目名称和分类别名进行调整
				docCount = business.getDocumentFactory().countByCategoryId( bundle );
				if( docCount > 0 ) {
					totalWhileCount = (int) (docCount/queryMaxCount) + 1;
					if( totalWhileCount > 0 ) {
						while( docCount > 0 && currenteWhileCount <= totalWhileCount ) {
							//查询1000个文档进行操作
							document_ids = business.getDocumentFactory().listByCategoryIdAndCategoryName(bundle, categoryInfo.getCategoryName(), queryMaxCount );							
							changeDocumentInfoWithCategory( emc, document_ids, categoryInfo );			
							logger.info( "cms processing batch operation: update app and category info for document, batch("+ currenteWhileCount +"/" + totalWhileCount + ") " );
							//当前循环次数+1
							currenteWhileCount ++;
							//重新查询剩余的文档数量
							docCount = business.getDocumentFactory().countByCategoryIdAndNotEqualsCategoryName( categoryInfo.getId(), categoryInfo.getCategoryName() );
						}
					}
				}
			}
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		ApplicationCache.notify( Document.class );
		ApplicationCache.notify( CategoryInfo.class );
	}

	/**
	 * 更新指定文档的栏目和分类信息内容
	 * @param emc
	 * @param document_ids
	 * @param categoryInfo
	 * @throws Exception
	 */
	private void changeDocumentInfoWithCategory( EntityManagerContainer emc, List<String> document_ids, CategoryInfo categoryInfo ) throws Exception {
		if( ListTools.isNotEmpty( document_ids ) ){
			emc.beginTransaction( Document.class );
			Document document = null;
			for( String docId : document_ids ){
				try {
					document = emc.find( docId, Document.class );
					document.setAppId( categoryInfo.getAppId() );
					document.setAppName( categoryInfo.getAppName() );
					document.setCategoryAlias( categoryInfo.getCategoryAlias() );
					document.setCategoryName( categoryInfo.getCategoryName() );
					if( document.getHasIndexPic() == null ){
						document.setHasIndexPic( false );
					}
					emc.check( document, CheckPersistType.all );
					logger.info( "cms processing batch operation: change app and category info for document: " + docId );
				}catch( Exception e ) {
					e.printStackTrace();
				}
			}
			emc.commit();
		}
	}
}
