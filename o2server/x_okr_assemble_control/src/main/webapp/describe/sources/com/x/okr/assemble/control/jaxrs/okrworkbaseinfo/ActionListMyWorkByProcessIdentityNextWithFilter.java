package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.WorkCommonSearchFilter;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.ExceptionSystemWorkManagerCheck;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionWorkBaseInfoProcess;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;

public class ActionListMyWorkByProcessIdentityNextWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListMyWorkByProcessIdentityNextWithFilter.class );
	
	protected ActionResult<List<WoOkrWorkBaseSimpleInfo>> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id, Integer count, WorkCommonSearchFilter wrapIn ) throws Exception {
		ActionResult<List<WoOkrWorkBaseSimpleInfo>> result = new ActionResult<>();
		List<WoOkrWorkBaseSimpleInfo> wraps = null;
		List<OkrWorkBaseInfo> okrWorkBaseInfoList = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		OkrWorkDetailInfo detail = null;
		List<String> ids = null;
		List<String> workOperation = null;
		List<String> workProcessIndentity = null;
		Boolean viewAble = true;//是否允许查看工作详情
		Boolean editAble = false; //是否允许编辑工作信息
		Boolean splitAble = false; //是否允许拆解工作
		Boolean authorizeAble = false; //是否允许进行授权
		Boolean tackbackAble = false; //是否允许被收回
		Boolean reportAble = false; //是否允许进行汇报
		Boolean deleteAble = false; //是否允许删除工作
		Boolean archiveAble = false; //是否允许归档工作
		Boolean appraiseAble = false; //是否允许启动工作考核
		Boolean progressAdjust = false; //是否允许调整工作进展
		Boolean isTopUnitWorkAdmin = false; //是否是顶层组织工作管理员
		
		String work_dismantling = "CLOSE";
		String work_authorize = "CLOSE";
		String report_usercreate = "CLOSE";
		Integer appraise_max_times = 0;
		
		String APPRAISE_MAX_TIMES = "0";
		Long total = 0L;
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
				APPRAISE_MAX_TIMES = okrConfigSystemService.getValueWithConfigCode( "APPRAISE_MAX_TIMES" );
				//System.out.println("APPRAISE_MAX_TIMES=" + APPRAISE_MAX_TIMES);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "根据指定的Code查询系统配置时发生异常。Code:" + "APPRAISE_MAX_TIMES" );
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
				appraise_max_times = Integer.parseInt( APPRAISE_MAX_TIMES );
			} catch (Exception e) {
				e.printStackTrace();
				logger.warn("系统配置APPRAISE_MAX_TIMES不是合法数字。" );
				appraise_max_times = 0;
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
				//信息状态要是正常的，已删除的数据不需要查询出来
				wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
				
				okrWorkBaseInfoList = okrWorkBaseInfoService.listWorkNextWithFilter( id, count, wrapIn );

				//从数据库中查询符合条件的对象总数
				total = okrWorkBaseInfoService.getWorkCountWithFilter( wrapIn );
				wraps = WoOkrWorkBaseSimpleInfo.copier.copy( okrWorkBaseInfoList );

				result.setCount( total );
				for( WoOkrWorkBaseSimpleInfo wrap : wraps ){
					workProcessIndentity = new ArrayList<>();
					workOperation = new ArrayList<>();
					appraiseAble = false;
					viewAble = true;//是否允许查看工作详情
					editAble = false; //是否允许编辑工作信息
					splitAble = false; //是否允许拆解工作
					authorizeAble = false; //是否允许进行授权
					tackbackAble = false; //是否允许被收回
					reportAble = false; //是否允许进行汇报
					deleteAble = false; //是否允许删除工作
					
					detail = okrWorkDetailInfoService.get( wrap.getId() );
					if( wrap.getAppraiseTimes() == null ) {
						wrap.setAppraiseTimes(0);
					}
					if( okrUserCache.isOkrManager() || isTopUnitWorkAdmin ){
						//如果用户是管理,或者是部署者,可以对执行中的，不是草稿的工作进行工作进度调整
						if( !"草稿".equals(  wrap.getWorkProcessStatus() ) && !"已归档".equals( wrap.getStatus() ) ){
							progressAdjust = true;
							//如果未归档的工作，工作管理员可以启动考核
							if( okrUserCache.isOkrManager() || isTopUnitWorkAdmin ){//如果用户是管理,或者是部署者
								if( !"审核中".equals( wrap.getCurrentAppraiseStatus() )) {
									if( appraise_max_times > 0 && wrap.getAppraiseTimes() < appraise_max_times ) {
										appraiseAble = true;
									}
//									else {
//										System.out.println("考核次数已经达到最大值， appraise_max_times=" + appraise_max_times);
//									}
								}
							}
						}
					}
					
					//判断工作是否已经完成
					if( wrap.getIsCompleted() || wrap.getOverallProgress() == 1 ) {
						workProcessIndentity.add( "COMPLETED" );//工作已经完成
					}
	
					if( detail != null ){
						Integer maxCharCount = wrapIn.getMaxCharacterNumber();
						if( maxCharCount == null ){
							maxCharCount = 30;
						}
						if( maxCharCount != -1 && detail.getProgressAction() != null && detail.getProgressAction().length() > maxCharCount ){
							wrap.setProgressAction( detail.getProgressAction().substring(0, maxCharCount) + "..." );
						}else{
							wrap.setProgressAction( detail.getProgressAction() );
						}
						if( maxCharCount != -1 && detail.getWorkDetail() != null && detail.getWorkDetail().length() > maxCharCount ){
							wrap.setWorkDetail( detail.getWorkDetail().substring(0, maxCharCount) + "..." );
						}else{
							wrap.setWorkDetail( detail.getWorkDetail() );
						}
					}
					
					//获取该工作中当前责任人相关的授权信息
					okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.getLastAuthorizeRecord( wrap.getId(), okrUserCache.getLoginIdentityName(), null );
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
					
					//判断工作处理职责身份: NONE(无权限)|VIEW(观察者)|READ(阅知者)|COOPERATE(协助者)|RESPONSIBILITY(责任者)
					workProcessIndentity.add("VIEW");//能查出来肯定可见
					viewAble = true;
					if ( okrWorkProcessIdentityService.isMyDeployWork( okrUserCache.getLoginIdentityName(), wrap.getId() )){
						workProcessIndentity.add("DEPLOY");//判断工作是否由我部署
						if( "草稿".equals(  wrap.getWorkProcessStatus() )){
							editAble = true; //工作的部署者可以进行工作信息编辑， 草稿状态下可编辑，部署下去了就不能编辑了
						}else{
							if( !"已归档".equals( wrap.getStatus() )){
								if( okrUserCache.isOkrManager() || isTopUnitWorkAdmin ){//如果用户是管理,或者是部署者
									archiveAble = true;
								}
							}
						}
						try{
							//部署者在该工作没有部署下级工作（被下级拆解）的情况下,如果工作未被归档，可以删除
							ids = okrWorkBaseInfoService.getSubNormalWorkBaseInfoIds( wrap.getId() );
							if( ids == null || ids.isEmpty() ){
								if( !"已归档".equals( wrap.getStatus() )){
									deleteAble = true; //部署者的工作 在 未被拆解和未被授权的情况下,可以被删除
								}else {
									if( !workProcessIndentity.contains("ARCHIVE")) {
										workProcessIndentity.add("ARCHIVE");//工作已归档
									}
								}
							}else{
								wrap.setHasSubWorks( true );
							}
						}catch( Exception e ){
							logger.warn( "system list sub work ids by workId got an exception.");
							logger.error( e );
						}
					}
					if ( okrWorkProcessIdentityService.isMyReadWork( okrUserCache.getLoginIdentityName(), wrap.getId() )){
						workProcessIndentity.add("READ");//判断工作是否由我阅知
					}					
					if ( okrWorkProcessIdentityService.isMyCooperateWork( okrUserCache.getLoginIdentityName(), wrap.getId() )){
						workProcessIndentity.add("COOPERATE");//判断工作是否由我协助
					}
					if ( okrWorkProcessIdentityService.isMyResponsibilityWork( okrUserCache.getLoginIdentityName(), wrap.getId() )){
						workProcessIndentity.add("RESPONSIBILITY");//判断工作是否由我负责
						//如果该工作未归档 ，正常执行中，那么责任者可以进行工作授权
						if( !"已归档".equalsIgnoreCase( wrap.getStatus() ) ){
							if( !wrap.getIsCompleted() ){
								//未完成的工作
								if( !tackbackAble ){
									authorizeAble = true;
								}
								reportAble = true;
								splitAble = true;
							}
						}else {
							if( !workProcessIndentity.contains("ARCHIVE")) {
								workProcessIndentity.add("ARCHIVE");//工作已归档
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
					if( reportAble ){
						if( "OPEN".equalsIgnoreCase( report_usercreate )){
							workOperation.add( "REPORT" );
						}
					}
					if( archiveAble ){
						workOperation.add( "ARCHIVE" );
					}
					if( progressAdjust ){
						workOperation.add( "PROGRESS" );
					}
					if( deleteAble ){
						workOperation.add( "DELETE" );
					}
					if( appraiseAble ){
						workOperation.add( "APPRAISE" );
					}
					wrap.setWorkProcessIdentity( workProcessIndentity );
					wrap.setOperation( workOperation );
				}
				result.setData( wraps );
			}catch(Exception e){
				Exception exception = new ExceptionWorkBaseInfoProcess( e, "系统根据条件进行数据列表查询时发生异常!" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WoOkrWorkBaseSimpleInfo>() );
		}
		
		return result;
	}
	
}