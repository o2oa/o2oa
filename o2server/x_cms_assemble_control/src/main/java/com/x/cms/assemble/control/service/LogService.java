package com.x.cms.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Log;
import com.x.cms.core.express.tools.DateOperation;


public class LogService {
	
	/**
	 * 保存日志信息
	 * @param person  处理人帐号
 	 * @param description   日志描述
	 * @param appId   应用ID
	 * @param categoryId  分类ID
	 * @param documentId  文档ID
	 * @param fileId      文件附件ID
	 * @param operationLevel  操作级别：应用|文件|分类|文档
	 * @param operationType  操作类别：新增|更新|删除
	 * @return
	 * @throws Exception 
	 */
	public boolean log( EntityManagerContainer emc, String person, String description, String appId, String categoryId, String documentId, String fileId, String operationLevel, String operationType ) throws Exception {
		if( emc == null ){
			emc = EntityManagerContainerFactory.instance().create();
		}
		String operatorUid = person;
		Log log = new Log();
		log.setDescription(description);
		log.setAppId(appId);
		log.setCategoryId(categoryId);
		log.setDocumentId(documentId);
		log.setFileId(fileId);
		log.setOperatorName(operatorUid);
		log.setOperatorUid(operatorUid);
		log.setOperationType(operationType);
		log.setOperationLevel(operationLevel);
		try {
			emc.beginTransaction(Log.class);
			emc.persist( log, CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
		return true;
	}
	
	/**
	 * 对操作日志信息进行清理
	 * @param stay_yearnum_operationLog 日志保留年份
	 * @param stay_count_operationLog 日志保留条目数
	 * @throws Exception 
	 */
	public void clean( Integer stay_yearnum_operationLog, Integer stay_count_operationLog ) throws Exception {
		//先按时间清理
		cleanWithStayYearNumber( stay_yearnum_operationLog );
		cleanWithMaxCount( stay_count_operationLog );
	}

	/**
	 * 按最大保留条目数进行清理
	 * @param stay_count_operationLog
	 * @throws Exception 
	 */
	private void cleanWithMaxCount(Integer stay_count_operationLog) throws Exception {
		Business business = null;
		List<String> ids = null;
		String last_id = null;
		Log log = null;
		Long total = 0L;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			total = business.getLogFactory().getTotal();
			if( total > stay_count_operationLog ) {
				//将记录条目数减到stay_count_operationLog的60%
				ids = business.getLogFactory().getRecordIdsWithCount( (int)( stay_count_operationLog * 0.6 ));
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
	private void cleanWithStayYearNumber( Integer stay_yearnum_operationLog ) throws Exception {
		if( stay_yearnum_operationLog == null ) {
			throw new Exception("stay_yearnum_operationLog is null!");
		}
		//1、计算三年期限的时间点，三年前的1月1日, 最好再加一年
		Integer year = DateOperation.getYearNumber( new Date() );
		Date limitDate = DateOperation.getDateFromString( ( year-4 ) + "-01-01 00:00:00");
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
		Log log = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business( emc );
			ids = business.getLogFactory().listOverTime( overTime );
			if( ids != null && !ids.isEmpty() ) {
				emc.beginTransaction( Log.class );
				for( String id : ids ) {
					log = emc.find( id, Log.class );
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
