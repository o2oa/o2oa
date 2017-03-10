package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ExcuteSubmitDraftReport extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSubmitDraftReport.class );

	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, WrapInOkrWorkReportBaseInfo wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		Integer maxReportCount = null;
		String report_progress = null;
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		}catch(Exception e){
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		// 对wrapIn里的信息进行校验
		// 校验工作ID是否存在
		if ( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ) {
			check = false;
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}

		// 设置当前登录用户为创建工作汇报的用户
		if (check) {
			try {
				wrapIn.setCreatorName(effectivePerson.getName());
				wrapIn.setCreatorIdentity(okrUserManagerService.getFistIdentityNameByPerson(effectivePerson.getName()));
			} catch (Exception e) {
				check = false;
				Exception exception = new UserOrganizationQueryException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				wrapIn.setCreatorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity(wrapIn.getCreatorIdentity()));
			} catch (Exception e) {
				check = false;
				Exception exception = new UserOrganizationQueryException( e, wrapIn.getCreatorIdentity() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				wrapIn.setCreatorCompanyName( okrUserManagerService.getCompanyNameByIdentity(wrapIn.getCreatorIdentity()));
			} catch (Exception e) {
				check = false;
				Exception exception = new UserOrganizationQueryException( e, wrapIn.getCreatorIdentity() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
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
					Exception exception = new WorkNotExistsException( wrapIn.getWorkId() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkQueryByIdException( e, wrapIn.getWorkId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
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
					Exception exception = new CenterWorkNotExistsException( okrWorkBaseInfo.getCenterId() );
					result.error( exception );
					logger.error( exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new CenterWorkQueryByIdException( e, okrWorkBaseInfo.getCenterId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);	
			}
		}
		
		// 补充汇报人信息
		if (check) {
			// 校验汇报者姓名
			wrapIn.setReporterName(okrUserCache.getLoginUserName());
			wrapIn.setReporterIdentity(okrUserCache.getLoginIdentityName() );
			wrapIn.setReporterOrganizationName(okrUserCache.getLoginUserOrganizationName());
			wrapIn.setReporterCompanyName(okrUserCache.getLoginUserCompanyName());
		}
		
		if( check && wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( wrapIn.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportQueryByIdException( e, wrapIn.getId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
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
						Exception exception = new WorkReportMaxReportCountQueryException( e, okrWorkBaseInfo.getId() );
						result.error( exception );
						logger.error( exception, effectivePerson, request, null);
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
				okrWorkReportBaseInfo = okrWorkReportFlowService.submitReportInfo( wrapIn, okrCenterWorkInfo, okrWorkBaseInfo);
				//提交完成，需要分析一下工作的进展情况, 用户有可能根据配置要求在汇报过程中提交工作进度数字信息
				try {
					okrWorkBaseInfoService.analyseWorkProgress( okrWorkBaseInfo.getId(), report_progress, dateOperation.getNowDateTime() );
				} catch (Exception e1 ) {
					logger.warn( "system analyse work progres got an exceptin.", e1);
					throw e1;
				}
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				result.setData( new WrapOutId( okrWorkReportBaseInfo.getId()) );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportSubmitException( e, okrWorkReportBaseInfo.getId() );
				result.error( exception );
				logger.error( exception, effectivePerson, request, null);
			}
		}
		return result;
	}
	
}