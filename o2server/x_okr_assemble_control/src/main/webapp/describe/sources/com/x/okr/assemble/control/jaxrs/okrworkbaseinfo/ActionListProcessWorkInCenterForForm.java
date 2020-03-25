package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

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
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ActionListProcessWorkInCenterForForm extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListProcessWorkInCenterForForm.class );
	
	
	/**
	 * 列表：查看|拆解|授权
	 * 
	 * @param effectivePerson
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	protected ActionResult<List<WoOkrWorkBaseSimpleInfo>> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		List<WoOkrWorkBaseSimpleInfo> result_wraps = new ArrayList<>();
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		WoOkrWorkBaseSimpleInfo wrapOutOkrWorkBaseInfo = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
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
		
		List<String> query_statuses = new ArrayList<String>();
		String loginIdentity = null; //当前用户登录身份名称
		String work_dismantling = "CLOSE";
		String work_authorize = "CLOSE";
		String report_usercreate = "CLOSE";
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
				loginIdentity = okrUserCache.getLoginIdentityName();	
				
				//获取所有当前用户身份在该中心工作中有关系的所有工作信息
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkInCenterByIdentity( loginIdentity, id, query_statuses );
				
				//然后遍历所有的可查看的工作，将上级部署给我，或者需要我参与的工作放在一起， 排除我部署的工作
				if( okrWorkBaseInfoList != null ){
					for( OkrWorkBaseInfo okrWorkBaseInfo : okrWorkBaseInfoList ){
						if( okrWorkBaseInfo.getDeployerIdentity() != null && !okrWorkBaseInfo.getDeployerIdentity().isEmpty() ){
							if( okrWorkBaseInfo.getDeployerIdentity().equals( loginIdentity ) || okrWorkBaseInfo.getDeployerIdentity().indexOf( loginIdentity ) >= 0){
								if( !okrWorkBaseInfo.getResponsibilityIdentity().equals( loginIdentity )){
									continue; //排除我部署给别人的的工作
								}
							}
						}
						workProcessIndentity = new ArrayList<>();
						workOperation = new ArrayList<>();
						viewAble = true;//是否允许查看工作详情
						editAble = false; //是否允许编辑工作信息
						splitAble = false; //是否允许拆解工作
						authorizeAble = false; //是否允许进行授权
						tackbackAble = false; //是否允许被收回
						archiveAble = false; //是否允许归档工作
						deleteAble = false; //是否允许删除工作
						
						wrapOutOkrWorkBaseInfo = WoOkrWorkBaseSimpleInfo.copier.copy( okrWorkBaseInfo );
						
						detail = okrWorkDetailInfoService.get( wrapOutOkrWorkBaseInfo.getId() );
						if( detail != null ){
							wrapOutOkrWorkBaseInfo.setProgressAction( detail.getProgressAction() );
							wrapOutOkrWorkBaseInfo.setWorkDetail( detail.getWorkDetail() );
						}
						
						if( okrWorkBaseInfo.getIsCompleted() || okrWorkBaseInfo.getOverallProgress() == 1 ) {
							workProcessIndentity.add( "COMPLETED" );
						}
						
						//获取该工作中当前责任人相关的授权信息
						okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.getLastAuthorizeRecord( wrapOutOkrWorkBaseInfo.getId(), okrUserCache.getLoginIdentityName(), null );
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
						
						if( !"已归档".equals( okrWorkBaseInfo.getStatus() )){
							if( okrUserCache.isOkrManager() || isTopUnitWorkAdmin ){//如果用户是管理,或者是部署者
								archiveAble = true;
							}
						}
						
						//判断工作处理职责身份: NONE(无权限)|VIEW(观察者)|READ(阅知者)|COOPERATE(协助者)|RESPONSIBILITY(责任者)
						workProcessIndentity.add("VIEW");//能查出来肯定可见
						viewAble = true;
						if ( okrWorkProcessIdentityService.isMyDeployWork( okrUserCache.getLoginIdentityName(), wrapOutOkrWorkBaseInfo.getId() )){
							workProcessIndentity.add("DEPLOY");//判断工作是否由我部署
							if( "草稿".equals(  wrapOutOkrWorkBaseInfo.getWorkProcessStatus() )){
								editAble = true; //工作的部署者可以进行工作信息编辑， 草稿状态下可编辑，部署下去了就不能编辑了
							}
							
							try{
								//部署者在该工作没有部署下级工作（被下级拆解）的情况下,可以删除
								ids = okrWorkBaseInfoService.getSubNormalWorkBaseInfoIds( wrapOutOkrWorkBaseInfo.getId() );
								if( ids == null || ids.isEmpty() ){
									deleteAble = true; //部署者的工作 在 未被拆解和未被授权的情况下,可以被删除
								}else{
									wrapOutOkrWorkBaseInfo.setHasSubWorks( true );
								}
							}catch( Exception e ){
								logger.warn( "system list sub work ids by workId got an exception." );
								logger.error(e);
							}
						}
						if ( okrWorkProcessIdentityService.isMyReadWork( okrUserCache.getLoginIdentityName(), wrapOutOkrWorkBaseInfo.getId() )){
							workProcessIndentity.add("READ");//判断工作是否由我阅知
						}					
						if ( okrWorkProcessIdentityService.isMyCooperateWork( okrUserCache.getLoginIdentityName(), wrapOutOkrWorkBaseInfo.getId() )){
							workProcessIndentity.add("COOPERATE");//判断工作是否由我协助
						}
						if ( okrWorkProcessIdentityService.isMyResponsibilityWork( okrUserCache.getLoginIdentityName(), wrapOutOkrWorkBaseInfo.getId() )){
							workProcessIndentity.add("RESPONSIBILITY");//判断工作是否由我负责
							//如果该工作未归档 ，正常执行中，那么责任者可以进行工作授权
							if( !"已归档".equalsIgnoreCase( wrapOutOkrWorkBaseInfo.getStatus() ) ){
								if( !wrapOutOkrWorkBaseInfo.getIsCompleted() ){
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
						wrapOutOkrWorkBaseInfo.setWorkProcessIdentity( workProcessIndentity );
						wrapOutOkrWorkBaseInfo.setOperation( workOperation );
						result_wraps.add( wrapOutOkrWorkBaseInfo );
					}
					total = result_wraps.size();
				}
				if( result_wraps != null && !result_wraps.isEmpty() ){
					SortTools.asc( result_wraps, "completeDateLimitStr" );
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