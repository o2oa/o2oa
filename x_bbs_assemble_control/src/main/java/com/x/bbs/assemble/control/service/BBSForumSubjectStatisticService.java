package com.x.bbs.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class BBSForumSubjectStatisticService {
	
	public void statisticSubjectTotalAndReplayTotalForForum( List<BBSForumInfo> forumInfoList ) throws Exception {
		if( forumInfoList  == null || forumInfoList.isEmpty() ){
			throw new Exception( "forumInfoList is null, return null!" );
		}
		Business business = null;
		List<BBSSectionInfo> mainSectionList = null;
		List<BBSSectionInfo> sectionList = null;
		Long count = 0L;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			forumInfoList = business.forumInfoFactory().listAll();
			if( forumInfoList != null && !forumInfoList.isEmpty() ){
				emc.beginTransaction( BBSForumInfo.class );
				emc.beginTransaction( BBSSectionInfo.class );
				for( BBSForumInfo forumInfo : forumInfoList ){
					mainSectionList = business.sectionInfoFactory().listMainSectionByForumId( forumInfo.getId() );
					if( mainSectionList != null && !mainSectionList.isEmpty() ){
						for( BBSSectionInfo mainSectionInfo : mainSectionList ){
							sectionList = business.sectionInfoFactory().listSubSectionByMainSectionId( mainSectionInfo.getId() );
							if( sectionList != null && !sectionList.isEmpty() ){
								for( BBSSectionInfo sectionInfo : mainSectionList ){
									//统计版块下面的贴子数量
									count = business.subjectInfoFactory().countByMainAndSubSectionId( sectionInfo.getId(), null );
									sectionInfo.setSubjectTotal( count.longValue() );
									count = business.replyInfoFactory().countBySectionId( sectionInfo.getId() );
									sectionInfo.setReplyTotal( count.longValue() );
									count = business.subjectInfoFactory().countSubjectForTodayBySectionId( sectionInfo.getId() );
									sectionInfo.setSubjectTotalToday( count.longValue() );
									count = business.replyInfoFactory().countReplyForTodayBySectionId( mainSectionInfo.getId() );
									sectionInfo.setReplyTotalToday( count.longValue() );
									emc.check( sectionInfo, CheckPersistType.all );
								}
							}
							//统计主版块下面的贴子数量
							count = business.subjectInfoFactory().countByMainAndSubSectionId( mainSectionInfo.getId(), null );
							mainSectionInfo.setSubjectTotal( count.longValue() );
							count = business.replyInfoFactory().countBySectionId( mainSectionInfo.getId() );
							mainSectionInfo.setReplyTotal( count.longValue() );
							count = business.subjectInfoFactory().countSubjectForTodayBySectionId( mainSectionInfo.getId() );
							mainSectionInfo.setSubjectTotalToday( count.longValue() );
							count = business.replyInfoFactory().countReplyForTodayBySectionId( mainSectionInfo.getId() );
							mainSectionInfo.setReplyTotalToday( count.longValue() );
							emc.check( mainSectionInfo, CheckPersistType.all );
						}
					}
					//统计论坛版块数量
					count = business.sectionInfoFactory().countAllSectionByForumId( forumInfo.getId() );
					forumInfo.setSectionTotal( count.longValue() );
					//统计论坛下面的贴子数量
					count = business.subjectInfoFactory().countByForumId( forumInfo.getId(), null );
					forumInfo.setSubjectTotal( count.longValue() );
					count = business.replyInfoFactory().countByForumId( forumInfo.getId() );
					forumInfo.setReplyTotal( count.longValue() );
					count = business.subjectInfoFactory().countForTodayByForumId( forumInfo.getId() );
					forumInfo.setSubjectTotalToday( count.longValue() );
					count = business.replyInfoFactory().countForTodayByForumId( forumInfo.getId() );
					forumInfo.setReplyTotalToday( count.longValue() );
					emc.check( forumInfo, CheckPersistType.all );
				}
			}
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
	}
	
	public void statisticReplyTotalForSubjects( List<BBSSubjectInfo> subjectIds) throws Exception {
		if( subjectIds  == null || subjectIds.isEmpty() ){
			throw new Exception( "subjectIds is null, return null!" );
		}		
		Business business = null;
		BBSSubjectInfo subject = null;
		Long count = 0L;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( BBSSubjectInfo.class );
			for( BBSSubjectInfo _subject : subjectIds ){
				subject = emc.find( _subject.getId(), BBSSubjectInfo.class );
				if( subject != null ){
					count = business.replyInfoFactory().countBySubjectId( _subject.getId() );
					subject.setReplyTotal( count.longValue() );
					emc.check( subject, CheckPersistType.all );
				}
			}
			emc.commit();
		}catch( Exception e ){
			throw e;
		}
	}
}