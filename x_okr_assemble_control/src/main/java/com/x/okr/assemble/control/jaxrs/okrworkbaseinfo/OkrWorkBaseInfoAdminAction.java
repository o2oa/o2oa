package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

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
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapOutOkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

/**
 * 具体工作项有短期工作还长期工作，短期工作不需要自动启动定期汇报，由人工撰稿汇报即可
 */

@Path( "admin/okrworkbaseinfo" )
public class OkrWorkBaseInfoAdminAction extends StandardJaxrsAction{	
	private Logger logger = LoggerFactory.getLogger( OkrWorkBaseInfoAdminAction.class );

	@HttpMethodDescribe(value = "根据ID获取OkrWorkBaseInfo对象.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		try {
			result = new ExcuteGetForAdmin().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteGetForAdminn got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID强制删除OkrWorkBaseInfo数据对象.", response = WrapOutOkrWorkBaseInfo.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteForce( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDeleteForce().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteDeleteForce got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	@HttpMethodDescribe( value = "根据中心工作ID获取我可以看到的所有OkrWorkBaseInfo对象.", response = WrapOutOkrWorkBaseInfo.class )
	@GET
	@Path( "center/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWorkInCenter( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<>();
		try {
			result = new ExcuteListAllWorkByCenterId().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.warn( "system excute ExcuteListAllWorkByCenterId got an exception. " );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[草稿],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = WrapInAdminFilter.class)
	@PUT
	@Path( "filter/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInAdminFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		Boolean check = true;

//		if( check ){
//			if( wrapIn == null ){
//				check = false;
//				result.error( new Exception( "请求传入的参数为空，无法继续保存工作信息!" ) );
//				result.setUserMessage( "请求传入的参数为空，无法继续保存工作信息!" );
//			}
//		}
		if( check ){
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "title", wrapIn.getFilterLikeContent() );
				likesMap.put( "shortWorkDetail", wrapIn.getFilterLikeContent() );
				likesMap.put( "centerTitle", wrapIn.getFilterLikeContent() );
				likesMap.put( "creatorIdentity", wrapIn.getFilterLikeContent() );
				likesMap.put( "workType", wrapIn.getFilterLikeContent() );
				likesMap.put( "responsibilityEmployeeName", wrapIn.getFilterLikeContent() );
				likesMap.put( "workProcessStatus", wrapIn.getFilterLikeContent() );
			}
		}
		if( check ){
			BeanCopyTools<OkrWorkBaseInfo, WrapOutOkrWorkBaseInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkBaseInfo.class, WrapOutOkrWorkBaseInfo.class, null, WrapOutOkrWorkBaseInfo.Excludes);
			sequenceField = wrapIn.getSequenceField();
			try{
				result = this.standardListNext( wrapout_copier, id, count, sequenceField,  equalsMap, notEqualsMap, likesMap, insMap, notInsMap, membersMap, notMembersMap, false, wrapIn.getOrder() );
			}catch(Throwable th){
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[草稿],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = WrapInAdminFilter.class)
	@PUT
	@Path( "filter/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListPrevWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInAdminFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		Boolean check = true;

//		if( check ){
//			if( wrapIn == null ){
//				check = false;
//				result.error( new Exception( "请求传入的参数为空，无法继续保存工作信息!" ) );
//				result.setUserMessage( "请求传入的参数为空，无法继续保存工作信息!" );
//			}
//		}
		if( check ){
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "title", wrapIn.getFilterLikeContent() );
				likesMap.put( "shortWorkDetail", wrapIn.getFilterLikeContent() );
				likesMap.put( "centerTitle", wrapIn.getFilterLikeContent() );
				likesMap.put( "creatorIdentity", wrapIn.getFilterLikeContent() );
				likesMap.put( "workType", wrapIn.getFilterLikeContent() );
				likesMap.put( "responsibilityEmployeeName", wrapIn.getFilterLikeContent() );
				likesMap.put( "workProcessStatus", wrapIn.getFilterLikeContent() );
			}
		}
		if( check ){
			sequenceField = wrapIn.getSequenceField();
			try{
				BeanCopyTools<OkrWorkBaseInfo, WrapOutOkrWorkBaseInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkBaseInfo.class, WrapOutOkrWorkBaseInfo.class, null, WrapOutOkrWorkBaseInfo.Excludes);
				result = this.standardListNext( wrapout_copier, id, count, sequenceField,  equalsMap, notEqualsMap, likesMap, insMap, notInsMap, membersMap, notMembersMap, true, wrapIn.getOrder() );
			}catch(Throwable th){
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
}