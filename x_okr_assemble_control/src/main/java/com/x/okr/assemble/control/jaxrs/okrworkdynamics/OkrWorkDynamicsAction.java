package com.x.okr.assemble.control.jaxrs.okrworkdynamics;
import java.util.ArrayList;
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
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapOutOkrWorkBaseInfo;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkPersonService;
import com.x.okr.entity.OkrWorkDynamics;


@Path( "okrworkdynamics" )
public class OkrWorkDynamicsAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkDynamicsAction.class );
	private BeanCopyTools<OkrWorkDynamics, WrapOutOkrWorkDynamics> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkDynamics.class, WrapOutOkrWorkDynamics.class, null, WrapOutOkrWorkDynamics.Excludes);
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	private OkrWorkPersonService okrWorkPersonService = new OkrWorkPersonService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();

	@HttpMethodDescribe(value = "新建或者更新OkrWorkDynamics对象.", request = WrapInOkrWorkDynamics.class, response = WrapOutOkrWorkDynamics.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrWorkDynamics wrapIn) {
		ActionResult<WrapOutOkrWorkDynamics> result = new ActionResult<>();
		OkrWorkDynamics okrWorkDynamics = null;
		if( wrapIn != null ){
			try {
				okrWorkDynamics = okrWorkDynamicsService.save( wrapIn );
				if( okrWorkDynamics != null ){
					result.setUserMessage( okrWorkDynamics.getId() );
				}else{
					result.error( new Exception( "系统在保存信息时发生异常!" ) );
					result.setUserMessage( "系统在保存信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存信息时发生异常!" );
				logger.error( "OkrWorkDynamicsService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkDynamics数据对象.", response = WrapOutOkrWorkDynamics.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkDynamics> result = new ActionResult<>();
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrWorkDynamicsService.delete( id );
			result.setUserMessage( "成功删除工作动态数据信息。id=" + id );
		}catch(Exception e){
			logger.error( "system delete okrWorkDynamicsService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除工作动态数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkDynamics对象.", response = WrapOutOkrWorkDynamics.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkDynamics> result = new ActionResult<>();
		WrapOutOkrWorkDynamics wrap = null;
		OkrWorkDynamics okrWorkDynamics = null;
		//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to get okrWorkDynamics{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrWorkDynamics = okrWorkDynamicsService.get( id );
			if( okrWorkDynamics != null ){
				wrap = wrapout_copier.copy( okrWorkDynamics );
				result.setData(wrap);
			}else{
				logger.error( "system can not get any object by {'id':'"+id+"'}. " );
			}
		} catch (Throwable th) {
			logger.error( "system get by id got an exception" );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的WrapOutOkrWorkDynamics,下一页.", response = WrapOutOkrWorkBaseInfo.class, request = WrapInFilter.class)
	@PUT
	@Path( "filter/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkDynamics>> result = new ActionResult<List<WrapOutOkrWorkDynamics>>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutOkrWorkDynamics> wrapOutOkrWorkDynamicsList = null;
		List<OkrWorkDynamics> dynamicsList = null;
		List<String> deploy_ids = null;
		List<String> work_ids = null;
		List<String> statuses =  new ArrayList<String>();
		OkrUserCache  okrUserCache  = null;
		String identity = null;
		Long total = 0L;
		boolean check = true;
		
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
		if( count == null ){
			count = 20;
		}
		
		if( check ){
			identity = okrUserCache.getLoginIdentityName();
			if( identity == null ){
				check = false;
				logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
				result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
				result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
			}
		}
		
		if( check ){
			statuses.add( "正常" );
			
			//计算可以查看的范围
			if( okrUserCache.isOkrSystemAdmin() ){
				logger.debug( "用户是OkrSystemAdmin." );
				//如果是系统管理员，可以查看全部，不需要进行ID过滤
				wrapIn.setCenterIds( null );
				wrapIn.setWorkId( null );
				wrapIn.setOkrSystemAdmin(true);
			}else{
				wrapIn.setOkrSystemAdmin(false);
				//如果不是管理员：
				//先查询用户部署的中心工作ID，这些中心工作可以全部看到
				try {
					deploy_ids = okrWorkPersonService.listDistinctCenterIdsByPersonIdentity( identity, "部署者", statuses );
				} catch (Exception e) {
					check = false;
					logger.error( "system search center id from workperson[listDistinctCenterIdsByPersonIdentity] got an exception.", e );
					result.error( e );
					result.setUserMessage( "系统获取用户部署的中心工作时发生异常！" );
				}
				
				//再查询非这些中心工作下面可以观察的的其他工作的IDS
				try {
					work_ids = okrWorkPersonService.listDistinctWorkIdsByPersonIndentity( identity, "观察者", deploy_ids );
				} catch (Exception e) {
					check = false;
					logger.error( "system search work id from workperson[listDistinctWorkIdsByPersonIndentity] got an exception.", e );
					result.error( e );
					result.setUserMessage( "系统获取用户可以查看的工作时发生异常！" );
				}
				wrapIn.setWorkIds( work_ids );
				wrapIn.setCenterIds( deploy_ids );
			}			
		}
		
		if( check ){
			wrapIn.setUserIdentity( identity );
			try{
				dynamicsList = okrWorkDynamicsService.listDynamicNextWithFilter( id, count, wrapIn );
				wrapOutOkrWorkDynamicsList = wrapout_copier.copy( dynamicsList );
				total = okrWorkDynamicsService.getDynamicCountWithFilter(wrapIn);
				result.setData( wrapOutOkrWorkDynamicsList );
				result.setCount( total );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}	
}
