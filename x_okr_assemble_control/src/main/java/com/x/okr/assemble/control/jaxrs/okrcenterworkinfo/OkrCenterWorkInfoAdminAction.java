package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

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
import com.x.okr.assemble.control.service.OkrCenterWorkInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.entity.OkrCenterWorkInfo;

@Path( "admin/okrcenterworkinfo" )
public class OkrCenterWorkInfoAdminAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( OkrCenterWorkInfoAdminAction.class );
	private BeanCopyTools<OkrCenterWorkInfo, WrapOutOkrCenterWorkInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrCenterWorkInfo.class, WrapOutOkrCenterWorkInfo.class, null, WrapOutOkrCenterWorkInfo.Excludes);
	private OkrCenterWorkInfoService okrCenterWorkInfoService = new OkrCenterWorkInfoService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	
	@HttpMethodDescribe(value = "根据ID删除OkrCenterWorkInfo数据对象.", response = WrapOutOkrCenterWorkInfo.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
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
				result.setUserMessage( "需要删除的中心工作ID为空，无法进行数据查询。" );
				result.error( new Exception( "需要删除的中心工作ID为空，无法进行数据查询！" ) );
				logger.error( "id is null, system can not delete any object." );
			}
		}
		if(check){
			try {
				okrCenterWorkInfo = okrCenterWorkInfoService.get(id);
			} catch (Exception e) {
				logger.error( "system get okrCenterWorkInfo by id got an exception, {'id':'"+id+"'}", e );
				check = false;
				result.setUserMessage( "根据ID查询中心工作数据过程中发生异常。" );
				result.error( e );
			}
		}
		if(check){
			if( okrCenterWorkInfo == null ){
				check = false;
				result.setUserMessage( "中心工作不存在。" );
				result.error( new Exception("中心工作不存在") );
			}
		}
		if(check){
			try{
				okrCenterWorkInfoService.delete( id );
				result.setUserMessage( "成功删除中心工作数据信息。id=" + id );
			}catch(Exception e){
				check = false;
				logger.error( "system delete okrCenterWorkInfoService get an exception, {'id':'"+id+"'}", e );
				result.setUserMessage( "删除中心工作数据过程中发生异常。" );
				result.error( e );
			}
		}
		if( check ){
			try {
				okrWorkDynamicsService.workDynamic(
						okrCenterWorkInfo.getId(), 
						null,
						okrCenterWorkInfo.getTitle(),
						"保存中心工作", 
						currentPerson.getName(), 
						currentPerson.getName(), 
						currentPerson.getName(), 
						"删除中心工作：" + okrCenterWorkInfo.getTitle(), 
						"中心工作删除成功！"
				);
				result.setUserMessage( okrCenterWorkInfo.getId() );
			} catch (Exception e) {
				logger.error( "okrWorkDynamicsService workDynamic got an exception", e);
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
				result.setUserMessage( "需要查询的中心工作ID为空，无法进行数据查询。" );
				result.error( new Exception( "需要查询的中心工作ID为空，无法进行数据查询！" ) );
				logger.error( "id is null, system can not query any object." );
			}
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
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo,下一页.", response = WrapOutOkrCenterWorkInfo.class, request = WrapInAdminFilter.class)
	@PUT
	@Path( "filter/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInAdminFilter wrapIn ) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
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
				wrapIn = new WrapInAdminFilter();
			}
		}
		if( check ){
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "title", wrapIn.getFilterLikeContent() );
			}
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "defaultWorkType", wrapIn.getFilterLikeContent() );
			}
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "description", wrapIn.getFilterLikeContent() );
			}
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "processStatus", wrapIn.getFilterLikeContent() );
			}
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "deployerName", wrapIn.getFilterLikeContent() );
			}
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "deployerOrganizationName", wrapIn.getFilterLikeContent() );
			}
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "deployerCompanyName", wrapIn.getFilterLikeContent() );
			}
		}
		if( check ){
			sequenceField = wrapIn.getSequenceField();
			try{
				result = this.standardListNext( wrapout_copier, id, count, sequenceField,  equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
						membersMap, notMembersMap, false, wrapIn.getOrder() );
			}catch( Exception e ){
				logger.error( "system pagenate center work query got an exception.", e );
				result.setUserMessage("系统在分页查询中心工作信息时发生异常。");
				result.error( e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo,下一页.", response = WrapOutOkrCenterWorkInfo.class, request = WrapInAdminFilter.class)
	@PUT
	@Path( "filter/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListPrevWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInAdminFilter wrapIn ) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
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
				wrapIn = new WrapInAdminFilter();
			}
		}	
		if( check ){
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "title", wrapIn.getFilterLikeContent() );
			}
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "defaultWorkType", wrapIn.getFilterLikeContent() );
			}
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "description", wrapIn.getFilterLikeContent() );
			}
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "processStatus", wrapIn.getFilterLikeContent() );
			}
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "deployerName", wrapIn.getFilterLikeContent() );
			}
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "deployerOrganizationName", wrapIn.getFilterLikeContent() );
			}
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "deployerCompanyName", wrapIn.getFilterLikeContent() );
			}
		}		
		if( check ){
			sequenceField = wrapIn.getSequenceField();
			try{
				result = this.standardListPrev( wrapout_copier, id, count, sequenceField,  equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
						membersMap, notMembersMap, false, wrapIn.getOrder() );
			}catch( Exception e ){
				logger.error( "system pagenate center work query got an exception.", e );
				result.setUserMessage("系统在分页查询中心工作信息时发生异常。");
				result.error( e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}