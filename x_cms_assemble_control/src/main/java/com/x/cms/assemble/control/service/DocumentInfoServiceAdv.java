package com.x.cms.assemble.control.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.content.tools.DataHelper;


public class DocumentInfoServiceAdv {
	
	private Logger logger = LoggerFactory.getLogger( DocumentInfoServiceAdv.class );
	private LogService logService = new LogService();
	private UserManagerService userManagerService = new UserManagerService();
	private DocumentInfoService documentInfoService = new DocumentInfoService();
	private FileInfoService fileInfoService = new FileInfoService();
	
	public List<Document> listByCatagoryId( String catagoryId ) throws Exception {
		if( catagoryId == null || catagoryId.isEmpty() ){
			throw new Exception("catagoryId is null!");
		}
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			ids = documentInfoService.listByCatagoryId( emc, catagoryId );
			return documentInfoService.list( emc, ids );
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

	public Document view( String id, EffectivePerson currentPerson ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null!");
		}
		Document document = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			document = documentInfoService.get( emc, id );
			logService.log( emc, currentPerson.getName(), "用户["+currentPerson.getName()+"]访问了文档", document.getAppId(), document.getCatagoryId(), document.getId(), "", "DOCUMENT", "访问" );
			return document;
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Map<?, ?> getDocumentData( Document document ) throws Exception {
		if( document == null ){
			throw new Exception("document is null!");
		}
		DataHelper dataHelper = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			dataHelper = new DataHelper( emc, document.getAppId(), document.getCatagoryId(), document.getId() );
			return dataHelper.get();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<FileInfo> getAttachmentList(Document document) throws Exception {
		if( document == null ){
			throw new Exception("document is null!");
		}
		if( document.getAttachmentList() == null || document.getAttachmentList().isEmpty() ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return fileInfoService.list( emc, document.getAttachmentList() );
		} catch ( Exception e ) {
			throw e;
		}
	}
	

}
