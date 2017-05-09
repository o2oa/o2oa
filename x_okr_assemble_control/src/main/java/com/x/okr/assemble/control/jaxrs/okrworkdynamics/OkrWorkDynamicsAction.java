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

import com.google.gson.JsonElement;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.DeployWorkIdsQueryException;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.ViewableWorkIdsQueryException;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.WorkDynamicsDeleteException;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.WorkDynamicsFilterException;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.WorkDynamicsIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.WorkDynamicsNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.WorkDynamicsQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.WorkDynamicsSaveException;
import com.x.okr.assemble.control.jaxrs.okrworkdynamics.exception.WrapInConvertException;
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

	@HttpMethodDescribe(value = "新建或者更新OkrWorkDynamics对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		OkrWorkDynamics okrWorkDynamics = null;
		WrapInOkrWorkDynamics wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrWorkDynamics.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try {
				okrWorkDynamics = okrWorkDynamicsService.save( wrapIn );
				result.setData( new WrapOutId( okrWorkDynamics.getId() ));
			} catch (Exception e) {
				Exception exception = new WorkDynamicsSaveException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkDynamics数据对象.", response = WrapOutId.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		
		if( id == null || id.isEmpty() ){
			Exception exception = new WorkDynamicsIdEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}else{
			try{
				okrWorkDynamicsService.delete( id );
				result.setData( new WrapOutId( id ));
			}catch( Exception e ){
				Exception exception = new WorkDynamicsDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
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
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapOutOkrWorkDynamics wrap = null;
		OkrWorkDynamics okrWorkDynamics = null;
		//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to get okrWorkDynamics{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			Exception exception = new WorkDynamicsIdEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}else{
			try {
				okrWorkDynamics = okrWorkDynamicsService.get( id );
				if( okrWorkDynamics != null ){
					wrap = wrapout_copier.copy( okrWorkDynamics );
					result.setData(wrap);
				}else{
					Exception exception = new WorkDynamicsNotExistsException( id );
					result.error( exception );
					//logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				Exception exception = new WorkDynamicsQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的WrapOutOkrWorkDynamics,下一页.", response = WrapOutOkrWorkDynamics.class, request = JsonElement.class)
	@PUT
	@Path( "filter/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement) {
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
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}

		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new GetOkrUserCacheException( e, currentPerson.getName()  );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}	
		}
		
		if( check && okrUserCache == null ){
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName()  );
			result.error( exception );
			//logger.error( e, currentPerson, request, null);
		}
		if( count == null ){
			count = 20;
		}
		
		if( check ){
			identity = okrUserCache.getLoginIdentityName();
			if( identity == null ){
				check = false;
				Exception exception = new UserNoLoginException( currentPerson.getName()  );
				result.error( exception );
				//logger.error( e, currentPerson, request, null);
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
					Exception exception = new DeployWorkIdsQueryException( e, identity  );
					result.error( exception );
					logger.error( e, currentPerson, request, null);
				}
				
				//再查询不在deploy_ids这些中心工作下面可以观察的的其他工作的IDS
				try {
					work_ids = okrWorkPersonService.listDistinctWorkIdsByPersonIndentity( null, identity, "观察者", deploy_ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new ViewableWorkIdsQueryException( e, identity  );
					result.error( exception );
					logger.error( e, currentPerson, request, null);
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
			}catch(Exception e){
				Exception exception = new WorkDynamicsFilterException( e );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WrapOutOkrWorkDynamics>() );
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}	
}
