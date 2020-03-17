package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.DocumentCommend;

/**
 * 文档点赞信息持久化管理的服务类
 * 
 * @author O2LEE
 */
public class DocCommendPersistService {
	
	private DocCommendService docCommendService = new DocCommendService();
	
	public DocumentCommend create( DocumentCommend documentCommend ) throws Exception {
		if( documentCommend == null ){
			throw new Exception("wrapIn document commend is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return docCommendService.create(emc, documentCommend );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public DocumentCommend create( String personName, String docId ) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			throw new Exception("docId can not empty!");
		}
		if( StringUtils.isEmpty( personName ) ){
			throw new Exception("personName can not empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			DocumentCommend documentCommend = new DocumentCommend();
			documentCommend.setCommendPerson( personName );
			documentCommend.setDocumentId( docId );
			return docCommendService.create(emc, documentCommend );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> delete( String docId, String personName ) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			throw new Exception("docId is empty!");
		}
		if( StringUtils.isEmpty( personName ) ){
			throw new Exception("personName is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			List<String> ids = docCommendService.listByDocAndPerson(emc, docId, personName, 99 );
			if( ListTools.isNotEmpty( ids ) ) {
				return docCommendService.delete(emc, ids );
			}else {
				return null;
			}
			
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public DocumentCommend delete( String id ) throws Exception {
		if( StringUtils.isEmpty( id ) ){
			throw new Exception("id is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return docCommendService.delete(emc, id);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
