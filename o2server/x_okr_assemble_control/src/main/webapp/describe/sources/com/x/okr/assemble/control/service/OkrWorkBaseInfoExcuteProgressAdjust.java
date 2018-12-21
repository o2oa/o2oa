package com.x.okr.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkPerson;
import com.x.okr.entity.OkrWorkReportBaseInfo;

/**
 * 工作进展情况调整
 * @author liyi
 *
 */
public class OkrWorkBaseInfoExcuteProgressAdjust {

	
	/**
	 * 根据ID修改工作进展情况，同时修改最后一次生效的汇报进度
	 * 
	 * @param workId
	 * @param percent
	 * 
	 * @throws Exception
	 */
	public void excute( EntityManagerContainer emc, String workId, Integer percent ) throws Exception {
		if( workId == null || workId.isEmpty() ){
			throw new Exception( "workId is null, system can not archive any object." );
		}
		if( percent == null  ){
			percent = 0;
		}
		List<String> ids = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;		
		List<OkrWorkPerson> okrWorkPersonList = null;
		Business business = new Business(emc);

		emc.beginTransaction( OkrWorkBaseInfo.class );
		emc.beginTransaction( OkrWorkPerson.class );
		emc.beginTransaction( OkrWorkReportBaseInfo.class );
		
		okrWorkBaseInfo = emc.find( workId, OkrWorkBaseInfo.class );
		
		if( okrWorkBaseInfo != null ){
			okrWorkReportBaseInfo = business.okrWorkReportBaseInfoFactory().getLastCompletedReport( workId );
			
			if( !"草稿".equals( okrWorkBaseInfo.getWorkProcessStatus() )){
				if( percent == 100 ){
					okrWorkBaseInfo.setCompleteTime( new Date() );
					okrWorkBaseInfo.setIsCompleted( true );
					okrWorkBaseInfo.setOverallProgress( percent );
					okrWorkBaseInfo.setWorkProcessStatus( "已完成" );
					if( okrWorkReportBaseInfo != null ){
						okrWorkReportBaseInfo.setProgressPercent( percent );
						okrWorkReportBaseInfo.setIsWorkCompleted( true );
						emc.check( okrWorkReportBaseInfo, CheckPersistType.all );
					}
				}else{
					okrWorkBaseInfo.setIsCompleted( false );
					okrWorkBaseInfo.setOverallProgress( percent );
					okrWorkBaseInfo.setWorkProcessStatus( "执行中" );
					if( okrWorkReportBaseInfo != null ){
						okrWorkReportBaseInfo.setProgressPercent( percent );
						okrWorkReportBaseInfo.setIsWorkCompleted( false );
						emc.check( okrWorkReportBaseInfo, CheckPersistType.all );
					}
				}
				emc.check( okrWorkBaseInfo, CheckPersistType.all );
			}
		}
	
		ids = business.okrWorkPersonFactory().listByWorkId( workId, null );
		if( ids != null && ids.size() > 0 ){
			okrWorkPersonList = business.okrWorkPersonFactory().list(ids);
			for( OkrWorkPerson okrWorkPerson : okrWorkPersonList ){
				if( okrWorkPerson != null ){
					okrWorkPerson.setStatus( "正常" );
					if( percent == 100 ){
						okrWorkPerson.setIsCompleted( true );
						okrWorkPerson.setWorkProcessStatus( "已完成" );
					}else{
						okrWorkPerson.setIsCompleted( false );
						okrWorkPerson.setWorkProcessStatus( "执行中" );
					}
					emc.check( okrWorkPerson, CheckPersistType.all );
				}
			}
		}

		emc.commit();
	}
}