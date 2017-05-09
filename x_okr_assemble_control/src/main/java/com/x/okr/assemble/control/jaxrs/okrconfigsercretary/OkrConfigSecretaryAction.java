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

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.InTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.application.jaxrs.MemberTerms;
import com.x.base.core.application.jaxrs.NotEqualsTerms;
import com.x.base.core.application.jaxrs.NotInTerms;
import com.x.base.core.application.jaxrs.NotMemberTerms;
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
import com.x.okr.assemble.control.jaxrs.okrconfigsercretary.exception.WrapInConvertException;
import com.x.okr.entity.OkrConfigSecretary;

@Path( "okrconfigsecretary" )
public class OkrConfigSecretaryAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrConfigSecretaryAction.class );	
	
	@HttpMethodDescribe(value = "根据员工姓名获取相应的秘书配置列表.", response = WrapOutOkrConfigSecretary.class)
	@GET
	@Path( "list/my" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMySercretary(@Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult< List<WrapOutOkrConfigSecretary> > result = new ActionResult<>();
		try {
			result = new ExcuteListMySercretary().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	@HttpMethodDescribe(value = "新建或者更新OkrConfigSecretary对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save(@Context HttpServletRequest request, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapInOkrConfigSecretary wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrConfigSecretary.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrConfigSecretary数据对象.", response = WrapOutOkrConfigSecretary.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrConfigSecretary对象.", response = WrapOutOkrConfigSecretary.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrConfigSecretary> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的OkrConfigSecretary,下一页.", response = WrapOutOkrConfigSecretary.class, request = JsonElement.class)
	@PUT
	@Path( "filter/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutOkrConfigSecretary>> result = new ActionResult<>();
		BeanCopyTools<OkrConfigSecretary, WrapOutOkrConfigSecretary> wrapout_copier = BeanCopyToolsBuilder.create( OkrConfigSecretary.class, WrapOutOkrConfigSecretary.class, null, WrapOutOkrConfigSecretary.Excludes);
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInFilterSecretary wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterSecretary.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
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
			}catch(Exception e){
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的OkrConfigSecretary,上一页.", response = WrapOutOkrConfigSecretary.class, request = JsonElement.class)
	@PUT
	@Path( "filter/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutOkrConfigSecretary>> result = new ActionResult<>();
		BeanCopyTools<OkrConfigSecretary, WrapOutOkrConfigSecretary> wrapout_copier = BeanCopyToolsBuilder.create( OkrConfigSecretary.class, WrapOutOkrConfigSecretary.class, null, WrapOutOkrConfigSecretary.Excludes);
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInFilterSecretary wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterSecretary.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
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
			}catch(Exception e){
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
}