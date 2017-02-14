package com.x.okr.assemble.control.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.WrapInOkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkAuthorizeRecord;

/**
 * 类   名：OkrWorkAuthorizeRecordService<br/>
 * 实体类：OkrWorkAuthorizeRecord<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkAuthorizeRecordService{
	private Logger logger = LoggerFactory.getLogger( OkrWorkAuthorizeRecordService.class );
	private BeanCopyTools<WrapInOkrWorkAuthorizeRecord, OkrWorkAuthorizeRecord> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrWorkAuthorizeRecord.class, OkrWorkAuthorizeRecord.class, null, WrapInOkrWorkAuthorizeRecord.Excludes );
	/**
	 * 根据传入的ID从数据库查询OkrWorkAuthorizeRecord对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkAuthorizeRecord get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrWorkAuthorizeRecord.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrWorkAuthorizeRecord对象
	 * @param wrapIn
	 */
	public OkrWorkAuthorizeRecord save( WrapInOkrWorkAuthorizeRecord wrapIn ) throws Exception {
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				okrWorkAuthorizeRecord =  emc.find( wrapIn.getId(), OkrWorkAuthorizeRecord.class );
				if( okrWorkAuthorizeRecord != null ){
					emc.beginTransaction( OkrWorkAuthorizeRecord.class );
					wrapin_copier.copy( wrapIn, okrWorkAuthorizeRecord );
					emc.check( okrWorkAuthorizeRecord, CheckPersistType.all );	
					emc.commit();
				}else{
					okrWorkAuthorizeRecord = new OkrWorkAuthorizeRecord();
					emc.beginTransaction( OkrWorkAuthorizeRecord.class );
					wrapin_copier.copy( wrapIn, okrWorkAuthorizeRecord );
					okrWorkAuthorizeRecord.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrWorkAuthorizeRecord, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.error( "OkrWorkAuthorizeRecord update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkAuthorizeRecord = new OkrWorkAuthorizeRecord();
				emc.beginTransaction( OkrWorkAuthorizeRecord.class );
				wrapin_copier.copy( wrapIn, okrWorkAuthorizeRecord );
				emc.persist( okrWorkAuthorizeRecord, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.error( "OkrWorkAuthorizeRecord create got a error!", e);
				throw e;
			}
		}
		return okrWorkAuthorizeRecord;
	}
	
	/**
	 * 根据ID从数据库中删除OkrWorkAuthorizeRecord对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkAuthorizeRecord = emc.find(id, OkrWorkAuthorizeRecord.class);
			if (null == okrWorkAuthorizeRecord) {
				logger.error( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrWorkAuthorizeRecord.class );
				emc.remove( okrWorkAuthorizeRecord, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	/**
	 * 根据【工作授权人】身份获取第一次授权记录信息
	 * @param workId
	 * @param authorizeIdentity
	 * @return
	 * @throws Exception
	 */
	public OkrWorkAuthorizeRecord getFirstAuthorizeRecord( String workId, String authorizeIdentity ) throws Exception {
		Business business = null;
		if( workId == null || workId.isEmpty() ){
			logger.error( "workId is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkAuthorizeRecordFactory().getFirstAuthorizeRecord( workId, authorizeIdentity );
		} catch ( Exception e ) {
			throw e;
		}
	}
	/**
	 * 根据【工作承担人】获取最后的一次授权记录信息
	 * @param workId
	 * @param undertakerIdentity
	 * @return
	 * @throws Exception
	 */
	public OkrWorkAuthorizeRecord getLastAuthorizeRecord( String workId, String identity ) throws Exception {
		Business business = null;
		if( workId == null || workId.isEmpty() ){
			logger.error( "workId is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkAuthorizeRecordFactory().getLastAuthorizeRecord( workId, identity );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByAuthorizor( String workId, String authorizeIdentity, Integer delegateLevel) throws Exception {
		Business business = null;
		if( workId == null || workId.isEmpty() ){
			logger.error( "workId is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkAuthorizeRecordFactory().listByAuthorizor( workId, authorizeIdentity, delegateLevel );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByWorkId(String id) throws Exception {
		Business business = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkAuthorizeRecordFactory().listByWorkId( id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<OkrWorkAuthorizeRecord> list(List<String> ids) throws Exception {
		Business business = null;
		if( ids == null || ids.isEmpty() ){
			logger.error( "ids is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkAuthorizeRecordFactory().list( ids );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
}
