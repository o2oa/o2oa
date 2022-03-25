package com.x.attendance.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;

/**
 * 考勤打卡记录分析服务类
 * @author LIYI
 *
 */
public class AttendanceDetailMobileAnalyseServiceAdv {
	private AttendanceDetailAnalyseService attendanceDetailAnalyseService = new AttendanceDetailAnalyseService();
	private AttendanceDetailMobileAnalyseService attendanceDetailMobileAnalyseService = new AttendanceDetailMobileAnalyseService();
	
	/**
	 * 1、查询该用户是否已经有当天的考勤数据，如果有，则进行时间的更新
	 * 2、如果当前的打卡记录是上班打卡，则只进行记录不进行分析
	 * 3、如果当前的打卡记录是下班打卡，则更新考勤信息后，对考勤记录进行分析
	 * 4、查询该用户是否仍有未分析的考勤数据，如果日期不是今天，那么，全部进行分析
	 *
	 * @param id
	 * @return
	 */
	public Boolean analyseAttendanceDetailMobile( String id, Boolean debugger ) throws Exception {
		Business business = null;
		AttendanceDetail attendanceDetail = null;
		AttendanceDetailMobile attendanceDetailMobile = null;
		List<String> ids = null;
		EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
		try{
			business = new Business(emc);
			attendanceDetailMobile = emc.find( id, AttendanceDetailMobile.class );
			if( attendanceDetailMobile != null ){
				attendanceDetail = attendanceDetailMobileAnalyseService.composeAttendanceDetailMobile( emc, id );
				if( attendanceDetail != null ){
					if( StringUtils.isNotEmpty( attendanceDetail.getOffDutyTime() ) ){
//						attendanceDetailAnalyseService.analyseAttendanceDetail(emc, attendanceDetail, debugger );
					}
				}
				
				ids = business.getAttendanceDetailFactory().listAnalysenessDetailsByEmployee( attendanceDetailMobile.getEmpName() );
				if( ids != null && !ids.isEmpty() ){
					for( String detailId : ids ){
						attendanceDetail = emc.find( detailId, AttendanceDetail.class );
						if( attendanceDetail != null ){
							//只要不是和手机打卡记录同一天的都需要进行分析
							if( !attendanceDetail.getRecordDateString().equals( attendanceDetailMobile.getRecordDateString() )){
								attendanceDetailAnalyseService.analyseAttendanceDetail( emc, attendanceDetail, debugger );
							}
						}
					}
				}
			}
			attendanceDetailMobile = emc.find( id, AttendanceDetailMobile.class );
			if( attendanceDetailMobile != null ){
				emc.beginTransaction( AttendanceDetailMobile.class );
				attendanceDetailMobile.setRecordStatus( 1 );
				emc.check( attendanceDetailMobile, CheckPersistType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			attendanceDetailMobile = emc.find( id, AttendanceDetailMobile.class );
			if( attendanceDetailMobile != null ){
				emc.beginTransaction( AttendanceDetailMobile.class );
				attendanceDetailMobile.setRecordStatus( -1 );
				emc.check( attendanceDetailMobile, CheckPersistType.all );
				emc.commit();
			}
			throw e;
		}
		return null;
	}
}
