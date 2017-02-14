package com.x.attendance.assemble.control.jaxrs.fileimport;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.common.excel.reader.IRowReader;
import com.x.attendance.assemble.control.ApplicationGobal;

public class AttendancePersonExcelReader implements IRowReader{
	
	private Logger logger = LoggerFactory.getLogger( AttendancePersonExcelReader.class );
	
	/* 业务逻辑实现方法
	 * @see com.eprosun.util.excel.IRowReader#getRows(int, int, java.util.List)
	 */
	public void getRows( int sheetIndex, int curRow, List<String> colmlist, String fileKey, int startRow ) {
		if( curRow < startRow ){
			return;
		}
		//1-员工姓名	
		//2-员工号
		//3-日期	
		//4-签到时间	
		//5-签退时间	
		if ( ApplicationGobal.importFileCheckResultMap ==  null ) {
			ApplicationGobal.importFileCheckResultMap = new HashMap<String, CacheImportFileStatus>();
		}
		
		if( ApplicationGobal.importFileCheckResultMap.get( fileKey ) == null ){
			ApplicationGobal.importFileCheckResultMap.put( fileKey, new CacheImportFileStatus() );
		}
		
		CacheImportFileStatus cacheImportFileStatus = ApplicationGobal.importFileCheckResultMap.get( fileKey );
		
		if( colmlist!= null && colmlist.size() > 0 ){
			
			cacheImportFileStatus.setRowCount(curRow);
			
			if( cacheImportFileStatus.getDetailList() == null ){
				cacheImportFileStatus.setDetailList( new ArrayList<CacheImportRowDetail>());
			}
			
			boolean checkSuccess = true;
			Date datetime = null;
			DateOperation dateOperation = new DateOperation();
			
			CacheImportRowDetail cacheImportRowDetail = new CacheImportRowDetail();
			if( !colmlist.get(0).isEmpty() && !colmlist.get(2).isEmpty()){
				cacheImportRowDetail.setEmployeeName( colmlist.get(0).trim() );  //员工姓名
				if( colmlist.get(1) != null && !colmlist.get(1).trim().isEmpty()){
					cacheImportRowDetail.setEmployeeNo( colmlist.get(1).trim() );    //员工号
				}
				cacheImportRowDetail.setRecordDateString( colmlist.get(2) ); //打卡日期
				if( colmlist.size() > 3 ){
					cacheImportRowDetail.setOnDutyTime( colmlist.get(3) );    //上班打卡时间
				}
				if( colmlist.size() > 4 ){
					cacheImportRowDetail.setOffDutyTime( colmlist.get(4) );   //下班打卡时间
				}
				cacheImportRowDetail.setCheckStatus( "success" );         //设置数据检查状态为正常
				
				//下面检查三个日期和时间格式是否正常，并且进行转换
				try{
					datetime = dateOperation.getDateFromString( cacheImportRowDetail.getRecordDateString() );
					cacheImportRowDetail.setRecordDateStringFormated( dateOperation.getDateStringFromDate( datetime, "YYYY-MM-DD") ); //打卡日期
					cacheImportRowDetail.setRecordYearString( dateOperation.getYear( datetime ) );
					cacheImportRowDetail.setRecordMonthString( dateOperation.getMonth(datetime) );
				}catch( Exception e ){
					checkSuccess = false;
					cacheImportRowDetail.setCheckStatus("error");
					cacheImportRowDetail.setDescription( cacheImportRowDetail.getDescription() + "打卡日期格式异常：" + cacheImportRowDetail.getRecordDateString() );
					logger.error("数据导入第"+curRow+"行，打卡日期格式异常，时间：" + cacheImportRowDetail.getRecordDateString(), e);
				}
				
				if( cacheImportRowDetail.getOnDutyTime() != null && cacheImportRowDetail.getOnDutyTime().trim().length() > 0 ){
					try{
						datetime = dateOperation.getDateFromString( cacheImportRowDetail.getOnDutyTime() );
						cacheImportRowDetail.setOnDutyTimeFormated( dateOperation.getDateStringFromDate( datetime, "HH:mm:ss") ); //上班打卡时间
					}catch( Exception e ){
						checkSuccess = false;
						cacheImportRowDetail.setCheckStatus("error");
						cacheImportRowDetail.setDescription( cacheImportRowDetail.getDescription() + "上班打卡时间格式异常：" + cacheImportRowDetail.getOnDutyTime() );
						logger.error("数据导入第"+curRow+"行，上班打卡时间格式异常，时间：" + cacheImportRowDetail.getOnDutyTime(), e);
					}
				}
				
				if( cacheImportRowDetail.getOffDutyTime() != null && cacheImportRowDetail.getOffDutyTime().trim().length() > 0 ){
					try{
						datetime = dateOperation.getDateFromString( cacheImportRowDetail.getOffDutyTime() );
						cacheImportRowDetail.setOffDutyTimeFormated( dateOperation.getDateStringFromDate( datetime, "HH:mm:ss") ); //下班打卡时间
					}catch( Exception e ){
						checkSuccess = false;
						cacheImportRowDetail.setCheckStatus("error");
						cacheImportRowDetail.setDescription( cacheImportRowDetail.getDescription() + "下班打卡时间格式异常：" + cacheImportRowDetail.getOffDutyTime() );
						logger.error("数据导入第"+curRow+"行，下班打卡时间格式异常，时间：" +cacheImportRowDetail.getOffDutyTime(), e);
					}
				}
				
				if( !checkSuccess ){
					cacheImportFileStatus.setCheckStatus("error");
					cacheImportFileStatus.setErrorCount( (cacheImportFileStatus.getErrorCount() + 1) );
				}
				
				cacheImportFileStatus.getDetailList().add( cacheImportRowDetail );
			}
			
			
			//for( String a : colmlist ){
			//	System.out.print( a + " | " );
			//}
			//System.out.println("");
		}
		//if( cacheImportFileStatus != null && cacheImportFileStatus.getDetailList() != null ){
		//	logger.debug( "【分析导入文件】导入文件["+fileKey+"]里有"+cacheImportFileStatus.getDetailList().size()+"条数据可供导入。" );
		//}
	}
}
