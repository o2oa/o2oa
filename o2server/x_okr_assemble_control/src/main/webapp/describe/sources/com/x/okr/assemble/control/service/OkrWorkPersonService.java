package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInOkrWorkPerson;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkPerson;

public class OkrWorkPersonService {

	private static  Logger logger = LoggerFactory.getLogger( OkrWorkPersonService.class );
	private WrapCopier<WrapInOkrWorkPerson, OkrWorkPerson> wrapin_copier = WrapCopierFactory.wo( WrapInOkrWorkPerson.class, OkrWorkPerson.class, null, WrapInOkrWorkPerson.Excludes );
	private DateOperation dateOperation = new DateOperation();
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	/**
	 * 根据传入的ID从数据库查询OkrWorkPerson对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkPerson get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			return emc.find( id, OkrWorkPerson.class );
		}catch( Exception e ){
			throw e;
		}
	}

	/**
	 * 向数据库保存OkrWorkPerson对象
	 * @param wrapIn
	 */
	public void saveCenterWorkPerson( WrapInOkrWorkPerson wrapIn ) throws Exception {
		OkrWorkPerson entity = new OkrWorkPerson();
		entity = wrapin_copier.copy( wrapIn, entity );
		if( wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
			entity.setId( wrapIn.getId() );
		}
		saveCenterWorkPerson( entity );
	}
	
