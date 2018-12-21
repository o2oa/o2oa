package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionUserNoLogin;

public class ActionGetMyWorkStatistic extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGetMyWorkStatistic.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = new Wo();
		
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
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}		
		}		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName()  );
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
	
	public static class Wo  {
		
		public static List<String> Excludes = new ArrayList<String>();
		
		private String name = null;
		private Long responWorkTotal = 0L;
		private Long responProcessingWorkCount = 0L;
		private Long responCompletedWorkCount = 0L;
		private Long draftWorkCount = 0L;
		private Long overtimeResponWorkCount = 0L;
		private Long overtimeCooperWorkCount = 0L;
		private Long overtimeDeployWorkCount = 0L;
		private Long overtimenessResponWorkCount = 0L;
		private Long overtimenessCooperWorkCount = 0L;
		private Long overtimenessDeployWorkCount = 0L;
		private Double percent = 0.0;
		public String getName() {
			return name;
		}
		public Long getResponWorkTotal() {
			return responWorkTotal;
		}
		public Long getResponProcessingWorkCount() {
			return responProcessingWorkCount;
		}
		public Long getResponCompletedWorkCount() {
			return responCompletedWorkCount;
		}
		public Long getDraftWorkCount() {
			return draftWorkCount;
		}
		public Long getOvertimeResponWorkCount() {
			return overtimeResponWorkCount;
		}
		public Long getOvertimeCooperWorkCount() {
			return overtimeCooperWorkCount;
		}
		public Long getOvertimeDeployWorkCount() {
			return overtimeDeployWorkCount;
		}
		public Long getOvertimenessResponWorkCount() {
			return overtimenessResponWorkCount;
		}
		public Long getOvertimenessCooperWorkCount() {
			return overtimenessCooperWorkCount;
		}
		public Long getOvertimenessDeployWorkCount() {
			return overtimenessDeployWorkCount;
		}
		public Double getPercent() {
			return percent;
		}
		public void setName(String name) {
			this.name = name;
		}
		public void setResponWorkTotal(Long responWorkTotal) {
			this.responWorkTotal = responWorkTotal;
		}
		public void setResponProcessingWorkCount(Long responProcessingWorkCount) {
			this.responProcessingWorkCount = responProcessingWorkCount;
		}
		public void setResponCompletedWorkCount(Long responCompletedWorkCount) {
			this.responCompletedWorkCount = responCompletedWorkCount;
		}
		public void setDraftWorkCount(Long draftWorkCount) {
			this.draftWorkCount = draftWorkCount;
		}
		public void setOvertimeResponWorkCount(Long overtimeResponWorkCount) {
			this.overtimeResponWorkCount = overtimeResponWorkCount;
		}
		public void setOvertimeCooperWorkCount(Long overtimeCooperWorkCount) {
			this.overtimeCooperWorkCount = overtimeCooperWorkCount;
		}
		public void setOvertimeDeployWorkCount(Long overtimeDeployWorkCount) {
			this.overtimeDeployWorkCount = overtimeDeployWorkCount;
		}
		public void setOvertimenessResponWorkCount(Long overtimenessResponWorkCount) {
			this.overtimenessResponWorkCount = overtimenessResponWorkCount;
		}
		public void setOvertimenessCooperWorkCount(Long overtimenessCooperWorkCount) {
			this.overtimenessCooperWorkCount = overtimenessCooperWorkCount;
		}
		public void setOvertimenessDeployWorkCount(Long overtimenessDeployWorkCount) {
			this.overtimenessDeployWorkCount = overtimenessDeployWorkCount;
		}
		public void setPercent(Double percent) {
			this.percent = percent;
		}
		
	}
}