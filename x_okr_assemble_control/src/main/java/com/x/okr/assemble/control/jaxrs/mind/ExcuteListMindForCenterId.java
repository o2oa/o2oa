package com.x.okr.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteListMindForCenterId extends ExcuteBase {
	protected BeanCopyTools<OkrCenterWorkInfo, WrapOutOkrCenterWorkSimpleInfo> okrCenterSimpleInfo_wrapout_copier = BeanCopyToolsBuilder.create( OkrCenterWorkInfo.class, WrapOutOkrCenterWorkSimpleInfo.class, null, WrapOutOkrCenterWorkSimpleInfo.Excludes);
	private Logger logger = LoggerFactory.getLogger( ExcuteListMindForCenterId.class );
	
	protected ActionResult<WrapOutOkrCenterWorkSimpleInfo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String centerId ) throws Exception {
		ActionResult<WrapOutOkrCenterWorkSimpleInfo> result = new ActionResult<WrapOutOkrCenterWorkSimpleInfo>();
		List<WrapOutOkrWorkBaseSimpleInfo> wrapsWorkBaseInfoList_for_center = new ArrayList<WrapOutOkrWorkBaseSimpleInfo>();
		List<WrapOutOkrWorkBaseSimpleInfo> viewWorkBaseInfoList = new ArrayList<WrapOutOkrWorkBaseSimpleInfo>();
		WrapOutOkrWorkBaseSimpleInfo wrapOutOkrWorkBaseSimpleInfo = null;
		WrapOutOkrCenterWorkSimpleInfo center = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo  = null;
		//Boolean hasNoneSubmitReport = false;
		OkrUserCache  okrUserCache  = null;
		Boolean check = true;
		List<String> myWorkIds = null;
		List<String> query_statuses = new ArrayList<String>();
		query_statuses.add( "正常" );
		query_statuses.add( "已归档" );
		
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){//查询中心工作信息是否存在
			okrCenterWorkInfo = okrCenterWorkInfoService.get( centerId );
			if( okrCenterWorkInfo == null ){
				check = false;
				Exception exception = new CenterWorkNotExistsException( effectivePerson.getName() );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){//查询中心工作信息是否存在
			center = new WrapOutOkrCenterWorkSimpleInfo();
			center.setDefaultCompleteDateLimitStr( okrCenterWorkInfo.getDefaultCompleteDateLimitStr() );
			center.setProcessStatus( okrCenterWorkInfo.getProcessStatus() );
			center.setStatus( okrCenterWorkInfo.getStatus() );
			center.setDefaultWorkType( okrCenterWorkInfo.getDefaultWorkType() );
			center.setDescription( okrCenterWorkInfo.getDescription() );
			center.setId( okrCenterWorkInfo.getId() );
			center.setTitle( okrCenterWorkInfo.getTitle() );
			center.setCreateTime( okrCenterWorkInfo.getCreateTime() );
			center.setWatch( true );
		}
		
		if( check ){//获取用户可以看到的所有具体工作信息（有观察者身份的）
			if( !okrUserCache.isOkrSystemAdmin() ){
				try{
					myWorkIds = okrWorkPersonService.listDistinctWorkIdsWithMe( okrUserCache.getLoginIdentityName(), centerId );
				}catch( Exception e ){
					result.error( e );
					Exception exception = new ViewableWorkListException( e, okrUserCache.getLoginIdentityName(), centerId );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				try{
					myWorkIds = okrWorkBaseInfoService.listAllDeployedWorkIds( centerId, null );
				}catch( Exception e ){
					result.error( e );
					Exception exception = new DeployedWorkListAllException( e, centerId );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}				
			}				
		}
		
		if( check ){
			if( myWorkIds != null && !myWorkIds.isEmpty() ){
				for( String workId : myWorkIds ){
					
					if( workId != null && !workId.isEmpty() ){
						okrWorkBaseInfo = okrWorkBaseInfoService.get( workId );
						wrapOutOkrWorkBaseSimpleInfo = new WrapOutOkrWorkBaseSimpleInfo();
						wrapOutOkrWorkBaseSimpleInfo.setWatch( true );
						composeWorkInfo( okrWorkBaseInfo, wrapOutOkrWorkBaseSimpleInfo );
						if( !workContain( wrapOutOkrWorkBaseSimpleInfo, viewWorkBaseInfoList ) ){
							viewWorkBaseInfoList.add( wrapOutOkrWorkBaseSimpleInfo );
						}
						composeParentWork( okrWorkBaseInfo, viewWorkBaseInfoList, myWorkIds );
					}
				}
			}
		}
		
		if( check ){			
			for( WrapOutOkrWorkBaseSimpleInfo wrap_work : viewWorkBaseInfoList ){
				//判断工作是否有未提交的工作汇报
				//hasNoneSubmitReport = false;
				//hasNoneSubmitReport = okrWorkBaseInfoService.hasNoneSubmitReport( wrap_work.getId(), "草稿", "草稿", null );
				//wrap_work.setHasNoneSubmitReport( hasNoneSubmitReport );
				if( wrap_work.getParentId() == null || wrap_work.getParentId().isEmpty() ){
					wrap_work = composeSubWork( viewWorkBaseInfoList, wrap_work );
					wrapsWorkBaseInfoList_for_center.add( wrap_work );
				}
			}
			if( wrapsWorkBaseInfoList_for_center != null && !wrapsWorkBaseInfoList_for_center.isEmpty() ){
				try {
					SortTools.asc( wrapsWorkBaseInfoList_for_center, "completeDateLimitStr" );
				} catch (Exception e) {
					result.error( e );
					logger.error( e, effectivePerson, request, null);
				}
			}
			if( center != null ){
				center.setWorks( wrapsWorkBaseInfoList_for_center );
				
			}
		}
		result.setData( center );
		return result;
	}
    
	private void composeParentWork( OkrWorkBaseInfo okrWorkBaseInfo, List<WrapOutOkrWorkBaseSimpleInfo> viewWorkBaseInfoList, List<String> myWorkIds) throws Exception {
		if( okrWorkBaseInfo == null ){
			return;
		}
		if( myWorkIds == null ){
			return;
		}
		if( viewWorkBaseInfoList == null ){
			viewWorkBaseInfoList = new ArrayList<WrapOutOkrWorkBaseSimpleInfo>();
		}
		if( okrWorkBaseInfo.getParentWorkId() != null && !okrWorkBaseInfo.getParentWorkId().isEmpty() && !okrWorkBaseInfo.getId().equalsIgnoreCase( okrWorkBaseInfo.getParentWorkId() ) ){
			OkrWorkBaseInfo parentWork = null;
			WrapOutOkrWorkBaseSimpleInfo wrapOutOkrWorkBaseSimpleInfo = null;
			parentWork = okrWorkBaseInfoService.get( okrWorkBaseInfo.getParentWorkId() );
			if( parentWork != null ){
				wrapOutOkrWorkBaseSimpleInfo = new WrapOutOkrWorkBaseSimpleInfo();
				composeWorkInfo( parentWork, wrapOutOkrWorkBaseSimpleInfo );
				for( String id : myWorkIds ){
					if( id != null && !id.isEmpty() ){
						if( parentWork.getId().equalsIgnoreCase( id )){
							//是用户自己可以查看的工作, 用户可以点击开
							wrapOutOkrWorkBaseSimpleInfo.setWatch( true );
						}
					}
				}
				
				composeParentWork( parentWork, viewWorkBaseInfoList, myWorkIds );
				
				if( !workContain( wrapOutOkrWorkBaseSimpleInfo, viewWorkBaseInfoList ) ){
					viewWorkBaseInfoList.add( wrapOutOkrWorkBaseSimpleInfo );
				}				
			}
		}		
	}

	private void composeWorkInfo(OkrWorkBaseInfo work, WrapOutOkrWorkBaseSimpleInfo wrapOutOkrWorkBaseSimpleInfo) {
		if( work == null ){
			return;
		}
		if( wrapOutOkrWorkBaseSimpleInfo == null ){
			return;
		}
		wrapOutOkrWorkBaseSimpleInfo.setId( work.getId() );
		wrapOutOkrWorkBaseSimpleInfo.setTitle( work.getTitle() );
		wrapOutOkrWorkBaseSimpleInfo.setWorkProcessStatus( work.getWorkProcessStatus() );
		wrapOutOkrWorkBaseSimpleInfo.setParentId( work.getParentWorkId() );
		wrapOutOkrWorkBaseSimpleInfo.setIsOverTime( work.getIsOverTime() );
		wrapOutOkrWorkBaseSimpleInfo.setIsCompleted( work.getIsCompleted() );
		wrapOutOkrWorkBaseSimpleInfo.setOverallProgress( work.getOverallProgress() );
		wrapOutOkrWorkBaseSimpleInfo.setWorkType( work.getWorkType() );
		wrapOutOkrWorkBaseSimpleInfo.setCompleteDateLimitStr( work.getCompleteDateLimitStr() );
		wrapOutOkrWorkBaseSimpleInfo.setResponsibilityOrganizationName( work.getResponsibilityOrganizationName());
		wrapOutOkrWorkBaseSimpleInfo.setResponsibilityIdentity( work.getResponsibilityIdentity());
		wrapOutOkrWorkBaseSimpleInfo.setResponsibilityEmployeeName( work.getResponsibilityEmployeeName());
		wrapOutOkrWorkBaseSimpleInfo.setCooperateOrganizationName( work.getCooperateOrganizationName() );
		wrapOutOkrWorkBaseSimpleInfo.setCooperateEmployeeName( work.getCooperateEmployeeName() );
		wrapOutOkrWorkBaseSimpleInfo.setCooperateIdentity( work.getCooperateIdentity() );
	}

	private Boolean workContain( WrapOutOkrWorkBaseSimpleInfo wrapOutOkrWorkBaseSimpleInfo, List<WrapOutOkrWorkBaseSimpleInfo> viewWorkBaseInfoList) {
		if( wrapOutOkrWorkBaseSimpleInfo == null ){
			return true;
		}
		if( viewWorkBaseInfoList == null ){
			viewWorkBaseInfoList = new ArrayList<WrapOutOkrWorkBaseSimpleInfo>();
			return false;
		}
		for( WrapOutOkrWorkBaseSimpleInfo info : viewWorkBaseInfoList ){
			if( info.getId().equalsIgnoreCase( wrapOutOkrWorkBaseSimpleInfo.getId() )){
				return true;
			}
		}
		return false;
	}
	
	private WrapOutOkrWorkBaseSimpleInfo composeSubWork( List<WrapOutOkrWorkBaseSimpleInfo> viewWorkBaseInfoList, WrapOutOkrWorkBaseSimpleInfo wrap_work) {
		if( viewWorkBaseInfoList != null && !viewWorkBaseInfoList.isEmpty() ){
			for( WrapOutOkrWorkBaseSimpleInfo work : viewWorkBaseInfoList ){
				if( work.getParentId() != null && !work.getParentId().isEmpty() && work.getParentId().equalsIgnoreCase( wrap_work.getId() )){
				   //说明该工作是wrap_work的下级工作
					work = composeSubWork( viewWorkBaseInfoList, work );
					wrap_work.addNewSubWorkBaseInfo( work );
				}
			}
		}
		return wrap_work;
	}
	
}