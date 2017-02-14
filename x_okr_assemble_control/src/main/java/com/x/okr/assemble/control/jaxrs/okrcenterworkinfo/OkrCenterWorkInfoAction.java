package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

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
import com.x.okr.assemble.control.service.OkrCenterWorkInfoService;
import com.x.okr.assemble.control.service.OkrNotifyService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkPersonSearchService;
import com.x.okr.entity.OkrCenterWorkInfo;

/**
 * 中心工作审核，这个选择可以在部署中心工作的界面上操作
 * 1、需要根据系统配置判断是否已经启动了中心工作审核，如果启动了，那么中心工作是需要先审核再继续部署到个人的
 * 2、根据系统配置判断中心工作的审核员是默认的，还是页面上自己填写
 */

@Path( "okrcenterworkinfo" )
public class OkrCenterWorkInfoAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrCenterWorkInfoAction.class );
	private BeanCopyTools<OkrCenterWorkInfo, WrapOutOkrCenterWorkInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrCenterWorkInfo.class, WrapOutOkrCenterWorkInfo.class, null, WrapOutOkrCenterWorkInfo.Excludes);
	private OkrCenterWorkInfoService okrCenterWorkInfoService = new OkrCenterWorkInfoService();
	private OkrWorkPersonSearchService okrWorkPersonSearchService = new OkrWorkPersonSearchService();
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	private DateOperation dateOperation = new DateOperation();
	
	@HttpMethodDescribe(value = "新建或者更新OkrCenterWorkInfo对象.", request = WrapInOkrCenterWorkInfo.class, response = WrapOutOkrCenterWorkInfo.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post( @Context HttpServletRequest request, WrapInOkrCenterWorkInfo wrapIn ) {
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);		
		if( check ){
			okrUserCache = checkUserLogin( currentPerson.getName() );
			if( okrUserCache == null ){
				check = false;
				result.error( new Exception( "系统未获取到用户登录身份(登录用户所属组织)，请重新打开应用!" ) );
				result.setUserMessage( "系统未获取到用户登录身份(登录用户所属组织)，请重新打开应用!" );
			}
		}
		if( wrapIn != null ){
			wrapIn.setProcessStatus( "草稿" );
			if( check ){
				//创建人和部署人信息直接取当前操作人和登录人身份
				wrapIn.setCreatorName( currentPerson.getName() );
				if( currentPerson.getName().equals( okrUserCache.getLoginUserName())){
					wrapIn.setCreatorOrganizationName( okrUserCache.getLoginUserOrganizationName());
					wrapIn.setCreatorCompanyName( okrUserCache.getLoginUserCompanyName());
					wrapIn.setCreatorIdentity( okrUserCache.getLoginIdentityName() );
				}else{
					//需要查询创建者的相关身份
					try{
						wrapIn.setCreatorIdentity( okrUserManagerService.getFistIdentityNameByPerson( currentPerson.getName() ));
						wrapIn.setCreatorOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( wrapIn.getCreatorIdentity() ));
						wrapIn.setCreatorCompanyName( okrUserManagerService.getCompanyNameByIdentity( wrapIn.getCreatorIdentity() ) );
					}catch(Exception e){
						check = false;
						result.setUserMessage( "系统通过操作用户查询用户身份和组织信息时发生异常!" );
						result.error( e );
						logger.error( "系统通过操作用户查询用户身份和组织信息时发生异常!", e );
					}
				}
			}
			
			if( check ){
				wrapIn.setDeployerName( okrUserCache.getLoginUserName() );
				if( currentPerson.getName().equals( okrUserCache.getLoginUserName())){
					wrapIn.setDeployerOrganizationName( okrUserCache.getLoginUserOrganizationName());
					wrapIn.setDeployerCompanyName( okrUserCache.getLoginUserCompanyName());
					wrapIn.setDeployerIdentity( okrUserCache.getLoginIdentityName() );
				}
			}
			
			//补充部署工作的年份和月份
			if( check ){
				try{
					wrapIn.setDeployYear( dateOperation.getYear( new Date() ));
					wrapIn.setDeployMonth( dateOperation.getMonth( new Date() ));
				}catch( Exception e ){
					check = false;
					result.error( new Exception( "系统补充部署工作的年份和月份信息时发生异常。" ) );
					result.setUserMessage( "系统补充部署工作的年份和月份信息时发生异常。" );
					logger.error( "system get now date for year and month got an exception.", e );
				}	
			}
					
			//补充部署工作的默认最迟完成年份的日期形式
			if( check ){
				if( wrapIn.getDefaultCompleteDateLimitStr() != null && !wrapIn.getDefaultCompleteDateLimitStr().isEmpty() ){
					String date = null;
					try{
						date = dateOperation.getDateStringFromDate( dateOperation.getDateFromString ( wrapIn.getDefaultCompleteDateLimitStr()), "yyyy-MM-dd" ) + " 23:59:59";
						wrapIn.setDefaultCompleteDateLimit( dateOperation.getDateFromString( date ) );
					}catch( Exception e ){
						check = false;
						result.error( e );
						result.setUserMessage( "默认完成时限日期格式不正确，要求格式为：yyyy-mm-dd" );
					}
				}
			}	
			//中心工作部署后，根据需求，有的需要进行审批后才能继续部署，如果需要审批，那么isNeedAudit值为true，并且auditLeaderName不可为空。
			if( check ){
				if( wrapIn.getIsNeedAudit() && ( wrapIn.getAuditLeaderName() == null || wrapIn.getAuditLeaderName().isEmpty() ) ){
					check = false;
					result.error( new Exception( "中心工作需要审核，但是审核领导信息为空，请检查输入的信息内容!" ) );
					result.setUserMessage( "中心工作需要审核，但是审核领导信息为空，请检查输入的信息内容!" );
				}
			}
			/**
			//判断审核领导组织信息是否存在，如果不存在，则需要补充组织信息
			if( check ){
				if( wrapIn.getAuditLeaderName() != null && !wrapIn.getAuditLeaderName().isEmpty() ){
					try{
						if( wrapIn.getAuditLeaderOrganizationName() == null || wrapIn.getAuditLeaderOrganizationName().isEmpty() ){
							wrapIn.setAuditLeaderOrganizationName( okrUserManagerService.getDepartmentNameByEmployeeName( wrapIn.getAuditLeaderName()) );
						}
					}catch(Exception e){
						check = false;
						result.error( "ERROR" );
						result.setUserMessage( "系统根据审核领导姓名补充审核领导组织信息时发生异常。" );
						logger.error( "system get organization name by audit leader name["+ wrapIn.getAuditLeaderName() +"] got an exception.", e );
					}	
				}	
			}
			
			//判断审核领导公司信息是否存在，如果不存在，则需要补充公司信息
			if( check ){
				if( wrapIn.getAuditLeaderName() != null && !wrapIn.getAuditLeaderName().isEmpty() ){
					try{
						if( wrapIn.getAuditLeaderCompanyName() == null || wrapIn.getAuditLeaderCompanyName().isEmpty() ){
							if( wrapIn.getAuditLeaderOrganizationName() != null && !wrapIn.getAuditLeaderOrganizationName().isEmpty() ){
								wrapIn.setAuditLeaderCompanyName( okrUserManagerService.getCompanyNameByOrganizationName( wrapIn.getAuditLeaderOrganizationName() ) );
							}else{
								wrapIn.setAuditLeaderCompanyName( okrUserManagerService.getCompanyNameByEmployeeName( wrapIn.getAuditLeaderName() ) );
							}
						}
					}catch(Exception e){
						check = false;
						result.error( "ERROR" );
						result.setUserMessage( "系统根据审核领导姓名补充审核领导公司信息时发生异常。" );
						logger.error( "system get company name by audit leader name["+ wrapIn.getAuditLeaderName() +"] got an exception.", e );
					}		
				}
			}
			**/

			//开始保存中心工作信息
			if( check ){
				try {
					okrCenterWorkInfo = okrCenterWorkInfoService.save( wrapIn );
					okrWorkDynamicsService.workDynamic(
							okrCenterWorkInfo.getId(), 
							null,
							okrCenterWorkInfo.getTitle(),
							"保存中心工作", 
							currentPerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"保存中心工作：" + okrCenterWorkInfo.getTitle(), 
							"中心工作保存成功！"
					);
					result.setUserMessage( okrCenterWorkInfo.getId() );
				} catch (Exception e) {
					result.setUserMessage( "系统在保存中心工作信息时发生异常!" );
					result.error( e );
					logger.error( "OkrCenterWorkInfoService save object get an exception", e);
				}
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存中心工作!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存中心工作!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	private OkrUserCache checkUserLogin(String name) {
		OkrUserCache okrUserCache = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( name );
		} catch (Exception e) {
			logger.error( "system get login indentity with person name got an exception", e );
			return null;
		}
		if(  okrUserCache == null || okrUserCache.getLoginIdentityName() == null ){
			return null;
		}
		if( okrUserCache.getLoginUserName() == null ){
			return null;
		}
		if( okrUserCache.getLoginUserOrganizationName() == null ){
			return null;
		}
		if( okrUserCache.getLoginUserCompanyName() == null ){
			return null;
		}
		return okrUserCache;
	}

	@HttpMethodDescribe(value = "根据ID删除OkrCenterWorkInfo数据对象.", response = WrapOutOkrCenterWorkInfo.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson currentPerson = this.effectivePerson(request);
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrUserCache  okrUserCache  = null;
		Boolean check = true;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		if( check ){
			okrUserCache = checkUserLogin( currentPerson.getName() );
			if( okrUserCache == null ){
				check = false;
				result.error( new Exception( "系统未获取到用户登录身份(登录用户所属组织)，请重新打开应用!" ) );
				result.setUserMessage( "系统未获取到用户登录身份(登录用户所属组织)，请重新打开应用!" );
			}
		}
		if( check ){
			try {
				okrCenterWorkInfo = okrCenterWorkInfoService.get( id );
			} catch (Throwable th) {
				logger.error( "system get by id get an exception" );
				th.printStackTrace();
				result.error(th);
			}
		}
		try{
			okrCenterWorkInfoService.delete( id );
			result.setUserMessage( "成功删除中心工作数据信息。id=" + id );
			if( okrCenterWorkInfo != null ){
				okrWorkDynamicsService.workDynamic(
						okrCenterWorkInfo.getId(), 
						null,
						okrCenterWorkInfo.getTitle(),
						"删除中心工作", 
						currentPerson.getName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"删除中心工作：" + okrCenterWorkInfo.getTitle(), 
						"中心工作删除成功！"
				);
			}else{
				okrWorkDynamicsService.workDynamic(
						id, 
						null,
						"无",
						"删除中心工作", 
						currentPerson.getName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"删除中心工作：" + id, 
						"中心工作删除成功！"
				);
			}
		}catch(Exception e){
			logger.error( "system delete okrCenterWorkInfoService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除中心工作数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID归档OkrCenterWorkInfo数据对象.", response = WrapOutOkrCenterWorkInfo.class)
	@GET
	@Path( "archive/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response archive(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson currentPerson = this.effectivePerson(request);
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		OkrUserCache  okrUserCache  = null;
		Boolean check = true;
		if( id == null || id.isEmpty() ){
			check = false;
			result.setUserMessage( "需要归档的中心工作ID为空，无法对中心工作进行归档！" );
			result.error( new Exception( "需要归档的中心工作ID为空，无法对中心工作进行归档！" ) );
			logger.error( "id is null, system can not delete any object." );
		}
		if( check ){
			okrUserCache = checkUserLogin( currentPerson.getName() );
			if( okrUserCache == null ){
				check = false;
				result.error( new Exception( "系统未获取到用户登录身份(登录用户所属组织)，请重新打开应用!" ) );
				result.setUserMessage( "系统未获取到用户登录身份(登录用户所属组织)，请重新打开应用!" );
			}
		}
		if( check ){
			try {
				okrCenterWorkInfo = okrCenterWorkInfoService.get( id );
			} catch (Throwable th) {
				logger.error( "system get by id get an exception" );
				th.printStackTrace();
				result.error(th);
			}
		}
		if( check ){
			try{
				okrCenterWorkInfoService.archive( id );
				result.setUserMessage( "成功归档中心工作数据信息。id=" + id );
				if( okrCenterWorkInfo != null ){
					okrWorkDynamicsService.workDynamic(
							okrCenterWorkInfo.getId(), 
							null,
							okrCenterWorkInfo.getTitle(),
							"归档中心工作", 
							currentPerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"归档中心工作：" + okrCenterWorkInfo.getTitle(), 
							"中心工作归档成功！"
					);
				}else{
					okrWorkDynamicsService.workDynamic(
							id, 
							null,
							"无",
							"归档中心工作", 
							currentPerson.getName(), 
							okrUserCache.getLoginUserName(), 
							okrUserCache.getLoginIdentityName() , 
							"归档中心工作：" + id, 
							"中心工作归档成功！"
					);
				}
			}catch(Exception e){
				logger.error( "system archive okrCenterWorkInfoService get an exception, {'id':'"+id+"'}", e );
				result.setUserMessage( "归档中心工作数据过程中发生异常。" );
				result.error( e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取OkrCenterWorkInfo对象.", response = WrapOutOkrCenterWorkInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<>();
		WrapOutOkrCenterWorkInfo wrap = null;
		OkrCenterWorkInfo OkrCenterWorkInfo = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			OkrCenterWorkInfo = okrCenterWorkInfoService.get(id);
			if( OkrCenterWorkInfo != null ){
				wrap = wrapout_copier.copy( OkrCenterWorkInfo );
				result.setData(wrap);
			}else{
				logger.error( "system can not get any object by {'id':'"+id+"'}. " );
			}
		} catch (Throwable th) {
			logger.error( "system get by id get an exception" );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[草稿],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "draft/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
		List<WrapOutOkrCenterWorkInfo> wraps = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = null;
		Long total = 0L;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		try{			
			//信息状态要是正常的，已删除的数据不需要查询出来
			wrapIn.setProcessIdentities( null );
			wrapIn.setWorkProcessStatuses( null );
			wrapIn.setEmployeeNames( null );
			wrapIn.setEmployeeIdentities(null);
			wrapIn.setCompanyNames( null );
			wrapIn.setOrganizationNames( null );
			wrapIn.setInfoStatuses( null );
			wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
			
			wrapIn.addQueryInfoStatus( "正常" );	
			wrapIn.addQueryWorkProcessStatus( "草稿" );
			wrapIn.addQueryProcessIdentity( "部署者" );
			okrCenterWorkInfoList = okrCenterWorkInfoService.listCenterNextWithFilter( id, count, wrapIn );
			
			//从数据库中查询符合条件的对象总数
			total = okrCenterWorkInfoService.getCenterCountWithFilter( wrapIn );
			wraps = wrapout_copier.copy( okrCenterWorkInfoList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrCenterWorkInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[草稿],上一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "draft/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
		List<WrapOutOkrCenterWorkInfo> wraps = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = null;
		Long total = 0L;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		try{			
			//信息状态要是正常的，已删除的数据不需要查询出来
			wrapIn.setProcessIdentities( null );
			wrapIn.setWorkProcessStatuses( null );
			wrapIn.setEmployeeNames( null );
			wrapIn.setEmployeeIdentities(null);
			wrapIn.setCompanyNames( null );
			wrapIn.setOrganizationNames( null );
			wrapIn.setInfoStatuses( null );
			wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
			
			wrapIn.addQueryInfoStatus( "正常" );	
			wrapIn.addQueryWorkProcessStatus( "草稿" );
			wrapIn.addQueryProcessIdentity( "部署者" );
			okrCenterWorkInfoList = okrCenterWorkInfoService.listCenterPrevWithFilter( id, count, wrapIn );
			
			//从数据库中查询符合条件的对象总数
			total = okrCenterWorkInfoService.getCenterCountWithFilter( wrapIn );
			wraps = wrapout_copier.copy( okrCenterWorkInfoList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrCenterWorkInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[草稿],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "deployed/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDeployedNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
		List<WrapOutOkrCenterWorkInfo> wraps = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = null;
		Long total = 0L;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		try{			
			//信息状态要是正常的，已删除的数据不需要查询出来
			wrapIn.setProcessIdentities( null );
			wrapIn.setEmployeeNames( null );
			wrapIn.setEmployeeIdentities(null);
			wrapIn.setCompanyNames( null );
			wrapIn.setOrganizationNames( null );
			wrapIn.setInfoStatuses( null );
			wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
			
			wrapIn.addQueryInfoStatus( "正常" );	
			if( wrapIn.getWorkProcessStatuses() == null ){
				wrapIn.addQueryWorkProcessStatus( "待审核" );
				wrapIn.addQueryWorkProcessStatus( "待确认" );
				wrapIn.addQueryWorkProcessStatus( "执行中" );
				wrapIn.addQueryWorkProcessStatus( "已完成" );
				wrapIn.addQueryWorkProcessStatus( "已撤消" );
			}
			wrapIn.addQueryProcessIdentity( "观察者" );
			okrCenterWorkInfoList = okrCenterWorkInfoService.listCenterNextWithFilter( id, count, wrapIn );
			
			//从数据库中查询符合条件的对象总数
			total = okrCenterWorkInfoService.getCenterCountWithFilter( wrapIn );
			wraps = wrapout_copier.copy( okrCenterWorkInfoList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrCenterWorkInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[草稿],上一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "deployed/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyDeployedPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
		List<WrapOutOkrCenterWorkInfo> wraps = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = null;
		Long total = 0L;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		try{	
			//信息状态要是正常的，已删除的数据不需要查询出来
			wrapIn.setProcessIdentities( null );
			//wrapIn.setWorkProcessStatuses( null );
			wrapIn.setEmployeeNames( null );
			wrapIn.setEmployeeIdentities(null);
			wrapIn.setCompanyNames( null );
			wrapIn.setOrganizationNames( null );
			wrapIn.setInfoStatuses( null );
			wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
			
			wrapIn.addQueryInfoStatus( "正常" );	
			if( wrapIn.getWorkProcessStatuses() == null ){
				wrapIn.addQueryWorkProcessStatus( "待审核" );
				wrapIn.addQueryWorkProcessStatus( "待确认" );
				wrapIn.addQueryWorkProcessStatus( "执行中" );
				wrapIn.addQueryWorkProcessStatus( "已完成" );
				wrapIn.addQueryWorkProcessStatus( "已撤消" );
			}
			wrapIn.addQueryProcessIdentity( "观察者" );
			okrCenterWorkInfoList = okrCenterWorkInfoService.listCenterPrevWithFilter( id, count, wrapIn );
			
			//从数据库中查询符合条件的对象总数
			total = okrCenterWorkInfoService.getCenterCountWithFilter( wrapIn );
			wraps = wrapout_copier.copy( okrCenterWorkInfoList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrCenterWorkInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[草稿],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "read/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyReadNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
		List<WrapOutOkrCenterWorkInfo> wraps = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = null;
		Long total = 0L;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		try{			
			//信息状态要是正常的，已删除的数据不需要查询出来
			wrapIn.setProcessIdentities( null );
			wrapIn.setEmployeeNames( null );
			wrapIn.setEmployeeIdentities(null);
			wrapIn.setCompanyNames( null );
			wrapIn.setOrganizationNames( null );
			wrapIn.setInfoStatuses( null );
			wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
			
			wrapIn.addQueryInfoStatus( "正常" );	
			if( wrapIn.getWorkProcessStatuses() == null ){
				wrapIn.addQueryWorkProcessStatus( "待审核" );
				wrapIn.addQueryWorkProcessStatus( "待确认" );
				wrapIn.addQueryWorkProcessStatus( "执行中" );
				wrapIn.addQueryWorkProcessStatus( "已完成" );
				wrapIn.addQueryWorkProcessStatus( "已撤消" );
			}
			wrapIn.addQueryProcessIdentity( "观察者" );
			okrCenterWorkInfoList = okrCenterWorkInfoService.listCenterNextWithFilter( id, count, wrapIn );
			
			//从数据库中查询符合条件的对象总数
			total = okrCenterWorkInfoService.getCenterCountWithFilter( wrapIn );
			wraps = wrapout_copier.copy( okrCenterWorkInfoList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrCenterWorkInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[草稿],上一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "read/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyReadPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
		List<WrapOutOkrCenterWorkInfo> wraps = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = null;
		Long total = 0L;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		try{			
			//信息状态要是正常的，已删除的数据不需要查询出来
			wrapIn.setProcessIdentities( null );
			wrapIn.setEmployeeNames( null );
			wrapIn.setEmployeeIdentities(null);
			wrapIn.setCompanyNames( null );
			wrapIn.setOrganizationNames( null );
			wrapIn.setInfoStatuses( null );
			wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
			
			wrapIn.addQueryInfoStatus( "正常" );	
			if( wrapIn.getWorkProcessStatuses() == null ){
				wrapIn.addQueryWorkProcessStatus( "待审核" );
				wrapIn.addQueryWorkProcessStatus( "待确认" );
				wrapIn.addQueryWorkProcessStatus( "执行中" );
				wrapIn.addQueryWorkProcessStatus( "已完成" );
				wrapIn.addQueryWorkProcessStatus( "已撤消" );
			}
			wrapIn.addQueryProcessIdentity( "观察者" );
			okrCenterWorkInfoList = okrCenterWorkInfoService.listCenterPrevWithFilter( id, count, wrapIn );
			
			//从数据库中查询符合条件的对象总数
			total = okrCenterWorkInfoService.getCenterCountWithFilter( wrapIn );
			wraps = wrapout_copier.copy( okrCenterWorkInfoList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter okrCenterWorkInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[已归档],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "archive/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyArchiveNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
		List<WrapOutOkrCenterWorkInfo> wraps = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = null;
		Long total = 0L;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		try{			
			//信息状态要是正常的，已删除的数据不需要查询出来
			wrapIn.setProcessIdentities( null );
			wrapIn.setEmployeeNames( null );
			wrapIn.setEmployeeIdentities(null);
			wrapIn.setCompanyNames( null );
			wrapIn.setOrganizationNames( null );
			wrapIn.setInfoStatuses( null );
			wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
			wrapIn.addQueryInfoStatus( "已归档" );
			wrapIn.addQueryProcessIdentity( "观察者" );
			okrCenterWorkInfoList = okrCenterWorkInfoService.listCenterNextWithFilter( id, count, wrapIn );
			
			//从数据库中查询符合条件的对象总数
			total = okrCenterWorkInfoService.getCenterCountWithFilter( wrapIn );
			wraps = wrapout_copier.copy( okrCenterWorkInfoList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter archive okrCenterWorkInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo[已归档],上一页.", response = WrapOutOkrCenterWorkInfo.class, request = com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter.class)
	@PUT
	@Path( "archive/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyArchivePrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
		List<WrapOutOkrCenterWorkInfo> wraps = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = null;
		Long total = 0L;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( wrapIn == null ){
			wrapIn = new com.x.okr.assemble.control.jaxrs.okrworkperson.WrapInFilter();
		}
		try{			
			//信息状态要是正常的，已删除的数据不需要查询出来
			wrapIn.setProcessIdentities( null );
			wrapIn.setEmployeeNames( null );
			wrapIn.setEmployeeIdentities(null);
			wrapIn.setCompanyNames( null );
			wrapIn.setOrganizationNames( null );
			wrapIn.setInfoStatuses( null );
			wrapIn.addQueryEmployeeIdentities( okrUserCache.getLoginIdentityName() );
			
			wrapIn.addQueryInfoStatus( "已归档" );
			wrapIn.addQueryProcessIdentity( "观察者" );
			okrCenterWorkInfoList = okrCenterWorkInfoService.listCenterPrevWithFilter( id, count, wrapIn );
			
			//从数据库中查询符合条件的对象总数
			total = okrCenterWorkInfoService.getCenterCountWithFilter( wrapIn );
			wraps = wrapout_copier.copy( okrCenterWorkInfoList );
			result.setCount( total );
			result.setData( wraps );
		}catch(Throwable th){
			logger.error( "system filter archive okrCenterWorkInfo got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的OkrCenterWorkInfo,下一页.", response = WrapOutOkrCenterWorkInfo.class, request = WrapInFilter.class)
	@PUT
	@Path( "filter/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
		List<WrapOutOkrCenterWorkInfo> wraps = null;
		List<OkrCenterWorkInfo> okrCenterWorkInfoList = null;
		Long total = 0L;
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( id == null || id.isEmpty() ){
			id = "(0)";
		}			
		if( count == null ){
			count = 12;
		}
		if( wrapIn == null ){
			wrapIn = new WrapInFilter();
		}
		
		// 对wrapIn里的信息进行校验
		if (check && okrUserCache.getLoginUserOrganizationName() == null) {
			check = false;
			result.error(new Exception("系统未获取到用户登录身份(登录用户名)，请重新打开应用!"));
			result.setUserMessage("系统未获取到用户登录身份(登录用户名)，请重新打开应用!");
			logger.error("系统未获取到用户登录身份(登录用户名)，请重新打开应用!");
		}
		
		if( check ){
			try{
				wrapIn.setIdentity( okrUserCache.getLoginIdentityName()  );
				wrapIn.addQueryInfoStatus( "正常" );	
				wrapIn.addQueryInfoStatus( "已归档" );	
				//直接从Person里查询已经分页好的中心工作ID
				okrCenterWorkInfoList = okrWorkPersonSearchService.listNextCenterIdsWithFilter( id, count, wrapIn );
				total = okrWorkPersonSearchService.getCenterCountWithFilter( wrapIn );
				wraps = wrapout_copier.copy( okrCenterWorkInfoList );
				result.setData( wraps );
				result.setCount( total );
			}catch( Exception e ){
				result.setUserMessage( "系统在分页查询中心工作信息时发生异常。" );
				logger.error( "system filter okrCenterWorkInfo got an exception.", e );
				result.error( e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "正式部署中心工作.", response = WrapOutOkrCenterWorkInfo.class, request = WrapInFilter.class)
	@PUT
	@Path( "deploy" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deploy( @Context HttpServletRequest request, WrapInFilter wrapIn ) {
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<>();
		List<String> ids = wrapIn.getCenterIds();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		if( ids == null || ids.size() == 0 ){
			logger.error( "center ids is empty, can not deploy center work to user!" );
			result.error( new Exception( "传入的ID为空，无法继续部署中心工作！" ) );
			result.setUserMessage( "传入的ID为空，无法继续部署中心工作！" );
		}else{
			//中心工作只需要修改中心工作状态为 [执行中] 即可
			for( String id : ids ){
				try{
					okrCenterWorkInfo = okrCenterWorkInfoService.get(id);
					if( okrCenterWorkInfo != null ){
						okrCenterWorkInfoService.deploy( id );
						//中心工作部署成功，通知部署者
						new OkrNotifyService().notifyDeployerForCenterWorkDeploySuccess( okrCenterWorkInfo );
						result.setUserMessage( "中心工作部署成功！" );
					}else{
						logger.error( "system can not fount object, okrCenterWorkInfo{'id':'"+ id +"'} is not exist." );
						result.error( new Exception( "中心工作不存在，无法继续部署工作！" ) );
						result.setUserMessage( "中心工作不存在，无法继续部署工作！" );
					}
				}catch( Exception e){
					logger.error( "system deploy center work{'id':'"+ id +"'} got an exception.", e );
					result.error( e );
					result.setUserMessage( "中心工作部署发生异常！" );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}