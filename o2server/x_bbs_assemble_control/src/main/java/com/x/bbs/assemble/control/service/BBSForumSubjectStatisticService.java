package com.x.bbs.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class BBSForumSubjectStatisticService {
	
	public void statisticSubjectTotalAndReplayTotalForForum( List<BBSForumInfo> forumInfoList ) throws Exception {
		if( forumInfoList  == null || forumInfoList.isEmpty() ){
			throw new Exception( "forumInfoList is null, return null!" );
		}
		
		forumInfoList.forEach( f -> {
			Business business = null;
			List<BBSSectionInfo> mainSectionList = null;
			List<BBSSectionInfo> sectionList = null;
			Long count = 0L;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				business = new Business( emc );
				emc.beginTransaction( BBSForumInfo.class );
				emc.beginTransaction( BBSSectionInfo.class );
				
				mainSectionList = business.sectionInfoFactory().listMainSectionByForumId( f.getId() );
				if( ListTools.isNotEmpty( mainSectionList ) ){
					for( BBSSectionInfo mainSectionInfo : mainSectionList ){
						sectionList = business.sectionInfoFactory().listSubSectionByMainSectionId( mainSectionInfo.getId() );
						if( ListTools.isNotEmpty( sectionList ) ){
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

								if( StringUtils.isEmpty( sectionInfo.getReplyMessageNotifyType() )){
									sectionInfo.setReplyMessageNotifyType("0,0,0");
								}

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

						if( StringUtils.isEmpty( mainSectionInfo.getReplyMessageNotifyType() )){
							mainSectionInfo.setReplyMessageNotifyType("0,0,0");
						}

						emc.check( mainSectionInfo, CheckPersistType.all );
					}
				}

				//统计论坛版块数量
				count = business.sectionInfoFactory().countAllSectionByForumId( f.getId() );
				f.setSectionTotal( count.longValue() );
				//统计论坛下面的贴子数量
				count = business.subjectInfoFactory().countByForumId( f.getId(), null );
				f.setSubjectTotal( count.longValue() );
				count = business.replyInfoFactory().countByForumId( f.getId() );
				f.setReplyTotal( count.longValue() );
				count = business.subjectInfoFactory().countForTodayByForumId( f.getId() );
				f.setSubjectTotalToday( count.longValue() );
				count = business.replyInfoFactory().countForTodayByForumId( f.getId() );
				f.setReplyTotalToday( count.longValue() );

				if( StringUtils.isEmpty( f.getReplyMessageNotifyType() )){
					f.setReplyMessageNotifyType("0,0,0");
				}

				emc.check( f, CheckPersistType.all );
				emc.commit();
			}catch( Exception e ){
				e.printStackTrace();
			}
		});
	}
	
	public void statisticReplyTotalForSubjects( List<BBSSubjectInfo> subjectIds ) throws Exception {
		if( subjectIds  == null || subjectIds.isEmpty() ){
			throw new Exception( "subjectIds is null, return null!" );
		}
		subjectIds.forEach( s -> {
			Business business = null;
			BBSSubjectInfo subject = null;
			Long count = 0L;
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				business = new Business( emc );
				emc.beginTransaction( BBSSubjectInfo.class );
				subject = emc.find( s.getId(), BBSSubjectInfo.class );
				if( subject != null ){
					count = business.replyInfoFactory().countBySubjectId( s.getId(), true );
					subject.setReplyTotal( count.longValue() );
					emc.check( subject, CheckPersistType.all );
				}
				emc.commit();
			}catch( Exception e ){
				e.printStackTrace();
			}
		});
		
		
		
	}
}