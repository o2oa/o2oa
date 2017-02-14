package com.x.okr.assemble.control.jaxrs.okrtask;
import java.util.ArrayList;
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
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.service.OkrTaskService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;
import com.x.okr.entity.OkrTask;


@Path( "admin/okrtask" )
public class OkrTaskAdminAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrTaskAdminAction.class );
	private BeanCopyTools<OkrTask, WrapOutOkrTask> wrapout_copier = BeanCopyToolsBuilder.create( OkrTask.class, WrapOutOkrTask.class, null, WrapOutOkrTask.Excludes);
	private OkrTaskService okrTaskService = new OkrTaskService();
	private OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	
	@HttpMethodDescribe(value = "根据过滤条件列表我的待办列表,下一页.", response = WrapOutOkrTask.class, request = WrapInAdminFilter.class)
	@PUT
	@Path( "filter/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListTaskNext(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInAdminFilter wrapIn) {
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
		
//		EffectivePerson currentPerson = this.effectivePerson(request);
//		Organization organization = new Organization();
//		Boolean hasPermission = false;
//		try {
//			hasPermission = organization.role().hasAny(currentPerson.getName(),"OkrSystemAdmin" );
//			if( !hasPermission ){
//				check = false;
//				result.error( new Exception("用户未拥有操作权限[OkrSystemAdmin]！") );
//				result.setUserMessage( "用户未拥有操作权限[OkrSystemAdmin]！" );
//			}
//		} catch (Exception e) {
//			logger.error( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!", e );
//			check = false;
//			result.error( e );
//			result.setUserMessage( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!" );
//		}
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception( "请求传入的参数为空，无法继续保存工作信息!" ) );
				result.setUserMessage( "请求传入的参数为空，无法继续保存工作信息!" );
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
				result = this.standardListNext( wrapout_copier, id, count, sequenceField,  equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
						membersMap, notMembersMap, true, wrapIn.getOrder() );
				
			}catch( Exception e ){
				logger.error( "系统查询待办信息列表时发生异常!", e );
				check = false;
				result.error( e );
				result.setUserMessage( "系统查询待办信息列表时发生异常!" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据过滤条件列表我的待办列表,下一页.", response = WrapOutOkrTask.class, request = WrapInAdminFilter.class)
	@PUT
	@Path( "filter/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListTaskPrev(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInAdminFilter wrapIn) {
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
		
//		EffectivePerson currentPerson = this.effectivePerson(request);
//		Organization organization = new Organization();
//		Boolean hasPermission = false;
//		try {
//			hasPermission = organization.role().hasAny(currentPerson.getName(),"OkrSystemAdmin" );
//			if( !hasPermission ){
//				check = false;
//				result.error( new Exception("用户未拥有操作权限[OkrSystemAdmin]！") );
//				result.setUserMessage( "用户未拥有操作权限[OkrSystemAdmin]！" );
//			}
//		} catch (Exception e) {
//			logger.error( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!", e );
//			check = false;
//			result.error( e );
//			result.setUserMessage( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!" );
//		}
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception( "请求传入的参数为空，无法继续保存工作信息!" ) );
				result.setUserMessage( "请求传入的参数为空，无法继续保存工作信息!" );
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
				result = this.standardListPrev( wrapout_copier, id, count, sequenceField,  equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
						membersMap, notMembersMap, true, wrapIn.getOrder() );
				
			}catch( Exception e ){
				logger.error( "系统查询待办信息列表时发生异常!", e );
				check = false;
				result.error( e );
				result.setUserMessage( "系统查询待办信息列表时发生异常!" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrTask数据对象.", response = WrapOutOkrTask.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		ActionResult<WrapOutOkrTask> result = new ActionResult<>();
		OkrTask okrTask = null;
		Boolean check = true;
		
		EffectivePerson currentPerson = this.effectivePerson(request);
//		Organization organization = new Organization();
//		Boolean hasPermission = false;
//		try {
//			hasPermission = organization.role().hasAny(currentPerson.getName(),"OkrSystemAdmin" );
//			if( !hasPermission ){
//				check = false;
//				result.error( new Exception("用户未拥有操作权限[OkrSystemAdmin]！") );
//				result.setUserMessage( "用户未拥有操作权限[OkrSystemAdmin]！" );
//			}
//		} catch (Exception e) {
//			logger.error( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!", e );
//			check = false;
//			result.error( e );
//			result.setUserMessage( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!" );
//		}
		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				result.setUserMessage( "需要删除的待办ID为空，无法进行数据删除。" );
				result.error( new Exception( "需要删除的待办ID为空，无法进行数据删除！" ) );
				logger.error( "id is null, system can not delete any object." );
			}
		}
		
		if( check ){
			try {
				okrTask = okrTaskService.get( id );
				if( okrTask == null ){
					check = false;
					result.setUserMessage( "需要删除的待办数据不存在。" );
					result.error( new Exception( "需要删除的待办数据不存在，无法进行数据删除！" ) );
					logger.error( "system can not get any object by {'id':'"+id+"'}. " );
				}
			} catch ( Exception e) {
				check = false;
				result.setUserMessage( "系统在根据ID查询待办数据信息时发生异常。" );
				result.error( e );
				logger.error( "system get by id got an exception", e );
			}
		}
		if( check ){
			try{
				okrTaskService.delete( id );
				result.setUserMessage( "成功删除待办数据信息。id=" + id );
			}catch(Exception e){
				logger.error( "system delete okrTaskService get an exception, {'id':'"+id+"'}", e );
				result.setUserMessage( "删除待办数据过程中发生异常。" );
				result.error( e );
			}
		}
		if( check ){
			if( "工作汇报".equals( okrTask.getDynamicObjectType() )){
				try{
					List<String> workTypeList = new ArrayList<String>();
					workTypeList.add( okrTask.getWorkType() );
					okrWorkReportTaskCollectService.checkReportCollectTask( okrTask.getTargetIdentity(), workTypeList );
				}catch( Exception e ){
					logger.error( "待办信息删除成功，但对汇报者进行汇报待办汇总发生异常。", e );
				}
			}
		}
		if( check ){
			try{
				okrWorkDynamicsService.taskDynamic(
						okrTask.getCenterId(), 
						okrTask.getCenterTitle(), 
						okrTask.getWorkId(), 
						okrTask.getWorkTitle(), 
						okrTask.getTitle(), 
						id, 
						"删除待办待阅", 
						currentPerson.getName(), 
						"删除待办待阅：" + okrTask.getTitle(), 
						"管理员删除待办待阅操作成功！", 
						okrTask.getTargetName(), 
						okrTask.getTargetIdentity()
				);
			}catch(Exception e){
				logger.error( "system record taskDynamic get an exception", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrTask对象.", response = WrapOutOkrTask.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrTask> result = new ActionResult<>();
		WrapOutOkrTask wrap = null;
		OkrTask okrTask = null;
		Boolean check = true;
		
//		EffectivePerson currentPerson = this.effectivePerson(request);
//		Organization organization = new Organization();
//		Boolean hasPermission = false;
//		try {
//			hasPermission = organization.role().hasAny(currentPerson.getName(),"OkrSystemAdmin" );
//			if( !hasPermission ){
//				check = false;
//				result.error( new Exception("用户未拥有操作权限[OkrSystemAdmin]！") );
//				result.setUserMessage( "用户未拥有操作权限[OkrSystemAdmin]！" );
//			}
//		} catch (Exception e) {
//			logger.error( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!", e );
//			check = false;
//			result.error( e );
//			result.setUserMessage( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!" );
//		}
		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				result.setUserMessage( "需要查询的待办ID为空，无法进行数据查询。" );
				result.error( new Exception( "需要查询的待办ID为空，无法进行数据查询！" ) );
				logger.error( "id is null, system can not query any object." );
			}
		}
		if( check ){
			try {
				okrTask = okrTaskService.get( id );
				if( okrTask != null ){
					wrap = wrapout_copier.copy( okrTask );
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
}
