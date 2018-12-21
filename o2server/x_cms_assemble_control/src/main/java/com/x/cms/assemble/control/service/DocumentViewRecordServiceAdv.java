package com.x.cms.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentViewRecord;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.tools.DateOperation;

/**
 * 对文档访问记录信息进行管理的服务类（高级）
 * 高级服务器可以利用Service完成事务控制
 * 
 * @author O2LEE
 */
public class DocumentViewRecordServiceAdv {
	
	private UserManagerService userManagerService = new UserManagerService();
	private DocumentViewRecordService documentViewRecordService = new DocumentViewRecordService();
	
	public List<DocumentViewRecord> list( List<String> ids ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByDocument( String docId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.listByDocument( emc, docId );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listByPerson( String personName ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.listByPerson( emc, personName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public void deleteByDocument( String docId ) throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			documentViewRecordService.deleteByDocument( emc, docId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * TODO:记录访问日志，一个用户一篇文档只保留一条记录，更新访问次数和最后访问时间
	 * 
	 * @param document
	 * @param personName
	 * @throws Exception
	 */
	public void addViewRecord( String docId, String personName ) throws Exception {
		DocumentViewRecord documentViewRecord = null;
		Document document = null;
		Business business = null;
		List<String> ids = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			emc.beginTransaction( DocumentViewRecord.class );
			ids = business.documentViewRecordFactory().listByDocAndPerson( docId, personName );
			if( ids != null && !ids.isEmpty() ){
				int i = 0;
				for( String id : ids ){
					i++;
					documentViewRecord = emc.find( id, DocumentViewRecord.class );
					if( i == 1 ){
						documentViewRecord.setLastViewTime( new Date() );
						if( documentViewRecord.getViewCount() == null || documentViewRecord.getViewCount() == 0 ){
							documentViewRecord.setViewCount( 1 );
						}
						documentViewRecord.setViewCount( documentViewRecord.getViewCount() + 1 );
						emc.check( documentViewRecord, CheckPersistType.all ); 
					}else{
						//删除多余的日志数据
						emc.remove( documentViewRecord, CheckRemoveType.all );
					}
				}
			}else{
				document = emc.find( docId, Document.class );
				if( document != null ){
					documentViewRecord = new DocumentViewRecord();
					documentViewRecord.setAppId( document.getAppId() );
					documentViewRecord.setCategoryId( document.getCategoryId() );
					documentViewRecord.setDocumentId( document.getId() );
					documentViewRecord.setViewerName( personName );
					documentViewRecord.setAppName( document.getAppName() );
					documentViewRecord.setCategoryName( document.getCategoryName() );
					documentViewRecord.setTitle( document.getTitle() );
					documentViewRecord.setLastViewTime( new Date() );
					documentViewRecord.setViewCount( 1 );
					documentViewRecord.setViewerTopUnitName( userManagerService.getTopUnitNameWithPerson( personName ));
					documentViewRecord.setViewerUnitName( userManagerService.getUnitNameWithPerson( personName ));
					emc.persist( documentViewRecord, CheckPersistType.all ); 
				}else{
					throw new Exception("document is not exits, system can not save view record.id:" + docId );
				}
			}
			emc.commit();
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<DocumentViewRecord> listNextWithDocIds( String id, String docId, Integer count, String order) throws Exception {
		if( docId == null ){
			throw new Exception("docId is null!");
		}
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.listNextWithDocIds( emc, id, docId, count, order );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public Long countWithDocIds( String docId ) throws Exception {
		if( docId == null ){
			throw new Exception("docId is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return documentViewRecordService.countWithDocIds( emc, docId );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 对文档访问日志信息进行清理
	 * @param stay_yeanumr_viewRecord 日志保留年份
	 * @param stay_count_viewRecord 日志保留条目数
	 * @throws Exception 
	 */
	public void clean( Integer stay_yeanumr_viewRecord, Integer stay_count_viewRecord ) throws Exception {
		//先按时间清理
		cleanWithStayYearNumber( stay_yeanumr_viewRecord );
		cleanWithMaxCount( stay_count_viewRecord );
	}

	/**
	 * 按最大保留条目数进行清理
	 * @param stay_count_operationLog
	 * @throws Exception 
	 */
	private void cleanWithMaxCount(Integer stay_count_viewRecord ) throws Exception {
		Business business = null;
		List<String> ids = null;
		String last_id = null;
		Log log = null;
		Long total = 0L;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			total = business.getLogFactory().getTotal();
			if( total > stay_count_viewRecord ) {
				//将记录条目数减到stay_count_operationLog的60%
				ids = business.getLogFactory().getRecordIdsWithCount( Integer.parseInt(( stay_count_viewRecord * 0.6 )+""));
			}
			if( ids != null && !ids.isEmpty() ) {
				//取最后一个，以确定最早可以保留下来的创建时间
				last_id = ids.get( (ids.size() -1) );
				log = emc.find( last_id, Log.class );
				//根据时间进行清理，说明这个时间以前的数据都不能保留了
				cleanWithDate( log.getCreateTime() );
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 按保留年份对日志进行清理
	 * @param stay_yearnum_operationLog
	 * @throws Exception 
	 */
	private void cleanWithStayYearNumber( Integer stay_yeanumr_viewRecord ) throws Exception {
		if( stay_yeanumr_viewRecord == null ) {
			throw new Exception("stay_yeanumr_viewRecord is null!");
		}
		//1、计算三年期限的时间点，三年前的1月1日, 最好再加一年
		DateOperation dateOperation = new DateOperation();
		Integer year = dateOperation.getYearNumber( new Date() );
		Date limitDate = dateOperation.getDateFromString( ( year-4 ) + "-01-01 00:00:00");
		cleanWithDate( limitDate );
	}
	
	
	/**
	 * 按保留年份对日志进行清理
	 * @param overTime
	 * @throws Exception 
	 */
	private void cleanWithDate( Date overTime ) throws Exception {
		if( overTime == null ) {
			throw new Exception("overTime is null!");
		}
		Business business = null;
		List<String> ids = null;
		DocumentViewRecord log = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			ids = business.documentViewRecordFactory().listOverTime( overTime );
			if( ids != null && !ids.isEmpty() ) {
				emc.beginTransaction( DocumentViewRecord.class );
				for( String id : ids ) {
					log = emc.find( id, DocumentViewRecord.class );
					if( log != null ) {
						emc.remove( log, CheckRemoveType.all );
					}
				}
				emc.commit();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
}
