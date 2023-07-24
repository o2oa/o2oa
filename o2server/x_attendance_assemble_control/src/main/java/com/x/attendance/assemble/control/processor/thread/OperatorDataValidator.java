package com.x.attendance.assemble.control.processor.thread;

import java.util.Date;
import java.util.List;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.processor.EntityImportDataDetail;
import com.x.attendance.assemble.control.processor.ImportOptDefine;
import com.x.attendance.assemble.control.processor.monitor.StatusImportFileDetail;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class OperatorDataValidator implements Runnable {

	private static  Logger logger = LoggerFactory.getLogger( OperatorDataValidator.class );
	
	private UserManagerService userManagerService = null;
	
	private EntityImportDataDetail cacheImportRowDetail = null;
	private Boolean debugger = false;
	
	public OperatorDataValidator( EntityImportDataDetail cacheImportRowDetail, Boolean debugger ) {
		userManagerService = new UserManagerService();
		this.cacheImportRowDetail = cacheImportRowDetail ;
		this.debugger = debugger;
	}
	
	@Override
	public void run() {
		 execute( cacheImportRowDetail );
	}
	
	private void execute( EntityImportDataDetail cacheImportRowDetail ) {
		Integer curRow = cacheImportRowDetail.getCurRow();
		List<String> colmlist = cacheImportRowDetail.getColmlist();
		if( colmlist == null ) {
			return;
		}
		if( colmlist != null && !colmlist.get(0).isEmpty() && !colmlist.get(2).isEmpty()){
			try {
				check( cacheImportRowDetail.getFile_id(), curRow, colmlist );
			}catch( Exception e ) {
				logger.error( e );
			}
		}
	}
	
	private void check( String fileKey, Integer curRow, List<String> colmlist ) {

		StatusImportFileDetail cacheImportFileStatus = StatusSystemImportOpt.getInstance().getCacheImportFileStatus( fileKey );
		
		cacheImportFileStatus.setCurrentProcessName( ImportOptDefine.VALIDATE );
		
		cacheImportFileStatus.setProcessing_validate( true );
		
		cacheImportFileStatus.setProcessing( true );
		
		if( colmlist!= null && colmlist.size() > 0 ){
			
			Boolean checkSuccess = true;
			Boolean personExists = true;
			Date datetime = null;
			DateOperation dateOperation = new DateOperation();
			
			EntityImportDataDetail cacheImportRowDetail = new EntityImportDataDetail();
			
			cacheImportRowDetail.setCurRow( curRow );
			
			if( !colmlist.get(0).isEmpty() && !colmlist.get(2).isEmpty() ){
				
				cacheImportRowDetail.setEmployeeName( colmlist.get(0).trim() );  //员工姓名
				
				if( colmlist.get(1) != null && !colmlist.get(1).trim().isEmpty()){
					cacheImportRowDetail.setEmployeeNo( colmlist.get(1).trim() );    //员工号
				}
				
				cacheImportRowDetail.setRecordDateString( colmlist.get(2) ); //打卡日期
				
				if( colmlist.size() > 3 ){
					cacheImportRowDetail.setOnDutyTime( colmlist.get(3) );    //上午上班打卡时间
				}
				if( colmlist.size() > 4 ){
					cacheImportRowDetail.setMorningOffDutyTime( colmlist.get(4) );    //上午下班打卡时间
				}
				if( colmlist.size() > 5 ){
					cacheImportRowDetail.setAfternoonOnDutyTime( colmlist.get(5) );    //下午上班打卡时间
				}
				
				if( colmlist.size() > 6 ){
					cacheImportRowDetail.setOffDutyTime( colmlist.get(6) );   //下午下班打卡时间
				}
				
				cacheImportRowDetail.setCheckStatus( "success" );         //设置数据检查状态为正常
				
				if( checkSuccess ) {
					//查询人员是否存在
					personExists = checkPersonExists( cacheImportFileStatus, cacheImportRowDetail.getEmployeeName() );
					if( !personExists ) {
						checkSuccess = false;
						cacheImportRowDetail.setCheckStatus("error");
						cacheImportRowDetail.setDescription( cacheImportRowDetail.getDescription() + ", 员工不存在！" );
						logger.info("step 2, data check on row "+curRow+", found an error! person '"+ cacheImportRowDetail.getEmployeeName() +"' not exists." );
					}
				}
				
				if( checkSuccess ) {
					if( cacheImportRowDetail.getEmployeeName() == null || cacheImportRowDetail.getEmployeeName().isEmpty() ) {
						checkSuccess = false;
						cacheImportRowDetail.setCheckStatus("error");
						cacheImportRowDetail.setDescription( cacheImportRowDetail.getDescription() + ", '员工标识'为空！" );
						logger.info("step 2, data check on row "+curRow+", found an error! field 'employeeName' is null." );
					}
				}
				
				if( checkSuccess ) {
					if( cacheImportRowDetail.getRecordDateString() == null || cacheImportRowDetail.getRecordDateString().isEmpty() ) {
						checkSuccess = false;
						cacheImportRowDetail.setCheckStatus("error");
						cacheImportRowDetail.setDescription( cacheImportRowDetail.getDescription() + ", '打卡日期'为空！" );
						logger.info("step 2, data check on row "+curRow+", found an error! field 'recordDateString' is null." );
					}
				}
				
				if( checkSuccess ) {
					//下面检查三个日期和时间格式是否正常，并且进行转换
					try{
						datetime = dateOperation.getDateFromString( cacheImportRowDetail.getRecordDateString() );
						cacheImportRowDetail.setRecordDate( datetime );
						cacheImportRowDetail.setRecordDateStringFormated( dateOperation.getDateStringFromDate( datetime, "YYYY-MM-DD") ); //打卡日期
						cacheImportRowDetail.setRecordYearString( dateOperation.getYear( datetime ) );
						cacheImportRowDetail.setRecordMonthString( dateOperation.getMonth(datetime) );
						if( Integer.parseInt( cacheImportRowDetail.getRecordYearString() ) > dateOperation.getYearNumber( new Date() )
								|| Integer.parseInt( cacheImportRowDetail.getRecordYearString() ) < 2000 
						) {
							throw new Exception("record date error:" + cacheImportRowDetail.getRecordDateString() );
						}
						//设置一下该文件中所有数据的日期范围
						cacheImportFileStatus.sendStartTime(datetime);
						cacheImportFileStatus.sendEndTime( datetime );
					}catch( Exception e ){
						checkSuccess = false;
						cacheImportRowDetail.setCheckStatus("error");
						cacheImportRowDetail.setDescription( cacheImportRowDetail.getDescription() + "打卡日期格式异常：" + cacheImportRowDetail.getRecordDateString() );
						logger.info("step 2, data check on row "+curRow+", found an error! format on field 'recordDate'：" + cacheImportRowDetail.getRecordDateString(), e);
					}
				}
				
				if( checkSuccess ) {
					if( cacheImportRowDetail.getOnDutyTime() != null && cacheImportRowDetail.getOnDutyTime().trim().length() > 0 ){
						try{
							datetime = dateOperation.getDateFromString( cacheImportRowDetail.getOnDutyTime() );
							cacheImportRowDetail.setOnDutyTimeFormated( dateOperation.getDateStringFromDate( datetime, "HH:mm:ss") ); //上午上班打卡时间
						}catch( Exception e ){
							checkSuccess = false;
							cacheImportRowDetail.setCheckStatus("error");
							cacheImportRowDetail.setDescription( cacheImportRowDetail.getDescription() + "上午上班打卡时间格式异常：" + cacheImportRowDetail.getOnDutyTime() );
							logger.info("step 2, data check on row "+curRow+", found an error!format on field 'onDutyTime'：" + cacheImportRowDetail.getOnDutyTime(), e);
						}
					}
				}
				
				if( checkSuccess ) {
					if( cacheImportRowDetail.getMorningOffDutyTime() != null && cacheImportRowDetail.getMorningOffDutyTime().trim().length() > 0 ){
						try{
							datetime = dateOperation.getDateFromString( cacheImportRowDetail.getMorningOffDutyTime() );
							cacheImportRowDetail.setMorningOffDutyTimeFormated( dateOperation.getDateStringFromDate( datetime, "HH:mm:ss") ); //上午下班打卡时间
						}catch( Exception e ){
							checkSuccess = false;
							cacheImportRowDetail.setCheckStatus("error");
							cacheImportRowDetail.setDescription( cacheImportRowDetail.getDescription() + "上午下班打卡时间格式异常：" + cacheImportRowDetail.getMorningOffDutyTime() );
							logger.info("step 2, data check on row "+curRow+", found an error!format on field 'onDutyTime'：" + cacheImportRowDetail.getMorningOffDutyTime(), e);
						}
					}
				}
				
				if( checkSuccess ) {
					if( cacheImportRowDetail.getAfternoonOnDutyTime() != null && cacheImportRowDetail.getAfternoonOnDutyTime().trim().length() > 0 ){
						try{
							datetime = dateOperation.getDateFromString( cacheImportRowDetail.getAfternoonOnDutyTime() );
							cacheImportRowDetail.setAfternoonOnDutyTimeFormated( dateOperation.getDateStringFromDate( datetime, "HH:mm:ss") ); //上午下班打卡时间
						}catch( Exception e ){
							checkSuccess = false;
							cacheImportRowDetail.setCheckStatus("error");
							cacheImportRowDetail.setDescription( cacheImportRowDetail.getDescription() + "上午下班打卡时间格式异常：" + cacheImportRowDetail.getAfternoonOnDutyTime() );
							logger.info("step 2, data check on row "+curRow+", found an error!format on field 'onDutyTime'：" + cacheImportRowDetail.getAfternoonOnDutyTime(), e);
						}
					}
				}
				
				if( checkSuccess ) {
					if( cacheImportRowDetail.getOffDutyTime() != null && cacheImportRowDetail.getOffDutyTime().trim().length() > 0 ){
						try{
							datetime = dateOperation.getDateFromString( cacheImportRowDetail.getOffDutyTime() );
							cacheImportRowDetail.setOffDutyTimeFormated( dateOperation.getDateStringFromDate( datetime, "HH:mm:ss") ); //下班打卡时间
						}catch( Exception e ){
							checkSuccess = false;
							cacheImportRowDetail.setCheckStatus("error");
							cacheImportRowDetail.setDescription( cacheImportRowDetail.getDescription() + "下午下班打卡时间格式异常：" + cacheImportRowDetail.getOffDutyTime() );
							logger.info("step 2, data check on row "+curRow+", found an error!format on field 'offDutyTime'：" + cacheImportRowDetail.getOffDutyTime(), e);
						}
					}
				}
				
				if( !checkSuccess ){
					cacheImportFileStatus.setCheckStatus( "error" );
					cacheImportFileStatus.setCheckStatus( "error" );
					cacheImportFileStatus.increaseErrorCount( 1 );
					logger.debug( debugger, ">>>>>>>>>>record check error:" + cacheImportRowDetail.getDescription() );
					cacheImportFileStatus.addErrorList( cacheImportRowDetail );
				}
				
				cacheImportFileStatus.addDetailList( cacheImportRowDetail );
				cacheImportFileStatus.increaseProcess_validate_count( 1 );
			}
		}
	}

	private Boolean checkPersonExists( StatusImportFileDetail cacheImportFileStatus, String personName ) {
		if( !cacheImportFileStatus.getPersonList().isEmpty() && cacheImportFileStatus.getPersonList().contains( personName ) ) {
			//如果已经缓存了，那么肯定存在的
			return true;
		}else {
			//检查人员是否存在
			try{
				personName = userManagerService.checkPersonExists( personName );
				if( personName == null || personName.isEmpty() ) {
					return false;
				}else {
					cacheImportFileStatus.addPersonList( personName );
					return true;
				}
			}catch(Exception e) {
				return false;
			}
		}
	}

	
}
