package com.x.okr.assemble.control.jaxrs.okrtask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonElement;
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
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.entity.OkrTask;


@Path( "okrtask" )
public class OkrTaskAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrTaskAction.class );
	
	@HttpMethodDescribe(value = "获取我的所有工作汇报汇总的内容.", response = WrapOutOkrTaskCollect.class )
	@GET
	@Path( "department/reportTaskCollect/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response showTaskCollect(@Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutOkrTaskCollect>> result = new ActionResult<>();
		try {
			result = new ExcuteListTaskCollect().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteListTaskCollect got an exception.id:" + id);
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "处理指定的待阅信息.", response = WrapOutOkrTask.class )
	@GET
	@Path( "process/read/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response processRead(@Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteReadProcess().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteReadProcess got an exception.id:" + id);
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrTask对象.", response = WrapOutOkrTask.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrTask> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteGet got an exception.id:" + id);
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "根据过滤条件列表我的待办列表,下一页.", response = WrapOutOkrTask.class, request = JsonElement.class)
	@PUT
	@Path( "filter/my/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyTaskNext(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutOkrTask>> result = new ActionResult<>();	
		OkrUserCache  okrUserCache  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
		BeanCopyTools<OkrTask, WrapOutOkrTask> wrapout_copier = BeanCopyToolsBuilder.create( OkrTask.class, WrapOutOkrTask.class, null, WrapOutOkrTask.Excludes);
		WrapInFilterTask wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterTask.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}

		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
			} catch (Exception e ) {
				check = false;
				Exception exception = new GetOkrUserCacheException( e, currentPerson.getName() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new UserNoLoginException( currentPerson.getName() );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
//		if( wrapIn == null ){
//			check = false;
//			result.error( new Exception( "请求传入的参数为空，无法继续保存工作信息!" ) );
//		}
		if( check ){
			try{
				//只允许查询属于自己的登录身份的数据
				EqualsTerms equalsMap = new EqualsTerms();
				NotEqualsTerms notEqualsMap = new NotEqualsTerms();
				InTerms insMap = new InTerms();
				NotInTerms notInsMap = new NotInTerms();
				MemberTerms membersMap = new MemberTerms();
				NotMemberTerms notMembersMap = new NotMemberTerms();
				LikeTerms likesMap = new LikeTerms();
				equalsMap.put( "targetIdentity", okrUserCache.getLoginIdentityName()  );
				Collection<String> dynamicObjectTypeNotIn = null;
				if( notInsMap.get( "dynamicObjectType" ) == null ){
					dynamicObjectTypeNotIn = new ArrayList<String>();
				}else{
					if( notInsMap.get( "dynamicObjectType" ) != null ){
						dynamicObjectTypeNotIn = ( Collection<String> )notInsMap.get( "dynamicObjectType" );
					}
				}
				dynamicObjectTypeNotIn.add( "工作汇报" );
				notInsMap.put( "dynamicObjectType", dynamicObjectTypeNotIn );
				
				result = this.standardListNext( wrapout_copier, id, count, wrapIn.getSequenceField(), 
						equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
						membersMap, notMembersMap, true, wrapIn.getOrder() );
			}catch(Throwable th){
				th.printStackTrace();
				result.error(th);
			}
		}else{
			result.setCount( 0L );
			result.setData( new ArrayList<WrapOutOkrTask>() );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
