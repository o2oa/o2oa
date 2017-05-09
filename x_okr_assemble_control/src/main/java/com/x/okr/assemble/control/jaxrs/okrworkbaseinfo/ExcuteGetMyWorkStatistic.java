package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.UserNoLoginException;

public class ExcuteGetMyWorkStatistic extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteGetMyWorkStatistic.class );
	
	protected ActionResult<WrapOutOkrWorkStatistic> execute( HttpServletRequest request,EffectivePerson effectivePerson ) throws Exception {
		ActionResult<WrapOutOkrWorkStatistic> result = new ActionResult<>();
		WrapOutOkrWorkStatistic wrap = new WrapOutOkrWorkStatistic();
		
		Long responWorkTotal = 0L;
		Long responProcessingWorkCount = 0L;
		Long responCompletedWorkCount = 0L;
		Long responOvertimeWorkCount = 0L;
		Long draftWorkCount = 0L;
		
		Long overtimeResponWorkCount = 0L;
		Long overtimeCooperWorkCount = 0L;
		Long overtimeDeployWorkCount = 0L;
		
		Long overtimenessResponWorkCount = 0L;
		Long overtimenessCooperWorkCount = 0L;
		Long overtimenessDeployWorkCount = 0L;
		
		
		Double percent = 0.0;
		String identity = null;
		List<String> status = new ArrayList<String>();
		status.add( "正常" );
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}		
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName()  );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			identity = okrUserCache.getLoginIdentityName() ;
		}
		//根据登录用户身份进行数据统计查询
		if( check ){
			try{
				//我负责的工作总数
				responWorkTotal = okrWorkPersonService.getWorkTotalByCenterId( identity, status, "责任者" );
				//我负责的执行中的工作总数
				responProcessingWorkCount = okrWorkPersonService.getProcessingWorkCountByCenterId( identity, status, "责任者" );
				//我负责的完成的工作总数
				responCompletedWorkCount = okrWorkPersonService.getCompletedWorkCountByCenterId( identity, status, "责任者" );
				//我负责的已超期的工作总数
				responOvertimeWorkCount = okrWorkPersonService.getOvertimeWorkCountByCenterId( identity, status, "责任者" );
				//我的工作草稿数
				draftWorkCount = okrWorkPersonService.getDraftWorkCountByCenterId( identity, status, "责任者" );
				//已超期的工作中， 我部署的，我负责的， 我协助的的
				overtimeResponWorkCount = okrWorkPersonService.getOvertimeWorkCountByCenterId( identity, status, "责任者" );
				overtimeCooperWorkCount = okrWorkPersonService.getOvertimeWorkCountByCenterId( identity, status, "协助者" );
				overtimeDeployWorkCount = okrWorkPersonService.getOvertimeWorkCountByCenterId( identity, status, "部署者" );
				//未超期的工作中， 我部署的，我负责的， 我协助的的
				overtimenessResponWorkCount = okrWorkPersonService.getOvertimenessWorkCountByCenterId( identity, status, "责任者" );
				overtimenessCooperWorkCount = okrWorkPersonService.getOvertimenessWorkCountByCenterId( identity, status, "协助者" );
				overtimenessDeployWorkCount = okrWorkPersonService.getOvertimenessWorkCountByCenterId( identity, status, "部署者" );
				
				if( responWorkTotal > 0 ){
					percent = ( (double)responWorkTotal - (double)responOvertimeWorkCount ) / (double)responWorkTotal;
				}
			}catch(Exception e){
				logger.warn( "system count my okrWorkBaseInfo got an exception." );
				result.error( e );
			}
		}
		
		if( check ){
			wrap.setPercent( percent );
			wrap.setResponWorkTotal(responWorkTotal);
			wrap.setResponProcessingWorkCount(responProcessingWorkCount);
			wrap.setResponCompletedWorkCount(responCompletedWorkCount);
			wrap.setDraftWorkCount( draftWorkCount );
			
			wrap.setOvertimeResponWorkCount(overtimeResponWorkCount);
			wrap.setOvertimeCooperWorkCount(overtimeCooperWorkCount);
			wrap.setOvertimeDeployWorkCount(overtimeDeployWorkCount);
			
			wrap.setOvertimenessResponWorkCount(overtimenessResponWorkCount);
			wrap.setOvertimenessCooperWorkCount(overtimenessCooperWorkCount);
			wrap.setOvertimenessDeployWorkCount(overtimenessDeployWorkCount);
			
		}
		result.setData( wrap );
		
		return result;
	}
	
}