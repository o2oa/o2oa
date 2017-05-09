package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.CenterWorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.CenterWorkQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.SystemConfigQueryByCodeException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.UserOrganizationQueryException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportMaxReportCountQueryException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportSaveException;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ExcuteSaveDraftReport extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteSaveDraftReport.class );

	protected ActionResult<WrapOutId> execute( HttpServletRequest request,EffectivePerson effectivePerson, String workAdminIdentity, WrapInOkrWorkReportBaseInfo wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		Integer maxReportCount = null;
		List<String> ids = null;
		Boolean check = true;
		String report_auto_over = "CLOSE";
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		}catch(Exception e){
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
		}
		
		//校验工作ID是否存在
		if( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ){
			check = false;
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
		}
				
		//设置当前登录用户为创建工作汇报的用户
		if (check) {
			try {
				wrapIn.setCreatorName( effectivePerson.getName() );
				wrapIn.setCreatorIdentity( okrUserManagerService.getFistIdentityNameByPerson(effectivePerson.getName()) );
			} catch (Exception e) {
				check = false;
				Exception exception = new UserOrganizationQueryException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				wrapIn.setCreatorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity(wrapIn.getCreatorIdentity()));
			} catch (Exception e) {
				check = false;
				Exception exception = new UserOrganizationQueryException( e, wrapIn.getCreatorIdentity() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				wrapIn.setCreatorCompanyName( okrUserManagerService.getCompanyNameByIdentity(wrapIn.getCreatorIdentity()) );
			} catch (Exception e) {
				check = false;
				Exception exception = new UserOrganizationQueryException( e, wrapIn.getCreatorIdentity() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		//补充工作相关信息标题
		if( check ){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
				if( okrWorkBaseInfo != null ){
					wrapIn.setWorkType( okrWorkBaseInfo.getWorkType() );
					wrapIn.setWorkTitle( okrWorkBaseInfo.getTitle() );
				}else{
					check = false;
					Exception exception = new WorkNotExistsException( wrapIn.getWorkId() );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkQueryByIdException( e, wrapIn.getWorkId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		//补充中心工作相关信息
		if( check ){
			try {
				okrCenterWorkInfo = okrCenterWorkInfoService.get( okrWorkBaseInfo.getCenterId() );
				if( okrCenterWorkInfo != null ){
					wrapIn.setCenterId( okrCenterWorkInfo.getId() );
					wrapIn.setCenterTitle( okrCenterWorkInfo.getTitle() );
				}else{
					check = false;
					Exception exception = new CenterWorkNotExistsException( okrWorkBaseInfo.getCenterId() );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new CenterWorkQueryByIdException( e, okrWorkBaseInfo.getCenterId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);	
			}
		}	
		//补充汇报人信息
		if( check ){
			//校验汇报者姓名
			wrapIn.setReporterName( okrUserCache.getLoginUserName() );
			wrapIn.setReporterIdentity( okrUserCache.getLoginIdentityName() );
			wrapIn.setReporterOrganizationName( okrUserCache.getLoginUserOrganizationName() );
			wrapIn.setReporterCompanyName( okrUserCache.getLoginUserCompanyName() );
			wrapIn.setCurrentProcessorName( okrUserCache.getLoginUserName() );
			wrapIn.setCurrentProcessorIdentity( okrUserCache.getLoginIdentityName() );
			wrapIn.setCurrentProcessorOrganizationName( okrUserCache.getLoginUserOrganizationName() );
			wrapIn.setCurrentProcessorCompanyName( okrUserCache.getLoginUserCompanyName() );
		}
		//补充状态信息
		if( check ){
			//草稿|管理员督办|领导批示|已完成
			wrapIn.setProcessStatus( "草稿" );
			wrapIn.setStatus( "正常" );
		}
		if( check ){
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get( wrapIn.getId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportQueryByIdException( e, okrWorkBaseInfo.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( okrWorkReportBaseInfo == null ){
				//如果是保存新的草稿，那么补充汇报标题以及汇报次数信息
				if( wrapIn.getReportCount() == null || wrapIn.getReportCount() == 0 ){
					try {
						maxReportCount = okrWorkReportOperationService.getMaxReportCount( okrWorkBaseInfo.getId() );
						wrapIn.setReportCount( ( maxReportCount + 1 ) );
					} catch (Exception e) {
						check = false;
						Exception exception = new WorkReportMaxReportCountQueryException( e, okrWorkBaseInfo.getId() );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
				}
				
				if( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty() ){
					//根据已知信息组织汇报标题和汇简要标题
					wrapIn.setTitle(  okrWorkBaseInfo.getTitle() );
					wrapIn.setShortTitle( "第" + wrapIn.getReportCount() + "次工作汇报" );
				}
				
				//草稿|管理员督办|领导批示|已完成
				wrapIn.setProcessStatus( "草稿" );
				wrapIn.setStatus( "正常" );
			}else{
				wrapIn.setReportCount( okrWorkReportBaseInfo.getReportCount() );
				wrapIn.setTitle(  okrWorkReportBaseInfo.getTitle() );
				wrapIn.setShortTitle( okrWorkReportBaseInfo.getShortTitle() );
				wrapIn.setProcessStatus( okrWorkReportBaseInfo.getProcessStatus() );
				wrapIn.setStatus( okrWorkReportBaseInfo.getStatus() );
			}
		}
		
		if( check ){
			try {
				report_auto_over = okrConfigSystemService.getValueWithConfigCode( "REPORT_AUTO_OVER" );
			} catch (Exception e) {
				check = false;
				Exception exception = new SystemConfigQueryByCodeException( e, "REPORT_AUTO_OVER" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( report_auto_over != null && "OPEN".equals( report_auto_over )){
				//根据配置查询该工作所有正在流转中的工作汇报ID列表,包括草稿
				try {
					ids = okrWorkReportQueryService.listProcessingReportIdsByWorkId( okrWorkBaseInfo.getId() );
					if (ids != null && !ids.isEmpty() ) {
						for ( String id : ids ) {
							if( wrapIn != null && wrapIn.getId() !=null && wrapIn.getId().trim().length() > 20 ){
								if( !id.equals( wrapIn.getId() )){
									okrWorkReportFlowService.dispatchToOver(id);
								}
							}
						}
					}
				} catch (Exception e) {
					logger.warn( "system dispatch processing report to over got an exception." );
					logger.error(e);
				}
			}
			try {
				okrWorkReportBaseInfo = okrWorkReportOperationService.save( wrapIn );
				result.setData( new WrapOutId( okrWorkReportBaseInfo.getId() ));
			} catch (Exception e) {
				Exception exception = new WorkReportSaveException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
				
			}
		}
		return result;
	}
	
}