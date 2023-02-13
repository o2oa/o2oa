package com.x.attendance.assemble.control.processor.monitor;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StatusSystemImportOpt{
	
	private Boolean processing = false;
	
	private Long aliveTheradCount = 0L;
	
	private Long taskCount = 0L;
	
	/**
	 * 该文件的导入数据是否正在补充
	 */
	private Boolean processing_supplement = false;
	private long process_supplement_count = 0;	
	private long process_supplement_total = 0;
	private long process_supplement_error = 0;
	/**
	 * 该文件的导入数据是否正在分析
	 */
	private Boolean processing_analysis = false;
	private long process_analysis_count = 0;	
	private long process_analysis_total = 0;
	private long process_analysis_error = 0;
	
	private Boolean processing_statistic = false;
	
	private Boolean debugger = false;
	
	/**
	 * 记录数据导入过程以及校验结果的map
	 */
	private static final Map<String, StatusImportFileDetail> importFileCheckResultMap = new ConcurrentHashMap<>();
	
	private StatusSystemImportOpt() {}
	
	private static StatusSystemImportOpt single = null;  
    
    public static StatusSystemImportOpt getInstance() {  
        if ( single == null ) {    
             single = new StatusSystemImportOpt();
             MonitorFileDataOpt.getInstance().start();
        }    
        return single;  
    }  
	
	public synchronized void cleanCacheImportFileStatus( String file_id ) {
		if( importFileCheckResultMap.containsKey( file_id )) {
			importFileCheckResultMap.put( file_id, null );
		}
	}
	
	public synchronized void removeCacheImportFileStatus( String file_id ) {
		if( importFileCheckResultMap.containsKey( file_id )) {
			importFileCheckResultMap.remove( file_id );
		}
	}
	
	public StatusImportFileDetail getCacheImportFileStatus( String file_id ) {
		if( importFileCheckResultMap.get( file_id ) == null ) {
			importFileCheckResultMap.put( file_id, new StatusImportFileDetail() );
		}
		return importFileCheckResultMap.get( file_id );
	}
	
	public synchronized void addCacheImportFileStatus( StatusImportFileDetail importFileDetail ) {
		if( importFileDetail == null || importFileDetail.getFileId() == null || importFileDetail.getFileId().isEmpty() ) {
			return;
		}
		importFileCheckResultMap.put( importFileDetail.getFileId(), importFileDetail );
	}
	
	public Boolean isProcessing() {
		Set<String> keySet = importFileCheckResultMap.keySet();
		Iterator<String> keyIterator = keySet.iterator();
		StatusImportFileDetail cacheImportFileStatus = null;
		String key = null;
		while( keyIterator.hasNext() ) {
			key = keyIterator.next();
			cacheImportFileStatus = importFileCheckResultMap.get( key );
			if( cacheImportFileStatus.getProcessing() ) {
				processing = true;
				return true;
			}
		}
		return processing;
	}

	public Boolean isChildrenProcessing() {
		Set<String> keySet = importFileCheckResultMap.keySet();
		Iterator<String> keyIterator = keySet.iterator();
		StatusImportFileDetail cacheImportFileStatus = null;
		String key = null;
		while( keyIterator.hasNext() ) {
			key = keyIterator.next();
			cacheImportFileStatus = importFileCheckResultMap.get( key );
			if( cacheImportFileStatus.getProcessing() ) {
				processing = true;
				return true;
			}
		}
		return false;
	}
	
	public Set<String> getCheckMapKeySet() {
		return importFileCheckResultMap.keySet();
	}
	
	public synchronized void increaseProcess_supplement_count(long increaseCount) {
		this.process_supplement_count = this.process_supplement_count + increaseCount;
	}

	public synchronized void increaseProcess_supplement_total(long increaseCount) {
		this.process_supplement_total = this.process_supplement_total + increaseCount;
	}
	
	public synchronized void increaseProcess_analysis_count(long increaseCount) {
		this.process_analysis_count = this.process_analysis_count + increaseCount;
	}

	public synchronized void increaseProcess_analysis_total(long increaseCount) {
		this.process_analysis_total = this.process_analysis_total + increaseCount;
	}
	
	public synchronized void increaseProcess_supplement_error(long increaseCount) {
		this.process_supplement_error = this.process_supplement_error + increaseCount;
	}
	
	public synchronized void increaseProcess_analysis_error(long increaseCount) {
		this.process_analysis_error = this.process_analysis_error + increaseCount;
	}
	
	public synchronized void setProcessing_supplement(Boolean processing_supplement) {
		this.processing_supplement = processing_supplement;
	}
	
	public synchronized void setProcessing_analysis(Boolean processing_analysis) {
		this.processing_analysis = processing_analysis;
	}
	
	public void setProcess_supplement_count(long process_supplement_count) {
		this.process_supplement_count = process_supplement_count;
	}

	public synchronized void setProcess_supplement_total(long process_supplement_total) {
		this.process_supplement_total = process_supplement_total;
	}

	public synchronized void setProcess_analysis_count(long process_analysis_count) {
		this.process_analysis_count = process_analysis_count;
	}

	public synchronized void setProcess_analysis_total(long process_analysis_total) {
		this.process_analysis_total = process_analysis_total;
	}
	
	public synchronized void setProcessing(Boolean processing) {
		this.processing = processing;
	}
	
	public synchronized void setProcess_supplement_error(long process_supplement_error) {
		this.process_supplement_error = process_supplement_error;
	}

	public synchronized void setProcess_analysis_error(long process_analysis_error) {
		this.process_analysis_error = process_analysis_error;
	}

	public void setProcessing_statistic(Boolean processing_statistic) {
		this.processing_statistic = processing_statistic;
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

	public Boolean getProcessing() {
		return processing;
	}

	public Boolean getProcessing_statistic() {
		return processing_statistic;
	}

	public Long getAliveTheradCount() {
		return aliveTheradCount;
	}

	public void setAliveTheradCount( Long aliveTheradCount ) {
		this.aliveTheradCount = aliveTheradCount;
	}

	public Long getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(Long taskCount) {
		this.taskCount = taskCount;
	}

	public long getProcess_supplement_error() {
		return process_supplement_error;
	}

	public long getProcess_analysis_error() {
		return process_analysis_error;
	}

	public Boolean getDebugger() {
		return debugger;
	}

	public void setDebugger(Boolean debugger) {
		this.debugger = debugger;
	}	
}
