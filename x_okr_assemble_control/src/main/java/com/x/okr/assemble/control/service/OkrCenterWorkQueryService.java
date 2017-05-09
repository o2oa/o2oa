package com.x.okr.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.Business;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapInFilterCenterWorkInfo;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkPerson;

public class OkrCenterWorkQueryService {
	
	private Logger logger = LoggerFactory.getLogger( OkrCenterWorkQueryService.class );
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	
	/**
	 * 根据传入的ID从数据库查询OkrCenterWorkInfo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrCenterWorkInfo get( String id ) throws Exception {
		if( id  == null || id.isEmpty() ){
			throw new Exception( "id is null, return null!" );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
			return emc.find( id, OkrCenterWorkInfo.class );
		}catch( Exception e ){
			throw e;
		}
	}
	
	/**
	 * 查询下一页的信息数据，直接调用Factory里的方法
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrCenterWorkInfo> listNextWithFilter( String id, Integer count, WrapInFilterCenterWorkInfo wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if ( !StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrCenterWorkInfo.class ), "sequence" );
				}
			}
			return business.okrCenterWorkInfoFactory().listNextWithFilter(id, count, sequence, wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 查询上一页的信息数据，直接调用Factory里的方法
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrCenterWorkInfo> listPrevWithFilter( String id, Integer count, WrapInFilterCenterWorkInfo wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty( emc.find( id, OkrCenterWorkInfo.class ), "sequence" );
				}
			}
			return business.okrCenterWorkInfoFactory().listPrevWithFilter(id, count, sequence, wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 查询符合条件的数据总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public Long getCountWithFilter( WrapInFilterCenterWorkInfo wrapIn ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrCenterWorkInfoFactory().getCountWithFilter(wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	
	/**
	 * 查询下一页的信息数据，直接调用Factory里的方法
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrCenterWorkInfo> listCenterNextWithFilter( String id, Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = new ArrayList<OkrCenterWorkInfo>();
		List<OkrWorkPerson> okrWorkPersonList = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		wrapIn.setInfoType( "CENTERWORK" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			//要根据ID和相应的条件在workPerson里查出指定的记录的sequence, 而不是中心工作表里的数据
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				okrWorkPersonList = business.okrWorkPersonFactory().listCenterWorkPerson( id, wrapIn );
				if( okrWorkPersonList != null && !okrWorkPersonList.isEmpty() ){
					sequence = okrWorkPersonList.get( 0 ).getSequence();
				}
			}
			okrWorkPersonList = business.okrWorkPersonFactory().listNextWithFilter( id, count, sequence, wrapIn );
			if( okrWorkPersonList != null && !okrWorkPersonList.isEmpty() ){
				for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
					okrCenterWorkInfo = emc.find( okrWorkPerson.getCenterId(), OkrCenterWorkInfo.class );
					if( okrCenterWorkInfo != null && !okrCenterWorkInfoList.contains( okrCenterWorkInfo )){
						okrCenterWorkInfoList.add( okrCenterWorkInfo );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return okrCenterWorkInfoList;
	}
	
	/**
	 * 查询上一页的信息数据，直接调用Factory里的方法
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrCenterWorkInfo> listCenterPrevWithFilter( String id, Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		Object sequence = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = new ArrayList<OkrCenterWorkInfo>();
		List<OkrWorkPerson> okrWorkPersonList = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		wrapIn.setInfoType( "CENTERWORK" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			//要根据ID和相应的条件在workPerson里查出指定的记录的sequence, 而不是中心工作表里的数据
			if( id != null && !"(0)".equals(id) && id.trim().length() > 20 ){
				okrWorkPersonList = business.okrWorkPersonFactory().listCenterWorkPerson( id, wrapIn );
				if( okrWorkPersonList != null && !okrWorkPersonList.isEmpty() ){
					sequence = okrWorkPersonList.get( 0 ).getSequence();
				}
			}
			okrWorkPersonList = business.okrWorkPersonFactory().listPrevWithFilter(id, count, sequence, wrapIn);
			if( okrWorkPersonList != null && !okrWorkPersonList.isEmpty() ){
				for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
					okrCenterWorkInfo = emc.find( okrWorkPerson.getCenterId(), OkrCenterWorkInfo.class );
					if( okrCenterWorkInfo != null && !okrCenterWorkInfoList.contains( okrCenterWorkInfo )){
						okrCenterWorkInfoList.add( okrCenterWorkInfo );
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return okrCenterWorkInfoList;
	}
	
	/**
	 * 查询符合条件的数据总数
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public Long getCenterCountWithFilter( com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) throws Exception {
		Business business = null;
		if( wrapIn == null ){
			throw new Exception( "wrapIn is null!" );
		}
		wrapIn.setInfoType( "CENTERWORK" );
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkPersonFactory().getCountWithFilter(wrapIn);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listAllProcessingCenterWorkIds( List<String> processStatus, List<String> status ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrCenterWorkInfoFactory().listAllProcessingCenterWorkIds( processStatus, status );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void countWorkWithCenterId(String centerId, List<String> status) {
		Long workTotal = 0L;
		Long processingWorkCount = 0L;
		Long completedWorkCount = 0L;
		Long overtimeWorkCount = 0L;
		Long draftWorkCount = 0L;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		try{
			workTotal = okrWorkBaseInfoService.getWorkTotalByCenterId( centerId, status );
			processingWorkCount = okrWorkBaseInfoService.getProcessingWorkCountByCenterId( centerId, status );
			completedWorkCount = okrWorkBaseInfoService.getCompletedWorkCountByCenterId( centerId, status );
			overtimeWorkCount = okrWorkBaseInfoService.getOvertimeWorkCountByCenterId( centerId, status );
			draftWorkCount = okrWorkBaseInfoService.getDraftWorkCountByCenterId( centerId, status );
			
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrCenterWorkInfo = emc.find( centerId, OkrCenterWorkInfo.class );
				if( okrCenterWorkInfo != null ){
					emc.beginTransaction(OkrCenterWorkInfo.class );
					okrCenterWorkInfo.setWorkTotal(workTotal);
					okrCenterWorkInfo.setProcessingWorkCount(processingWorkCount);
					okrCenterWorkInfo.setCompletedWorkCount(completedWorkCount);
					okrCenterWorkInfo.setOvertimeWorkCount(overtimeWorkCount);
					okrCenterWorkInfo.setDraftWorkCount(draftWorkCount);				
					if( workTotal > 0 && workTotal == completedWorkCount ){
						okrCenterWorkInfo.setProcessStatus("已完成");
					}	
					emc.check( okrCenterWorkInfo, CheckPersistType.all );
					emc.commit();
				}else{
					throw new Exception("okrCenterWorkInfo{'id':'"+centerId+"'} not exists.");
				}
			} catch ( Exception e ) {
				throw e;
			}		
		}catch(Exception e){
			logger.warn( "system count work info by center info got an exception." );
			logger.error( e );
		}
	}

	public List<OkrCenterWorkInfo> listAllCenterWorks( String status ) throws Exception {
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrCenterWorkInfoFactory().listAllCenterWorks( status );
		} catch ( Exception e ) {
			throw e;
		}
	}
}