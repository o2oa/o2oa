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
import com.x.cms.core.entity.Review;

/**
 * 批处理操作执行
 */
public class CmsBatchOperationProcessService {
	
	public static String OPT_OBJ_DOCUMENT = "DOCUMENT";
	public static String OPT_OBJ_CATEGORY = "CATEGORY";
	public static String OPT_OBJ_APPINFO = "APPINFO";
	public static String OPT_TYPE_PERMISSION = "PERMISSION";
	public static String OPT_TYPE_UPDATENAME = "UPDATENAME";
	public static String OPT_TYPE_DELETE = "DELETE";
	
	private static  Logger logger = LoggerFactory.getLogger( CmsBatchOperationProcessService.class );
	private DocumentInfoService documentInfoService = new DocumentInfoService();
	private ReviewService reviewService = new ReviewService();
	/**
	 * 批处理操作执行
	 * @param cmsBatchOperation
	 * @return
	 * @throws Exception 
	 */
	public String process( CmsBatchOperation cmsBatchOperation ) throws Exception {
		logger.info( "process -> Cms processing batch operation: " + cmsBatchOperation.toString() );
		//先把cmsBatchOperation状态修改为执行中
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			cmsBatchOperation = emc.find( cmsBatchOperation.getId(), CmsBatchOperation.class );
			if( cmsBatchOperation != null ) {
				cmsBatchOperation.setIsRunning( true );
				emc.beginTransaction( CmsBatchOperation.class );
				emc.check( cmsBatchOperation, CheckPersistType.all );
				emc.commit();
				logger.info( "process -> cms change batch operation running......: " );
			}			
		} catch (Exception e) {
			throw e;
		}
		
