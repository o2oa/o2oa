package com.x.bbs.assemble.control.factory;

import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSSubjectVoteResult;

/**
 * 类   名：BBSSubjectVoteResultFactory<br/>
 * 实体类：BBSSubjectVoteResult<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:26
**/
public class BBSSubjectVoteResultFactory extends AbstractFactory {

	public BBSSubjectVoteResultFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "获取指定Id的BBSSubjectVoteResult实体信息对象" )
	public BBSSubjectVoteResult get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSSubjectVoteResult.class );
	}
	
	//@MethodDescribe( "获取指定Id的BBSSubjectVoteResult实体信息对象" )
	public String getStatisticResult( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		BBSSubjectVoteResult subjectVoteResult = this.entityManagerContainer().find( id, BBSSubjectVoteResult.class );
		if( subjectVoteResult != null ){
			return subjectVoteResult.getStatisticContent();
		}
		return null;
	}
}
