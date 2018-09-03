package com.x.cms.assemble.search.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.cms.core.entity.Document;

/**
 * 对文档信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class DocumentInfoServiceAdv {
	
	private DocumentInfoService documentInfoService = new DocumentInfoService();
	
	public List<Document> listByCategoryId( String categoryId, String status ) throws Exception {
		if( categoryId == null || categoryId.isEmpty() ){
			throw new Exception("categoryId is null!");
		}
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			ids = documentInfoService.listByCategoryId( emc, categoryId, status );
			return documentInfoService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listIdsByCategoryId( String categoryId, String status ) throws Exception {
		if( categoryId == null || categoryId.isEmpty() ){
			throw new Exception("categoryId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.listByCategoryId( emc, categoryId, status );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Document get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentInfoService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
}
