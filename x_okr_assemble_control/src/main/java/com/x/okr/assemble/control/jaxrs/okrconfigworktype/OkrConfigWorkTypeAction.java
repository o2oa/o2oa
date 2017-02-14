package com.x.okr.assemble.control.jaxrs.okrconfigworktype;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrConfigWorkTypeService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkPersonService;
import com.x.okr.entity.OkrConfigWorkType;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;


@Path( "okrconfigworktype" )
public class OkrConfigWorkTypeAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrConfigWorkTypeAction.class );
	private BeanCopyTools<OkrConfigWorkType, WrapOutOkrConfigWorkType> wrapout_copier = BeanCopyToolsBuilder.create( OkrConfigWorkType.class, WrapOutOkrConfigWorkType.class, null, WrapOutOkrConfigWorkType.Excludes);
	private OkrConfigWorkTypeService okrConfigWorkTypeService = new OkrConfigWorkTypeService();
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
	
	private Ehcache cache = ApplicationCache.instance().getCache( OkrConfigWorkType.class );
	private String catchNamePrefix = this.getClass().getName();
		
	@HttpMethodDescribe(value = "新建或者更新OkrConfigWorkType对象.", request = WrapInOkrConfigWorkType.class, response = WrapOutOkrConfigWorkType.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrConfigWorkType wrapIn) {
		ActionResult<WrapOutOkrConfigWorkType> result = new ActionResult<>();
		OkrConfigWorkType okrConfigWorkType = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+okrUserCache.getLoginIdentityName() +"'] try to save OkrConfigWorkType......" );
		if( wrapIn != null ){
			try {
				okrConfigWorkType = okrConfigWorkTypeService.save( wrapIn );
				if( okrConfigWorkType != null ){
					ApplicationCache.notify( OkrConfigWorkType.class );
					result.setUserMessage( okrConfigWorkType.getId() );
				}else{
					result.error( new Exception( "系统在保存信息时发生异常!" ) );
					result.setUserMessage( "系统在保存信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存信息时发生异常!" );
				logger.error( "OkrConfigWorkTypeService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrConfigWorkType数据对象.", response = WrapOutOkrConfigWorkType.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrConfigWorkType> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+okrUserCache.getLoginIdentityName() +"'] try to delete okrConfigWorkType{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrConfigWorkTypeService.delete( id );
			ApplicationCache.notify( OkrConfigWorkType.class );
			
			result.setUserMessage( "成功删除工作类别数据信息。id=" + id );
		}catch(Exception e){
			logger.error( "system delete okrConfigWorkTypeService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除工作类别数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrConfigWorkType对象.", response = WrapOutOkrConfigWorkType.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrConfigWorkType> result = new ActionResult<>();
		WrapOutOkrConfigWorkType wrap = null;
		OkrConfigWorkType okrConfigWorkType = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+okrUserCache.getLoginIdentityName() +"'] try to get okrConfigWorkType{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		
		String cacheKey = catchNamePrefix + "." + id;
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>System get okrConfigWorkType from cache. cacheKey:"+cacheKey );
			wrap = (WrapOutOkrConfigWorkType) element.getObjectValue();
			result.setData( wrap );
		}else{
			try {
				okrConfigWorkType = okrConfigWorkTypeService.get( id );
				if( okrConfigWorkType != null ){
					wrap = wrapout_copier.copy( okrConfigWorkType );
					
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
	
	@HttpMethodDescribe(value = "获取OkrConfigWorkType对象列表.", response = WrapOutOkrConfigWorkType.class)
	@GET
	@Path( "all" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response list(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutOkrConfigWorkType>> result = new ActionResult<>();
		List<WrapOutOkrConfigWorkType> wraps = null;
		List<OkrConfigWorkType> okrConfigWorkTypeList = null;
		
		String cacheKey = catchNamePrefix + ".all";
		Element element = null;
		
		element = cache.get( cacheKey );
		if( element != null ){
			wraps = ( List<WrapOutOkrConfigWorkType> ) element.getObjectValue();
			result.setData( wraps );
		}else{
			try {
				okrConfigWorkTypeList = okrConfigWorkTypeService.listAll();
				if( okrConfigWorkTypeList != null && !okrConfigWorkTypeList.isEmpty() ){
					wraps = wrapout_copier.copy( okrConfigWorkTypeList );
					cache.put( new Element( cacheKey, wraps ) );
					result.setData( wraps );
				}
			} catch ( Exception e) {
				logger.error( "system get by id got an exception", e );
				result.error( e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取OkrConfigWorkType对象列表，并且统计每一类所有的中心工作数量.", response = WrapOutOkrConfigWorkType.class)
	@GET
	@Path( "countAll" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response countAll(@Context HttpServletRequest request ) {
		ActionResult<List<WrapOutOkrConfigWorkType>> result = new ActionResult<>();
		List<WrapOutOkrConfigWorkType> wraps = null;
		List<OkrConfigWorkType> okrConfigWorkTypeList = null;
		List<String> centerIds = null;
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
		try {
			okrConfigWorkTypeList = okrConfigWorkTypeService.listAll();
			if( okrConfigWorkTypeList != null && !okrConfigWorkTypeList.isEmpty() ){
				wraps = wrapout_copier.copy( okrConfigWorkTypeList );
				for( WrapOutOkrConfigWorkType wrap : wraps ){
					//统计用户可以看到的每一个类别的中心工作数量
					centerIds = okrWorkPersonService.countCenterWorkByWorkType( wrap.getWorkTypeName(), okrUserCache.getLoginIdentityName() , "观察者" );
					if( centerIds != null ){
						wrap.setCenterCount( centerIds.size() );
					}else{
						wrap.setCenterCount( 0 );
					}
				}
				result.setData( wraps );
			}
		} catch ( Exception e) {
			logger.error( "system get by id got an exception", e );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
