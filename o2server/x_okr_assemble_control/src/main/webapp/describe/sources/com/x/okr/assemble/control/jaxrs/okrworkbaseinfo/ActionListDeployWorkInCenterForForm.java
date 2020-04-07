package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.SortTools;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionSystemWorkManagerCheck;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkBaseInfoProcess;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ActionListDeployWorkInCenterForForm extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListDeployWorkInCenterForForm.class );
	
	@SuppressWarnings("unused")
	protected ActionResult<List<WoOkrWorkBaseSimpleInfo>> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		List<WoOkrWorkBaseSimpleInfo> wraps = null;
		List<WoOkrWorkBaseSimpleInfo> wraps_all = null;
		List<WoOkrWorkBaseSimpleInfo> result_wraps = new ArrayList<>();
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		List<OkrWorkBaseInfo> deployOkrWorkBaseInfoList = new ArrayList<OkrWorkBaseInfo>();
		
		//存储装配中的信息
		Map<String, WoOkrWorkBaseSimpleInfo> resultWorkMap = new HashMap<String, WoOkrWorkBaseSimpleInfo>();
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		WoOkrWorkBaseSimpleInfo wrapOutOkrWorkBaseInfo = null;
		WoOkrWorkBaseSimpleInfo wrapOutParentWorkInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkDetailInfo detail = null;
		Integer total = 0;
		List<String> ids = null;
		List<String> workOperation = null;
		List<String> workProcessIndentity = null;
		Boolean viewAble = true;//是否允许查看工作详情
		Boolean editAble = false; //是否允许编辑工作信息
		Boolean splitAble = false; //是否允许拆解工作
		Boolean authorizeAble = false; //是否允许进行授权
		Boolean tackbackAble = false; //是否允许被收回
		Boolean deleteAble = false; //是否允许删除工作
		Boolean archiveAble = false; //是否允许归档工作
		Boolean isTopUnitWorkAdmin = false; //是否是顶层组织工作管理员
		
		String work_dismantling = "CLOSE";
		String work_authorize = "CLOSE";
		String report_usercreate = "CLOSE";
		Set<String> keySet = null;
		Iterator<String> iterator = null;
		List<String> query_statuses = new ArrayList<String>();
		String loginIdentity = null; //当前用户登录身份名称	
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
			try {
				work_dismantling = okrConfigSystemService.getValueWithConfigCode( "WORK_DISMANTLING" );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "根据指定的Code查询系统配置时发生异常。Code:" + "WORK_DISMANTLING" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		if( check ){
			try {
				work_authorize = okrConfigSystemService.getValueWithConfigCode( "WORK_AUTHORIZE" );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "根据指定的Code查询系统配置时发生异常。Code:" + "WORK_AUTHORIZE" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		if( check ){
			try {
				report_usercreate = okrConfigSystemService.getValueWithConfigCode( "REPORT_USERCREATE" );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "根据指定的Code查询系统配置时发生异常。Code:" + "REPORT_USERCREATE" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		if( check ){
			try {
				if( okrUserManagerService.isOkrWorkManager( okrUserCache.getLoginIdentityName() )){
					isTopUnitWorkAdmin = true;
				}
			} catch (Exception e ) {
				Exception exception = new ExceptionSystemWorkManagerCheck( e, okrUserCache.getLoginIdentityName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try{
				okrCenterWorkInfo = okrCenterWorkInfoService.get( id );
			}catch(Exception e){
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "查询指定ID的中心工作信息时发生异常。ID：" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try{	
				loginIdentity = okrUserCache.getLoginIdentityName() ;			
				
				//获取所有当前用户身份部署的工作信息
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkInCenterByIdentity( loginIdentity, id, query_statuses );
				
				//然后遍历所有的可查看的工作，将我部署的工作放在一起:deployOkrWorkBaseInfoList
				if( okrWorkBaseInfoList != null ){
					for( OkrWorkBaseInfo okrWorkBaseInfo : okrWorkBaseInfoList ){
						if( okrWorkBaseInfo.getDeployerIdentity().equals( loginIdentity )){
							deployOkrWorkBaseInfoList.add( okrWorkBaseInfo );
						}
					}
					wraps_all = WoOkrWorkBaseSimpleInfo.copier.copy( okrWorkBaseInfoList );
					wraps = WoOkrWorkBaseSimpleInfo.copier.copy( deployOkrWorkBaseInfoList );
					total = deployOkrWorkBaseInfoList.size();
				}
				
				if( wraps != null ){					
					//组织所有工作的上下级关系
					for( WoOkrWorkBaseSimpleInfo info : wraps ){
						workProcessIndentity = new ArrayList<>();
						workOperation = new ArrayList<>();
						viewAble = true;//是否允许查看工作详情
						editAble = false; //是否允许编辑工作信息
						splitAble = false; //是否允许拆解工作
						authorizeAble = false; //是否允许进行授权
						tackbackAble = false; //是否允许被收回
						archiveAble = false; //是否允许归档工作
						deleteAble = false; //是否允许删除工作
						
						detail = okrWorkDetailInfoService.get( info.getId() );
						if( detail != null ){
							info.setWorkDetail( detail.getWorkDetail() );
						}
						
						if( info.getIsCompleted() || info.getOverallProgress() == 1 ) {
							workProcessIndentity.add( "COMPLETED" );
						}
						
						//获取该工作中当前责任人相关的授权信息
						okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.getLastAuthorizeRecord( info.getId(), okrUserCache.getLoginIdentityName(), null );
						if( okrWorkAuthorizeRecord != null ){
							//工作授权状态: NONE(未授权)|AUTHORING(授权中)|TACKBACK(已收回)|CANCEL(已失效)
							if( "正常".equals( okrWorkAuthorizeRecord.getStatus() ) ){
								workProcessIndentity.add("AUTHORIZE");
								//要判断一下,当前用户是授权人,还是承担人
								if( okrUserCache.getLoginIdentityName().equals( okrWorkAuthorizeRecord.getDelegatorIdentity() )){//授权人
									tackbackAble = true;
								}
							}else if( "已失效".equals( okrWorkAuthorizeRecord.getStatus() ) ){
								workProcessIndentity.add("AUTHORIZECANCEL");
							}else if( "已收回".equals( okrWorkAuthorizeRecord.getStatus() ) ){
								workProcessIndentity.add("TACKBACK");
							}
						}
						
						if( !"已归档".equals( info.getStatus() )){
							if( okrUserCache.isOkrManager() || isTopUnitWorkAdmin ){//如果用户是管理,或者是部署者
								archiveAble = true;
							}
						}
						
						//判断工作处理职责身份: NONE(无权限)|VIEW(观察者)|READ(阅知者)|COOPERATE(协助者)|RESPONSIBILITY(责任者)
						workProcessIndentity.add("VIEW");//能查出来肯定可见
						viewAble = true;
						if ( okrWorkProcessIdentityService.isMyDeployWork( okrUserCache.getLoginIdentityName(), info.getId() )){
							workProcessIndentity.add("DEPLOY");//判断工作是否由我部署
							if( "草稿".equals(  info.getWorkProcessStatus() )){
								editAble = true; //工作的部署者可以进行工作信息编辑， 草稿状态下可编辑，部署下去了就不能编辑了
							}
							
							try{
								//部署者在该工作没有部署下级工作（被下级拆解）的情况下,可以删除
								ids = okrWorkBaseInfoService.getSubNormalWorkBaseInfoIds( info.getId() );
								if( ids == null || ids.isEmpty() ){
									deleteAble = true; //部署者的工作 在 未被拆解和未被授权的情况下,可以被删除
								}else{
									info.setHasSubWorks( true );
								}
							}catch( Exception e ){
								logger.warn( "system list sub work ids by workId got an exception." );
								logger.error(e);
							}
						}
						if ( okrWorkProcessIdentityService.isMyReadWork( okrUserCache.getLoginIdentityName(), info.getId() )){
							workProcessIndentity.add("READ");//判断工作是否由我阅知
						}					
						if ( okrWorkProcessIdentityService.isMyCooperateWork( okrUserCache.getLoginIdentityName(), info.getId() )){
							workProcessIndentity.add("COOPERATE");//判断工作是否由我协助
						}
						if ( okrWorkProcessIdentityService.isMyResponsibilityWork( okrUserCache.getLoginIdentityName(), info.getId() )){
							workProcessIndentity.add("RESPONSIBILITY");//判断工作是否由我负责
							//如果该工作未归档 ，正常执行中，那么责任者可以进行工作授权
							if( !"已归档".equalsIgnoreCase( info.getStatus() ) ){
								if( !info.getIsCompleted() ){
									//未完成的工作
									if( !tackbackAble ){
										authorizeAble = true;
									}
									splitAble = true;
								}
							}
						}
						
						if( viewAble ){
							workOperation.add( "VIEW" );
						}
						if( editAble ){
							workOperation.add( "EDIT" );
						}
						if( splitAble ){
							if( "OPEN".equalsIgnoreCase( work_dismantling )){
								workOperation.add( "SPLIT" );
							}
						}
						if( authorizeAble ){
							if( "OPEN".equalsIgnoreCase( work_authorize )){
								workOperation.add( "AUTHORIZE" );
							}
						}
						if( tackbackAble ){
							if( "OPEN".equalsIgnoreCase( work_authorize )){
								workOperation.add( "TACKBACK" );
							}
						}
						if( archiveAble ){
							workOperation.add( "ARCHIVE" );
						}
						if( deleteAble ){
							workOperation.add( "DELETE" );
						}
						info.setWorkProcessIdentity( workProcessIndentity );
						info.setOperation( workOperation );
						
						//-------------------------------------------------------------------
						//查询工作的上级工作放到resultWorkMap里
						if( info != null && info.getParentWorkId() !=null && !info.getParentWorkId().isEmpty()){
							//先从resultWorkMap里查询上级工作对象
							wrapOutParentWorkInfo = resultWorkMap.get( info.getParentWorkId() );
						}else{
							//没有上级工作
							wrapOutParentWorkInfo = resultWorkMap.get( info.getCenterId() );
						}						
						if( wrapOutParentWorkInfo != null ){
							wrapOutParentWorkInfo.addNewSubWorkBaseInfo( info );
						}else{
							//map里没有上级工作信息，尝试从所有的工作里查询上级工作信息
							for( WoOkrWorkBaseSimpleInfo _info : wraps_all ){
								if( info.getParentWorkId() != null && info.getParentWorkId().equalsIgnoreCase( _info.getId() )){
									wrapOutParentWorkInfo = _info;
								}
							}
							if( wrapOutParentWorkInfo != null ){
								//如果从所有工作中查询到上级工作信息,那么添加到Map里
								wrapOutParentWorkInfo.addNewSubWorkBaseInfo( info );
								resultWorkMap.put( wrapOutParentWorkInfo.getId(), wrapOutParentWorkInfo );
							}
						}
						if( wrapOutParentWorkInfo == null ){
							//如果仍没有找到上级工作,那么使用中心工作作为展现的上级工作
							if( okrCenterWorkInfo != null ){
								wrapOutParentWorkInfo = new WoOkrWorkBaseSimpleInfo();
								wrapOutParentWorkInfo.setWorkOrCenter( "CENTER" );
								wrapOutParentWorkInfo.setId( okrCenterWorkInfo.getId() );
								wrapOutParentWorkInfo.setTitle( okrCenterWorkInfo.getTitle() );
								wrapOutParentWorkInfo.setCenterId( okrCenterWorkInfo.getId() );
								wrapOutParentWorkInfo.setCenterTitle( okrCenterWorkInfo.getTitle() );
								wrapOutParentWorkInfo.setCompleteDateLimitStr( okrCenterWorkInfo.getDefaultCompleteDateLimitStr() );
								wrapOutParentWorkInfo.addNewSubWorkBaseInfo( info );
								resultWorkMap.put( info.getCenterId(), wrapOutParentWorkInfo );
							}
						}
					}				
					keySet = resultWorkMap.keySet();
					iterator = keySet.iterator();
					while( iterator.hasNext() ){
						wrapOutOkrWorkBaseInfo = resultWorkMap.get( iterator.next() );
						if( wrapOutOkrWorkBaseInfo != null ){
							SortTools.asc( wrapOutOkrWorkBaseInfo.getSubWorks(), "createTime" );
							result_wraps.add( wrapOutOkrWorkBaseInfo );
						}
					}
				}
				if( result_wraps != null && !result_wraps.isEmpty() ){
					SortTools.asc( result_wraps, "createTime" );
				}
				result.setCount( Long.valueOf( total + "" ) );
				result.setData( result_wraps );
			}catch(Exception e){
				logger.warn( "system filter okrWorkBaseInfo got an exception." );
				logger.error( e );
				result.error( e );
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WoOkrWorkBaseSimpleInfo>() );
		}
		
		return result;
	}
	
}