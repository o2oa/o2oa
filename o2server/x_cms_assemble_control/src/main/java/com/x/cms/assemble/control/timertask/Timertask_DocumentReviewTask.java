package com.x.cms.assemble.control.timertask;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.control.service.PermissionOperateService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

/**
 * 定时代理:定期进行对栏目，分类修改的分析，对文档Review的更新
 * 
 * @author O2LEE
 *
 */
public class Timertask_DocumentReviewTask implements Job {

	private static Logger logger = LoggerFactory.getLogger(Timertask_DocumentReviewTask.class);
	private static AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	private static CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	private static DocumentInfoServiceAdv documentInfoServiceAdv = new DocumentInfoServiceAdv();
	private static PermissionOperateService permissionOperateService = new PermissionOperateService();
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {		
		logger.info("CMS Timertask_DocumentReviewTask excute begin......");
		try {
			//查询所有reviewed=false的栏目，将所有栏目涉及到的所有CategoryInfo打上reviewed=false, 然后栏目reviewed=true
			List<String> documentIds = null;
			List<CategoryInfo> categoryList = null;
			List<AppInfo> appInfoList = appInfoServiceAdv.listInReviewAppInfoList();
			if( ListTools.isNotEmpty( appInfoList )) {
				for( AppInfo appInfo : appInfoList ) {
					//查询所有的Category, Category不会很多，一次性查完
					categoryList = categoryInfoServiceAdv.listByAppId( appInfo.getId() );
					if( ListTools.isNotEmpty( categoryList )) {
						for( CategoryInfo categoryInfo : categoryList ) {
							try {
								categoryInfoServiceAdv.inReview( categoryInfo.getId() );
							}catch( Exception e ) {
								e.printStackTrace( );
							}
						}
					}
					appInfoServiceAdv.reviewed( appInfo.getId() );
				}
			}
			
			//查询所有reviewed=false的分类信息，分析所有的文档，给文档打上reviewed=false，然后分类reviewed=true
			categoryList = categoryInfoServiceAdv.listInReviewCategoryInfoList();
			if( ListTools.isNotEmpty( categoryList )) {
				for( CategoryInfo categoryInfo : categoryList ) {
					//logger.info("CMS Timertask_DocumentReviewTask flag document to inreview, category:" + categoryInfo.getId() );
					//查询所有的文档，把所有的文档都标识为reviewed =false
					documentIds = documentInfoServiceAdv.listReviewedIdsByCategoryId( categoryInfo.getId(), 1000 );
					do{
						if( ListTools.isNotEmpty( documentIds )) {
							for( String documentId : documentIds ) {
								//logger.info("CMS Timertask_DocumentReviewTask flag document to inreview, document:" + documentId );
								documentInfoServiceAdv.inReview( documentId );
							}
						}
						documentIds = documentInfoServiceAdv.listReviewedIdsByCategoryId( categoryInfo.getId(), 1000 );
					}while( ListTools.isNotEmpty( documentIds ) );
					categoryInfoServiceAdv.reviewed( categoryInfo.getId() );
				}
			}
			
			//查询1000个reviewed=false的文档，更新文档Review
			documentIds = documentInfoServiceAdv.listInReviewIds( 1000 );
			int maxTimes = 5; //查询执行3次
			do {
				maxTimes--;
				if( ListTools.isNotEmpty( documentIds ) ) {
					for( String documentId : documentIds ) {
						//logger.info("CMS Timertask_DocumentReviewTask refreshReview document:" + documentId );
						permissionOperateService.refreshReview(documentId);
					}
				}
				documentIds = documentInfoServiceAdv.listInReviewIds( 1000 );
			}while( maxTimes > 0 && ListTools.isNotEmpty( documentIds ) );
			logger.info("CMS Timertask_DocumentReviewTask excute completed!");
		} catch (Exception e) {
			logger.warn("Timertask_DocumentReviewTask operation logs excute got an exception.");
			logger.error(e);
		}
	}

}