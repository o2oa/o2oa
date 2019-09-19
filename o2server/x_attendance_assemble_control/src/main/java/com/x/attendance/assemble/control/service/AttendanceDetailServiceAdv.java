package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.exception.ExceptionAttendanceDetailProcess;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class AttendanceDetailServiceAdv {
	private DateOperation dateOperation = new DateOperation();
	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailServiceAdv.class );
	private AttendanceDetailService attendanceDetailService = new AttendanceDetailService();
	private AttendanceDetailMobileService attendanceDetailMobileService = new AttendanceDetailMobileService();
	protected AttendanceDetailAnalyseServiceAdv attendanceDetailAnalyseServiceAdv = new AttendanceDetailAnalyseServiceAdv();
	protected AttendanceWorkDayConfigServiceAdv attendanceWorkDayConfigServiceAdv = new AttendanceWorkDayConfigServiceAdv();
	protected AttendanceStatisticalCycleServiceAdv attendanceStatisticCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
	
	public AttendanceDetail get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<AttendanceDetail> list( List<String> ids ) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listByBatchName( String file_id ) throws Exception {
		if( file_id == null || file_id.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listByBatchName( emc, file_id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据开始和结束日期以及员工信息补充缺失的打卡信息
	 * @param startDate
	 * @param endDate
	 * @param attendanceEmployeeConfig
	 * @throws Exception
	 */
	public void dataSupplement( Date startDate, Date endDate, AttendanceEmployeeConfig attendanceEmployeeConfig) throws Exception {
		DateOperation dateOperation = new DateOperation();
		List<String> dayList = dateOperation.listDateStringBetweenDate( startDate, endDate );
		if( dayList == null ){
			return ;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceDetailService.checkAndReplenish( emc, dayList, attendanceEmployeeConfig );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public String getMaxRecordDate() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.getMaxRecordDate( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listUserAttendanceDetailByYearAndMonth( String q_empName, String q_year, String q_month ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listUserAttendanceDetailByYearAndMonth( emc, q_empName, q_year, q_month );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listUserAttendanceDetailByCycleYearAndMonth( String q_empName, String cycleYear, String cycleMonth ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listUserAttendanceDetailByCycleYearAndMonth( emc, q_empName, cycleYear, cycleMonth );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listTopUnitAttendanceDetailByYearAndMonth( List<String> topUnitNames, String q_year, String q_month) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listTopUnitAttendanceDetailByYearAndMonth( emc, topUnitNames, q_year, q_month );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listUnitAttendanceDetailByYearAndMonth( List<String> unitNames, String q_year, String q_month ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.listUnitAttendanceDetailByYearAndMonth( emc, unitNames, q_year, q_month );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> getAllAnalysenessDetails( String startDate, String endDate, String personName ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.getAllAnalysenessDetails( emc, startDate, endDate, personName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> getAllAnalysenessPersonNames( String startDate, String endDate ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.getAllAnalysenessPersonNames( emc, startDate, endDate );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void archive( String id ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		String datetime = dateOperation.getNowDateTime();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			attendanceDetailService.archive( emc, id, datetime );	
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public void archiveAll() throws Exception {
		DateOperation dateOperation = new DateOperation();
		String datetime = dateOperation.getNowDateTime();
		List<String> ids = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business( emc );
			ids = business.getAttendanceDetailFactory().listNonArchiveDetailInfoIds();
			if( ids != null && !ids.isEmpty() ){
				for( String id : ids ){
					try{
						attendanceDetailService.archive( emc, id, datetime );
					}catch( Exception e ){
						logger.warn( "system archive attendance appeal info got an exception." );
						logger.error(e);
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	public synchronized AttendanceDetail save(AttendanceDetail attendanceDetail) throws Exception {
		if( attendanceDetail == null ){
			throw new Exception("attendanceDetail is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailService.save( emc, attendanceDetail );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceDetailMobile save(AttendanceDetailMobile attendanceDetailMobile) throws Exception {
		if( attendanceDetailMobile == null ){
			throw new Exception("attendanceDetailMobile is null!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return attendanceDetailService.save( emc, attendanceDetailMobile );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public Long countAttendanceDetailMobileForPage( String empNo, String empName, String signDescription,
			String startDate, String endDate ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailMobileService.countAttendanceDetailMobileForPage( emc, empNo, empName, signDescription,
					startDate, endDate );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<AttendanceDetailMobile> listAttendanceDetailMobileForPage(String empNo, String empName,
			String signDescription, String startDate, String endDate, Integer selectTotal) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailMobileService.listAttendanceDetailMobileForPage( emc, empNo, empName, signDescription,
					startDate, endDate, selectTotal );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public AttendanceDetailMobile getMobile(String id) throws Exception {
		if( id == null || id.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return attendanceDetailMobileService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void pushToDetail(String distinguishedName, String recordDateString ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<AttendanceDetailMobile> mobileDetails = attendanceDetailMobileService.listAttendanceDetailMobileWithEmployee( emc, distinguishedName, recordDateString );
			if( ListTools.isNotEmpty( mobileDetails )) {
				AttendanceDetailMobile mobileDetail = mobileDetails.get( 0 );
				String onDutyTime = getOnDutyTime( mobileDetails );
				String offDutyTime = getOffDutyTime( mobileDetails );
				
				AttendanceDetail detail = attendanceDetailService.listDetailWithEmployee( emc, distinguishedName, recordDateString );
				if( detail == null ) {
					detail = new AttendanceDetail();
					detail.setEmpNo( mobileDetail.getEmpNo() );
					detail.setEmpName( mobileDetail.getEmpName() );
					if( mobileDetail.getRecordDate() != null ) {
						detail.setYearString( dateOperation.getYear( mobileDetail.getRecordDate() ) );
						detail.setMonthString( dateOperation.getMonth( mobileDetail.getRecordDate() ) );
					}
					detail.setRecordDateString( mobileDetail.getRecordDateString() );
					detail.setOnDutyTime( onDutyTime );
					detail.setOffDutyTime( offDutyTime );
					detail.setRecordStatus( 0 );
					detail.setBatchName( "FromMobile_" + dateOperation.getNowTimeChar() );	
					
					emc.beginTransaction( AttendanceDetail.class );
					emc.persist( detail , CheckPersistType.all );
					emc.commit();
				}else {
					detail.setEmpNo( mobileDetail.getEmpNo() );
					detail.setEmpName( mobileDetail.getEmpName() );
					if( mobileDetail.getRecordDate() != null ) {
						detail.setYearString( dateOperation.getYear( mobileDetail.getRecordDate() ) );
						detail.setMonthString( dateOperation.getMonth( mobileDetail.getRecordDate() ) );
					}
					detail.setRecordDateString( mobileDetail.getRecordDateString() );
					detail.setOnDutyTime( onDutyTime );
					detail.setOffDutyTime( offDutyTime );
					detail.setRecordStatus( 0 );
					detail.setBatchName( "FromMobile_" + dateOperation.getNowTimeChar() );	
					
					emc.beginTransaction( AttendanceDetail.class );
					emc.check( detail , CheckPersistType.all );
					emc.commit();
				}

				emc.beginTransaction( AttendanceDetailMobile.class );
				for( AttendanceDetailMobile detailMobile : mobileDetails ) {
					detailMobile.setRecordStatus(1);
					emc.check( detailMobile , CheckPersistType.all );
				}
				emc.commit();
				
				List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
				Map<String, Map<String, List<AttendanceStatisticalCycle>>> topUnitAttendanceStatisticalCycleMap = null;
				try {
					attendanceWorkDayConfigList = attendanceWorkDayConfigServiceAdv.listAll();
					topUnitAttendanceStatisticalCycleMap = attendanceStatisticCycleServiceAdv.getCycleMapFormAllCycles( false );
					
					attendanceDetailAnalyseServiceAdv.analyseAttendanceDetail( detail, attendanceWorkDayConfigList, topUnitAttendanceStatisticalCycleMap, false );
					logger.info( ">>>>>>>>>>attendance detail analyse completed.person:" + detail.getEmpName() + ", date:" + detail.getRecordDateString());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	private String getOffDutyTime( List<AttendanceDetailMobile> mobileDetails ) throws Exception {
		Date offDutyTime = null;
		Date signTime = null;
		String offDutyTimeString = null;
		if( ListTools.isNotEmpty( mobileDetails ) && mobileDetails.size() >=2 ) {
			for( AttendanceDetailMobile detailMobile : mobileDetails ) {
				signTime = dateOperation.getDateFromString(detailMobile.getSignTime() );
				if( offDutyTime != null && signTime != null && offDutyTime.before( signTime )) {
					offDutyTime = signTime;
					offDutyTimeString = detailMobile.getSignTime();
				}else if( offDutyTime == null ){
					offDutyTime = signTime;
					offDutyTimeString = detailMobile.getSignTime();
				}
			}
		}
		return offDutyTimeString;
	}

	private String getOnDutyTime(List<AttendanceDetailMobile> mobileDetails) throws Exception {
		Date onDutyTime = null;
		Date signTime = null;
		String onDutyTimeString = null;
		for( AttendanceDetailMobile detailMobile : mobileDetails ) {
			signTime = dateOperation.getDateFromString(detailMobile.getSignTime() );
			if( onDutyTime != null && signTime != null && onDutyTime.after( signTime )) {
				onDutyTime = signTime;
				onDutyTimeString = detailMobile.getSignTime();
			}else if( onDutyTime == null ){
				onDutyTime = signTime;
				onDutyTimeString = detailMobile.getSignTime();
			}
		}
		return onDutyTimeString;
	}
	
}
