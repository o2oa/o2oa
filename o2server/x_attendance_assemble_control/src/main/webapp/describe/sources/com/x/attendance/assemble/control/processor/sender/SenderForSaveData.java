package com.x.attendance.assemble.control.processor.sender;

import java.util.List;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.processor.EntityImportDataDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.attendance.assemble.control.processor.thread.DataProcessThreadFactory;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 将EXCEL分析出来的数据发送到队列，由相应的处理器进行数据存储操作
 * 
 * @author O2LEE
 *
 */
public class SenderForSaveData {

	private static  Logger logger = LoggerFactory.getLogger( SenderForSaveData.class );
	
	public void execute( StatusImportFileDetail cacheImportFileStatus, Boolean debugger ) {
		
		String file_id = cacheImportFileStatus.getFileId();
		
		//如果数据校验完成 ，并且全部成功，那么可以进行数据保存，先单线程删除数据
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business( emc );
			AttendanceDetail detail = null;
			
			// 先删除这一个文件ID下的所有记录
			List<String> ids = business.getAttendanceDetailFactory().listByBatchName( file_id );
			emc.beginTransaction(AttendanceDetail.class);
			if (ids != null && ids.size() > 0) {
				for (String id : ids) {
					detail = emc.find(id, AttendanceDetail.class);
					emc.remove( detail );
				}
			}
			emc.commit();
		} catch (Exception e) {
			logger.warn( "file["+ file_id +"]，delete attendance detail got an exception.ID:" + file_id );
			logger.error( e );
		}
		
		//开始对数据进行保存，发送到queue-consumerFileDataSaver
		logger.info("file["+ file_id +"], system will send save data to queeu, record total:" + cacheImportFileStatus.getDetailList().size() );
		
		//将错误数据量恢复为0
		cacheImportFileStatus.setErrorCount( 0 );
		for ( EntityImportDataDetail cacheImportRowDetail : cacheImportFileStatus.getDetailList() ) {
			if( cacheImportRowDetail != null ) {
				try {
					//將數據推入隊列，在队列中进行数据持久化存储
					cacheImportRowDetail.setFile_id( file_id );
					cacheImportRowDetail.setData_type("saveData");
					cacheImportFileStatus.increaseProcess_save_total( 1 );
					DataProcessThreadFactory.getInstance().submit( cacheImportRowDetail, debugger );
				} catch (Exception e) {
					logger.error( e );
				}
			}else {
				logger.warn( "需要保存的数据为空......" );
			}
		}
	}
	
}
