package com.x.okr.assemble.control.jaxrs.okrtask;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.x.okr.entity.OkrTask;
import com.x.organization.core.express.Organization;


@Path( "admin/okrtask" )
public class OkrTaskAdminAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrTaskAdminAction.class );
	
	@HttpMethodDescribe(value = "根据ID删除OkrTask数据对象.", response = WrapOutOkrTask.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteDelete got an exception.");
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
	
	@HttpMethodDescribe(value = "根据过滤条件列表我的待办列表,下一页.", response = WrapOutOkrTask.class, request = JsonElement.class)
	@PUT
	@Path( "filter/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListTaskNext(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutOkrTask>> result = new ActionResult<>();
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Organization organization = new Organization();
		Boolean hasPermission = false;
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
				hasPermission = organization.role().hasAny(currentPerson.getName(),"OkrSystemAdmin" );
				if( !hasPermission ){
					check = false;
					Exception exception = new InsufficientPermissionsException( currentPerson.getName(), "OkrSystemAdmin" );
					result.error( exception );
					logger.error( exception, currentPerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new OkrSystemAdminCheckException( e, currentPerson.getName() );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		}
		if( check ){
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "dynamicObjectTitle", wrapIn.getFilterLikeContent() );
				likesMap.put( "targetName", wrapIn.getFilterLikeContent() );
				likesMap.put( "targetIdentity", wrapIn.getFilterLikeContent() );
				likesMap.put( "dynamicObjectType", wrapIn.getFilterLikeContent() );
			}
		}
		if( check ){
			sequenceField = wrapIn.getSequenceField();
			try{
				BeanCopyTools<OkrTask, WrapOutOkrTask> wrapout_copier = BeanCopyToolsBuilder.create( OkrTask.class, WrapOutOkrTask.class, null, WrapOutOkrTask.Excludes);
				result = this.standardListNext( wrapout_copier, id, count, sequenceField,  equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
						membersMap, notMembersMap, true, wrapIn.getOrder() );
				
			}catch( Exception e ){
				check = false;
				logger.warn( "系统查询待办信息列表时发生异常!");
				result.error( e );
				logger.error( e, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据过滤条件列表我的待办列表,下一页.", response = WrapOutOkrTask.class, request = WrapInFilterTask.class)
	@PUT
	@Path( "filter/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListTaskPrev(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInFilterTask wrapIn) {
		ActionResult<List<WrapOutOkrTask>> result = new ActionResult<>();
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		Boolean check = true;
		
		EffectivePerson currentPerson = this.effectivePerson(request);
		Organization organization = new Organization();
		Boolean hasPermission = false;
		try {
			hasPermission = organization.role().hasAny(currentPerson.getName(),"OkrSystemAdmin" );
			if( !hasPermission ){
				check = false;
				Exception exception = new InsufficientPermissionsException( currentPerson.getName(), "OkrSystemAdmin" );
				result.error( exception );
				logger.error( exception, currentPerson, request, null);
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new OkrSystemAdminCheckException( e, currentPerson.getName() );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
//		if( check ){
//			if( wrapIn == null ){
//				check = false;
//				result.error( new Exception( "请求传入的参数为空，无法继续保存工作信息!" ) );
//				result.setUserMessage( "请求传入的参数为空，无法继续保存工作信息!" );
//			}
//		}
		if( check ){
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "dynamicObjectTitle", wrapIn.getFilterLikeContent() );
				likesMap.put( "targetName", wrapIn.getFilterLikeContent() );
				likesMap.put( "targetIdentity", wrapIn.getFilterLikeContent() );
				likesMap.put( "dynamicObjectType", wrapIn.getFilterLikeContent() );
			}
		}
		if( check ){
			sequenceField = wrapIn.getSequenceField();
			try{
				BeanCopyTools<OkrTask, WrapOutOkrTask> wrapout_copier = BeanCopyToolsBuilder.create( OkrTask.class, WrapOutOkrTask.class, null, WrapOutOkrTask.Excludes);
				result = this.standardListPrev( wrapout_copier, id, count, sequenceField,  equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
						membersMap, notMembersMap, true, wrapIn.getOrder() );
				
			}catch( Exception e ){
				check = false;
				logger.warn( "系统查询待办信息列表时发生异常!");
				result.error( e );
				logger.error( e, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	
}
