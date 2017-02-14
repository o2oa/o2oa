package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

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

import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.InTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.application.jaxrs.MemberTerms;
import com.x.base.core.application.jaxrs.NotEqualsTerms;
import com.x.base.core.application.jaxrs.NotInTerms;
import com.x.base.core.application.jaxrs.NotMemberTerms;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrConfigSecretaryService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrUserManagerService;
import com.x.okr.entity.OkrConfigSecretary;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path( "okrconfigsecretary" )
public class OkrConfigSecretaryAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrConfigSecretaryAction.class );
	private BeanCopyTools<OkrConfigSecretary, WrapOutOkrConfigSecretary> wrapout_copier = BeanCopyToolsBuilder.create( OkrConfigSecretary.class, WrapOutOkrConfigSecretary.class, null, WrapOutOkrConfigSecretary.Excludes);
	private OkrConfigSecretaryService okrConfigSecretaryService = new OkrConfigSecretaryService();
	private OkrUserManagerService okrUserManagerService = new OkrUserManagerService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	
	private Ehcache cache = ApplicationCache.instance().getCache( OkrConfigSecretary.class );
	private String catchNamePrefix = this.getClass().getName();
	
	@HttpMethodDescribe(value = "根据员工姓名获取相应的秘书配置列表.", response = WrapOutOkrConfigSecretary.class)
	@GET
	@Path( "list/my" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getMySercretary(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutOkrConfigSecretary>> result = new ActionResult<List<WrapOutOkrConfigSecretary>>();
		List<WrapOutOkrConfigSecretary> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson( request );
		List<String> ids = null;
		List<OkrConfigSecretary> okrConfigSecretaryList = null;
		String cacheKey = catchNamePrefix + "." + currentPerson.getName();
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			wraps = (List<WrapOutOkrConfigSecretary>) element.getObjectValue();
			result.setData( wraps );
		}else{
			try {
				ids = okrConfigSecretaryService.listIdsByPerson( currentPerson.getName() );
				if( ids != null && ids.size() > 0 ){
					okrConfigSecretaryList = okrConfigSecretaryService.listByIds( ids );
				}
				if( okrConfigSecretaryList != null ){
					wraps = wrapout_copier.copy( okrConfigSecretaryList );
					
					cache.put( new Element( cacheKey, wraps ) );
					
					result.setData( wraps );
				}else{
					logger.warn( "system can not get any object by {'user':'"+ currentPerson.getName() +"'}. " );
				}
			} catch (Throwable th) {
				logger.error( "system get by id got an exception" );
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	@HttpMethodDescribe(value = "新建或者更新OkrConfigSecretary对象.", request = WrapInOkrConfigSecretary.class, response = WrapOutOkrConfigSecretary.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrConfigSecretary wrapIn) {
		ActionResult<WrapOutOkrConfigSecretary> result = new ActionResult<>();
		OkrConfigSecretary okrConfigSecretary = null;
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

		if( wrapIn != null ){
			if( check ){
				if( okrUserCache.getLoginUserOrganizationName()  != null && !okrUserCache.getLoginUserOrganizationName().isEmpty() ){
					wrapIn.setSecretaryOrganizationName( okrUserCache.getLoginUserOrganizationName() );
				}else{
					check = false;
					result.error( new Exception( "用户的登录身份异常，请重新打开应用或联系管理!" ) );
					result.setUserMessage( "用户的登录身份异常，请重新打开应用或联系管理!" );
					logger.error( "login user organization name is null!" );
				}
			}
			
			if( check ){
				if( okrUserCache.getLoginUserCompanyName()  != null && !okrUserCache.getLoginUserCompanyName().isEmpty() ){
					wrapIn.setSecretaryCompanyName( okrUserCache.getLoginUserCompanyName() );
				}else{
					check = false;
					result.error( new Exception( "用户的登录身份异常，请重新打开应用或联系管理!" ) );
					result.setUserMessage( "用户的登录身份异常，请重新打开应用或联系管理!" );
					logger.error( "login user company name is null!" );
				}
			}
			
			if( check ){
				if( wrapIn.getLeaderIdentity() == null || wrapIn.getLeaderIdentity().isEmpty() ){
					check = false;
					result.error( new Exception( "传入的代理领导身份信息为空，无法保存秘书配置信息!" ) );
					result.setUserMessage( "传入的代理领导身份信息为空，无法保存秘书配置信息!" );
					logger.error( "leader identity name is null!" );
				}else{
					//补充代理领导所属组织名称和公司名称
					try {
						wrapIn.setLeaderOrganizationName( okrUserManagerService.getDepartmentNameByIdentity( wrapIn.getLeaderIdentity() ) );
					} catch (Exception e) {
						result.error( e );
						result.setUserMessage( "根据代理领导身份查询所属组织名称发生异常，无法保存秘书配置信息!" );
						logger.error( "system query organization name by identity got an exception!", e );
					}
					try {
						wrapIn.setLeaderCompanyName( okrUserManagerService.getCompanyNameByIdentity( wrapIn.getLeaderIdentity() ) );
					} catch (Exception e) {
						result.error( e );
						result.setUserMessage( "根据代理领导身份查询所属公司名称发生异常，无法保存秘书配置信息!" );
						logger.error( "system query company name by identity got an exception!", e );
					}
				}
			}
			
			if( check ){
				try {
					okrConfigSecretary = okrConfigSecretaryService.save( wrapIn );
					if( okrConfigSecretary != null ){
						ApplicationCache.notify( OkrConfigSecretary.class );
						
						result.setUserMessage( okrConfigSecretary.getId() );
					}else{
						result.error( new Exception( "系统在保存信息时发生异常!" ) );
						result.setUserMessage( "系统在保存信息时发生异常!" );
					}
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统在保存信息时发生异常!" );
					logger.error( "OkrConfigSecretaryService save object got an exception", e );
				}
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrConfigSecretary数据对象.", response = WrapOutOkrConfigSecretary.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrConfigSecretary> result = new ActionResult<>();
		OkrConfigSecretary okrConfigSecretary = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}		
		try{
			okrConfigSecretary = okrConfigSecretaryService.get( id );
			if( okrConfigSecretary != null ){
				okrConfigSecretaryService.delete( id );
				ApplicationCache.notify( OkrConfigSecretary.class );
				
				result.setUserMessage( "成功删除秘书配置信息。id=" + id );
			}else{
				result.setUserMessage( "秘书配置信息不存在，不需要删除。id=" + id );
			}
		}catch(Exception e){
			logger.error( "system delete okrConfigSecretaryService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除秘书配置过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrConfigSecretary对象.", response = WrapOutOkrConfigSecretary.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrConfigSecretary> result = new ActionResult<>();
		WrapOutOkrConfigSecretary wrap = null;
		OkrConfigSecretary okrConfigSecretary = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		
		String cacheKey = catchNamePrefix + "." + id;
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			//logger.debug(">>>>>>>>>>>>>>>>>>>>>>System get okrConfigSecretary from cache. cacheKey:"+cacheKey );
			wrap = ( WrapOutOkrConfigSecretary ) element.getObjectValue();
			result.setData( wrap );
		}else{
			try {
				okrConfigSecretary = okrConfigSecretaryService.get( id );
				if( okrConfigSecretary != null ){
					wrap = wrapout_copier.copy( okrConfigSecretary );
					
					cache.put( new Element( cacheKey, wrap ) );
					
					result.setData(wrap);
				}else{
					logger.error( "system can not get any object by {'id':'"+id+"'}. " );
				}
			} catch (Throwable th) {
				logger.error( "system get by id got an exception" );
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的OkrConfigSecretary,下一页.", response = WrapOutOkrConfigSecretary.class, request = WrapInFilter.class)
	@PUT
	@Path( "filter/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrConfigSecretary>> result = new ActionResult<>();
		if( id == null || id.isEmpty() ){
			id = "(0)";
		}
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		
		try{
			result = this.standardListNext( wrapout_copier, id, count, wrapIn.getSequenceField(), 
					equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
					membersMap, notMembersMap, wrapIn.isAndJoin(), wrapIn.getOrder() );
		}catch(Throwable th){
			logger.error( "system filter OkrConfigSecretary got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的OkrConfigSecretary,上一页.", response = WrapOutOkrConfigSecretary.class, request = WrapInFilter.class)
	@PUT
	@Path( "filter/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrConfigSecretary>> result = new ActionResult<>();
		if( id == null || id.isEmpty() ){
			id = "(0)";
		}
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		try {
			result = this.standardListNext( wrapout_copier, id, count, wrapIn.getSequenceField(), 
					equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
					membersMap, notMembersMap, wrapIn.isAndJoin(), wrapIn.getOrder() );
		} catch (Throwable th) {
			logger.error( "system filter OkrConfigSecretary got an exception." );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
}