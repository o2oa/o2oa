package com.x.okr.assemble.control.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrworkprocesslink.WrapInOkrWorkProcessLink;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkProcessLink;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapPerson;

/**
 * 类   名：OkrWorkProcessLinkService<br/>
 * 实体类：OkrWorkProcessLink<br/>
 * 作   者：Liyi<br/>
 * 单   位：O2 Team<br/>
 * 日   期：2016-05-20 17:17:27
**/
public class OkrWorkProcessLinkService{
	private Logger logger = LoggerFactory.getLogger( OkrWorkProcessLinkService.class );
	private BeanCopyTools<WrapInOkrWorkProcessLink, OkrWorkProcessLink> wrapin_copier = BeanCopyToolsBuilder.create( WrapInOkrWorkProcessLink.class, OkrWorkProcessLink.class, null, WrapInOkrWorkProcessLink.Excludes );
	
	/**
	 * 根据传入的ID从数据库查询OkrWorkProcessLink对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkProcessLink get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return emc.find( id, OkrWorkProcessLink.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 向数据库保存OkrWorkProcessLink对象
	 * @param wrapIn
	 */
	public OkrWorkProcessLink save( WrapInOkrWorkProcessLink wrapIn ) throws Exception {
		OkrWorkProcessLink okrWorkProcessLink = null;
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
		//根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkProcessLink =  emc.find( wrapIn.getId(), OkrWorkProcessLink.class );
				if( okrWorkProcessLink != null ){
					emc.beginTransaction( OkrWorkProcessLink.class );
					wrapin_copier.copy( wrapIn, okrWorkProcessLink );
					emc.check( okrWorkProcessLink, CheckPersistType.all );	
					emc.commit();
				}else{
					okrWorkProcessLink = new OkrWorkProcessLink();
					emc.beginTransaction( OkrWorkProcessLink.class );
					wrapin_copier.copy( wrapIn, okrWorkProcessLink );
					okrWorkProcessLink.setId( wrapIn.getId() );//使用参数传入的ID作为记录的ID
					emc.persist( okrWorkProcessLink, CheckPersistType.all);	
					emc.commit();
				}
			}catch( Exception e ){
				logger.error( "OkrWorkProcessLink update/ got a error!" );
				throw e;
			}
		}else{//没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkProcessLink = new OkrWorkProcessLink();
				emc.beginTransaction( OkrWorkProcessLink.class );
				wrapin_copier.copy( wrapIn, okrWorkProcessLink );
				emc.persist( okrWorkProcessLink, CheckPersistType.all);	
				emc.commit();
			}catch( Exception e ){
				logger.error( "OkrWorkProcessLink create got a error!", e);
				throw e;
			}
		}
		return okrWorkProcessLink;
	}
	
	/**
	 * 根据ID从数据库中删除OkrWorkProcessLink对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrWorkProcessLink okrWorkProcessLink = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkProcessLink = emc.find(id, OkrWorkProcessLink.class);
			if (null == okrWorkProcessLink) {
				logger.error( "object is not exist {'id':'"+ id +"'}" );
			}else{
				emc.beginTransaction( OkrWorkProcessLink.class );
				emc.remove( okrWorkProcessLink, CheckRemoveType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据工作信息来维护工作审核人员链
	 * 工作部署过程中，会实时记录工作所在的审核层级，根据工作所在的层级可以获取审核人信息
	 * @param okrWorkBaseInfo
	 * @throws Exception 
	 */
	public void saveWorkProcessLinkByWorkBaseInfo( OkrWorkBaseInfo okrWorkBaseInfo, String employeeIdentity ) throws Exception {
		//根据工作当前审核层级来确定
		OkrWorkProcessLink okrWorkProcessLink = null;
		
		if( okrWorkBaseInfo == null ){
			logger.error( "okrWorkBaseInfo is null, system can not delete any object." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			okrWorkProcessLink = business.okrWorkProcessLinkFactory().listByWorkIdAndProcessLevel( okrWorkBaseInfo.getId(), okrWorkBaseInfo.getWorkAuditLevel() );
			if( okrWorkProcessLink == null){
				saveNewProcessLink( okrWorkBaseInfo, employeeIdentity );
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 保存一条新的工作处理链记录
	 * @param okrWorkBaseInfo
	 * @throws Exception 
	 */
	private void saveNewProcessLink( OkrWorkBaseInfo okrWorkBaseInfo, String employeeIdentity ) throws Exception {
		if( okrWorkBaseInfo == null ){
			throw new Exception( "okrWorkBaseInfo is null!" );
		}
		if( employeeIdentity == null ){
			throw new Exception( "employeeIdentity is null!" );
		}
		Business business = null;
		WrapDepartment department = null; 
		WrapPerson wrapPerson  = null;
		String title = okrWorkBaseInfo.getTitle();
		String centerId = okrWorkBaseInfo.getCenterId();
		String centerTitle = okrWorkBaseInfo.getCenterTitle();
		String workId = okrWorkBaseInfo.getId();
		String processorName = null;
		String processorOrganizationName = null;
		String processorCompanyName = null;
		Integer processLevel = okrWorkBaseInfo.getWorkAuditLevel();
		String activityName = "工作审核";
		//根据当前处理人，获取组织名称和公司名称
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( employeeIdentity != null && !employeeIdentity.isEmpty()){
				wrapPerson = business.organization().person().getWithIdentity( employeeIdentity );
				if( wrapPerson != null ){
					processorName = wrapPerson.getName();
					department = business.organization().department().getWithIdentity( employeeIdentity );
					if( department != null ){
						processorOrganizationName = department.getName();
						processorCompanyName = department.getCompany();
					}else{
						throw new Exception( "根据员工身份["+employeeIdentity+"]未能查询到任何组织信息。" );
					}
				}else{
					throw new Exception( "根据员工身份["+employeeIdentity+"]未能查询到任何个人信息。" );
				}
			}
		}catch( Exception e ){
			logger.error( "new OkrWorkProcessLink create got a error!", e);
			throw e;
		}
		//根据信息构建一个对象，准备保存到数据库
		OkrWorkProcessLink okrWorkProcessLink = new OkrWorkProcessLink(title, centerId, centerTitle, workId, processorName, employeeIdentity, processorOrganizationName, processorCompanyName, processLevel, activityName);
		//将数据保存到数据库中
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction( OkrWorkProcessLink.class );
			emc.persist( okrWorkProcessLink, CheckPersistType.all);	
			emc.commit();
		}catch( Exception e ){
			logger.error( "new OkrWorkProcessLink create got a error!", e);
			throw e;
		}
	}
}
