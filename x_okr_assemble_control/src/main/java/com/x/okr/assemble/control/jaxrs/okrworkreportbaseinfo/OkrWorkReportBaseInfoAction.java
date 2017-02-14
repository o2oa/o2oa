package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.common.date.DateOperation;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapOutOkrWorkReportPersonLink;
import com.x.okr.assemble.control.jaxrs.okrworkreportprocesslog.WrapOutOkrWorkReportProcessLog;
import com.x.okr.assemble.control.service.OkrCenterWorkInfoService;
import com.x.okr.assemble.control.service.OkrConfigSystemService;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkReportBaseInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportPersonLinkService;
import com.x.okr.assemble.control.service.OkrWorkReportProcessLogService;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;
import com.x.okr.entity.OkrWorkReportDetailInfo;
import com.x.okr.entity.OkrWorkReportPersonLink;
import com.x.okr.entity.OkrWorkReportProcessLog;
import com.x.organization.core.express.wrap.WrapPerson;

@Path( "okrworkreportbaseinfo" )
public class OkrWorkReportBaseInfoAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkReportBaseInfoAction.class );
	private BeanCopyTools<OkrWorkReportBaseInfo, WrapOutOkrWorkReportBaseInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportBaseInfo.class, WrapOutOkrWorkReportBaseInfo.class, null, WrapOutOkrWorkReportBaseInfo.Excludes);
	private BeanCopyTools<OkrWorkReportPersonLink, WrapOutOkrWorkReportPersonLink> okrWorkReportPersonLink_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportPersonLink.class, WrapOutOkrWorkReportPersonLink.class, null, WrapOutOkrWorkReportPersonLink.Excludes);
	private BeanCopyTools<OkrWorkReportProcessLog, WrapOutOkrWorkReportProcessLog> okrWorkReportProcessLog_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkReportProcessLog.class, WrapOutOkrWorkReportProcessLog.class, null, WrapOutOkrWorkReportProcessLog.Excludes);
	private OkrWorkReportBaseInfoService okrWorkReportBaseInfoService = new OkrWorkReportBaseInfoService();
	private OkrWorkReportDetailInfoService okrWorkReportDetailInfoService = new OkrWorkReportDetailInfoService();
	private OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
	private OkrWorkReportProcessLogService okrWorkReportProcessLogService = new OkrWorkReportProcessLogService();
	private OkrCenterWorkInfoService okrCenterWorkInfoService = new OkrCenterWorkInfoService();
	private OkrWorkBaseInfoService okrWorkBaseInfoService = new OkrWorkBaseInfoService();
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrConfigSystemService okrConfigSystemService = new OkrConfigSystemService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	private OkrTaskService okrTaskService = new OkrTaskService();
	private OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	private DateOperation dateOperation = new DateOperation();
	
	@HttpMethodDescribe(value = "根据ID获取OkrWorkReportBaseInfo对象.", response = WrapOutOkrWorkReportBaseInfo.class)
	@GET
	@Path( "draft/{workId}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response draft(@Context HttpServletRequest request, @PathParam( "workId" ) String workId) {
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		WrapOutOkrWorkReportBaseInfo wrap = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Integer maxReportCount = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		//对wrapIn里的信息进行校验
		//先根据workId获取该工作汇报的草稿信息，如果有，则直接展示内容，如果没有则进行新建操作
		wrap = new WrapOutOkrWorkReportBaseInfo();
		
		//设置当前登录用户为创建工作汇报的用户
		wrap.setCreatorName( currentPerson.getName() );
		if( check ){
			try {
				wrap.setCreatorIdentity( okrUserManagerService.getFistIdentityNameByPerson(currentPerson.getName()) );
			} catch ( Exception e ) {
				check = false;
				logger.error( "system query user identity got an exception.user:" + currentPerson.getName(), e );
				result.error( e );
				result.setUserMessage( "根据员工姓名:'"+ currentPerson.getName() +"'获取员工身份时发生异常!" );
			}
		}
		
		if( check ){
			try {
				wrap.setCreatorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( wrap.getCreatorIdentity()));
			} catch ( Exception e ) {
				check = false;
				logger.error( "system query user department name got an exception.user:" + currentPerson.getName(), e );				
				result.error(e);
				result.setUserMessage( "根据员工姓名:'"+ currentPerson.getName() +"'获取员工所在部门名称时发生异常!" );
			}
		}
		
		if( check ){
			try {
				wrap.setCreatorCompanyName( okrUserManagerService.getCompanyNameByIdentity( wrap.getCreatorIdentity() ));
			} catch ( Exception e ) {
				check = false;
				logger.error( "system query user company name got an exception.user:" + currentPerson.getName(), e );
				result.error(e);
				result.setUserMessage( "根据员工姓名:'"+ currentPerson.getName() +"'获取员工所在公司名称时发生异常!" );
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
					logger.error( "okrWorkBaseInfo{'id':'"+workId+"'} is not exsits." );
					result.error(new Exception( "根据工作ID:'"+ workId +"'无法获取到工作信息!" ));
					result.setUserMessage( "根据工作ID:'"+ workId +"'无法获取到工作信息!" );
				}
			} catch (Exception e) {
				check = false;
				logger.error( "system query okrWorkBaseInfo{'id':'"+ workId +"'} got an exception.", e );
				result.error( e );
				result.setUserMessage( "根据工作ID:'"+ workId +"'获取到工作信息发生异常!" );			
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
					logger.error( "system query okrCenterWorkInfo by okrWorkBaseInfo{'id':'"+ workId +"'} got an exception." );
					result.setUserMessage( "根据工作ID:'"+ workId +"'无法获取到中心工作信息!" );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "根据工作ID:'"+ workId +"'获取到中心工作信息发生异常，无法继续保存汇报信息!" );
				logger.error( "system query okrCenterWorkInfo{'id':'"+ okrCenterWorkInfo.getId() +"'} got an exception.", e );
			}
		}
		
		if( check ){
			try {
				maxReportCount = okrWorkReportBaseInfoService.getMaxReportCount( okrWorkBaseInfo.getId() );
				wrap.setReportCount( ( maxReportCount + 1 ) );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据工作ID获取最大汇报次序发生异常！" );
				logger.error( "system get max report count by work id got an exception.", e );
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
		result.setData( wrap );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "新建或者更新OkrWorkReportBaseInfo对象.", request = WrapInOkrWorkReportBaseInfo.class, response = WrapOutOkrWorkReportBaseInfo.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post( @Context HttpServletRequest request, WrapInOkrWorkReportBaseInfo wrapIn ) {
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = new OkrWorkBaseInfo ();
		EffectivePerson currentPerson = this.effectivePerson(request);
		String workAdminIdentity = null;
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( check &&  wrapIn == null ){
			check = false;
			result.error( new Exception( "保存汇报信息时未获取到工作ID，无法继续保存汇报信息!" ) );
		}
		
		if( check ){
			try {
				workAdminIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
			} catch (Exception e) {
				logger.error( "system get system config 'REPORT_SUPERVISOR' got an exception", e );
				check = false;
				result.setUserMessage( "查询系统配置[REPORT_SUPERVISOR]时发生异常。" );
				result.error(e);
			}
		}
		if( check ){
			if( wrapIn.getWorkId() != null && !wrapIn.getWorkId().isEmpty() ){
				try {
					okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
					if( okrWorkBaseInfo == null ){
						check = false;
						result.error( new Exception( "工作信息不存在，无法继续保存汇报信息" ) );
						result.setUserMessage( "工作信息不存在，无法继续保存汇报信息!" );
					}
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统在根据工作ID查询工作信息时发生异常，无法继续保存汇报信息" ) );
					result.setUserMessage( "系统在根据工作ID查询工作信息时发生异常，无法继续保存汇报信息!" );
					logger.error( "system query okrWorkBaseInfo{'id':'" +wrapIn.getWorkId()+ "'} got an exception ." );
				}
			}else{
				check = false;
				result.error( new Exception( "工作ID为空，无法继续保存汇报信息" ) );
				result.setUserMessage( "工作ID为空，无法继续保存汇报信息!" );
			}
		}
		//对wrapIn里的信息进行校验		
		//查询汇报是否存在，如果存在，则不需要再新建一个了，直接更新
		if( check && wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
			try {
				okrWorkReportBaseInfo = okrWorkReportBaseInfoService.get( wrapIn.getId() );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统在根据ID查询汇报信息时发生异常，无法继续保存汇报信息" ) );
				result.setUserMessage( "系统在根据ID查询汇报信息时发生异常，无法继续保存汇报信息!" );
				logger.error( "system query okrWorkReportBaseInfo{'id':'" +wrapIn.getId()+ "'} got an exception ." );
			}
		}
		if( check ){
			if( okrWorkReportBaseInfo == null || "草稿".equals( okrWorkReportBaseInfo.getActivityName() )){
				result = draftReportSave( wrapIn, currentPerson, workAdminIdentity );
			}else if( "管理员督办".equals( okrWorkReportBaseInfo.getActivityName() )){
				//管理员填写督办信息
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				wrapIn.setTitle( okrWorkReportBaseInfo.getTitle() );
				result = reportAdminAuditionSave( okrWorkReportBaseInfo.getId(), wrapIn.getAdminSuperviseInfo() );
			}else{
				//领导阅知
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				wrapIn.setTitle( okrWorkReportBaseInfo.getTitle() );
				result = reportLeaderOpinoinSave( wrapIn, okrUserCache.getLoginIdentityName() );
			}
		}
		if( check ){
			if (result.getType().name().equals("success")) {
				String reportTitle = okrWorkBaseInfo.getTitle();
				if( okrWorkReportBaseInfo != null && okrWorkReportBaseInfo.getTitle() != null ){
					reportTitle = okrWorkReportBaseInfo.getTitle();
				}
				try {
					okrWorkDynamicsService.reportDynamic(
							okrWorkBaseInfo.getCenterId(), 
							okrWorkBaseInfo.getCenterTitle(), 
							okrWorkBaseInfo.getId(), 
							okrWorkBaseInfo.getTitle(), 
							reportTitle, 
							wrapIn.getId(), 
							"保存工作汇报", 
							currentPerson.getName(),
							okrUserCache.getLoginUserName(),
							okrUserCache.getLoginIdentityName(), "保存工作汇报：" + wrapIn.getTitle(),
							"工作汇报保存成功！");
				} catch (Exception e) {
					logger.error("system save reportDynamic got an exception.", e);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	/**
	 * 保存领导审阅信息
	 * @param wrapIn
	 * @param currentPerson
	 * @param workAdminIdentity
	 * @return
	 */
	private ActionResult<WrapOutOkrWorkReportBaseInfo> reportLeaderOpinoinSave( WrapInOkrWorkReportBaseInfo wrapIn, String processorIdentity ) {
		
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo  = null;
		boolean check = true;
		
		if( check ){
			//校验工作ID是否存在
			if( wrapIn.getOpinion() == null || wrapIn.getOpinion().isEmpty() ){
				check = false;
				result.error( new Exception( "领导审阅信息为空，无法继续保存汇报信息" ) );
				result.setUserMessage( "领导审阅信息为空，无法继续保存汇报信息!" );
				logger.error( "opinion is null, can not save report info." );
			}
		}
		
		if( check ){
			if( wrapIn.getId() == null || wrapIn.getId().isEmpty() ){
				check = false;
				result.error( new Exception( "保存汇报审批意见时未获取到汇报ID，无法继续保存汇报信息" ) );
				result.setUserMessage( "保存汇报审批意见时未获取到汇报ID，无法继续保存汇报信息!" );
				logger.error( "report id is null, can not save report info." );
			}
		}
		
		if( check ){
			try{
				okrWorkReportBaseInfo = okrWorkReportBaseInfoService.get( wrapIn.getId() );
				if( okrWorkReportBaseInfo == null ){
					check = false;
					result.error( new Exception( "汇报信息不存在，无法继续保存汇报处理信息!" ) );
					result.setUserMessage( "汇报信息不存在，无法继续保存汇报处理信息!" );
					logger.error( "report id is null, can not save report info." );
				}
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "汇报信息不存在，无法继续保存汇报处理信息!" );
				logger.error( "report id is null, can not save report info." );
			}
		}
		
		if( check ){
			try {
				okrWorkReportBaseInfoService.saveLeaderOpinionInfo( okrWorkReportBaseInfo, wrapIn.getOpinion(), processorIdentity );			
				result.setUserMessage( "督办信息保存成功！" );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在保存信息时发生异常!" );
				logger.error( "OkrWorkReportBaseInfoService save object got an exception.", e );
			}
		}
		return result;
	}

	/**
	 * 保存管理员督办信息
	 * @param wrapIn
	 * @param currentPerson
	 * @param workAdminIdentity
	 * @return
	 */
	private ActionResult<WrapOutOkrWorkReportBaseInfo> reportAdminAuditionSave( String reportId, String adminSuperviseInfo ) {
		
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		boolean check = true;
		
		//校验工作ID是否存在
		if( adminSuperviseInfo == null || adminSuperviseInfo.isEmpty() ){
			check = false;
			result.error( new Exception( "管理员督办信息为空，无法继续保存汇报信息" ) );
			result.setUserMessage( "管理员督办信息为空，无法继续保存汇报信息!" );
			logger.error( "adminSuperviseInfo is null, can not save report info." );
		}
		
		if( check ){
			try {
				okrWorkReportBaseInfoService.saveAdminSuperviseInfo( reportId, adminSuperviseInfo );			
				result.setUserMessage( "督办信息保存成功！" );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在保存信息时发生异常!" );
				logger.error( "OkrWorkReportBaseInfoService save object got an exception.", e );
			}
		}
		return result;
	}

	/**
	 * 保存汇报信息草稿
	 * @param wrapIn
	 * @param currentPerson
	 * @param workAdminIdentity
	 * @return
	 */
	private ActionResult<WrapOutOkrWorkReportBaseInfo> draftReportSave( WrapInOkrWorkReportBaseInfo wrapIn, EffectivePerson currentPerson, String workAdminIdentity ){
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		Integer maxReportCount = null;
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		//校验工作ID是否存在
		if( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ){
			check = false;
			result.error( new Exception( "存汇报信息时未获取到工作ID，无法继续保存汇报信息" ) );
			result.setUserMessage( "保存汇报信息时未获取到工作ID，无法继续保存汇报信息!" );
			logger.error( " save draftReport, work id is null, can not save report info." );
		}
				
		//设置当前登录用户为创建工作汇报的用户
		if (check) {
			try {
				wrapIn.setCreatorName( currentPerson.getName() );
				wrapIn.setCreatorIdentity( okrUserManagerService.getFistIdentityNameByPerson(currentPerson.getName()) );
			} catch (Exception e) {
				check = false;
				logger.error( "system query user identity got an exception.user:" + currentPerson.getName(), e);
				result.error(e);
				result.setUserMessage( "根据员工姓名:'" + currentPerson.getName() + "'获取员工身份时发生异常!" );
			}
		}
		if (check) {
			try {
				wrapIn.setCreatorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity(wrapIn.getCreatorIdentity()));
			} catch (Exception e) {
				check = false;
				logger.error( "system query user department name got an exception.user:" + currentPerson.getName(), e);
				result.error(e);
				result.setUserMessage( "根据员工姓名:'" + currentPerson.getName() + "'获取员工所在部门名称时发生异常!" );
			}
		}
		if (check) {
			try {
				wrapIn.setCreatorCompanyName( okrUserManagerService.getCompanyNameByIdentity(wrapIn.getCreatorIdentity()) );
			} catch (Exception e) {
				check = false;
				logger.error( "system query user company name got an exception.user:" + currentPerson.getName(), e);
				result.error(e);
				result.setUserMessage( "根据员工姓名:'" + currentPerson.getName() + "'获取员工所在公司名称时发生异常!" );
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
					logger.error( "okrWorkBaseInfo{'id':'"+wrapIn.getWorkId()+"'} is not exsits." );
					result.error(new Exception( "根据工作ID:'"+ wrapIn.getWorkId() +"'无法获取到工作信息!" ));
					result.setUserMessage( "根据工作ID:'"+ wrapIn.getWorkId() +"'无法获取到工作信息!" );
				}
			} catch (Exception e) {
				check = false;
				logger.error( "system query okrWorkBaseInfo{'id':'"+ wrapIn.getWorkId() +"'} got an exception.", e );
				result.error( e );
				result.setUserMessage( "根据工作ID:'"+ wrapIn.getWorkId() +"'获取到工作信息发生异常!" );			
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
					logger.error( "system query okrCenterWorkInfo{'id':'"+ okrWorkBaseInfo.getCenterId() +"'} got an exception." );
					result.setUserMessage( "根据中心工作ID:'"+ okrWorkBaseInfo.getCenterId() +"'无法获取到中心工作信息!" );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "根据中心工作ID:'"+ okrWorkBaseInfo.getCenterId() +"'获取到中心工作信息发生异常，无法继续保存汇报信息!" );
				logger.error( "system query okrCenterWorkInfo{'id':'"+  okrWorkBaseInfo.getCenterId() +"'} got an exception.", e );
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
		if( check && okrWorkReportBaseInfo == null ){
			//补充汇报标题以及汇报次数信息
			if( wrapIn.getReportCount() == null || wrapIn.getReportCount() == 0 ){
				try {
					maxReportCount = okrWorkReportBaseInfoService.getMaxReportCount( okrWorkBaseInfo.getId() );
					wrapIn.setReportCount( ( maxReportCount + 1 ) );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统根据工作ID获取最大汇报次序发生异常！" );
					logger.error( "system get max report count by work id got an exception.", e );
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
		}
		if( check ){
			try {
				okrWorkReportBaseInfo = okrWorkReportBaseInfoService.save( wrapIn );
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				result.setUserMessage( "汇报信息保存成功！" );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在保存信息时发生异常!" );
				logger.error( "OkrWorkReportBaseInfoService save object got an exception.", e );
			}
		}
		return result;
	}
	
	
	/**
	 * 确定汇报工作流
	 * 1、先确定工作汇报工作流执行方式：1）工作管理员督办 - 中心工作阅知领导审阅； 2）工作部署者审阅
	 * 2、如果是方式1）
		    a.判断系统设置中是否有设置工作管理员
            b.如果有设置工作管理员，那么下一步处理者为工作管理员，如果没有设置工作管理员，那么判断中心工作是否有设置阅知领导
            c.如果中心工作没有设置阅知领导，那么下一步处理者为工作部署者审阅，并且在汇报的descript中说明原因
	 * 3、汇报工作流执行方式生效工作层级
	 * 
	 * @param request
	 * @param wrapIn
	 * @return
	 */
	@Path( "submit" )
	@HttpMethodDescribe(value = "提交工作汇报.", request = WrapInOkrWorkReportBaseInfo.class, response = WrapOutOkrWorkReportBaseInfo.class)
	@PUT
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response submit( @Context HttpServletRequest request, WrapInOkrWorkReportBaseInfo wrapIn ) {
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<WrapOutOkrWorkReportBaseInfo>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		logger.info( ">>>>>>>>>>>>>>>>submit提交汇报处理操作......" );
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( check &&  wrapIn == null ){
			check = false;
			result.error( new Exception( "保存汇报信息时未获取到工作ID，无法继续保存汇报信息!" ) );
		}
		
		//查询汇报是否存在，如果存在，则不需要再新建一个了，直接更新
		if( check && wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
			try {
				okrWorkReportBaseInfo = okrWorkReportBaseInfoService.get( wrapIn.getId() );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统在根据ID查询汇报信息时发生异常，无法继续保存汇报信息" ) );
				result.setUserMessage( "系统在根据ID查询汇报信息时发生异常，无法继续保存汇报信息!" );
				logger.error( "system query okrWorkReportBaseInfo{'id':'" +wrapIn.getId()+ "'} got an exception ." );
			}
		}
		
		if( check ){
			//logger.debug( ">>>>>>>>>>>>>>>>>wrapIn.getId()=" + wrapIn.getId() );			
			if( okrWorkReportBaseInfo == null || "草稿".equals( okrWorkReportBaseInfo.getActivityName() )){
				//logger.debug( ">>>>>>>>>>>>>>>>>保存新的汇报信息，wrapIn.getId()=" + wrapIn.getId() );
				wrapIn.setCurrentProcessLevel( 1 );
				result = submit_reportInfo( wrapIn, currentPerson );
			}else if( "管理员督办".equals( okrWorkReportBaseInfo.getActivityName() )){
				//logger.debug( ">>>>>>>>>>>>>>>>>工作管理员处理汇报信息[管理员督办]。" );
				//管理员填写督办信息
				wrapIn.setWorkType( okrWorkReportBaseInfo.getWorkType());
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				wrapIn.setTitle( okrWorkReportBaseInfo.getTitle() );
				result = submit_adminSupervise( okrWorkReportBaseInfo, wrapIn.getAdminSuperviseInfo(), currentPerson);
			}else{
				//领导阅知
				//logger.debug( ">>>>>>>>>>>>>>>>>领导审阅汇报信息。" );
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				wrapIn.setTitle( okrWorkReportBaseInfo.getTitle() );
				wrapIn.setWorkType( okrWorkReportBaseInfo.getWorkType());
				result = submit_leaderOpinoin( okrWorkReportBaseInfo, wrapIn.getOpinion(), currentPerson );
			}
			if( result.getType().name().equals("success")){
				try {
					okrWorkDynamicsService.reportDynamic(
							okrWorkReportBaseInfo.getCenterId(), 
							okrWorkReportBaseInfo.getCenterTitle(), 
							okrWorkReportBaseInfo.getWorkId(), 
							okrWorkReportBaseInfo.getWorkTitle(), 
							okrWorkReportBaseInfo.getTitle(), 
							wrapIn.getId(), 
							"提交工作汇报", 
							currentPerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName(), 
							"提交工作汇报：" + wrapIn.getTitle(), 
							"工作汇报提交成功！"
					);
				} catch (Exception e) {
					logger.error( "system save reportDynamic got an exception.", e );
				}
			}
		}
		
		if( check ){
			try {
				List<String> workTypeList = new ArrayList<String>();
				workTypeList.add( wrapIn.getWorkType() );
				okrWorkReportTaskCollectService.checkReportCollectTask( okrUserCache.getLoginIdentityName(), workTypeList );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "核对汇报待办汇总信息发生异常" ) );
				result.setUserMessage( "核对汇报待办汇总信息发生异常!" );
				logger.error( "check report collect got an exception ." );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private ActionResult<WrapOutOkrWorkReportBaseInfo> submit_leaderOpinoin( OkrWorkReportBaseInfo okrWorkReportBaseInfo, String opinion, EffectivePerson currentPerson ) {
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		List<String> ids = null;
		String reportor_audit_notice = null;
		WrapPerson report_supervisor = null;
		String report_supervisorIdentity = null; //督办员身份
		String report_supervisorName = null; //督办员身份
		String report_supervisorOrgName = null; //督办员身份
		String report_supervisorCompanyName = null; //督办员身份
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if ( check ) {
			if( opinion == null || opinion.isEmpty() ){
				opinion = "已阅。";
			}
		}
		
		if( check ){
			try {
				reportor_audit_notice = okrConfigSystemService.getValueWithConfigCode( "REPORTOR_AUDIT_NOTICE" );
			} catch (Exception e) {
				logger.error( "system get system config 'REPORTOR_AUDIT_NOTICE' got an exception", e );
				check = false;
				result.setUserMessage( "查询系统配置[REPORTOR_AUDIT_NOTICE]时发生异常。" );
				result.error(e);
			}
		}
		
		if( check ){
			try {
				report_supervisorIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
			} catch (Exception e) {
				logger.error( "system get system config 'REPORT_SUPERVISOR' got an exception", e );
				check = false;
				result.setUserMessage( "查询系统配置[REPORT_SUPERVISOR]时发生异常。" );
				result.error(e);
			}
		}
		
		if( check ){
			try {
				okrWorkReportBaseInfoService.leaderProcess( okrWorkReportBaseInfo, opinion, okrUserCache.getLoginIdentityName() );
				
				//是否每个环节都通知汇报人阅知
				if( "OPEN".equalsIgnoreCase( reportor_audit_notice )){
					//1、查询汇报人是否存在该汇报的待阅信息，如果有则不需要发送
					//2、如果汇报都暂无该汇报的待阅信息，那么，发送一条待阅信息
					ids = okrTaskService.listIdsByTargetActivityAndObjId( "工作汇报", okrWorkReportBaseInfo.getId(), "汇报确认", okrWorkReportBaseInfo.getReporterIdentity() );
					if( ids == null || ids.isEmpty() ){
						okrWorkReportBaseInfoService.addReportConfirmReader(  okrWorkReportBaseInfo, 
								okrWorkReportBaseInfo.getReporterIdentity() ,
								okrWorkReportBaseInfo.getReporterName(), 
								okrWorkReportBaseInfo.getReporterOrganizationName(), 
								okrWorkReportBaseInfo.getReporterCompanyName());
					}
					//看看流程是否过督办员审核
					if( okrWorkReportBaseInfo.getReportWorkflowType() != null && "ADMIN_AND_ALLLEADER".equalsIgnoreCase( okrWorkReportBaseInfo.getReportWorkflowType())){
						if( report_supervisorIdentity != null && !report_supervisorIdentity.isEmpty() ){
							ids = okrTaskService.listIdsByTargetActivityAndObjId( "工作汇报", okrWorkReportBaseInfo.getId(), "汇报确认", report_supervisorIdentity );
							if( ids == null || ids.isEmpty() ){
								report_supervisor = okrUserManagerService.getUserNameByIdentity( report_supervisorIdentity );
								if( report_supervisor != null ){
									report_supervisorName = report_supervisor.getName();
									report_supervisorOrgName = okrUserManagerService.getDepartmentNameByIdentity( report_supervisorIdentity );
									report_supervisorCompanyName = okrUserManagerService.getCompanyNameByIdentity( report_supervisorIdentity );
									ids = okrTaskService.listIdsByTargetActivityAndObjId( "工作汇报", okrWorkReportBaseInfo.getId(), "汇报确认", report_supervisorIdentity );
									if( ids == null || ids.isEmpty() ){
										okrWorkReportBaseInfoService.addReportConfirmReader(  okrWorkReportBaseInfo, 
												report_supervisorIdentity ,
												report_supervisorName, 
												report_supervisorOrgName, 
												report_supervisorCompanyName
										);
									}
								}
							}
						}
					}
				}
				result.setUserMessage( "汇报信息处理完成." );
			} catch (Exception e) {
				check = false;
				result.setUserMessage( "系统在处理工作汇报审阅信息时发生异常!" );
				result.error(e);
			}
		}
		return result;
	}

	private ActionResult<WrapOutOkrWorkReportBaseInfo> submit_adminSupervise( OkrWorkReportBaseInfo okrWorkReportBaseInfo, String adminSuperviseInfo, EffectivePerson currentPerson ) {
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if ( check ) {
			if( adminSuperviseInfo == null || adminSuperviseInfo.isEmpty() ){
				//check = false;
				//result.setUserMessage( "传入的督办信息内容为空，无法处理汇报信息!" );
				//result.error(new Exception( "adminSuperviseInfo is null." ));
				adminSuperviseInfo = "无";
			}
		}
		
		if( check ){
			try {
				okrWorkReportBaseInfoService.adminProcess( okrWorkReportBaseInfo, adminSuperviseInfo, okrUserCache.getLoginIdentityName()  );
				result.setUserMessage( "汇报信息处理完成." );
			} catch (Exception e) {
				check = false;
				result.setUserMessage( "系统在处理工作汇报审阅信息时发生异常!" );
				result.error(e);
			}
		}
		return result;
	}

	private ActionResult<WrapOutOkrWorkReportBaseInfo> submit_reportInfo( WrapInOkrWorkReportBaseInfo wrapIn, EffectivePerson currentPerson ) {
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<WrapOutOkrWorkReportBaseInfo>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		Integer maxReportCount = null;
		String report_progress = null;
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		// 对wrapIn里的信息进行校验
		// 校验工作ID是否存在
		if (wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty()) {
			check = false;
			result.error(new Exception( "存汇报信息时未获取到工作ID，无法继续保存汇报信息" ));
			result.setUserMessage( "保存汇报信息时未获取到工作ID，无法继续保存汇报信息!" );
			logger.error( "存汇报信息时未获取到工作ID，无法继续保存汇报信息." );
		}

		// 设置当前登录用户为创建工作汇报的用户
		if (check) {
			try {
				wrapIn.setCreatorName(currentPerson.getName());
				wrapIn.setCreatorIdentity(okrUserManagerService.getFistIdentityNameByPerson(currentPerson.getName()));
			} catch (Exception e) {
				check = false;
				logger.error( "system query creator user identity got an exception.user:" + currentPerson.getName(), e);
				result.error(e);
				result.setUserMessage( "根据创建者姓名:'" + currentPerson.getName() + "'获取员工身份时发生异常!" );
			}
		}
		
		if (check) {
			try {
				wrapIn.setCreatorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity(wrapIn.getCreatorIdentity()));
			} catch (Exception e) {
				check = false;
				logger.error(
						"system query creator user department name got an exception.user:" + currentPerson.getName(),
						e);
				result.error(e);
				result.setUserMessage( "根据创建者姓名:'" + currentPerson.getName() + "'获取员工所在部门名称时发生异常!" );
			}
		}
		if (check) {
			try {
				wrapIn.setCreatorCompanyName( okrUserManagerService.getCompanyNameByIdentity(wrapIn.getCreatorIdentity()));
			} catch (Exception e) {
				check = false;
				logger.error( "system query creator user company name got an exception.user:" + currentPerson.getName(), e);
				result.error(e);
				result.setUserMessage( "根据创建者姓名:'" + currentPerson.getName() + "'获取员工所在公司名称时发生异常!" );
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
					logger.error( "okrWorkBaseInfo{'id':'" + wrapIn.getWorkId() + "'} is not exsits." );
					result.error(new Exception( "根据工作ID:'" + wrapIn.getWorkId() + "'无法获取到工作信息!" ));
					result.setUserMessage( "根据工作ID:'" + wrapIn.getWorkId() + "'无法获取到工作信息!" );
				}
			} catch (Exception e) {
				check = false;
				logger.error( "system query okrWorkBaseInfo{'id':'" + wrapIn.getWorkId() + "'} got an exception.", e);
				result.error(e);
				result.setUserMessage( "根据工作ID:'" + wrapIn.getWorkId() + "'获取到工作信息发生异常!" );
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
					logger.error( "system query okrCenterWorkInfo{'id':'" + okrWorkBaseInfo.getCenterId() + "'} not exists." );
					result.setUserMessage( "根据中心工作ID:'" + okrWorkBaseInfo.getCenterId() + "'无法获取到中心工作信息!" );
				}
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage( "根据中心工作ID:'" + okrWorkBaseInfo.getCenterId() + "'获取到中心工作信息发生异常，无法继续保存汇报信息!" );
				logger.error( "system query okrCenterWorkInfo{'id':'" + okrWorkBaseInfo.getCenterId() + "'} got an exception.", e);
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
		
		//logger.debug( "wrapIn.getId()="+wrapIn.getId());
		
		if( check && wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
			try {
				okrWorkReportBaseInfo = okrWorkReportBaseInfoService.get( wrapIn.getId() );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统在根据ID查询汇报信息时发生异常，无法继续保存汇报信息" ) );
				result.setUserMessage( "系统在根据ID查询汇报信息时发生异常，无法继续保存汇报信息!" );
				logger.error( "system query okrWorkReportBaseInfo{'id':'" +wrapIn.getId()+ "'} got an exception ." );
			}
		}
		// 补充状态信息
		if (check) {
			if ( okrWorkReportBaseInfo == null) {
				//logger.debug( "okrWorkReportBaseInfo is not null!" );
				// 补充汇报标题以及汇报次数信息
				if (wrapIn.getReportCount() == null || wrapIn.getReportCount() == 0) {
					try {
						maxReportCount = okrWorkReportBaseInfoService.getMaxReportCount(okrWorkBaseInfo.getId());
						//logger.debug( "maxReportCount=" + maxReportCount );
						wrapIn.setReportCount((maxReportCount + 1));
					} catch (Exception e) {
						check = false;
						result.error(e);
						result.setUserMessage( "系统根据工作ID获取最大汇报次序发生异常！" );
						logger.error( "system get max report count by work id got an exception.", e);
					}
				}
				if (wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty()) {
					// 根据已知信息组织汇报标题和汇简要标题
					wrapIn.setTitle( okrWorkBaseInfo.getTitle() );
					wrapIn.setShortTitle( "第" + wrapIn.getReportCount() + "次工作汇报" );
				}
			} else {
				//logger.debug( "okrWorkReportBaseInfo.getReportCount()=" + okrWorkReportBaseInfo.getReportCount());
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
				report_progress = okrConfigSystemService.getValueWithConfigCode( "REPORT_PROGRESS" );
				if( report_progress == null || report_progress.isEmpty() ){
					report_progress = "CLOSE";
				}
			} catch (Exception e) {
				report_progress = "CLOSE";
				logger.error( "system get config got an exception.", e );
			}
		}
		if( check ){
			try {
				wrapIn.setSubmitTime( new Date() ); //保存提交时间
				wrapIn.setWorkType( okrWorkBaseInfo.getWorkType() );
				okrWorkReportBaseInfo = okrWorkReportBaseInfoService.submitReportInfo( wrapIn, okrCenterWorkInfo, okrWorkBaseInfo);
				
				//提交完成，需要分析一下工作的进展情况
				try {
					okrWorkBaseInfoService.analyseWorkProgress( okrWorkBaseInfo.getId(), report_progress, dateOperation.getNowDateTime() );
				} catch (Exception e1 ) {
					logger.error( "system analyse work progres got an exceptin.", e1);
				}
				
				wrapIn.setId( okrWorkReportBaseInfo.getId() );
				result.setUserMessage( "工作汇报提交完成，下一步处理人：" + okrWorkReportBaseInfo.getCurrentProcessorIdentity());
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage( "系统在提交汇报信息时发生异常！" );
				logger.error( "OkrWorkReportBaseInfoService save object got an exception", e);
			}
		}
		return result;
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkReportBaseInfo数据对象.", response = WrapOutOkrWorkReportBaseInfo.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + " try to delete okrWorkReportBaseInfo{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrWorkReportBaseInfoService.delete( id, currentPerson.getName() );
			result.setUserMessage( "成功删除工作汇报基础信息数据信息。id=" + id );
		}catch(Exception e){
			logger.error( "system delete okrWorkReportBaseInfoService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除工作汇报基础信息数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkReportBaseInfo对象.", response = WrapOutOkrWorkReportBaseInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkReportBaseInfo> result = new ActionResult<>();
		WrapOutOkrWorkReportBaseInfo wrap = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo  = null;
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		String workAdminIdentity = null;
		List<String> ids = null;
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( id == null || id.isEmpty() ){
			check = false;
			result.error( new Exception( "参数传入的id为空。" ));
			result.setUserMessage( "参数传入的id为空。" );
			logger.error( "id is null, system can not get any object." );
		}
		
		if( check ){
			if ( okrUserCache.getLoginUserName() == null ) {
				check = false;
				result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
				result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
			}
		}
		
		if( check ){
			try {
				okrWorkReportBaseInfo = okrWorkReportBaseInfoService.get( id );
			} catch (Exception e) {
				check = false;
				logger.error( "system get by id got an exception", e );
				result.setUserMessage( "系统根据ID查询汇报信息时发生异常。" );
				result.error(e);
			}
		}
		
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					wrap = wrapout_copier.copy( okrWorkReportBaseInfo );
				} catch (Exception e) {
					check = false;
					logger.error( "wrapout_copier copy okrWorkReportBaseInfo got an exception", e );
					result.setUserMessage( "转换汇报信息为输出格式时发生异常。" );
					result.error(e);
				}
			}
		}
		
		try {
			okrWorkBaseInfo = okrWorkBaseInfoService.get( wrap.getWorkId() );
			if( okrWorkBaseInfo == null ){
				logger.error( "okrWorkBaseInfo{'id':'"+wrap.getWorkId()+"'} is not exsits." );
				check = false;
				result.setUserMessage( "汇报所关联的工作信息不存在。" );
			}
		} catch (Exception e) {
			logger.error( "system get okrWorkBaseInfo{'id':'"+wrap.getWorkId()+"'} got an exception.", e );
			check = false;
			result.setUserMessage( "系统根据工作ID查询工作信息时发生异常。" );
			result.error(e);
		}
		
		// 查询汇报详细信息
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( id );
					if( okrWorkReportDetailInfo != null ){
						wrap.setWorkPlan( okrWorkReportDetailInfo.getWorkPlan() );
						wrap.setWorkPointAndRequirements( okrWorkReportDetailInfo.getWorkPointAndRequirements() );
						wrap.setProgressDescription( okrWorkReportDetailInfo.getProgressDescription() );
						wrap.setAdminSuperviseInfo( okrWorkReportDetailInfo.getAdminSuperviseInfo() );
					}
				} catch (Exception e) {
					logger.error( "system get okrWorkReportDetailInfo got an exception", e );
				}
			}
		}
		//查询所有的审批日志
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					ids = okrWorkReportProcessLogService.listByReportId( id );
					if( ids !=null ){
						okrWorkReportProcessLogList = okrWorkReportProcessLogService.list( ids );
						if( okrWorkReportProcessLogList != null ){
							wrap.setProcessLogs( okrWorkReportProcessLog_wrapout_copier.copy( okrWorkReportProcessLogList ) );
						}
					}
				} catch (Exception e) {
					logger.error( "system get okrWorkReportDetailInfo got an exception.", e );
				}
			}
		}
		
		if( check ){
			if( okrWorkReportBaseInfo != null ){
				try {
					workAdminIdentity = okrConfigSystemService.getValueWithConfigCode( "REPORT_SUPERVISOR" );
				} catch (Exception e) {
					logger.error( "system get system config 'REPORT_SUPERVISOR' got an exception.", e );
					check = false;
					result.setUserMessage( "查询系统配置[REPORT_SUPERVISOR]时发生异常。" );
					result.error(e);
				}
			}
		}
		
		if( check ){
			//判断当前处理人是什么身份
			if( wrap.getCreatorIdentity() != null && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( wrap.getCreatorIdentity())){
				wrap.setIsCreator( true );
			}
			if( wrap.getReporterIdentity() != null && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( wrap.getReporterIdentity())){
				wrap.setIsReporter(true);
			}
			//logger.debug( "wrap.getReportWorkflowType()=" + wrap.getReportWorkflowType() );
			if( "ADMIN_AND_ALLLEADER".equals( wrap.getReportWorkflowType() )){
				//从汇报审阅领导里进行比对
				if( wrap.getReadLeadersIdentity() != null && wrap.getReadLeadersIdentity().indexOf( okrUserCache.getLoginIdentityName()  ) >= 0 ){
					wrap.setIsReadLeader( true );
				}
			}else if( "DEPLOYER".equals( wrap.getReportWorkflowType() ) ){
				if( okrWorkBaseInfo != null ){
					//logger.debug( "okrWorkBaseInfo.getDeployerIdentity()=" + okrWorkBaseInfo.getDeployerIdentity() );
					//logger.debug( "okrUserCache.getLoginIdentityName() =" + okrUserCache.getLoginIdentityName()  );
					//对比当前工作的部署者是否是当前用户
					if( okrWorkBaseInfo.getDeployerIdentity() != null && okrWorkBaseInfo.getDeployerIdentity().equalsIgnoreCase( okrUserCache.getLoginIdentityName()  ) ){
						wrap.setIsReadLeader( true );
					}
				}
			}
			
			if( workAdminIdentity != null && !workAdminIdentity.isEmpty() && okrUserCache.getLoginIdentityName() .equalsIgnoreCase( workAdminIdentity )){
				wrap.setIsWorkAdmin( true );
			}
			result.setData(wrap);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据工作ID获取OkrWorkReportBaseInfo对象.", response = WrapOutOkrWorkReportBaseInfo.class)
	@GET
	@Path( "list/work/{workId}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByWork(@Context HttpServletRequest request, @PathParam( "workId" ) String workId) {
		ActionResult<List<WrapOutOkrWorkReportBaseInfo>> result = new ActionResult<List<WrapOutOkrWorkReportBaseInfo>>();
		List<WrapOutOkrWorkReportBaseInfo> wraps = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = null;
		List<OkrWorkReportProcessLog> okrWorkReportProcessLogList = null;
		OkrWorkReportDetailInfo okrWorkReportDetailInfo = null;
		List<String> ids = null;
		List<String> logsIds = null;
		if( workId == null || workId.isEmpty() ){
			logger.error( "workId is null, system can not get any OkrWorkReportBaseInfo object." );
		}
		try {
			ids = okrWorkReportBaseInfoService.listByWorkId( workId );
			if( ids != null && !ids.isEmpty()){
				okrWorkReportBaseInfoList = okrWorkReportBaseInfoService.listByIds( ids );
				if( okrWorkReportBaseInfoList != null && !okrWorkReportBaseInfoList.isEmpty() ){
					wraps = wrapout_copier.copy( okrWorkReportBaseInfoList );
					for( WrapOutOkrWorkReportBaseInfo wrap : wraps ){
						try {
							logsIds = okrWorkReportProcessLogService.listByReportId( wrap.getId() );
							if( logsIds !=null ){
								okrWorkReportProcessLogList = okrWorkReportProcessLogService.list( logsIds );
								if( okrWorkReportProcessLogList != null ){
									wrap.setProcessLogs( okrWorkReportProcessLog_wrapout_copier.copy( okrWorkReportProcessLogList ) );
								}
							}
						} catch (Exception e) {
							logger.error( "system get okrWorkReportDetailInfo got an exception.", e );
						}
						try {
							okrWorkReportDetailInfo = okrWorkReportDetailInfoService.get( wrap.getId() );
							if( okrWorkReportDetailInfo != null ){
								wrap.setWorkPlan( okrWorkReportDetailInfo.getWorkPlan() );
								wrap.setWorkPointAndRequirements( okrWorkReportDetailInfo.getWorkPointAndRequirements() );
								wrap.setProgressDescription( okrWorkReportDetailInfo.getProgressDescription() );
							}
						} catch (Exception e) {
							logger.error( "system query okrWorkReportDetailInfo by id got an exception", e );
							result.setUserMessage( "根据ID查询汇报详细信息时发生异常。" );
							result.error(e);
						}
					}
					result.setData( wraps );
				}
			}else{
				result.setUserMessage( "系统根据工作ID未能查询到任何汇报信息。" );
				logger.error( "system can not get any object by {'workId':'"+workId+"'}. " );
			}
		} catch ( Exception e ) {
			logger.error( "system get by id got an exception", e );
			result.error( e );
			result.setUserMessage( "系统根据工作ID查询所有汇报信息列表发生异常。" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportBaseInfo[草稿],下一页.", response = WrapOutOkrWorkReportBaseInfo.class, request = WrapInFilter.class)
	@PUT
	@Path( "draft/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDraftNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkReportBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkReportBaseInfo> wraps = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = null;
		Long total = 0L;
		boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( wrapIn == null ){
			wrapIn = new WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "正常" );
			wrapIn.addQueryProcessStatus( "草稿" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		
		try{
			okrWorkReportBaseInfoList = okrWorkReportBaseInfoService.listNextWithFilter( id, count, wrapIn );			
			//从数据库中查询符合条件的对象总数
			total = okrWorkReportBaseInfoService.getCountWithFilter( wrapIn );
			wraps = wrapout_copier.copy( okrWorkReportBaseInfoList );
			result.setCount( total );
			result.setData( wraps );
		}catch( Throwable th ){
			logger.error( "system filter okrWorkBaseInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportBaseInfo[草稿],上一页.", response = WrapOutOkrWorkReportBaseInfo.class, request = WrapInFilter.class)
	@PUT
	@Path( "draft/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDraftPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkReportBaseInfo>> result = new ActionResult<>();
		List<WrapOutOkrWorkReportBaseInfo> wraps = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = null;
		Long total = 0L;
		boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson( request );		
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( wrapIn == null ){
			wrapIn = new WrapInFilter();
		}		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "正常" );
			wrapIn.addQueryProcessStatus( "草稿" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		try{
			okrWorkReportBaseInfoList = okrWorkReportBaseInfoService.listPrevWithFilter( id, count, wrapIn );			
			//从数据库中查询符合条件的对象总数
			total = okrWorkReportBaseInfoService.getCountWithFilter( wrapIn );
			wraps = wrapout_copier.copy( okrWorkReportBaseInfoList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrWorkBaseInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportPersonLink[处理中（待办）],下一页.", response = WrapOutOkrWorkReportPersonLink.class, request = com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter.class)
	@PUT
	@Path( "task/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyTaskNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkReportPersonLink>> result = new ActionResult<>();
		List<WrapOutOkrWorkReportPersonLink> wraps = null;
		List<OkrWorkReportPersonLink>  okrWorkReportPersonLinkList = null;
		Long total = 0L;
		boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "正常" );
			//待处理|处理中|已处理
			wrapIn.addQueryProcessStatus( "处理中" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		
		try{
			okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listNextWithFilter( id, count, wrapIn );			
			//从数据库中查询符合条件的对象总数
			total = okrWorkReportPersonLinkService.getCountWithFilter( wrapIn );
			wraps = okrWorkReportPersonLink_wrapout_copier.copy( okrWorkReportPersonLinkList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrWorkBaseInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportPersonLink[处理中（待办）],上一页.", response = WrapOutOkrWorkReportPersonLink.class, request = com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter.class)
	@PUT
	@Path( "task/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyTaskPrevWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkReportPersonLink>> result = new ActionResult<>();
		List<WrapOutOkrWorkReportPersonLink> wraps = null;
		List<OkrWorkReportPersonLink>  okrWorkReportPersonLinkList = null;
		Long total = 0L;
		boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "正常" );
			//待处理|处理中|已处理
			wrapIn.addQueryProcessStatus( "处理中" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		
		try{
			okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listPrevWithFilter( id, count, wrapIn );			
			//从数据库中查询符合条件的对象总数
			total = okrWorkReportPersonLinkService.getCountWithFilter( wrapIn );
			wraps = okrWorkReportPersonLink_wrapout_copier.copy( okrWorkReportPersonLinkList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrWorkBaseInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportPersonLink[已处理（已办）],下一页.", response = WrapOutOkrWorkReportPersonLink.class, request = com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter.class)
	@PUT
	@Path( "process/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyProcessNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkReportPersonLink>> result = new ActionResult<>();
		List<WrapOutOkrWorkReportPersonLink> wraps = null;
		List<OkrWorkReportPersonLink>  okrWorkReportPersonLinkList = null;
		Long total = 0L;
		boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}	
		
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "正常" );
			//待处理|处理中|已处理
			wrapIn.addQueryProcessStatus( "已处理" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		
		try{
			okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listNextWithFilter( id, count, wrapIn );			
			//从数据库中查询符合条件的对象总数
			total = okrWorkReportPersonLinkService.getCountWithFilter( wrapIn );
			wraps = okrWorkReportPersonLink_wrapout_copier.copy( okrWorkReportPersonLinkList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrWorkBaseInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportPersonLink[已处理（已办）],上一页.", response = WrapOutOkrWorkReportPersonLink.class, request = com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter.class)
	@PUT
	@Path( "process/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyProcessPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkReportPersonLink>> result = new ActionResult<>();
		List<WrapOutOkrWorkReportPersonLink> wraps = null;
		List<OkrWorkReportPersonLink>  okrWorkReportPersonLinkList = null;
		Long total = 0L;
		boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "正常" );
			//待处理|处理中|已处理
			wrapIn.addQueryProcessStatus( "已处理" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		
		try{
			okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listPrevWithFilter( id, count, wrapIn );			
			//从数据库中查询符合条件的对象总数
			total = okrWorkReportPersonLinkService.getCountWithFilter( wrapIn );
			wraps = okrWorkReportPersonLink_wrapout_copier.copy( okrWorkReportPersonLinkList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrWorkBaseInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportPersonLink[已归档],下一页.", response = WrapOutOkrWorkReportPersonLink.class, request = com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter.class)
	@PUT
	@Path( "archive/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyArchiveNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkReportPersonLink>> result = new ActionResult<>();
		List<WrapOutOkrWorkReportPersonLink> wraps = null;
		List<OkrWorkReportPersonLink>  okrWorkReportPersonLinkList = null;
		Long total = 0L;
		boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}	
		
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "已归档" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		
		try{
			okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listNextWithFilter( id, count, wrapIn );			
			//从数据库中查询符合条件的对象总数
			total = okrWorkReportPersonLinkService.getCountWithFilter( wrapIn );
			wraps = okrWorkReportPersonLink_wrapout_copier.copy( okrWorkReportPersonLinkList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrWorkBaseInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkReportPersonLink[已归档],上一页.", response = WrapOutOkrWorkReportPersonLink.class, request = com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter.class)
	@PUT
	@Path( "archive/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyArchivePrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkReportPersonLink>> result = new ActionResult<>();
		List<WrapOutOkrWorkReportPersonLink> wraps = null;
		List<OkrWorkReportPersonLink>  okrWorkReportPersonLinkList = null;
		Long total = 0L;
		boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson( request );
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && okrUserCache == null ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink.WrapInFilter();
		}
		
		if ( check && okrUserCache.getLoginUserName() == null ) {
			check = false;
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if ( check ) {
			wrapIn.addQueryInfoStatus( "已归档" );
			wrapIn.setProcessIdentity( okrUserCache.getLoginIdentityName()  );
		}
		
		try{
			okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listPrevWithFilter( id, count, wrapIn );			
			//从数据库中查询符合条件的对象总数
			total = okrWorkReportPersonLinkService.getCountWithFilter( wrapIn );
			wraps = okrWorkReportPersonLink_wrapout_copier.copy( okrWorkReportPersonLinkList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrWorkBaseInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
