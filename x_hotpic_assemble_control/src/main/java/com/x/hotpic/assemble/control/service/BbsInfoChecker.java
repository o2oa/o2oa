package com.x.hotpic.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.hotpic.assemble.control.service.inf.InfoCheckerInf;

public class BbsInfoChecker implements InfoCheckerInf {

	@Override
	public Boolean check( String infoId ) throws Exception {
		if( infoId  == null || infoId.isEmpty() ){
			throw new Exception( "bbs infoId is null!" );
		}
		BBSSubjectInfo subjectInfo = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			subjectInfo = emc.find( infoId, BBSSubjectInfo.class );
			if( subjectInfo != null ){
				return true;
			}
		}catch( Exception e ){
			throw e;
		}
		return false;
	}

}
