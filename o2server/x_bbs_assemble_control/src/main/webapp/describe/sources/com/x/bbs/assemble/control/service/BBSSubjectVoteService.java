package com.x.bbs.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.WiVoteOption;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.WiVoteOptionGroup;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSSubjectVoteResult;
import com.x.bbs.entity.BBSVoteOption;
import com.x.bbs.entity.BBSVoteOptionGroup;
import com.x.bbs.entity.BBSVoteRecord;
import org.apache.commons.lang3.StringUtils;

public class BBSSubjectVoteService {

	public void saveVoteOptions( BBSSubjectInfo subjectInfo, List<WiVoteOptionGroup> optionGroups ) throws Exception {
		if( optionGroups  == null || optionGroups.isEmpty() ){
			throw new Exception( "选项信息为空!" );
		}
		if( subjectInfo  == null ){
			throw new Exception( "subjectInfo is null!" );
		}
		BBSVoteOptionGroup voteOptionGroup = null;
		BBSVoteOption voteOption = null;
		List<BBSVoteOptionGroup> voteOptionGroupList = null;
		List<BBSVoteOption> voteOptionList = null;
		Business busines = null;
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			busines = new Business(emc);
			voteOptionList = busines.voteOptionFactory().listVoteOptionBySubject( subjectInfo.getId() );
			voteOptionGroupList = busines.voteOptionFactory().listVoteOptionGroupBySubject( subjectInfo.getId() );
			emc.beginTransaction( BBSVoteOptionGroup.class );
			emc.beginTransaction( BBSVoteOption.class );
			if( ListTools.isNotEmpty( voteOptionList ) ){
				for( BBSVoteOption option : voteOptionList ){
					emc.remove( option, CheckRemoveType.all );
				}
			}
			if( ListTools.isNotEmpty( voteOptionGroupList ) ){
				for( BBSVoteOptionGroup group : voteOptionGroupList ){
					emc.remove( group, CheckRemoveType.all );
				}
			}
			
			int groupIndex = 0;
			int optionIndex = 0;
			for( WiVoteOptionGroup group : optionGroups ){
				groupIndex ++;
				optionIndex = 0;
				voteOptionGroup = new BBSVoteOptionGroup();
				voteOptionGroup.setCreateTime( new Date() );
				voteOptionGroup.setCreatorName( subjectInfo.getCreatorName() );
				voteOptionGroup.setForumId( subjectInfo.getForumId() );
				voteOptionGroup.setSubjectId( subjectInfo.getId() );
				voteOptionGroup.setMainSectionId( subjectInfo.getMainSectionId() );
				voteOptionGroup.setSectionId( subjectInfo.getSectionId() );
				voteOptionGroup.setGroupName( group.getGroupName()  );
				voteOptionGroup.setOrderNumber( groupIndex );
				voteOptionGroup.setId( group.getId() );
				voteOptionGroup.setVoteChooseCount( group.getVoteChooseCount() );
				if( ListTools.isNotEmpty( group.getVoteOptions() ) ){
					for( WiVoteOption option : group.getVoteOptions() ){
						optionIndex++;
						voteOption = new BBSVoteOption();
						voteOption.setCreateTime( new Date() );
						voteOption.setCreatorName( subjectInfo.getCreatorName() );
						voteOption.setOptionPictureId( option.getOptionPictureId() );
						voteOption.setOptionGroupId( group.getId() );
						voteOption.setForumId( subjectInfo.getForumId() );
						voteOption.setMainSectionId( subjectInfo.getMainSectionId() );
						voteOption.setSectionId( subjectInfo.getSectionId() );
						voteOption.setSubjectId( subjectInfo.getId() );
						voteOption.setOptionContentType( option.getOptionContentType() );
						voteOption.setOptionTextContent( option.getOptionTextContent() );
						voteOption.setOrderNumber( optionIndex );
						voteOption.setId( option.getId() );
						emc.persist( voteOption, CheckPersistType.all );
					}
				}
				emc.persist( voteOptionGroup, CheckPersistType.all );
			}
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 提交投票结果
	 * 
	 * 1、记录投票日志
	 * 2、计算投票结果到投票选项组信息中
	 * 
	 * @param effectivePerson
	 * @param subjectInfo
	 * @param optionGroups
	 * @throws Exception 
	 */
	public void submitVoteResult( EffectivePerson effectivePerson, BBSSubjectInfo subjectInfo, List<WiVoteOptionGroup> optionGroups ) throws Exception {
		//BBSVoteOptionGroup voteOptionGroup = null;
		BBSVoteOption  voteOption = null;
		BBSVoteRecord voteRecord = null;
		BBSVoteOptionGroup voteOptionGroup = null;
		//Business busines = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			//busines = new Business(emc);
			//emc.beginTransaction( BBSVoteOptionGroup.class );
			emc.beginTransaction( BBSVoteOption.class );
			emc.beginTransaction( BBSVoteRecord.class );
			Business buiness = new Business(emc);
			for( WiVoteOptionGroup group : optionGroups ){
				voteOptionGroup = emc.find( group.getId(), BBSVoteOptionGroup.class );
				if( ListTools.isNotEmpty( group.getSelectedVoteOptionIds() ) ){
					for( String selectedOptionId : group.getSelectedVoteOptionIds()){
						voteOption = emc.find( selectedOptionId, BBSVoteOption.class );
						if( voteOption != null ){
							//查询一下是否已经投过票了
							List<BBSVoteRecord> recordList = buiness.voteRecordFactory().listVoteCountByUserAndGroup( effectivePerson.getDistinguishedName(), group.getId() );
							if( (ListTools.isEmpty( recordList) || recordList.size() < voteOptionGroup.getVoteChooseCount())
									&& !doseNotChoosen( recordList, selectedOptionId )){
								voteRecord = new BBSVoteRecord();
								voteRecord.setOptionGroupId( voteOptionGroup.getId() );
								voteRecord.setId( BBSVoteRecord.createId() );
								voteRecord.setOptionId( selectedOptionId );
								voteRecord.setForumId( subjectInfo.getForumId() );
								voteRecord.setMainSectionId( subjectInfo.getMainSectionId() );
								voteRecord.setSectionId( subjectInfo.getSectionId() );
								voteRecord.setSubjectId( subjectInfo.getId());
								voteRecord.setOptionValue( voteOption.getId() );
								voteRecord.setCreateTime( new Date() );
								voteRecord.setVotorName( effectivePerson.getDistinguishedName() );
								emc.persist( voteRecord, CheckPersistType.all );

								voteOption.setChooseCount( voteOption.getChooseCount() + 1 );
								emc.check( voteOption, CheckPersistType.all );
							}
						}
					}
				}
			}
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 选项是否已经被选择过了。
	 * @param recordList
	 * @param selectedOptionId
	 * @return
	 */
	private boolean doseNotChoosen(List<BBSVoteRecord> recordList, String selectedOptionId) {
		if( ListTools.isNotEmpty( recordList)){
			for( BBSVoteRecord voteRecord : recordList){
				if(StringUtils.equals( voteRecord.getOptionId(), selectedOptionId )){
					return true;
				}
			}
		}
		return false;
	}

	public void deleteAllVoteOptions( String subjectId ) throws Exception {
		if( subjectId  == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}

		List<BBSVoteOptionGroup> voteOptionGroupList = null;
		List<BBSVoteOption> voteOptionList = null;
		Business busines = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			busines = new Business(emc);
			voteOptionList = busines.voteOptionFactory().listVoteOptionBySubject(subjectId);
			voteOptionGroupList = busines.voteOptionFactory().listVoteOptionGroupBySubject(subjectId);
			emc.beginTransaction( BBSVoteOptionGroup.class );
			emc.beginTransaction( BBSVoteOption.class );
			if( ListTools.isNotEmpty( voteOptionList ) ){
				for( BBSVoteOption option : voteOptionList ){
					emc.remove( option, CheckRemoveType.all );
				}
			}
			if( ListTools.isNotEmpty( voteOptionGroupList ) ){
				for( BBSVoteOptionGroup group : voteOptionGroupList ){
					emc.remove( group, CheckRemoveType.all );
				}
			}
			emc.commit();
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
			voteOptionIds = business.voteOptionFactory().listVoteOptionIdsBySubject( subjectId );
			if( ListTools.isNotEmpty( voteOptionIds ) ){
				return business.voteOptionFactory().list( voteOptionIds );
			}else{
				return null;
			}
		}catch( Exception e ){
			throw e;
		}
	}
	
	public List<BBSVoteOptionGroup> listVoteOptionGroup( String subjectId ) throws Exception {
		if( subjectId  == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null, return null!" );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.voteOptionFactory().listVoteOptionGroupBySubject( subjectId );
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

	public List<BBSVoteOption> listVoteOptionByGroupId(String groupId ) throws Exception {
		if( groupId  == null || groupId.isEmpty() ){
			throw new Exception( "groupId is null!" );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			return business.voteOptionFactory().listVoteOptionByGroupId( groupId );
		}catch( Exception e ){
			throw e;
		}
	}

	public boolean hasVoted(EffectivePerson effectivePerson, String subjectId ) throws Exception {
		if( subjectId  == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}
		Business business = null;
		Long count = 0L;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			count = business.voteRecordFactory().getVoteCountByUserAndSubject( effectivePerson.getDistinguishedName(), subjectId );
			if( count > 0 ){
				return true;
			}
		}catch( Exception e ){
			throw e;
		}
		return false;
	}

	public boolean optionHasVoted( EffectivePerson effectivePerson, String optionId ) throws Exception {
		if( optionId  == null || optionId.isEmpty() ){
			throw new Exception( "optionId is null!" );
		}
		Business business = null;
		Long count = 0L;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			count = business.voteRecordFactory().getVoteCountByUserAndOption( effectivePerson.getDistinguishedName(), optionId );
			if( count > 0 ){
				return true;
			}
		}catch( Exception e ){
			throw e;
		}
		return false;
	}

	public Long countVoteRecordForSubject( String subjectId, String voteOptionId ) throws Exception {
		if( subjectId == null || subjectId.isEmpty() ){
			return 0L;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.voteRecordFactory().countVoteRecordForSubject( subjectId, voteOptionId );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSVoteRecord> listVoteRecordForPage(String subjectId, String voteOptionId, Integer maxRecordCount ) throws Exception {
		if( subjectId == null || subjectId.isEmpty() ){
			return null;
		}
		if( maxRecordCount == null ){
			maxRecordCount = 20;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.voteRecordFactory().listVoteRecordForPage( subjectId, voteOptionId, maxRecordCount );
		}catch( Exception e ){
			throw e;
		}
	}
}