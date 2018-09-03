package com.x.cms.assemble.search.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.cms.assemble.search.Business;
import com.x.cms.core.entity.FileInfo;

public class FileInfoServiceAdv {

		private FileInfoService fileInfoService = new FileInfoService();

		public List<FileInfo> getAttachmentList( String documentId ) throws Exception {
			if( documentId == null ){
				throw new Exception("documentId is null!");
			}

			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				Business business = new Business(emc);		
				List<String> ids = business.fileInfoFactory().listAttachmentByDocument( documentId );
				if( ids == null || ids.isEmpty() ){
					return null;
				}
				return fileInfoService.list( emc, ids );
			} catch ( Exception e ) {
				throw e;
			}
		}
}
