package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.dataadapter.webservice.sms.SmsMessageOperator;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionCenterWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionCenterWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionUserUnitQuery;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportMaxReportCountQuery;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportSubmit;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ActionSubmitDraftReport extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSubmitDraftReport.class );

	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, WiOkrWorkReportBaseInfo wrapIn ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkAuthorizeRecord okrWorkAuthorizeRecord = null;
		Integer maxReportCount = null;
		String report_progress = null;
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
		}catch(Exception e){
			check = false;
			Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		
		// 对wrapIn里的信息进行校验
		// 校验工作ID是否存在
		if ( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ) {
			check = false;
			Exception exception = new ExceptionWorkIdEmpty();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}

		// 设置当前登录用户为创建工作汇报的用户
		if (check) {
			try {
				wrapIn.setCreatorName(effectivePerson.getDistinguishedName());
				wrapIn.setCreatorIdentity(okrUserManagerService.getIdentityWithPerson(effectivePerson.getDistinguishedName()));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionUserUnitQuery( e, effectivePerson.getDistinguishedName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				wrapIn.setCreatorUnitName( okrUserManagerService.getUnitNameByIdentity(wrapIn.getCreatorIdentity()));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionUserUnitQuery( e, wrapIn.getCreatorIdentity() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				wrapIn.setCreatorTopUnitName( okrUserManagerService.getTopUnitNameByIdentity(wrapIn.getCreatorIdentity()));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionUserUnitQuery( e, wrapIn.getCreatorIdentity() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		// 补充工作相关信息标题
		if (check) {
			try {
				wrapIn.setWorkId( wrapIn.getWorkId() );
				okrWorkBaseInfo = okrWorkBaseInfoService.get(wrapIn.getWorkId());
				if (okrWorkBaseInfo != null) {
					wrapIn.setWorkTitle(okrWorkBaseInfo.getTitle());
				} else {
					check = false;
					Exception exception = new ExceptionWorkNotExists( wrapIn.getWorkId() );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkQueryById( e, wrapIn.getWorkId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		// 补充中心工作相关信息
		if (check) {
			try {
				okrCenterWorkInfo = okrCenterWorkInfoService.get( okrWorkBaseInfo.getCenterId() );
				if (okrCenterWorkInfo != null) {
					wrapIn.setCenterId(okrCenterWorkInfo.getId());
					wrapIn.setCenterTitle(okrCenterWorkInfo.getTitle());
				} else {
					check = false;
					Exception exception = new ExceptionCenterWorkNotExists( okrWorkBaseInfo.getCenterId() );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCenterWorkQueryById( e, okrWorkBaseInfo.getCenterId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);	
			}
		}
		
		// 补充汇报人信息
		if (check) {
			// 校验汇报者姓名
			wrapIn.setReporterName(okrUserCache.getLoginUserName());
			wrapIn.setReporterIdentity(okrUserCache.getLoginIdentityName() );
			wrapIn.setReporterUnitName(okrUserCache.getLoginUserUnitName());
			wrapIn.setReporterTopUnitName(okrUserCache.getLoginUserTopUnitName());
		}
		
		if( check && wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( wrapIn.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkReportQueryById( e, wrapIn.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		// 补充状态信息
		if (check) {
			if ( okrWorkReportBaseInfo == null) {
				// 补充汇报标题以及汇报次数信息
				if (wrapIn.getReportCount() == null || wrapIn.getReportCount() == 0) {
					try {
						maxReportCount = okrWorkReportOperationService.getMaxReportCount(okrWorkBaseInfo.getId());
						wrapIn.setReportCount((maxReportCount + 1));
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionWorkReportMaxReportCountQuery( e, okrWorkBaseInfo.getId() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
				if (wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty()) {
					// 根据已知信息组织汇报标题和汇简要标题
					wrapIn.setTitle( okrWorkBaseInfo.getTitle() );
					wrapIn.setShortTitle( "第" + wrapIn.getReportCount() + "次工作汇报" );
				}
			} else {
				wrapIn.setReportCount(okrWorkReportBaseInfo.getReportCount());
				wrapIn.setTitle(okrWorkReportBaseInfo.getTitle());
				wrapIn.setShortTitle(okrWorkReportBaseInfo.getShortTitle());
			}
			// 草稿|管理员督办|领导批示|已完成
			wrapIn.setProcessStatus( "草稿" );
			wrapIn.setStatus( "正常" );
		}
		
		if( check ){
			try {
				//是否汇报工作的进展进度数字
				report_progress = okrConfigSystemService.getValueWithConfigCode( "REPORT_PROGRESS" );
				if( report_progress == null || report_progress.isEmpty() ){
					report_progress = "CLOSE";
				}
			} catch (Exception e) {
				report_progress = "CLOSE";
				logger.warn( "system get config got an exception." );
				logger.error(e);
			}
		}
		
		if( check ){
			try {
				wrapIn.setSubmitTime( new Date() ); //保存提交时间
				wrapIn.setWorkType( okrWorkBaseInfo.getWorkType() );
				wrapIn.setCreateTime( wrapIn.getSubmitTime() );
				wrapIn.setUpdateTime( wrapIn.getSubmitTime() );
				okrWorkReportBaseInfo = okrWorkReportFlowService.submitReportInfo( wrapIn, okrCenterWorkInfo, okrWorkBaseInfo, wrapIn.getWorkPointAndRequirements(), 
						wrapIn.getProgressDescription(), wrapIn.getWorkPlan(), wrapIn.getAdminSuperviseInfo(), wrapIn.getMemo(), wrapIn.getOpinion() );	
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				result.setData( new Wo( okrWorkReportBaseInfo.getId()) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkReportSubmit( e, okrWorkReportBaseInfo.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				okrWorkBaseInfoService.analyseWorkProgress( okrWorkReportBaseInfo.getWorkId(),  okrWorkReportBaseInfo, report_progress, dateOperation.getNowDateTime() );
			} catch (Exception e ) {
				logger.warn( "system analyse work progres got an exceptin.", e );
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			//发送待阅信息
			//工作负责人提交汇报后，工作的所有授权者都需要收到一条关于该工作汇报的待阅信息，知晓工作负责人已经提交工作汇报
			//向该工作所有的[授权人]发送待阅信息
			if( okrWorkReportBaseInfo != null ) {
				List<String>authorize_ids = okrWorkAuthorizeRecordService.listByWorkId( okrWorkReportBaseInfo.getWorkId() );
				if( authorize_ids != null ){
					for( String authorize_id : authorize_ids ){
						okrWorkAuthorizeRecord = okrWorkAuthorizeRecordService.get( authorize_id );
						if( okrWorkAuthorizeRecord != null ){
							sendWorkReportReadTask( okrWorkReportBaseInfo, okrWorkAuthorizeRecord.getDelegatorIdentity() );
							//给授权人发送短信
							SmsMessageOperator.sendWithPersonName(okrWorkAuthorizeRecord.getDelegatorName(), "工作汇报'"+okrWorkReportBaseInfo.getTitle()+"'已经提交，请查看！");
						}
					}
				}
			}
			//记录工作动态信息
			if( okrWorkReportBaseInfo != null ) {
				WrapInWorkDynamic.sendWithWorkReport( okrWorkReportBaseInfo, 
						effectivePerson.getDistinguishedName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"提交工作汇报", 
						"新的工作汇报信息提交成功！"
				);
				//SmsMessageOperator.send(okrWorkReportBaseInfo.getWorkAdminName(), "工作汇报'"+okrWorkReportBaseInfo.getTitle()+"'已经提交，请查看！");
			}
		}
		return result;
	}
}