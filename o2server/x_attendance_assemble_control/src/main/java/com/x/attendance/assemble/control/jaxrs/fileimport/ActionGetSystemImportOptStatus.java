package com.x.attendance.assemble.control.jaxrs.fileimport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.jaxrs.ExceptionAttendanceProcess;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.processor.thread.DataProcessThreadFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionGetSystemImportOptStatus extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionGetSystemImportOptStatus.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		StatusSystemImportOpt systemImportOptStatus = null;
		Map<String, StatusImportFileDetail> importFileCheckResultMap = new HashMap<>();
		Wo wo = new Wo();
		StatusImportFileDetail cacheImportFileStatus = null;
		Set<String> keySet = null;
		Iterator<String> it = null;
		String key = null;
		Boolean check = true;

		if(check){
			try {
				systemImportOptStatus = StatusSystemImportOpt.getInstance();
				keySet = systemImportOptStatus.getCheckMapKeySet();
				it = keySet.iterator();
				while( it.hasNext() ) {
					key = it.next();
					cacheImportFileStatus = copyCacheImportFileStatus( systemImportOptStatus.getCacheImportFileStatus( key ));
					cacheImportFileStatus.setDetailList( null );
					importFileCheckResultMap.put( key, cacheImportFileStatus );
				}
				
				wo.setProcess_analysis_count( systemImportOptStatus.getProcess_analysis_count() );
				wo.setProcess_analysis_total( systemImportOptStatus.getProcess_analysis_total() );
				wo.setProcess_supplement_count( systemImportOptStatus.getProcess_supplement_count() );
				wo.setProcess_supplement_total( systemImportOptStatus.getProcess_supplement_total() );
				wo.setProcessing( systemImportOptStatus.getProcessing() );
				wo.setProcessing_analysis( systemImportOptStatus.getProcessing_analysis() );
				wo.setProcessing_statistic( systemImportOptStatus.getProcessing_statistic() );
				wo.setProcessing_supplement( systemImportOptStatus.getProcessing_supplement() );
				wo.setImportFileCheckResultMap( importFileCheckResultMap );
				wo.setAliveTheradCount( DataProcessThreadFactory.getInstance().getAliveThreadCount() );
				wo.setTaskCount( DataProcessThreadFactory.getInstance().getTaskCount() );
				
				result.setData( wo );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new ExceptionAttendanceProcess( e, "系统检查需要导入的数据文件时发生异常！" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		return result;
	}
	
	public static class Wo  {
		
		private Boolean processing = false;
		private Long aliveTheradCount = 0L;
		private Long taskCount = 0L;
		private Boolean processing_supplement = false;
		private long process_supplement_count = 0;	
		private long process_supplement_total = 0;
		private Boolean processing_analysis = false;
		private long process_analysis_count = 0;	
		private long process_analysis_total = 0;
		private Boolean processing_statistic = false;
		private Map<String, StatusImportFileDetail> importFileCheckResultMap = new HashMap<>();
		
		public Boolean getProcessing() {
			return processing;
		}
		public Boolean getProcessing_supplement() {
			return processing_supplement;
		}
		public long getProcess_supplement_count() {
			return process_supplement_count;
		}
		public long getProcess_supplement_total() {
			return process_supplement_total;
		}
		public Boolean getProcessing_analysis() {
			return processing_analysis;
		}
		public long getProcess_analysis_count() {
			return process_analysis_count;
		}
		public long getProcess_analysis_total() {
			return process_analysis_total;
		}
		public Boolean getProcessing_statistic() {
			return processing_statistic;
		}
		public Map<String, StatusImportFileDetail> getImportFileCheckResultMap() {
			return importFileCheckResultMap;
		}
		public void setProcessing(Boolean processing) {
			this.processing = processing;
		}
		public void setProcessing_supplement(Boolean processing_supplement) {
			this.processing_supplement = processing_supplement;
		}
		public void setProcess_supplement_count(long process_supplement_count) {
			this.process_supplement_count = process_supplement_count;
		}
		public void setProcess_supplement_total(long process_supplement_total) {
			this.process_supplement_total = process_supplement_total;
		}
		public void setProcessing_analysis(Boolean processing_analysis) {
			this.processing_analysis = processing_analysis;
		}
		public void setProcess_analysis_count(long process_analysis_count) {
			this.process_analysis_count = process_analysis_count;
		}
		public void setProcess_analysis_total(long process_analysis_total) {
			this.process_analysis_total = process_analysis_total;
		}
		public void setProcessing_statistic(Boolean processing_statistic) {
			this.processing_statistic = processing_statistic;
		}
		public void setImportFileCheckResultMap(Map<String, StatusImportFileDetail> importFileCheckResultMap) {
			this.importFileCheckResultMap = importFileCheckResultMap;
		}
		public Long getAliveTheradCount() {
			return aliveTheradCount;
		}
		public void setAliveTheradCount(Long aliveTheradCount) {
			this.aliveTheradCount = aliveTheradCount;
		}
		public Long getTaskCount() {
			return taskCount;
		}
		public void setTaskCount(Long taskCount) {
			this.taskCount = taskCount;
		}
		
	}
}