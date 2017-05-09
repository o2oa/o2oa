package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.CenterWorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.CenterWorkQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.UserOrganizationQueryException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.WorkReportMaxReportCountQueryException;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteDraftReport extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger( ExcuteDraftReport.class );

	protected ActionResult<WrapOutOkrWorkReportBaseInfo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String workId ) throws Exception {
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		WrapOutOkrWorkReportBaseInfo wrap = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Integer maxReportCount = null;
		String report_progress = "CLOSE";
		Boolean check = true;
		OkrUserCache  okrUserCache  = null;		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getName() );
		}catch(Exception e){
			check = false;
			Exception exception = new GetOkrUserCacheException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
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
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( effectivePerson.getName() );
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		//对wrapIn里的信息进行校验
		//先根据workId获取该工作汇报的草稿信息，如果有，则直接展示内容，如果没有则进行新建操作
		wrap = new WrapOutOkrWorkReportBaseInfo();
		//设置当前登录用户为创建工作汇报的用户
		wrap.setCreatorName( effectivePerson.getName() );
		if( check ){
			try {
				wrap.setCreatorIdentity( okrUserManagerService.getFistIdentityNameByPerson(effectivePerson.getName()) );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new UserOrganizationQueryException( e, effectivePerson.getName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				wrap.setCreatorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( wrap.getCreatorIdentity()));
			} catch ( Exception e ) {
				check = false;
				Exception exception = new UserOrganizationQueryException( e, wrap.getCreatorIdentity() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				wrap.setCreatorCompanyName( okrUserManagerService.getCompanyNameByIdentity( wrap.getCreatorIdentity() ));
			} catch ( Exception e ) {
				check = false;
				Exception exception = new UserOrganizationQueryException( e, wrap.getCreatorIdentity() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			//校验汇报者姓名
			wrap.setReporterName( okrUserCache.getLoginUserName() );
			wrap.setReporterIdentity( okrUserCache.getLoginIdentityName() );
			wrap.setReporterOrganizationName( okrUserCache.getLoginUserOrganizationName() );
			wrap.setReporterCompanyName( okrUserCache.getLoginUserCompanyName() );
			
			wrap.setCurrentProcessorName( okrUserCache.getLoginUserName() );
			wrap.setCurrentProcessorIdentity( okrUserCache.getLoginIdentityName() );
			wrap.setCurrentProcessorOrganizationName( okrUserCache.getLoginUserOrganizationName() );
			wrap.setCurrentProcessorCompanyName( okrUserCache.getLoginUserCompanyName() );
		}		
		//补充工作标题
		if( check ){
			try {
				wrap.setWorkId( workId );
				okrWorkBaseInfo = okrWorkBaseInfoService.get( workId );
				if( okrWorkBaseInfo != null ){
					wrap.setWorkType( okrWorkBaseInfo.getWorkType() );
					wrap.setWorkTitle( okrWorkBaseInfo.getTitle() );
				}else{
					check = false;
					Exception exception = new WorkNotExistsException( workId );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkQueryByIdException( e, workId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);			
			}
		}
		
		//补充中心工作信息
		if( check ){
			try {
				okrCenterWorkInfo = okrCenterWorkInfoService.get( okrWorkBaseInfo.getCenterId() );
				if( okrCenterWorkInfo != null ){
					wrap.setCenterId( okrCenterWorkInfo.getId() );
					wrap.setCenterTitle( okrCenterWorkInfo.getTitle() );
				}else{
					check = false;
					Exception exception = new CenterWorkNotExistsException( okrWorkBaseInfo.getCenterId() );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);	
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new CenterWorkQueryByIdException( e, okrWorkBaseInfo.getCenterId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				maxReportCount = okrWorkReportOperationService.getMaxReportCount( okrWorkBaseInfo.getId() );
				wrap.setReportCount( ( maxReportCount + 1 ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportMaxReportCountQueryException( e, okrWorkBaseInfo.getCenterId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			//草稿|管理员督办|领导批示|已完成
			wrap.setProcessStatus( "草稿" );
			wrap.setStatus( "正常" );
			//根据已知信息组织汇报标题和汇简要标题
			wrap.setTitle(  okrWorkBaseInfo.getTitle() );
			wrap.setShortTitle( "第" + wrap.getReportCount() + "次工作汇报" );
		}
		wrap.setIsCreator(true);
		wrap.setIsReporter(true);
		
		if( "OPEN".equals( report_progress )){
			wrap.setNeedReportProgress( true );
		}else{
			wrap.setNeedReportProgress( false );
		}
		
		result.setData( wrap );
		return result;
	}
	
}