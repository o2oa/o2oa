package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.DateOperation;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;


public class AttendanceAppealInfoService {

	public AttendanceAppealInfo get( EntityManagerContainer emc, String id ) throws Exception {
		if( id == null || id.isEmpty() || "(0)".equals( id )){
    		return null;
    	}
		return emc.find(id, AttendanceAppealInfo.class);
	}
	public List<AttendanceAppealInfo> list(EntityManagerContainer emc, List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
    		return null;
    	}
		return new Business(emc).getAttendanceAppealInfoFactory().list(ids);
	}
	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		AttendanceAppealInfo attendanceAppealInfo = null;
		if( StringUtils.isNotEmpty( id ) && !"(0)".equals( id )){
			attendanceAppealInfo = emc.find(id, AttendanceAppealInfo.class);
			if ( null == attendanceAppealInfo ) {
				throw new Exception("需要删除的申诉信息信息不存在。id=" + id);
			} else {
				emc.beginTransaction( AttendanceAppealInfo.class );
				emc.remove( attendanceAppealInfo, CheckRemoveType.all );
				emc.commit();
			}
		}
	}

	public AttendanceAppealInfo save( EntityManagerContainer emc, AttendanceAppealInfo attendanceAppealInfo ) throws Exception {
		AttendanceDetail attendanceDetail = null;
		AttendanceAppealInfo attendanceAppealInfo_temp = null;
		emc.beginTransaction(AttendanceAppealInfo.class);
		emc.beginTransaction(AttendanceDetail.class);
		attendanceAppealInfo_temp = emc.find( attendanceAppealInfo.getId(), AttendanceAppealInfo.class);
		attendanceDetail = emc.find( attendanceAppealInfo.getDetailId(), AttendanceDetail.class);
		if( attendanceDetail == null ){
			throw new Exception("attendance detail info not exists.");
		}else{
			if ( attendanceAppealInfo_temp != null ) {
				attendanceAppealInfo.copyTo( attendanceAppealInfo_temp, JpaObject.FieldsUnmodify );
				emc.check( attendanceAppealInfo_temp, CheckPersistType.all );				
			}else{
				emc.persist( attendanceAppealInfo, CheckPersistType.all);
			}
			//将打卡记录表里的打卡数据置为正在申诉中
			attendanceDetail.setAppealStatus(1);
			attendanceDetail.setAppealProcessor( attendanceAppealInfo.getCurrentProcessor() );
			attendanceDetail.setAppealReason( attendanceAppealInfo.getAppealReason());
			attendanceDetail.setAppealDescription( attendanceAppealInfo.getAppealDescription());
			emc.check(attendanceDetail, CheckPersistType.all);
			emc.commit();
			return attendanceAppealInfo;
		}
	}

	public AttendanceAppealInfo updateAppealProcessInfoForFirstProcess(EntityManagerContainer emc, String id, String unitName,
			String topUnitName, String processor, Date processTime, String opinion, Integer status, Boolean autoCommit ) throws Exception {
		AttendanceAppealInfo attendanceAppealInfo = emc.find(id, AttendanceAppealInfo.class);
		if( attendanceAppealInfo == null ){
			throw new Exception( "attendanceAppealInfo{'id':'"+ id +"'} not exists." );
		}
		if( autoCommit ){
			emc.beginTransaction(AttendanceAppealInfo.class);
		}
		attendanceAppealInfo.setProcessPersonUnit1( unitName );
		attendanceAppealInfo.setProcessPersonTopUnit1(topUnitName);
		attendanceAppealInfo.setProcessPerson1( processor );
		attendanceAppealInfo.setProcessTime1(new Date());
		attendanceAppealInfo.setOpinion1( opinion );
		attendanceAppealInfo.setStatus( status );
		emc.check(attendanceAppealInfo, CheckPersistType.all);
		
		if( autoCommit ){
			emc.commit();
		}
		return attendanceAppealInfo;
	}

	public AttendanceAppealInfo updateAppealProcessInfoForSecondProcess( EntityManagerContainer emc, String id,
			String unitName, String topUnitName, String processor, Date processTime, String opinion,
			Integer status, Boolean autoCommit ) throws Exception {
		AttendanceAppealInfo attendanceAppealInfo = emc.find(id, AttendanceAppealInfo.class);
		if( attendanceAppealInfo == null ){
			throw new Exception( "attendanceAppealInfo{'id':'"+ id +"'} not exists." );
		}
		if( autoCommit ){
			emc.beginTransaction(AttendanceAppealInfo.class);
		}
		attendanceAppealInfo.setProcessPersonUnit2( unitName );
		attendanceAppealInfo.setProcessPersonTopUnit2(topUnitName);
		attendanceAppealInfo.setProcessPerson2( processor );
		attendanceAppealInfo.setProcessTime2(new Date());
		attendanceAppealInfo.setOpinion2( opinion );
		attendanceAppealInfo.setStatus( status );
		emc.check(attendanceAppealInfo, CheckPersistType.all);
		
		if( autoCommit ){
			emc.commit();
		}
		return attendanceAppealInfo;
	}

	public void archive( EntityManagerContainer emc, String id, String datetime ) throws Exception {
		if( id == null ){
			throw new Exception("id can not be null.");
		}
		if( datetime == null ){
			datetime = new DateOperation().getNowDateTime();
		}
		AttendanceAppealInfo attendanceAppealInfo = emc.find( id, AttendanceAppealInfo.class );
		emc.beginTransaction(AttendanceAppealInfo.class);
		if( attendanceAppealInfo != null ){
			attendanceAppealInfo.setArchiveTime( datetime );
			emc.check(attendanceAppealInfo, CheckPersistType.all);
		}else{
			throw new Exception("attendanceAppealInfo{'id':'"+ id +"'} is not exists.");
		}
		emc.commit();
	}

	
}
