package com.x.attendance.assemble.control.jaxrs.fileimport;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.jaxrs.ExceptionAttendanceProcess;
import com.x.attendance.assemble.control.processor.EntityImportDataDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionGetFileOptStatusWithFile extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGetFileOptStatusWithFile.class);
	
	protected ActionResult<StatusImportFileDetail> execute( HttpServletRequest request, EffectivePerson effectivePerson, String file_id ) throws Exception {
		ActionResult<StatusImportFileDetail> result = new ActionResult<>();
		List<EntityImportDataDetail> _cacheImportRowDetailList = new ArrayList<EntityImportDataDetail>();
		StatusImportFileDetail cacheImportFileStatus = null;
		Boolean check = true;

		if(check){
			try {
				cacheImportFileStatus = copyCacheImportFileStatus(  StatusSystemImportOpt.getInstance().getCacheImportFileStatus( file_id ) );
				if ( !"success".equalsIgnoreCase( cacheImportFileStatus.getCheckStatus()) || cacheImportFileStatus.getErrorCount() > 0 ) {
					// 如果检查结果有错误，那么响应到浏览器端只返回20条有错误的数据信息
					if ( cacheImportFileStatus.getDetailList() != null && cacheImportFileStatus.getDetailList().size() > 0 ) {
						for ( EntityImportDataDetail cacheImportRowDetail : cacheImportFileStatus.getDetailList() ) {
							if ( !"success".equalsIgnoreCase(cacheImportRowDetail.getCheckStatus()) ) {
								_cacheImportRowDetailList.add( cacheImportRowDetail );
							}
							if (_cacheImportRowDetailList.size() >= 20) {
								break;
							}
						}
					}
				} else { // 返回20条数据
					if ( cacheImportFileStatus.getDetailList() != null && cacheImportFileStatus.getDetailList().size() > 0) {
						for ( EntityImportDataDetail cacheImportRowDetail : cacheImportFileStatus.getDetailList() ) {
							_cacheImportRowDetailList.add( cacheImportRowDetail );
							if ( _cacheImportRowDetailList.size() >= 100) {
								break;
							}
						}
					}
				}
				
				cacheImportFileStatus.setDetailList( _cacheImportRowDetailList );
				result.setCount( cacheImportFileStatus.getRowCount() );
				result.setData( cacheImportFileStatus );
				
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceProcess( e, "系统检查需要导入的数据文件时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return result;
	}
}