		if( "DOCUMENT".equalsIgnoreCase( cmsBatchOperation.getObjType() )) {
			if( "PERMISSION".equalsIgnoreCase( cmsBatchOperation.getOptType() )) {//文档处理
				//将categoryName是旧分类名称的所有文档的分类和栏目相关的信息更新掉，最后删除当前的批处理信息
				refreshDocumentReview( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );
			}else if( "DELETE".equalsIgnoreCase( cmsBatchOperation.getOptType()  )) {
				//删除文档，并且修改分类所属的栏目中的分类数量和文档数量
				deleteDocumentReview( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );
			}
		}else  if( "CATEGORY".equalsIgnoreCase( cmsBatchOperation.getObjType() )) {
			//分类处理：分类名称变更，分类删除
			if( "UPDATENAME".equalsIgnoreCase( cmsBatchOperation.getOptType() )) {
				if( StringUtils.isNotEmpty( cmsBatchOperation.getOldInfo() )) {
					//将categoryName是旧分类名称的所有文档的分类和栏目相关的信息更新掉，最后删除当前的批处理信息
					changeCategoryNameInDocument( cmsBatchOperation.getId(), cmsBatchOperation.getBundle(), cmsBatchOperation.getOldInfo() );
				}
			}else if( "PERMISSION".equalsIgnoreCase( cmsBatchOperation.getOptType()  )) {
				//分类修改权限
				refreshDocumentReviewInCagetory( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );				
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
			}else if( "PERMISSION".equalsIgnoreCase( cmsBatchOperation.getOptType()  )) {
				//栏目修改权限
				refreshDocumentReviewInAppInfo( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );		
			}else if( "DELETE".equalsIgnoreCase( cmsBatchOperation.getOptType()  )) {
				//删除分类以及文档，并且修改分类所属的栏目中的分类数量和文档数量
				deleteDocumentInApp( cmsBatchOperation.getId(), cmsBatchOperation.getBundle() );
			}
		}
		logger.info( "cms batch operation process completed." );
		return "error";
	}

	private void refreshDocumentReviewInAppInfo(String id, String appId) throws Exception {
		CmsBatchOperation cmsBatchOperation = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			//查询分类中所有的文档，重发为文档Review更新， 增加删除栏目批量操作（对分类和文档）的信息
			List<String> ids = null;
			List<Document> documentList = null;
			Integer maxQueryCount = 1000;
			Long count = business.getDocumentFactory().countByAppId(appId);
			Long maxTimes = count/maxQueryCount + 2;
			AppInfo appInfo = emc.find(appId, AppInfo.class );
			logger.info( "refreshDocumentReviewInAppInfo -> There are '" + count +"' document need refresh review, need to process '" + maxTimes + "' times ......" );
			for( int i=0; i<=maxTimes; i++ ) {
				ids = business.getDocumentFactory().listReviewedIdsByAppId( appId, maxQueryCount );
				logger.info( "refreshDocumentReviewInAppInfo -> This is the '" + ( i + 1 ) +"' times process......" );
				if(ListTools.isNotEmpty( ids )) {
					documentList = emc.list( Document.class, ids);
				}
				if( ListTools.isNotEmpty( documentList )) {
					CmsBatchOperationPersistService cmsBatchOperationPersistService = new CmsBatchOperationPersistService();
					for( Document document : documentList ) {
						emc.beginTransaction(Document.class );
						document.setReviewed( false );
						document.setAppName( appInfo.getAppName() );
						document.setAppAlias( appInfo.getAppAlias() );
						if( StringUtils.isEmpty( document.getAppAlias() )) {
							document.setAppAlias( appInfo.getAppName() );
						}
						emc.check( document, CheckPersistType.all );
						emc.commit();						
						cmsBatchOperationPersistService.addOperation( 
								CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT, 
								CmsBatchOperationProcessService.OPT_TYPE_PERMISSION, document.getId(), document.getId(), "栏目权限变更引起文档Review变更：ID=" + document.getId() );
					}
					
				}
				ids = null;
				documentList = null;
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
	}

	private void refreshDocumentReviewInCagetory(String id, String categoryId) throws Exception {
		CmsBatchOperation cmsBatchOperation = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			//查询分类中所有的文档，重发为文档Review更新， 增加删除栏目批量操作（对分类和文档）的信息
			List<String> ids = null;
			List<Document> documentList = null;
			Integer maxQueryCount = 1000;
			Long count = business.getDocumentFactory().countByCategoryId(categoryId);
			Long maxTimes = count/maxQueryCount + 2;
			logger.info( "refreshDocumentReviewInCagetory -> There are : " + count + " documents need to refresh review, maxTimes=" + maxTimes  );
			
			for( int i=0; i<=maxTimes; i++ ) {
				ids = business.getDocumentFactory().listReviewedIdsByCategoryId(categoryId, maxQueryCount );
				if(ListTools.isNotEmpty( ids )) {
					documentList = emc.list( Document.class, ids);
				}
				if( ListTools.isNotEmpty( documentList )) {
					CmsBatchOperationPersistService cmsBatchOperationPersistService = new CmsBatchOperationPersistService();
					for( Document document : documentList ) {
						emc.beginTransaction(Document.class );
						document.setReviewed( false );
						emc.check( document, CheckPersistType.all );
						emc.commit();
						
						logger.info( "refreshDocumentReviewInCagetory -> Send docment permission operation to queue[queueBatchOperation], document:" + document.getTitle()  );
						cmsBatchOperationPersistService.addOperation( 
								CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT, 
								CmsBatchOperationProcessService.OPT_TYPE_PERMISSION, document.getId(), document.getId(), "分类权限变更引起文档Review变更：ID=" + document.getId() );
					}
					
				}
				ids = null;
				documentList = null;
			}
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "refreshDocumentReviewInCagetory -> cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private void deleteDocumentReview(String id, String docId) throws Exception {
		CmsBatchOperation cmsBatchOperation = null;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			logger.info( "deleteDocumentReview -> delete all reviews for document: " + docId );
			reviewService.deleteDocumentReview( emc, docId );
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "deleteDocumentReview -> cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		ApplicationCache.notify( Review.class );
		ApplicationCache.notify( Document.class );
	}

	/**
	 * 根据数据库中的文档的信息，重新计算文档的Review信息
	 * 全部删除，然后再重新插入Review记录
	 * @param id
	 * @param docId
	 * @throws Exception 
	 */
	private void refreshDocumentReview( String id, String docId ) throws Exception {
		CmsBatchOperation cmsBatchOperation = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			cmsBatchOperation = emc.find( id, CmsBatchOperation.class );
			
			reviewService.refreshDocumentReview( emc, docId );
			
			Document document = emc.find( docId, Document.class );
			if( document != null ) {
				emc.beginTransaction( Document.class );
				document.setReviewed( true );
				
				if( StringUtils.isEmpty( document.getAppAlias()  )) {
					document.setAppAlias( document.getAppName() );
				}
				
				document.setSequenceAppAlias( document.getAppAlias() + document.getId() );
				document.setSequenceCategoryAlias( document.getCategoryAlias() + document.getId() );
				if( StringUtils.isNotEmpty( document.getTitle() ) && document.getTitle().length() > 30 ) {
					document.setSequenceTitle( document.getTitle().substring(0, 30) + document.getId() );
				}else {
					document.setSequenceTitle( document.getTitle() + document.getId() );
				}
				if( StringUtils.isNotEmpty( document.getCreatorPerson() ) && document.getCreatorPerson().length() > 50 ) {
					document.setSequenceCreatorPerson( document.getCreatorPerson().substring(0, 50) + document.getId() );
				}else {
					document.setSequenceCreatorPerson( document.getCreatorPerson() + document.getId() );
				}
				if( StringUtils.isNotEmpty( document.getCreatorUnitName() ) && document.getCreatorUnitName().length() > 50 ) {
					document.setSequenceCreatorUnitName( document.getCreatorUnitName().substring(0, 50) + document.getId() );
				}else {
					document.setSequenceCreatorUnitName( document.getCreatorUnitName() + document.getId() );
				}
				
				emc.check( document, CheckPersistType.all );
				emc.commit();
			}
			
			if( cmsBatchOperation != null ) {
				emc.beginTransaction( CmsBatchOperation.class );
				emc.remove( cmsBatchOperation, CheckRemoveType.all );
				logger.info( "refreshDocumentReview -> cms delete batch operation: " + cmsBatchOperation.toString()  );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
		ApplicationCache.notify( Review.class );
		ApplicationCache.notify( Document.class );
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
						logger.info( "deleteDocumentInApp -> Cms processing batch operation: remove category: " + categoryInfo.getId() + ", alias: " + categoryInfo.getCategoryAlias() );
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
										new CmsBatchOperationPersistService().addOperation( 
												CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT, 
												CmsBatchOperationProcessService.OPT_TYPE_DELETE, id, id, "栏目删除引起文档删除：ID=" + id );
										logger.info( "deleteDocumentInApp -> cms processing batch operation: remove document("+ currenteWhileCount +"/" + totalWhileCount + "): " + docId );
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
				logger.info( "deleteDocumentInApp -> cms delete batch operation: " + cmsBatchOperation.toString()  );
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
						logger.info( "changeAppNameInCategory -> cms processing batch operation: change category: " + categoryInfo.getId() + ", app_name: " + oldName + " -> " + categoryInfo.getAppName() );
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
								logger.info( "changeAppNameInCategory -> cms processing batch operation: update app and category info for document, batch("+ currenteWhileCount +"/" + totalWhileCount + ") " );
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
				logger.info( "changeAppNameInCategory -> cms delete batch operation: " + cmsBatchOperation.toString()  );
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
				logger.info( "deleteDocumentInCategory -> cms processing batch operation: remove category, id:"+ bundle );
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
								new CmsBatchOperationPersistService().addOperation( 
										CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT, 
										CmsBatchOperationProcessService.OPT_TYPE_DELETE, id, id, "分类删除引起文档删除：ID=" + id );
								logger.info( "deleteDocumentInCategory -> cms processing batch operation: remove document("+ currenteWhileCount +"/" + totalWhileCount + "): " + docId );
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
				logger.info( "deleteDocumentInCategory -> cms delete batch operation: " + cmsBatchOperation.toString()  );
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
							logger.info( "changeCategoryNameInDocument -> cms processing batch operation: update app and category info for document, batch("+ currenteWhileCount +"/" + totalWhileCount + ") " );
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
				logger.info( "changeCategoryNameInDocument -> cms delete batch operation: " + cmsBatchOperation.toString()  );
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
					logger.info( "changeDocumentInfoWithCategory -> cms processing batch operation: change app and category info for document: " + docId );
				}catch( Exception e ) {
					e.printStackTrace();
				}
			}
			emc.commit();
		}
	}
}
