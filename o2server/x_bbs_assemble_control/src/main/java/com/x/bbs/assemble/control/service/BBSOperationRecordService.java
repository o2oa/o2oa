package com.x.bbs.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSOperationRecord;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

/**
 * 论坛信息管理服务类
 * @author LIYI
 *
 */
public class BBSOperationRecordService {
	
	private static  Logger logger = LoggerFactory.getLogger( BBSOperationRecordService.class );
	
	/**
	 * 根据传入的ID从数据库查询BBSOperationRecord对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BBSOperationRecord get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, BBSOperationRecord.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存BBSOperationRecord对象
	 * @param wrapIn
	 */
	public BBSOperationRecord save( BBSOperationRecord _bBSForumInfo, String hostIp, String hostName ) throws Exception {
		BBSOperationRecord _bBSForumInfo_tmp = null;
		if( _bBSForumInfo.getId() == null ){
			_bBSForumInfo.setId( BBSOperationRecord.createId() );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			_bBSForumInfo_tmp = emc.find( _bBSForumInfo.getId(), BBSOperationRecord.class );
			if( _bBSForumInfo_tmp == null ){
				//创建一个新的记录
				emc.beginTransaction( BBSOperationRecord.class );
				emc.persist( _bBSForumInfo, CheckPersistType.all);	
				emc.commit();
			}else{
				//更新一条记录
				emc.beginTransaction( BBSOperationRecord.class );
				_bBSForumInfo.copyTo( _bBSForumInfo_tmp, JpaObject.FieldsUnmodify  );
				emc.check( _bBSForumInfo_tmp, CheckPersistType.all );	
				emc.commit();
			}			
		}catch( Exception e ){
			logger.warn( "system find BBSOperationRecord{'id':'"+_bBSForumInfo.getId()+"'} got an exception!" );
			throw e;
		}
		return _bBSForumInfo;
	}
	
