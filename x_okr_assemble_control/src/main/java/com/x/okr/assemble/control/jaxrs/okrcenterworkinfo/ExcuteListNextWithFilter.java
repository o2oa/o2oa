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

public class ExcuteListNextWithFilter extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteListNextWithFilter.class );
	
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
		// 对wrapIn里的信息进行校验
		if (check && okrUserCache.getLoginUserOrganizationName() == null) {
			check = false;
			Exception exception = new UserNoLoginException(effectivePerson.getName());
			result.error(exception);
			logger.error(exception, effectivePerson, request, null);
		}
		if( id == null || id.isEmpty() ){
			id = "(0)";
		}			
		if( count == null ){
			count = 12;
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		
		
		//logger.info( ">>>>>>>>>>>query count:" + count );
		if(check){
			if( !okrUserCache.isOkrSystemAdmin() ){
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				wrapIn.addQueryInfoStatus( "正常" );	
				wrapIn.addQueryInfoStatus( "已归档" );
				
				wrapIn.addQueryProcessIdentity( "观察者" );
				//直接从Person里查询已经分页好的中心工作ID
				total = okrCenterWorkQueryService.getCenterCountWithFilter( wrapIn );
				okrCenterWorkInfoList = okrCenterWorkQueryService.listCenterNextWithFilter( id, count, wrapIn );
			}else{
				//如果是管理员，则查询所有的数据
				WrapInFilterCenterWorkInfo wrapIn_admin = new WrapInFilterCenterWorkInfo();
				wrapIn_admin.setDefaultWorkTypes( wrapIn.getWorkTypes() );
				wrapIn_admin.addQueryInfoStatus( "正常" );
				wrapIn_admin.addQueryInfoStatus( "已归档" );
				
				wrapIn.addQueryProcessIdentity( "观察者" );
				//从数据库中查询符合条件的对象总数
				total = okrCenterWorkQueryService.getCountWithFilter( wrapIn_admin );
				okrCenterWorkInfoList = okrCenterWorkQueryService.listNextWithFilter( id, count, wrapIn_admin );
			}
			//logger.info( ">>>>>>>>>>>record total:" + total );
		}
		if(check){
			if( okrCenterWorkInfoList != null && !okrCenterWorkInfoList.isEmpty() ){
				try{
					wraps = wrapout_view_copier.copy( okrCenterWorkInfoList );
				}catch(Throwable th){
					Exception exception = new CenterWorkWrapOutException( th );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			}
		}	
		if(check){
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