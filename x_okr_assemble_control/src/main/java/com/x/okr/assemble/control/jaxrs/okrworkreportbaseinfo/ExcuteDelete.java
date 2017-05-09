package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportDeleteException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportQueryByIdException;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		List<OkrTask> taskList = null;
		List<String> taskTargetName = new ArrayList<String>();
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new WorkReportIdEmptyException();
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try{
				okrWorkReportOperationService.delete( id, effectivePerson.getName() );
				result.setData( new WrapOutId( id ));
			}catch(Exception e){
				Exception exception = new WorkReportDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				taskList = okrTaskService.listIdsByReportId( id );
			} catch (Exception e) {
				check = false;
				logger.warn( "system get task by report id got an exception" );
				logger.error(e);
			}
		}
		
		if( check ){
			if( taskList != null && !taskList.isEmpty() ){
				List<String> workTypeList = new ArrayList<String>();
				for( OkrTask task : taskList ){
					if( !taskTargetName.contains( task.getTargetIdentity() )){
						try{
							workTypeList.clear();
							workTypeList.add( task.getWorkType() );
							okrWorkReportTaskCollectService.checkReportCollectTask( task.getTargetIdentity(), workTypeList );
						}catch( Exception e ){
							logger.warn( "待办信息删除成功，但对汇报者进行汇报待办汇总发生异常。");
							logger.error(e);
						}
					}
				}
			}
		}
		
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					okrWorkDynamicsService.reportDynamic(
							okrWorkReportBaseInfo.getCenterId(), 
							okrWorkReportBaseInfo.getTitle(), 
							okrWorkReportBaseInfo.getWorkId(), 
							okrWorkReportBaseInfo.getWorkTitle(), 
							okrWorkReportBaseInfo.getTitle(), 
							id, 
							"删除工作汇报", 
							effectivePerson.getName(), 
							effectivePerson.getName(), 
							effectivePerson.getName(), 
							"删除工作汇报：" + okrWorkReportBaseInfo.getTitle(),
							"工作汇报删除成功！" );
				} catch (Exception e) {
					logger.warn( "okrWorkDynamicsService reportDynamic got an exception" );
					logger.error(e);
				}
			}
		}
		
		return result;
	}
	
}