	/**
	 * 向数据库保存OkrWorkPerson对象
	 * 不仅是ID要唯一，并且员工姓名，中心工作ID和工作ID，还有身份也是一套唯一键
	 * @param OkrWorkPerson
	 */
	public void saveCenterWorkPerson( OkrWorkPerson entity ) throws Exception {
		Business business = null;
		List<String> ids = null;
		List<String> stauts = new ArrayList<>();
		stauts.add("正常");
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//同一个员工在相同的工作里同一个身份只能有一条记录
			ids = business.okrWorkPersonFactory().listIdsForCenterWorkByCenterId( entity.getCenterId(), entity.getEmployeeIdentity(), entity.getProcessIdentity(), stauts );
			if( ids == null || ids.isEmpty() ){
				emc.beginTransaction( OkrWorkPerson.class );
				emc.persist( entity, CheckPersistType.all);	
				emc.commit();
			}
		}catch( Exception e ){
			logger.warn( "OkrWorkPerson update/ get a error!" );
			throw e;
		}
	}

	/**
	 * 根据ID从数据库中删除OkrWorkPerson对象
	 * @param id
	 * @throws Exception
	 */
	public void delete( String id ) throws Exception {
		OkrWorkPerson okrWorkPerson = null;
		if( id == null || id.isEmpty() ){
			throw new Exception( "id is null, system can not delete any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkPerson = emc.find(id, OkrWorkPerson.class);
			if (null == okrWorkPerson) {
				throw new Exception( "object is not exist {'id':'"+ id +"'}" );
			}else{		
				emc.beginTransaction( OkrWorkPerson.class );
				emc.remove( okrWorkPerson, CheckRemoveType.all );
				emc.commit();
			}			
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据用户姓名，查询用户有权限访问的所有具体工作ID列表
	 */
	public List<String> listDistinctWorkIdsByIdentity( String userIdentity, String centerId, List<String> statuses ) throws Exception {
		Business business = null;
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception( "userIdentity is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listDistinctWorkIdsByIdentity( userIdentity, centerId, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据用户姓名，查询用户有权限访问的所有具体工作ID列表
	 */
	public List<String> listDistinctWorkIdsWithMe( String userIdentity, String centerId ) throws Exception {
		Business business = null;
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception( "userIdentity is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listDistinctWorkIdsWithMe( userIdentity, centerId );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据用户姓名和处理身份，查询用户有权限访问的所有具体工作ID列表
	 * @param name
	 * @param processIdentity
	 * @return
	 * @throws Exception 
	 */
	public List<String> listDistinctWorkIdsByPerson(String name, String processIdentity ) throws Exception {
		Business business = null;
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null, system can not query any object." );
		}
		if( processIdentity == null || processIdentity.isEmpty() ){
			throw new Exception( "processIdentity is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listDistinctWorkIdsByPerson( name, processIdentity );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据用户姓名查询用户有权限访问的所有中心工作ID列表
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public List<String> listDistinctCenterIdsByPerson( String name, List<String> statuses ) throws Exception {
		Business business = null;
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listDistinctCenterIdsByPerson( name, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据用户身份查询用户有权限访问的所有中心工作ID列表
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public List<String> listDistinctCenterIdsByPersonIdentity( String userIdentity, String processIdentity, List<String> statuses ) throws Exception {
		Business business = null;
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception( "userIdentity is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listDistinctCenterIdsByPersonIdentity( userIdentity, processIdentity, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据组织名称查询用户有权限访问的所有中心工作ID列表
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public List<String> listDistinctCenterIdsByUnitName(String name, List<String> statuses ) throws Exception {
		Business business = null;
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listDistinctCenterIdsByUnitName( name, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据组织名称列表查询用户有权限访问的所有中心工作ID列表
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public List<String> listDistinctCenterIdsByUnitNames( List<String> names, List<String> statuses) throws Exception {
		Business business = null;
		if( names == null || names.isEmpty() ){
			throw new Exception( "names is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listDistinctCenterIdsByUnitNames( names, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据顶层组织名称查询用户有权限访问的所有中心工作ID列表
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public List<String> listDistinctCenterIdsByTopUnitName(String name, List<String> statuses) throws Exception {
		Business business = null;
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listDistinctCenterIdsByTopUnitName( name, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据顶层组织名称列表查询用户有权限访问的所有中心工作ID列表
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public List<String> listDistinctCenterIdsByTopUnitNames( List<String> names, List<String> statuses) throws Exception {
		Business business = null;
		if( names == null || names.isEmpty() ){
			throw new Exception( "names is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listDistinctCenterIdsByTopUnitNames( names, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据工作ID，获取工作的指定干系人
	 * @param id
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public List<String> getWorkPerson(String workId, String identity, List<String> statuses) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "id is null, system can not query responsibility for work." );
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().getWorkPerson( workId, identity, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据中心工作信息来维护中心工作干系人
	 * 为中心工作添加创建者，部署者，并且把两人都加入到中心工作观察者里
	 * @param okrCenterWorkInfo
	 * @throws Exception 
	 */
	public void saveCenterWorkPersonByCenterWork( OkrCenterWorkInfo okrCenterWorkInfo) throws Exception {
		OkrWorkPerson okrWorkPerson = null;
		if( okrCenterWorkInfo.getCreatorName() != null && !okrCenterWorkInfo.getCreatorName().isEmpty() ){
			okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, okrCenterWorkInfo.getCreatorName(), okrCenterWorkInfo.getCreatorIdentity(), okrCenterWorkInfo.getCreatorUnitName(), okrCenterWorkInfo.getCreatorTopUnitName(), "创建者", okrCenterWorkInfo.getCreateTime() );
			okrWorkPerson.setWorkProcessStatus( okrCenterWorkInfo.getProcessStatus() );
			saveCenterWorkPerson( okrWorkPerson );
			okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, okrCenterWorkInfo.getCreatorName(), okrCenterWorkInfo.getCreatorIdentity(), okrCenterWorkInfo.getCreatorUnitName(), okrCenterWorkInfo.getCreatorTopUnitName(), "观察者", okrCenterWorkInfo.getCreateTime() );
			okrWorkPerson.setWorkProcessStatus( okrCenterWorkInfo.getProcessStatus() );
			okrWorkPerson.setDiscription( "中心工作创建者" );
			saveCenterWorkPerson( okrWorkPerson );
		}
		if( okrCenterWorkInfo.getDeployerName() != null && !okrCenterWorkInfo.getDeployerName().isEmpty() ){
			okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, okrCenterWorkInfo.getDeployerName(), okrCenterWorkInfo.getDeployerIdentity(), okrCenterWorkInfo.getDeployerUnitName(), okrCenterWorkInfo.getDeployerTopUnitName(), "部署者", okrCenterWorkInfo.getCreateTime() );
			okrWorkPerson.setWorkProcessStatus( okrCenterWorkInfo.getProcessStatus() );
			saveCenterWorkPerson( okrWorkPerson );
			okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, okrCenterWorkInfo.getDeployerName(), okrCenterWorkInfo.getDeployerIdentity(), okrCenterWorkInfo.getDeployerUnitName(), okrCenterWorkInfo.getDeployerTopUnitName(), "观察者", okrCenterWorkInfo.getCreateTime() );
			okrWorkPerson.setWorkProcessStatus( okrCenterWorkInfo.getProcessStatus() );
			okrWorkPerson.setDiscription( "中心工作部署者" );
			saveCenterWorkPerson( okrWorkPerson );
		}
		
//		if( ListTools.isNotEmpty( okrCenterWorkInfo.getAuditLeaderIdentityList() )){
//			String userName = null, unitName = null, topUnitName = null;
//			for( String identity : okrCenterWorkInfo.getAuditLeaderIdentityList() ){
//				topUnitName = okrUserManagerService.getTopUnitNameByIdentity(identity);
//				unitName = okrUserManagerService.getUnitNameByIdentity(identity);
//				userName = okrUserManagerService.getPersonNameByIdentity (identity );
//				okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, userName, identity, unitName, topUnitName, "观察者", okrCenterWorkInfo.getCreateTime() );
//				okrWorkPerson.setWorkProcessStatus( okrCenterWorkInfo.getProcessStatus() );
//				okrWorkPerson.setDiscription( "中心工作阅知领导" );
//				saveCenterWorkPerson( okrWorkPerson );
//			}
//		}
		//为工作汇报审核领导添加观察权限
		if( ListTools.isNotEmpty( okrCenterWorkInfo.getReportAuditLeaderIdentityList() ) ){
			String userName = null, unitName = null, topUnitName = null;
			for( String identity : okrCenterWorkInfo.getReportAuditLeaderIdentityList() ){
				topUnitName = okrUserManagerService.getTopUnitNameByIdentity(identity);
				unitName = okrUserManagerService.getUnitNameByIdentity(identity);
				userName = okrUserManagerService.getPersonNameByIdentity (identity );
				okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, userName, identity, unitName, topUnitName, "观察者", okrCenterWorkInfo.getCreateTime() );
				okrWorkPerson.setWorkProcessStatus( okrCenterWorkInfo.getProcessStatus() );
				okrWorkPerson.setDiscription( "中心工作汇报审核领导" );
				saveCenterWorkPerson( okrWorkPerson );
			}
		}
	}
	
	public void addWorkPersonToList( List<OkrWorkPerson> okrWorkPersonList, OkrWorkPerson okrWorkPerson ){
		boolean exists = false;
		for( OkrWorkPerson _okrWorkPerson :  okrWorkPersonList){
			if( _okrWorkPerson.getWorkId() != null ){
				if( _okrWorkPerson.getWorkId().equals( okrWorkPerson.getWorkId())
					&& _okrWorkPerson.getEmployeeIdentity().equals( okrWorkPerson.getEmployeeIdentity() )
					&& _okrWorkPerson.getProcessIdentity().equals( okrWorkPerson.getProcessIdentity() ) 
				){
					exists = true;
				}
			}else{
				if( _okrWorkPerson.getCenterId().equals( okrWorkPerson.getCenterId())
					&& _okrWorkPerson.getEmployeeIdentity().equals( okrWorkPerson.getEmployeeIdentity() )
					&& _okrWorkPerson.getProcessIdentity().equals( okrWorkPerson.getProcessIdentity() ) 
				){
					exists = true;
				}
			}
		}
		if( !exists ){
			okrWorkPersonList.add( okrWorkPerson );
		}
	}
	
	/**
	 * 根据工作信息来维护工作干系人
	 * 为工作添加创建者，部署者，责任者，协助者，阅知者以及所有观察者
	 * @param okrWorkBaseInfo
	 * @throws Exception 
	 */
	public List<OkrWorkPerson> getWorkPersonListByWorkBaseInfoForWorkSave( OkrWorkBaseInfo okrWorkBaseInfo ) throws Exception {
		List<OkrWorkPerson> okrWorkPersonList = new ArrayList<OkrWorkPerson>();
		OkrWorkPerson okrWorkPerson = null;
		if( okrWorkBaseInfo.getCreatorName() != null && !okrWorkBaseInfo.getCreatorName().isEmpty() ){
			okrWorkPerson = createWorkPersonByWorkInfo( okrWorkBaseInfo, okrWorkBaseInfo.getCreatorIdentity(), "创建者" );
			addWorkPersonToList( okrWorkPersonList, okrWorkPerson );
			okrWorkPerson = createWorkPersonByWorkInfo( okrWorkBaseInfo, okrWorkBaseInfo.getCreatorIdentity(), "观察者" );
			addWorkPersonToList( okrWorkPersonList, okrWorkPerson );
		}
		if( okrWorkBaseInfo.getDeployerName() != null && !okrWorkBaseInfo.getDeployerName().isEmpty() ){
			okrWorkPerson = createWorkPersonByWorkInfo( okrWorkBaseInfo, okrWorkBaseInfo.getDeployerIdentity(), "部署者" );
			addWorkPersonToList( okrWorkPersonList, okrWorkPerson );
			if( !okrWorkBaseInfo.getCreatorName().equals( okrWorkBaseInfo.getDeployerName() ) ){
				okrWorkPerson = createWorkPersonByWorkInfo( okrWorkBaseInfo, okrWorkBaseInfo.getDeployerIdentity(), "观察者" );
				addWorkPersonToList( okrWorkPersonList, okrWorkPerson );
			}
		}
		return okrWorkPersonList;
	}
	
	public Boolean addWatcherForWork( String workId, List<String> watcherIdentities ) throws Exception {
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkPerson okrWorkPerson = null;
		List<String> ids = null;
		Business business = null;
		if( ListTools.isEmpty(watcherIdentities  )) {
			return true;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			okrWorkBaseInfo = emc.find( workId, OkrWorkBaseInfo.class );
			if( okrWorkBaseInfo == null ) {
				throw new Exception("OkrWorkBaseInfo {'id':'"+workId+"'} not match!" ) ;
			}
			emc.beginTransaction(OkrWorkPerson.class);
			for(String watcherIdentity : watcherIdentities) {
				okrWorkPerson = createWorkPersonByWorkInfo( okrWorkBaseInfo, watcherIdentity, "观察者" );
				ids = business.okrWorkPersonFactory().listByWorkAndIdentity(okrWorkPerson.getCenterId(),
						okrWorkPerson.getWorkId(), okrWorkPerson.getEmployeeIdentity(),
						okrWorkPerson.getProcessIdentity(), null );
				if( ListTools.isEmpty( ids )) {
					emc.persist(okrWorkPerson, CheckPersistType.all);
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return true;
	}
	
	
	/**
	 * 根据中心工作信息来维护工作干系人
	 * 为工作添加创建者，部署者以及所有观察者
	 * @param okrCenterWorkInfo
	 * @throws Exception 
	 */
	public List<OkrWorkPerson> getWorkPersonListByCenterWorkInfo( OkrCenterWorkInfo okrCenterWorkInfo ) throws Exception {
		List<OkrWorkPerson> okrWorkPersonList = new ArrayList<OkrWorkPerson>();
		OkrWorkPerson okrWorkPerson = null;
		if( okrCenterWorkInfo.getCreatorName() != null && !okrCenterWorkInfo.getCreatorName().isEmpty() ){
			okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, okrCenterWorkInfo.getCreatorName(), okrCenterWorkInfo.getCreatorIdentity(), okrCenterWorkInfo.getCreatorUnitName(), okrCenterWorkInfo.getCreatorTopUnitName(), "创建者", okrCenterWorkInfo.getCreateTime() );
			okrWorkPerson.setTopUnitName( okrCenterWorkInfo.getCreatorTopUnitName() );
			okrWorkPerson.setUnitName( okrCenterWorkInfo.getCreatorUnitName() );
			addWorkPersonToList( okrWorkPersonList, okrWorkPerson );
			okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, okrCenterWorkInfo.getCreatorName(), okrCenterWorkInfo.getCreatorIdentity(), okrCenterWorkInfo.getCreatorUnitName(), okrCenterWorkInfo.getCreatorTopUnitName(), "观察者", okrCenterWorkInfo.getCreateTime() );
			okrWorkPerson.setTopUnitName( okrCenterWorkInfo.getCreatorTopUnitName() );
			okrWorkPerson.setUnitName( okrCenterWorkInfo.getCreatorUnitName() );
			addWorkPersonToList( okrWorkPersonList, okrWorkPerson );
		}
		if( okrCenterWorkInfo.getDeployerName() != null && !okrCenterWorkInfo.getDeployerName().isEmpty() ){
			okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, okrCenterWorkInfo.getDeployerName(), okrCenterWorkInfo.getDeployerIdentity(), okrCenterWorkInfo.getDeployerUnitName(), okrCenterWorkInfo.getDeployerTopUnitName(), "部署者", okrCenterWorkInfo.getCreateTime() );
			okrWorkPerson.setTopUnitName( okrCenterWorkInfo.getDeployerTopUnitName() );
			okrWorkPerson.setUnitName( okrCenterWorkInfo.getDeployerUnitName() );
			addWorkPersonToList( okrWorkPersonList, okrWorkPerson );
			if( !okrCenterWorkInfo.getCreatorName().equals( okrCenterWorkInfo.getDeployerName() ) ){
				okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, okrCenterWorkInfo.getDeployerName(), okrCenterWorkInfo.getDeployerIdentity(), okrCenterWorkInfo.getDeployerUnitName(), okrCenterWorkInfo.getDeployerTopUnitName(), "观察者", okrCenterWorkInfo.getCreateTime() );
				okrWorkPerson.setTopUnitName( okrCenterWorkInfo.getDeployerTopUnitName() );
				okrWorkPerson.setUnitName( okrCenterWorkInfo.getDeployerUnitName() );
				addWorkPersonToList( okrWorkPersonList, okrWorkPerson );
			}
		}
		if( ListTools.isNotEmpty(okrCenterWorkInfo.getReportAuditLeaderNameList()) ){
			for( int i=0; i< okrCenterWorkInfo.getReportAuditLeaderNameList().size();i++) {
				String identity = null;
				String name = null;
				String unitName = null;
				String topUnitName = null;
				if(  okrCenterWorkInfo.getReportAuditLeaderNameList().size() > i ) {
					identity = okrCenterWorkInfo.getReportAuditLeaderNameList().get(i);
				}
				if(  okrCenterWorkInfo.getReportAuditLeaderIdentityList().size() > i ) {
					name = okrCenterWorkInfo.getReportAuditLeaderIdentityList().get(i);
				}
				if(  okrCenterWorkInfo.getReportAuditLeaderUnitNameList().size() > i ) {
					unitName = okrCenterWorkInfo.getReportAuditLeaderUnitNameList().get(i);
				}
				if(  okrCenterWorkInfo.getReportAuditLeaderTopUnitNameList().size() > i ) {
					topUnitName = okrCenterWorkInfo.getReportAuditLeaderTopUnitNameList().get(i);
				}
				okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, name, identity, unitName, topUnitName, "审核者", okrCenterWorkInfo.getCreateTime() );
				addWorkPersonToList( okrWorkPersonList, okrWorkPerson );
				if( !okrCenterWorkInfo.getCreatorName().equals( name ) ){
					okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, name, identity, unitName, topUnitName, "观察者", okrCenterWorkInfo.getCreateTime() );
					addWorkPersonToList( okrWorkPersonList, okrWorkPerson );
				}
			}
		}

		String userName = null, unitName = null, topUnitName = null;
		if( ListTools.isNotEmpty( okrCenterWorkInfo.getDefaultLeaderIdentityList() ) ){
			for( String identityName : okrCenterWorkInfo.getDefaultLeaderIdentityList() ){
				if( okrUserManagerService.getPersonNameByIdentity( identityName ) == null ){
					throw new Exception( "person not exsits, identity:" + identityName );
				}
				userName = okrUserManagerService.getPersonNameByIdentity(identityName);
				unitName = okrUserManagerService.getUnitNameByIdentity(identityName);
				topUnitName = okrUserManagerService.getTopUnitNameByIdentity(identityName);
				okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, userName, identityName, unitName, topUnitName, "阅知者", okrCenterWorkInfo.getCreateTime() );
				addWorkPersonToList( okrWorkPersonList, okrWorkPerson );
				okrWorkPerson = createWorkPersonByCenterInfo( okrCenterWorkInfo, userName, identityName, unitName, topUnitName, "观察者", okrCenterWorkInfo.getCreateTime() );
				addWorkPersonToList( okrWorkPersonList, okrWorkPerson );
			}
		}
		return okrWorkPersonList;
	}

	/**
	 * 根据中心工作ID，姓名，身份来创建一个干系人信息
	 * @param id
	 * @param creatorName
	 * @param string
	 * @throws Exception 
	 */
	public OkrWorkPerson createWorkPersonByCenterInfo_test111( String centerId, String employeeIdentityName, String processIdentity, Date createTime) throws Exception {
		if( centerId  == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null, return null!" );
		}
		if( employeeIdentityName  == null || employeeIdentityName.isEmpty() ){
			throw new Exception( "employeeIdentityName is null, return null!" );
		}
		if( processIdentity  == null || processIdentity.isEmpty() ){
			throw new Exception( "processIdentity is null, return null!" );
		}
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		String userName = null, unitName = null, topUnitName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			okrCenterWorkInfo = emc.find( centerId, OkrCenterWorkInfo.class );
		}catch( Exception e ){
			throw e;
		}
		if( okrCenterWorkInfo == null ){
			throw new Exception( "okrCenterWorkInfo{'id':'"+centerId+"'} is not exists." );
		}else{
			userName = okrUserManagerService.getPersonNameByIdentity(employeeIdentityName);
			unitName = okrUserManagerService.getUnitNameByIdentity(employeeIdentityName);
			topUnitName = okrUserManagerService.getTopUnitNameByIdentity(employeeIdentityName);
			return createWorkPersonByCenterInfo( okrCenterWorkInfo, userName, employeeIdentityName, unitName, topUnitName, processIdentity, createTime );
		}
	}
	
	/**
	 * 根据中心工作ID，姓名，身份来创建一个干系人信息
	 * @param id
	 * @param creatorName
	 * @param string
	 * @throws Exception 
	 */
	public OkrWorkPerson createWorkPersonByCenterInfo( OkrCenterWorkInfo okrCenterWorkInfo, String employeeName, String employeeIdentity, String employeeUnitName, String empoyeeTopUnit, String processIdentity, Date createTime) throws Exception {
		String deployYear = null, deployMonth = null;
		Date now = new Date();
		if( okrCenterWorkInfo == null ){
			throw new Exception( " okrCenterWorkInfo id is null, can not create new OkrWorkPerson!" );
		}
		if( employeeName == null ){
			throw new Exception( " employeeName is null, can not create new OkrWorkPerson!" );
		}
		if( employeeIdentity == null ){
			throw new Exception( " employeeIdentity is null, can not create new OkrWorkPerson!" );
		}
		if( employeeUnitName == null ){
			throw new Exception( " employeeUnitName is null, can not create new OkrWorkPerson!" );
		}
		if( empoyeeTopUnit == null ){
			throw new Exception( " empoyeeTopUnit is null, can not create new OkrWorkPerson!" );
		}
		if( processIdentity == null ){
			throw new Exception( " processIdentity is null, can not create new OkrWorkPerson!" );
		}
		if( createTime == null ){
			createTime = new Date();
		}
		try{
			deployYear = dateOperation.getYear( createTime );
			deployMonth = dateOperation.getMonth( createTime );
			if( "0".equals( deployYear ) ){
				deployYear = dateOperation.getYear( now );
				deployMonth = dateOperation.getMonth( now );
			}
		}catch(Exception e){
			logger.warn( "system get year and month from createtime got an exception: " );
			return null;
		}
		
		//根据以上信息创建一个新的工作干系人信息对象
		OkrWorkPerson okrWorkPerson = new OkrWorkPerson();
		okrWorkPerson.setCenterId( okrCenterWorkInfo.getId() );
		okrWorkPerson.setCenterTitle( okrCenterWorkInfo.getTitle() );
		okrWorkPerson.setEmployeeName(employeeName);
		okrWorkPerson.setEmployeeIdentity(employeeIdentity);
		okrWorkPerson.setUnitName( employeeUnitName );
		okrWorkPerson.setTopUnitName(empoyeeTopUnit);
		okrWorkPerson.setDeployMonth(deployMonth);
		okrWorkPerson.setDeployYear(deployYear);
		
		okrWorkPerson.setDeployDateStr( okrCenterWorkInfo.getDeployDateStr() );
		if( okrCenterWorkInfo.getCreateTime() != null ){
			okrWorkPerson.setWorkCreateDateStr( dateOperation.getDateStringFromDate( okrCenterWorkInfo.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
		}else{
			okrWorkPerson.setWorkCreateDateStr( dateOperation.getDateStringFromDate( new Date(), "yyyy-MM-dd HH:mm:ss"));
		}
		okrWorkPerson.setCompleteDateLimitStr( okrCenterWorkInfo.getDefaultCompleteDateLimitStr() );
		okrWorkPerson.setCompleteDateLimit( okrCenterWorkInfo.getDefaultCompleteDateLimit() );
		okrWorkPerson.setRecordType( "中心工作" );
		
		okrWorkPerson.setProcessIdentity( processIdentity );
		okrWorkPerson.setIsDelegateTarget( false );
		okrWorkPerson.setIsOverTime( false );
		okrWorkPerson.setStatus( "正常" );
		okrWorkPerson.setWorkId( null );
		okrWorkPerson.setWorkTitle( null );
		okrWorkPerson.setWorkType( okrCenterWorkInfo.getDefaultWorkType() );
		okrWorkPerson.setWorkLevel( okrCenterWorkInfo.getDefaultWorkLevel() );
		okrWorkPerson.setViewTime( null );
		okrWorkPerson.setIsCompleted( false );
		okrWorkPerson.setParentWorkId( null );
		okrWorkPerson.setWorkDateTimeType( null );
		okrWorkPerson.setWorkProcessStatus( null);
		return okrWorkPerson;	
	}
	
	/**
	 * 根据中心工作ID，工作ID，姓名，身份来创建一个干系人信息
	 * @param centerId
	 * @param workId
	 * @param creatorName
	 * @param string
	 * @throws Exception 
	 */
	public OkrWorkPerson createWorkPersonByWorkInfo( OkrWorkBaseInfo okrWorkBaseInfo, String employeeIdentity, String processIdentity ) throws Exception {
		String deployYear = null, deployMonth = null;
		if( okrWorkBaseInfo == null ){
			throw new Exception( " okrWorkBaseInfo is null, can not create new OkrWorkPerson!" );
		}
		if( employeeIdentity == null ){
			throw new Exception( " employeeIdentity is null, can not create new OkrWorkPerson!" );
		}
		if( processIdentity == null ){
			throw new Exception( " processIdentity is null, can not create new OkrWorkPerson!" );
		}
		
		Date createTime = new Date();
		String personName  = null;
		personName = okrUserManagerService.getPersonNameByIdentity( employeeIdentity );
		if( personName != null ){
			String employeeUnitName = okrUserManagerService.getUnitNameByIdentity( employeeIdentity );
			String empoyeeTopUnit = okrUserManagerService.getTopUnitNameByIdentity( employeeIdentity );
			try{
				deployYear = dateOperation.getYear( createTime );
				deployMonth = dateOperation.getMonth( createTime );
			}catch(Exception e){
				logger.warn( "system get year and month from createtime got an exception: " );
				return null;
			}
			//根据以上信息创建一个新的工作干系人信息对象
			OkrWorkPerson okrWorkPerson = new OkrWorkPerson();
			okrWorkPerson.setCenterId( okrWorkBaseInfo.getCenterId() );
			okrWorkPerson.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );
			okrWorkPerson.setEmployeeIdentity( employeeIdentity );
			okrWorkPerson.setEmployeeName( personName );
			okrWorkPerson.setUnitName( employeeUnitName );
			okrWorkPerson.setTopUnitName( empoyeeTopUnit );
			okrWorkPerson.setDeployMonth( deployMonth );
			okrWorkPerson.setDeployYear( deployYear );
			
			okrWorkPerson.setDeployDateStr( okrWorkBaseInfo.getDeployDateStr() );
			if( okrWorkBaseInfo.getCreateTime() != null ){
				okrWorkPerson.setWorkCreateDateStr( dateOperation.getDateStringFromDate( okrWorkBaseInfo.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
			}else{
				okrWorkPerson.setWorkCreateDateStr( dateOperation.getDateStringFromDate( new Date(), "yyyy-MM-dd HH:mm:ss"));
			}
			okrWorkPerson.setCompleteDateLimitStr( okrWorkBaseInfo.getCompleteDateLimitStr() );
			okrWorkPerson.setCompleteDateLimit( okrWorkBaseInfo.getCompleteDateLimit() );
			okrWorkPerson.setRecordType( "具体工作" );
			
			okrWorkPerson.setProcessIdentity( processIdentity );
			okrWorkPerson.setIsOverTime( okrWorkBaseInfo.getIsOverTime() );
			okrWorkPerson.setWorkId( okrWorkBaseInfo.getId() );
			okrWorkPerson.setWorkTitle( okrWorkBaseInfo.getTitle() );
			okrWorkPerson.setWorkType( okrWorkBaseInfo.getWorkType() );
			okrWorkPerson.setWorkLevel( okrWorkBaseInfo.getWorkLevel() );
			okrWorkPerson.setIsCompleted( okrWorkBaseInfo.getIsCompleted() );
			okrWorkPerson.setParentWorkId( okrWorkBaseInfo.getParentWorkId() );
			okrWorkPerson.setWorkDateTimeType( okrWorkBaseInfo.getWorkDateTimeType() );
			okrWorkPerson.setWorkProcessStatus( okrWorkBaseInfo.getWorkProcessStatus() );
			okrWorkPerson.setViewTime( null );
			okrWorkPerson.setIsDelegateTarget( false );
			okrWorkPerson.setStatus( "正常" );
			return okrWorkPerson;	
		}else{
			throw new Exception( "user not exsits: " + employeeIdentity );
		}
	}

	/**
	 * 根据指定的IDS获取对象列表
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	public List<OkrWorkPerson> listByIds( List<String> ids ) throws Exception {
		Business business = null;
		if( ids == null || ids.size() == 0 ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().list( ids );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据中心工作ID，获取该中心工作信息（不包括下级工作信息）所有的干系人信息
	 * 中心工作信息的干系人信息，workId为空
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdsForCenterWorkByCenterId( String centerId, List<String> statuses ) throws Exception {
		Business business = null;
		if( centerId == null || centerId.isEmpty() ){
			throw new Exception( "centerId is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listIdsForCenterWorkByCenterId( centerId, null, null, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据工作ID，获取该工作信息（不包括下级工作信息）所有的干系人信息
	 * 中心工作信息的干系人信息，workId为空
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<String> listIdsForWork( String workId, List<String> statuses ) throws Exception {
		Business business = null;
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listByWorkId( workId, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listIdsByWorkAndUserIdentity( String workId, String userIdentity, List<String> statuses ) throws Exception {
		Business business = null;
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null, system can not query any object." );
		}
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception( "userIdentity is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listByWorkIdAndUserIdentity( workId, userIdentity, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listDistinctIdentity() throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listDistinctIdentity();
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listCenterWorkIdsByWorkType( List<String> workTypeName, String loginIdentity, String processIdentity ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listCenterWorkIdsByWorkType( workTypeName, loginIdentity, processIdentity );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByWorkAndIdentity( String centerId, String workId, String employeeIdentity, String processIdentity, List<String> statuses) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listByWorkAndIdentity( centerId, workId, employeeIdentity, processIdentity, statuses);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listDistinctWorkIdsByWorkAndIdentity( String centerId, String workId, String employeeIdentity, String processIdentity, List<String> statuses) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listDistinctWorkIdsByWorkAndIdentity( centerId, workId, employeeIdentity, processIdentity, statuses);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<OkrWorkPerson> list(List<String> ids) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().list(ids);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByAuthorizeRecordIds(List<String> authorizeRecordIds, List<String> statuses ) throws Exception {
		Business business = null;
		if( authorizeRecordIds == null || authorizeRecordIds.isEmpty() ){
			throw new Exception( "authorizeRecordIds is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listByAuthorizeRecordIds( authorizeRecordIds, statuses );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 
	 * @param centerId
	 * @param identity
	 * @param processIdentity
	 * @param notInCenterIds 排除的中心工作ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listDistinctWorkIdsByPersonIndentity( String centerId, String identity, String processIdentity, List<String> notInCenterIds) throws Exception {
		Business business = null;
		if( identity == null || identity.isEmpty() ){
			throw new Exception( "identity is null, system can not query any object." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listDistinctWorkIdsByPersonIndentity( centerId, identity, processIdentity, notInCenterIds );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long getWorkTotalByCenterId( String identity, List<String> status, String processIdentity) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().getWorkTotalByCenterId( identity, status, processIdentity );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long getProcessingWorkCountByCenterId(String identity, List<String> status, String processIdentity) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().getProcessingWorkCountByCenterId( identity, status, processIdentity );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long getCompletedWorkCountByCenterId(String identity, List<String> status, String processIdentity) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().getCompletedWorkCountByCenterId( identity, status, processIdentity );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long getOvertimeWorkCountByCenterId(String identity, List<String> status, String processIdentity) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().getOvertimeWorkCountByCenterId( identity, status, processIdentity );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Long getOvertimenessWorkCountByCenterId(String identity, List<String> status, String processIdentity) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().getOvertimenessWorkCountByCenterId( identity, status, processIdentity );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long getDraftWorkCountByCenterId(String identity, List<String> status, String processIdentity) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().getDraftWorkCountByCenterId( identity, status, processIdentity );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public OkrWorkPerson createCenterWorkPersonByWorkPersonInfo( OkrWorkPerson workPerson, String processIdentity, Date createTime ) throws Exception {
		if( workPerson == null ){
			return null;
		}
		String deployYear = null, deployMonth = null;
		Date now = new Date();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			okrCenterWorkInfo = business.okrCenterWorkInfoFactory().get( workPerson.getCenterId() );
			if( okrCenterWorkInfo == null ){
				throw new Exception("center work is not exists.id:" + workPerson.getCenterId() );
			}
		} catch ( Exception e ) {
			throw e;
		}
		
		try{
			deployYear = dateOperation.getYear( createTime );
			deployMonth = dateOperation.getMonth( createTime );
			if( "0".equals( deployYear ) ){
				deployYear = dateOperation.getYear( now );
				deployMonth = dateOperation.getMonth( now );
			}
		}catch(Exception e){
			logger.warn( "system get year and month from createtime got an exception: " );
			return null;
		}
		
		//根据以上信息创建一个新的工作干系人信息对象
		OkrWorkPerson okrWorkPerson = new OkrWorkPerson();
		okrWorkPerson.setCenterId( workPerson.getCenterId() );
		okrWorkPerson.setCenterTitle( workPerson.getCenterTitle() );
		okrWorkPerson.setEmployeeName( workPerson.getEmployeeName() );
		okrWorkPerson.setEmployeeIdentity( workPerson.getEmployeeIdentity() );
		okrWorkPerson.setUnitName( workPerson.getUnitName() );
		okrWorkPerson.setTopUnitName( workPerson.getTopUnitName() );
		okrWorkPerson.setDeployMonth( deployMonth );
		okrWorkPerson.setDeployYear( deployYear );
		
		okrWorkPerson.setDeployDateStr( okrCenterWorkInfo.getDeployDateStr() );
		if( okrCenterWorkInfo.getCreateTime() != null ){
			okrWorkPerson.setWorkCreateDateStr( dateOperation.getDateStringFromDate( okrCenterWorkInfo.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
		}else{
			okrWorkPerson.setWorkCreateDateStr( dateOperation.getDateStringFromDate( new Date(), "yyyy-MM-dd HH:mm:ss"));
		}
		okrWorkPerson.setCompleteDateLimitStr( okrCenterWorkInfo.getDefaultCompleteDateLimitStr() );
		okrWorkPerson.setCompleteDateLimit( okrCenterWorkInfo.getDefaultCompleteDateLimit() );
		okrWorkPerson.setRecordType( "中心工作" );
		
		if( processIdentity != null && !processIdentity.isEmpty() ){
			okrWorkPerson.setProcessIdentity( processIdentity );
		}else{
			okrWorkPerson.setProcessIdentity( workPerson.getProcessIdentity() );
		}
		okrWorkPerson.setIsDelegateTarget( false );
		okrWorkPerson.setIsOverTime( false );
		okrWorkPerson.setStatus( "正常" );
		okrWorkPerson.setWorkId( null );
		okrWorkPerson.setWorkTitle( null );
		okrWorkPerson.setWorkType( okrCenterWorkInfo.getDefaultWorkType() );
		okrWorkPerson.setWorkLevel( okrCenterWorkInfo.getDefaultWorkLevel() );
		okrWorkPerson.setViewTime( null );
		okrWorkPerson.setIsCompleted( false );
		okrWorkPerson.setParentWorkId( null );
		okrWorkPerson.setWorkDateTimeType( null );
		okrWorkPerson.setWorkProcessStatus( "执行中" );
		return okrWorkPerson;	
	}

	public List<String> listWorkPersonIdentitiesWithWorkId(String workId) throws Exception {
		Business business = null;	
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().listAllDistinctIdentityWithWorkId(workId);
		} catch ( Exception e ) {
			throw e;
		}
	}
}