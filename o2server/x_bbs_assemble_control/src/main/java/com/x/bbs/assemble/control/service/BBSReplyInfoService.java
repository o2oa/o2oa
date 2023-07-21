package com.x.bbs.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSUserInfo;

/**
 * 论坛信息管理服务类
 * @author LIYI
 *
 */
public class BBSReplyInfoService {

	private static  Logger logger = LoggerFactory.getLogger( BBSReplyInfoService.class );

	/**
	 * 根据传入的ID从数据库查询BBSReplyInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSReplyInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, BBSReplyInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 向数据库保存BBSReplyInfo对象
	 * @param _bBSReplyInfo
	 */
	public BBSReplyInfo save( BBSReplyInfo _bBSReplyInfo ) throws Exception {
		BBSReplyInfo _bBSReplyInfo_tmp = null;
		BBSSubjectInfo _subjectInfo = null;
		BBSSectionInfo _sectionInfo = null;
		BBSSectionInfo _mainSectoinInfo = null;
		BBSForumInfo _forumInfo = null;
		BBSUserInfo _BBSUserInfo = null;

		if( _bBSReplyInfo.getId() == null ){
			_bBSReplyInfo.setId( BBSReplyInfo.createId() );
		}
		Business business = null;
		Integer maxOrderNumber = 0;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			try{
				maxOrderNumber = business.replyInfoFactory().getMaxOrderNumber( _bBSReplyInfo.getSubjectId() );
			}catch(Exception e){
				maxOrderNumber = 0;
			}

			_forumInfo = emc.find( _bBSReplyInfo.getForumId(), BBSForumInfo.class );
			_mainSectoinInfo = emc.find( _bBSReplyInfo.getMainSectionId(), BBSSectionInfo.class );
			_sectionInfo = emc.find( _bBSReplyInfo.getSectionId(), BBSSectionInfo.class );
			_subjectInfo = emc.find( _bBSReplyInfo.getSubjectId(), BBSSubjectInfo.class );
			_bBSReplyInfo_tmp = emc.find( _bBSReplyInfo.getId(), BBSReplyInfo.class );

			emc.beginTransaction( BBSReplyInfo.class );
			emc.beginTransaction( BBSSubjectInfo.class );
			emc.beginTransaction( BBSSectionInfo.class );
			emc.beginTransaction( BBSForumInfo.class );
			if( _bBSReplyInfo_tmp == null ){
				//创建一个新的记录
				_bBSReplyInfo.setOrderNumber( ( maxOrderNumber + 1 ) );
				emc.persist( _bBSReplyInfo, CheckPersistType.all);
			}else{
				//更新一条记录
				_bBSReplyInfo.setCreateTime( _bBSReplyInfo_tmp.getCreateTime() );
				_bBSReplyInfo.copyTo( _bBSReplyInfo_tmp, JpaObject.FieldsUnmodify  );
				emc.check( _bBSReplyInfo_tmp, CheckPersistType.all );
			}
			if( _subjectInfo != null ){
				_subjectInfo.setLatestReplyTime( new Date() );
				_subjectInfo.setLatestReplyId( _bBSReplyInfo.getId() );
				_subjectInfo.setLatestReplyUser(_bBSReplyInfo.getCreatorName());
				_subjectInfo.setReplyTotal( _subjectInfo.getReplyTotal() + 1 );
				emc.check( _subjectInfo, CheckPersistType.all );
			}
			if( _sectionInfo != null ){
				_sectionInfo.setReplyTotalToday( _sectionInfo.getReplyTotalToday() + 1 );
				_sectionInfo.setReplyTotal( _sectionInfo.getReplyTotal() + 1 );
				emc.check( _sectionInfo, CheckPersistType.all );
			}
			if( _mainSectoinInfo != null ){
				if( !_mainSectoinInfo.getId().equals( _sectionInfo.getId() )){
					_mainSectoinInfo.setReplyTotalToday( _mainSectoinInfo.getReplyTotalToday() + 1 );
					_mainSectoinInfo.setReplyTotal( _mainSectoinInfo.getReplyTotal() + 1 );
					emc.check( _mainSectoinInfo, CheckPersistType.all );
				}
			}
			if( _forumInfo != null ){
				_forumInfo.setReplyTotal( _forumInfo.getReplyTotal() + 1 );
				emc.check( _forumInfo, CheckPersistType.all );
			}

			emc.commit();

			//个人回复数增加1
			BBSUserInfoService userInfoService = new BBSUserInfoService();
			BBSUserInfo userInfo = userInfoService.getByUserName( _bBSReplyInfo.getCreatorName());
			userInfo.setReplyCount(userInfo.getReplyCount() + 1);
			userInfo.setReplyCountToday(userInfo.getReplyCountToday() + 1);
			userInfoService.save(userInfo);

		}catch( Exception e ){
			logger.warn( "system find BBSReplyInfo{'id':'"+_bBSReplyInfo.getId()+"'} got an exception!" );
			throw e;
		}
		return _bBSReplyInfo;
	}

	/**
	 * 根据ID从数据库中删除BBSReplyInfo对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		BBSReplyInfo replyInfo = null;
		BBSSubjectInfo _subjectInfo = null;
		BBSSectionInfo _sectionInfo = null;
		BBSSectionInfo _mainSectoinInfo = null;
		BBSForumInfo _forumInfo = null;
		if( id == null || id.isEmpty() ){
			return;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			replyInfo = emc.find( id, BBSReplyInfo.class );
			if ( null == replyInfo ) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( BBSReplyInfo.class );
				emc.beginTransaction( BBSSubjectInfo.class );
				emc.beginTransaction( BBSSectionInfo.class );
				emc.beginTransaction( BBSForumInfo.class );

				_forumInfo = emc.find( replyInfo.getForumId(), BBSForumInfo.class );
				_mainSectoinInfo = emc.find( replyInfo.getMainSectionId(), BBSSectionInfo.class );
				_sectionInfo = emc.find( replyInfo.getSectionId(), BBSSectionInfo.class );
				_subjectInfo = emc.find( replyInfo.getSubjectId(), BBSSubjectInfo.class );
				emc.remove( replyInfo, CheckRemoveType.all );
				if( _subjectInfo != null ){
					if( _subjectInfo.getReplyTotal() > 0 ){
						_subjectInfo.setReplyTotal( _subjectInfo.getReplyTotal() - 1 );
						//如果当前删除的回复，是主贴采纳的解决方案，那么需要把主贴采纳的解决方案置空
						if( StringUtils.isNotEmpty( _subjectInfo.getAcceptReplyId() ) &&
							StringUtils.equals( _subjectInfo.getAcceptReplyId(), id )){
							_subjectInfo.setAcceptReplyId( null );
						}
						emc.check( _subjectInfo, CheckPersistType.all );
					}
				}
				if( _sectionInfo != null ){
					if( _sectionInfo.getReplyTotal() > 0 ){
						_sectionInfo.setReplyTotal( _sectionInfo.getReplyTotal() - 1 );
						emc.check( _sectionInfo, CheckPersistType.all );
					}
				}
				if( _mainSectoinInfo != null ){
					if( !_mainSectoinInfo.getId().equals( _sectionInfo.getId() )){
						if( _mainSectoinInfo.getReplyTotal() > 0 ){
							_mainSectoinInfo.setReplyTotal( _mainSectoinInfo.getReplyTotal() - 1 );
							emc.check( _mainSectoinInfo, CheckPersistType.all );
						}
					}
				}
				if( _forumInfo != null ){
					if( _forumInfo.getReplyTotal() > 0 ){
						_forumInfo.setReplyTotal( _forumInfo.getReplyTotal() - 1 );
						emc.check( _forumInfo, CheckPersistType.all );
					}
				}
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<BBSReplyInfo> listWithSubjectForPage(String subjectId, Boolean showSubReply, int maxCount, String orderType ) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId can not null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.replyInfoFactory().listWithSubjectForPage( subjectId, showSubReply, maxCount, orderType );
		}catch( Exception e ){
			throw e;
		}
	}

	public Long countWithSubjectForPage(String subjectId, Boolean showSubReply) throws Exception {
		if( subjectId == null ){
			throw new Exception( "subjectId can not null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.replyInfoFactory().countBySubjectId( subjectId, showSubReply );
		}catch( Exception e ){
			throw e;
		}
	}

	public Long countReplyByUserName( String creatorName ) throws Exception {
		if( creatorName == null ){
			throw new Exception( "creatorName can not null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.replyInfoFactory().countReplyForPage( creatorName, null, null, null, null );
		}catch( Exception e ){
			throw e;
		}
	}

	public List<BBSReplyInfo> listReplyByUserNameForPage(String creatorName, String orderType, int maxCount ) throws Exception {
		if( creatorName == null ){
			throw new Exception( "creatorName can not null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.replyInfoFactory().listReplyForPage( creatorName, null, null, null, null, orderType, maxCount );
		}catch( Exception e ){
			throw e;
		}
	}

	public Long countReplyForTodayByUserName( String userName ) throws Exception {
		if( StringUtils.isEmpty(userName) ){
			throw new Exception( "userName can not null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.replyInfoFactory().countReplyForTodayByUserName( userName, null, null, null, null );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 根据回复ID，查询二级回复列表
	 * @param replyId
	 * @return
	 */
    public List<BBSReplyInfo> listRelysWithRelyId(String replyId, String orderType ) throws Exception {
		if(StringUtils.isEmpty( replyId ) ){
			throw new Exception( "replyId can not null." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.replyInfoFactory().listReplyWithReplyId(replyId, orderType);
		}catch( Exception e ){
			throw e;
		}
    }

	public List<String> listAllSubReplyIds(String replyId, List<String> allIds ) throws Exception {
    	if( allIds == null ){
			allIds = new ArrayList<>();
		}
		if(StringUtils.isEmpty( replyId ) ){
			return allIds;
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			List<String> subIds = business.replyInfoFactory().listSubReplyIdsWithReplyId(replyId);
			if(ListTools.isNotEmpty( subIds )){
				for( String id : subIds ){
					if( !allIds.contains( id )){
						allIds.add( id );
						listAllSubReplyIds( id, allIds );
					}
				}
			}
		}
		return allIds;
	}
}
