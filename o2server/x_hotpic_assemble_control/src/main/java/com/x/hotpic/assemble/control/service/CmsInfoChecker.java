package com.x.hotpic.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.cms.core.entity.Document;
import com.x.hotpic.assemble.control.service.inf.InfoCheckerInf;

public class CmsInfoChecker implements InfoCheckerInf {

	@Override
	public Boolean check( String infoId ) throws Exception {
		if( infoId  == null || infoId.isEmpty() ){
			throw new Exception( "cms infoId is null!" );
		}
		Document document = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			document = emc.find( infoId, Document.class );
			if( document != null ){
				return true;
			}
		}catch( Exception e ){
			throw e;
		}
		return false;
	}

}
