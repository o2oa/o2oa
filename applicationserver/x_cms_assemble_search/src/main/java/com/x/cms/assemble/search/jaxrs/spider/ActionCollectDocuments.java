package com.x.cms.assemble.search.jaxrs.spider;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.search.bean.WrapAppInfo;
import com.x.cms.assemble.search.bean.WrapCategoryInfo;
import com.x.cms.assemble.search.bean.WrapDocument;
import com.x.cms.assemble.search.es.ElasticSearchJestClient;
import com.x.cms.assemble.search.service.DocumentSearchService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;

import io.searchbox.client.JestClient;

public class ActionCollectDocuments extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionCollectDocuments.class );
	
	protected ActionResult<WoId> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WoId> result = new ActionResult<>();
		WoId wrap = null;
		
		//先查询所有的栏目
		List<String> appInfo_ids = appInfoServiceAdv.listAllIds("信息");
		List<String> categoryInfo_ids_for_appInfo = null;
		List<String> document_ids = null;
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		WrapAppInfo wrapAppInfo = null;
		WrapCategoryInfo wrapCategoryInfo = null;
		WrapDocument wrapDocument = null;
		JestClient client = ElasticSearchJestClient.getClient();  
		DocumentSearchService documentDao = new DocumentSearchService(client);  
		
		if( ListTools.isNotEmpty( appInfo_ids )) {
			for( String appInfoId : appInfo_ids ) {
				appInfo = appInfoServiceAdv.get( appInfoId );
				if( appInfo != null ) {
					wrapAppInfo = new WrapAppInfo();
					appInfo.copyTo( wrapAppInfo );
				}
				categoryInfo_ids_for_appInfo = categoryInfoServiceAdv.listIdsByAppId(appInfoId);
				if( ListTools.isNotEmpty( categoryInfo_ids_for_appInfo )) {
					for( String categoryId : categoryInfo_ids_for_appInfo ) {
						categoryInfo = categoryInfoServiceAdv.get( categoryId );
						if( categoryInfo != null ) {
							wrapCategoryInfo = new WrapCategoryInfo();
							categoryInfo.copyTo( wrapCategoryInfo );
						}
						document_ids = documentInfoServiceAdv.listIdsByCategoryId( categoryId, "published");
						if( ListTools.isNotEmpty( document_ids )) {
							for( String docId : document_ids ) {
								document = documentInfoServiceAdv.get( docId );
								if( document != null ) {
									System.out.println(">>>>>>>>>>>>>>>>>>>>>>>正在尝试收集文件到搜索服务器：" + document.getTitle() );  
									wrapDocument = new WrapDocument();
									document.copyTo( wrapDocument );
									wrapDocument.setAppInfo(wrapAppInfo);
									wrapDocument.setCategoryInfo(wrapCategoryInfo);
									try {
										documentDao.insert( wrapDocument );  
									    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>文档收集成功！标题：" + document.getTitle() );  
									}catch( Exception e ) {
										System.out.println(">>>>>>>>>>>>>>>>>>>>>>>文档收集失败！标题：" + document.getTitle() );  
										logger.error( e, effectivePerson, request, null);
									}
								}
							}
						}
					}
				}
			}
		}
		result.setData(wrap);
		return result;
	}
}