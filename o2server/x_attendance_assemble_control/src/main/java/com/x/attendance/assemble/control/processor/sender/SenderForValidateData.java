package com.x.attendance.assemble.control.processor.sender;

import java.util.ArrayList;
import java.util.List;

import com.x.attendance.assemble.control.processor.EntityImportDataDetail;
import com.x.attendance.assemble.control.processor.ImportOptDefine;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.processor.thread.DataProcessThreadFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 将EXCEL分析出来的数据发送到队列，由相应的处理器进行数据校验操作
 * 
 * @author O2LEE
 *
 */
public class SenderForValidateData {

	private static  Logger logger = LoggerFactory.getLogger( SenderForValidateData.class );
	
	public void execute( int sheetIndex, int curRow, List<String> colmlist, String fileKey ) {
		
		StatusImportFileDetail cacheImportFileStatus = StatusSystemImportOpt.getInstance().getCacheImportFileStatus( fileKey );
		List<String> data = new ArrayList<>();
		for( String str : colmlist ) {
			data.add( str );
		}
		
		try {
			cacheImportFileStatus.setCurrentProcessName( ImportOptDefine.VALIDATE );
			//將數據推入隊列，在队列中进行数据格式校验
			cacheImportFileStatus.increaseProcess_validate_total( 1 );
			DataProcessThreadFactory.getInstance().submit( new EntityImportDataDetail( "excel", sheetIndex, curRow, fileKey, data ), StatusSystemImportOpt.getInstance().getDebugger() );
		} catch (Exception e) {
			logger.error( e );
		}
	}
	
}
