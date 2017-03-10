package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrCenterWorkInfo;

public class ExcuteListReadNextWithFilter extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListReadNextWithFilter.class );
	
	protected ActionResult<List<WrapOutOkrCenterWorkViewInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) throws Exception {
		ActionResult<List<WrapOutOkrCenterWorkViewInfo>> result = new ActionResult<>();
		List<WrapOutOkrCenterWorkViewInfo> wraps = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = null;
		Long total = 0L;
		List<String> processIdentities = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		} catch (Exception e) {
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
//		if( wrapIn == null ){
//			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
//		}
		if( check ){
			try{			
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.setProcessIdentities( null );
				wrapIn.setEmployeeNames( null );
				wrapIn.setEmployeeIdentities(null);
				wrapIn.setCompanyNames( null );
				wrapIn.setOrganizationNames( null );
				wrapIn.setInfoStatuses( null );
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				wrapIn.addQueryInfoStatus( "正常" );	
				if( wrapIn.getWorkProcessStatuses() == null ){
					wrapIn.addQueryWorkProcessStatus( "待审核" );
					wrapIn.addQueryWorkProcessStatus( "待确认" );
					wrapIn.addQueryWorkProcessStatus( "执行中" );
					wrapIn.addQueryWorkProcessStatus( "已完成" );
					wrapIn.addQueryWorkProcessStatus( "已撤消" );
				}
				wrapIn.addQueryProcessIdentity( "观察者" );
				okrCenterWorkInfoList = okrCenterWorkQueryService.listCenterNextWithFilter( id, count, wrapIn );
				
				//从数据库中查询符合条件的对象总数
				total = okrCenterWorkQueryService.getCenterCountWithFilter( wrapIn );
			}catch(Throwable th){
				Exception exception = new CenterWorkListFilterException( th, id, count );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if( check ){
			if( okrCenterWorkInfoList != null && !okrCenterWorkInfoList.isEmpty() ){
				try{
					wraps = wrapout_view_copier.copy( okrCenterWorkInfoList );
				}catch(Throwable th){
					check = false;
					Exception exception = new CenterWorkWrapOutException( th );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}	
		if( check ){
			if( wraps != null && !wraps.isEmpty() ){
				for( WrapOutOkrCenterWorkViewInfo wrap : wraps ){
					processIdentities = new ArrayList<>();
					processIdentities.add( "VIEW" );
					if( wrap.getReportAuditLeaderIdentity() != null && !wrap.getReportAuditLeaderIdentity().isEmpty() ){
						if( wrap.getReportAuditLeaderIdentity().indexOf( okrUserCache.getLoginIdentityName() ) > 0 ){
							processIdentities.add("REPORTAUDIT");//汇报审核领导
						}
					}
					if ( okrWorkProcessIdentityService.isMyDeployCenter( okrUserCache.getLoginIdentityName(), wrap.getId() )){
						processIdentities.add("DEPLOY");//判断工作是否由我阅知
					}
					if ( okrWorkProcessIdentityService.isMyReadCenter( okrUserCache.getLoginIdentityName(), wrap.getId() )){
						processIdentities.add("READ");//判断工作是否由我阅知
					}
					List<String> operations = new ExcuteListOperationWithId().execute( request, effectivePerson, okrUserCache, wrap.getId() );
					wrap.setOperation( operations );
					wrap.setWorkProcessIdentity( processIdentities );
				}
			}
			result.setCount( total );
			result.setData( wraps );
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WrapOutOkrCenterWorkViewInfo>() );
		}
		
		return result;
	}
	
}