	/**
	 * 根据ID从数据库中删除BBSOperationRecord对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		BBSOperationRecord operationRecord = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			operationRecord = emc.find( id, BBSOperationRecord.class );
			if ( null == operationRecord ) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( BBSOperationRecord.class );
				emc.remove( operationRecord, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void subjectOperation( String name, BBSSubjectInfo subjectInfo, String optType, String hostIp, String hostName ) throws Exception {		
		BBSOperationRecord operationRecord = new BBSOperationRecord();
		operationRecord.setForumId( subjectInfo.getForumId() );
		operationRecord.setForumName( subjectInfo.getForumName() );
		operationRecord.setMainSectionId( subjectInfo.getMainSectionId() );
		operationRecord.setMainSectionName( subjectInfo.getMainSectionName() );
		operationRecord.setSectionId( subjectInfo.getSectionId() );
		operationRecord.setSectionName( subjectInfo.getSectionName() );
		operationRecord.setSubjectId( subjectInfo.getId() );
		operationRecord.setObjectId( subjectInfo.getId() );
		operationRecord.setObjectName( subjectInfo.getTitle() );
		operationRecord.setOperatorName( name );
		operationRecord.setOptType( optType );
		operationRecord.setObjectType("SUBJECT");		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( BBSOperationRecord.class );
			emc.persist( operationRecord, CheckPersistType.all );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public void replyOperation( String name, BBSReplyInfo replyInfo, String optType, String hostIp, String hostName ) throws Exception {		
		BBSOperationRecord operationRecord = new BBSOperationRecord();
		operationRecord.setForumId( replyInfo.getForumId() );
		operationRecord.setForumName( replyInfo.getForumName() );
		operationRecord.setMainSectionId( replyInfo.getMainSectionId() );
		operationRecord.setMainSectionName( replyInfo.getMainSectionName() );
		operationRecord.setSectionId( replyInfo.getSectionId() );
		operationRecord.setSectionName( replyInfo.getSectionName() );
		operationRecord.setSubjectId( replyInfo.getId() );
		operationRecord.setObjectId( replyInfo.getId() );
		operationRecord.setObjectName( replyInfo.getTitle() );
		operationRecord.setOperatorName( name );
		operationRecord.setOptType( optType );
		operationRecord.setObjectType("REPLY");		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( BBSOperationRecord.class );
			emc.persist( operationRecord, CheckPersistType.all );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void forumOperation(String name, BBSForumInfo forumInfo, String optType, String hostIp, String hostName ) throws Exception {
		BBSOperationRecord operationRecord = new BBSOperationRecord();
		operationRecord.setForumId( forumInfo.getId() );
		operationRecord.setForumName( forumInfo.getForumName() );
		operationRecord.setMainSectionId( "" );
		operationRecord.setMainSectionName( "" );
		operationRecord.setSectionId( "" );
		operationRecord.setSectionName( "" );
		operationRecord.setSubjectId( "" );
		operationRecord.setObjectId( forumInfo.getId() );
		operationRecord.setObjectName( forumInfo.getForumName() );
		operationRecord.setOperatorName( name );
		operationRecord.setOptType( optType );
		operationRecord.setObjectType("FORUM");		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( BBSOperationRecord.class );
			emc.persist( operationRecord, CheckPersistType.all );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void sectionOperation(String name, BBSSectionInfo sectionInfo, String optType, String hostIp, String hostName ) throws Exception {
		BBSOperationRecord operationRecord = new BBSOperationRecord();
		operationRecord.setForumId( sectionInfo.getForumId() );
		operationRecord.setForumName( sectionInfo.getForumName() );
		operationRecord.setMainSectionId( sectionInfo.getMainSectionId() );
		operationRecord.setMainSectionName( sectionInfo.getMainSectionName() );
		operationRecord.setSectionId( sectionInfo.getId() );
		operationRecord.setSectionName( sectionInfo.getSectionName() );
		operationRecord.setSubjectId( sectionInfo.getId() );
		operationRecord.setObjectId( sectionInfo.getId() );
		operationRecord.setObjectName( sectionInfo.getSectionName() );
		operationRecord.setOperatorName( name );
		operationRecord.setOptType( optType );
		operationRecord.setObjectType("SECTION");		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( BBSOperationRecord.class );
			emc.persist( operationRecord, CheckPersistType.all );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void sectionIconOperation(String name, BBSSectionInfo sectionInfo, String optType, String hostIp, String hostName ) throws Exception {
		BBSOperationRecord operationRecord = new BBSOperationRecord();
		operationRecord.setForumId( sectionInfo.getForumId() );
		operationRecord.setForumName( sectionInfo.getForumName() );
		operationRecord.setMainSectionId( sectionInfo.getMainSectionId() );
		operationRecord.setMainSectionName( sectionInfo.getMainSectionName() );
		operationRecord.setSectionId( sectionInfo.getId() );
		operationRecord.setSectionName( sectionInfo.getSectionName() );
		operationRecord.setSubjectId( sectionInfo.getId() );
		operationRecord.setObjectId( sectionInfo.getId() );
		operationRecord.setObjectName( sectionInfo.getSectionName() );
		operationRecord.setOperatorName( name );
		operationRecord.setOptType( optType );
		operationRecord.setObjectType("SECTIONICON");
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( BBSOperationRecord.class );
			emc.persist( operationRecord, CheckPersistType.all );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void roleOperation( String name, BBSRoleInfo roleInfo, String optType, String hostIp, String hostName ) throws Exception {
		BBSOperationRecord operationRecord = new BBSOperationRecord();
		operationRecord.setForumId( roleInfo.getForumId() );
		operationRecord.setForumName( roleInfo.getForumName() );
		operationRecord.setMainSectionId( roleInfo.getMainSectionId() );
		operationRecord.setMainSectionName( roleInfo.getMainSectionName() );
		operationRecord.setSectionId( roleInfo.getId() );
		operationRecord.setSectionName( roleInfo.getSectionName() );
		operationRecord.setSubjectId( roleInfo.getId() );
		operationRecord.setObjectId( roleInfo.getId() );
		operationRecord.setObjectName( roleInfo.getSectionName() );
		operationRecord.setOperatorName( name );
		operationRecord.setOptType( optType );
		operationRecord.setObjectType("ROLE");
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( BBSOperationRecord.class );
			emc.persist( operationRecord, CheckPersistType.all );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void loginOperation( String name, String hostIp, String hostName ) throws Exception {
		BBSOperationRecord operationRecord = new BBSOperationRecord();
		operationRecord.setForumId( null );
		operationRecord.setForumName( null );
		operationRecord.setMainSectionId( null );
		operationRecord.setMainSectionName( null );
		operationRecord.setSectionId( null );
		operationRecord.setSectionName( null );
		operationRecord.setSubjectId( null );
		operationRecord.setObjectId( name );
		operationRecord.setObjectName( name );
		operationRecord.setOperatorName( name );
		operationRecord.setOptType( "LOGIN" );
		operationRecord.setObjectType("LOGIN");
		operationRecord.setHostIp(hostIp);
		operationRecord.setHostname(hostName);
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( BBSOperationRecord.class );
			emc.persist( operationRecord, CheckPersistType.all );
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> distinctAllOperationUserNames() throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.operationRecordFactory().distinctAllOperationUserNames();
		}catch( Exception e ){
			throw e;
		}
	}
}