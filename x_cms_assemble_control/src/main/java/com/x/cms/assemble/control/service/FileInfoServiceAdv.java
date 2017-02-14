package com.x.cms.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.cms.core.entity.FileInfo;

public class FileInfoServiceAdv {

		private FileInfoService fileInfoService = new FileInfoService();

		public FileInfo get(String id) throws Exception {
			if( id == null || id.isEmpty() ){
				throw new Exception("id is null!");
			}
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				return fileInfoService.get( emc, id );
			} catch ( Exception e ) {
				throw e;
			}
		}
	
}
