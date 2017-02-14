package com.x.bbs.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSSubjectVoteResult;
import com.x.bbs.entity.BBSVoteOption;
import com.x.bbs.entity.BBSVoteOptionBinary;

public class BBSSubjectVoteService {
	
	/**
	 * 根据传入的选项ID从数据库查询选项的图片base64编码
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSVoteOptionBinary getBBSVoteOptionBinary( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, BBSVoteOptionBinary.class );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据主题ID查询所有的投票选项列表
	 * @param subjectId
	 * @return
	 * @throws Exception
	 */
	public List<BBSVoteOption> listVoteOption( String subjectId ) throws Exception {
		if( subjectId  == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null, return null!" );
		}
		Business business = null;
		List<String> voteOptionIds = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			voteOptionIds = business.voteOptionFactory().listVoteOptionBySubjectIds( subjectId );
			if( voteOptionIds != null && !voteOptionIds.isEmpty() ){
				return business.voteOptionFactory().list( voteOptionIds );
			}else{
				return null;
			}
		}catch( Exception e ){
			throw e;
		}
	}

	public String getVoteResult( String subjectId ) throws Exception {
		if( subjectId  == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null, return null!" );
		}
		BBSSubjectVoteResult subjectVoteResult = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			subjectVoteResult = emc.find( subjectId, BBSSubjectVoteResult.class );
			if( subjectVoteResult != null ){
				return subjectVoteResult.getStatisticContent();
			}
			return null;
		}catch( Exception e ){
			throw e;
		}
	}

	public String getOptionBinaryContent( String optionId ) throws Exception {
		if( optionId  == null || optionId.isEmpty() ){
			throw new Exception( "optionId is null, return null!" );
		}
		BBSVoteOptionBinary voteOptionBinary = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			voteOptionBinary = emc.find( optionId, BBSVoteOptionBinary.class );
			if( voteOptionBinary != null ){
				return voteOptionBinary.getOptionBinary();
			}
			return null;
		}catch( Exception e ){
			throw e;
		}
	}
	
}