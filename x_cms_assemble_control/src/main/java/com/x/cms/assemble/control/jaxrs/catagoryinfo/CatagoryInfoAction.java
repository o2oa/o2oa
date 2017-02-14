package com.x.cms.assemble.control.jaxrs.catagoryinfo;

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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.SortTools;
import com.x.cms.assemble.control.service.CatagoryInfoServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.CatagoryInfo;


@Path("catagoryinfo")
public class CatagoryInfoAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( CatagoryInfoAction.class );
	private UserManagerService userManagerService = new UserManagerService();
	private CatagoryInfoServiceAdv catagoryInfoServiceAdv = new CatagoryInfoServiceAdv();
	private BeanCopyTools<CatagoryInfo, WrapOutCatagoryInfo> copier = BeanCopyToolsBuilder.create( CatagoryInfo.class, WrapOutCatagoryInfo.class, null, WrapOutCatagoryInfo.Excludes);
	private BeanCopyTools<WrapInCatagoryInfo, CatagoryInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInCatagoryInfo.class, CatagoryInfo.class, null, WrapInCatagoryInfo.Excludes );

	@HttpMethodDescribe(value = "获取用户有权限访问的所有分类列表", response = WrapOutCatagoryInfo.class)
	@GET
	@Path("list/user/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyCatagoryInfo( @Context HttpServletRequest request, @PathParam("appId")String appId ) {		
		ActionResult<List<WrapOutCatagoryInfo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutCatagoryInfo> wraps = null;
		List<String> ids = null;
		List<CatagoryInfo> catagoryInfoList = null;	
		Boolean check = true;
		Boolean isXAdmin = false;
		
		if( appId == null || appId.isEmpty() ){
			check = false;
			result.error( new Exception("系统未获取到参数appId.") );
			result.setUserMessage( "系统未获取到参数appId." );
		}
		
		if( check ){
			try {
				isXAdmin = userManagerService.isXAdmin( request, currentPerson );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统检查用户身份时发生异常." );
				logger.error( "system check user is xadmin got an exception.", e );
			}
		}
		if( check ){			
			if( isXAdmin ){
				try {
					ids = catagoryInfoServiceAdv.listByAppId( appId );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统根据栏目ID获取栏目下所有的分类信息时发生异常." );
					logger.error( "system qeury all catagory ids with appId got an exception.", e );
				}
			}else{
				try {
					ids = catagoryInfoServiceAdv.listByAppIdAndUserPermission( appId, currentPerson.getName() );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统根据栏目ID以及用户权限获取栏目下所有的分类信息时发生异常." );
					logger.error( "system qeury catagory ids with appId and user permission got an exception.", e );
				}
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					catagoryInfoList = catagoryInfoServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统根据分类ID列表获取栏目下所有的分类信息时发生异常." );
					logger.error( "system query catagory info list by ids got an exception.", e );
				}
			}
		}
		if( check ){
			if( catagoryInfoList != null && !catagoryInfoList.isEmpty() ){
				try {
					wraps = copier.copy( catagoryInfoList );
					SortTools.asc( wraps, "catagorySeq" );		
					result.setData(wraps);		
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统转换对象为输出格式时发生异常." );
					logger.error( "system copy catagory info list to wrap got an exception.", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有分类列表", response = WrapOutCatagoryInfo.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllCatagoryInfo( @Context HttpServletRequest request ) {		
		ActionResult<List<WrapOutCatagoryInfo>> result = new ActionResult<>();
		List<CatagoryInfo> catagoryInfoList = null;
		List<WrapOutCatagoryInfo> wraps = null;
		Boolean check = true;
		
		if( check ){
			try {
				catagoryInfoList = catagoryInfoServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统查询所有分类对象时发生异常。" );
				logger.error( "system query all catagory info got an exception.", e );
			}
		}
		if( check ){
			if( catagoryInfoList != null && !catagoryInfoList.isEmpty() ){
				try {
					wraps = copier.copy( catagoryInfoList );
					SortTools.asc( wraps, "appId", "catagorySeq" );		
					result.setData(wraps);		
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统转换对象为输出格式时发生异常." );
					logger.error( "system copy catagory info list to wrap got an exception.", e );
				}
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取分类对象.", response = WrapOutCatagoryInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutCatagoryInfo> result = new ActionResult<>();
		WrapOutCatagoryInfo wrap = null;
		CatagoryInfo catagoryInfo = null;
		Boolean check = true;
		
		if( id == null || id.isEmpty() ){
			check = false;
			result.error( new Exception("未获取到传入的参数id") );
			result.setUserMessage( "未获取到传入的参数id." );
		}
		if( check ){
			try {
				catagoryInfo = catagoryInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据ID查询分类对象时发生异常。" );
				logger.error( "system query catagory info with id got an exception.id:" + id, e );
			}
		}
		if( check ){
			if( catagoryInfo == null  ){
				check = false;
				result.error( new Exception("需要查询的数据不存在。") );
				result.setUserMessage( "需要查询的数据不存在。" );
			}else{
				try {
					wrap = copier.copy( catagoryInfo );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统转换对象为输出格式时发生异常." );
					logger.error( "system copy catagory info to wrap got an exception.", e );
				}
				result.setData(wrap);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "创建CatagoryInfo分类信息对象.", request = WrapInCatagoryInfo.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post( @Context HttpServletRequest request, WrapInCatagoryInfo wrapIn ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		String identityName = null;
		String departmentName = null;
		String companyName = null;
		CatagoryInfo catagoryInfo = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( wrapIn == null ){
			check = false;
			result.error( new Exception("系统未获取到需要保存的数据.") );
			result.setUserMessage( "系统未获取到需要保存的数据." );
		}
		if( check ){
			if ( !"xadmin".equalsIgnoreCase(currentPerson.getName()) ){
				identityName = wrapIn.getIdentity();
				if( identityName == null || identityName.isEmpty() ){
					try {
						identityName = userManagerService.getFistIdentityNameByPerson( currentPerson.getName() );
					} catch (Exception e) {
						check = false;
						result.error( e );
						result.setUserMessage( "系统在根据人员姓名查询第一个身份信息时发生异常，请检查是否为人员分配了部门。" );
						logger.error( "system query first identity name by person got an exception.name:" + currentPerson.getName() , e );
					}
				}
			}else{
				identityName = "xadmin";
				departmentName = "xadmin";
				companyName = "xadmin";
			}
		}
		if( check && !"xadmin".equals( identityName ) ){
			try {
				departmentName = userManagerService.getDepartmentNameByIdentity( identityName );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据人员身份查询部门名称时发生异常。" );
				logger.error( "system query department name by identity name got an exception.identityName:" + identityName , e );
			}
		}
		if( check && !"xadmin".equals( identityName ) ){
			try {
				companyName = userManagerService.getCompanyNameByIdentity( identityName );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据人员身份查询公司名称时发生异常。" );
				logger.error( "system query company name by identity name got an exception.identityName:" + identityName , e );
			}
		}
		if( check ){
			try {
				catagoryInfo = wrapin_copier.copy( wrapIn );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统将输入的参数转换为可保存对象时发生异常。" );
				logger.error( "system copy wrapIn to catagoryInfo object got an exception." , e );
			}
		}
		if( check ){
			if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
				catagoryInfo.setId( wrapIn.getId() );
			}
			catagoryInfo.setCreatorIdentity( identityName );
			catagoryInfo.setCreatorPerson( currentPerson.getName() );
			catagoryInfo.setCreatorDepartment( departmentName );
			catagoryInfo.setCreatorCompany( companyName );
			try {
				catagoryInfo = catagoryInfoServiceAdv.save( catagoryInfo, currentPerson );
				wrap = new WrapOutId( catagoryInfo.getId() );
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在保存栏目信息对象时发生异常。" );
				logger.error( "system save catagoryInfo got an exception." , e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "根据ID删除CatagoryInfo应用信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam("id") String id) {
		EffectivePerson currentPerson = this.effectivePerson(request);
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		CatagoryInfo catagoryInfo = null;
		Boolean check = true;
		if( id == null || id.isEmpty() ){
			check = false;
			result.error( new Exception("系统获取到需要删除的数据ID") );
			result.setUserMessage( "系统获取到需要删除的数据ID" );
		}
		if( check ){
			try {
				catagoryInfo = catagoryInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据ID获取分类信息时发生异常。" );
				logger.error( "system get catagory info with id got an exception. id:"+id, e );
			}
		}
		if( check ){
			if( catagoryInfo == null ){
				check = false;
				result.error( new Exception("需要删除的数据不存在。") );
				result.setUserMessage( "需要删除的数据不存在。" );
			}
		}
		if( check ){
			Boolean editAble = false;
			try {
				editAble = catagoryInfoServiceAdv.catagoryInfoEditAvailable( request, currentPerson, id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在检查用户是否有权限操作分类信息时发生异常。" );
				logger.error( "system check catagory info edit available got an exception. id:"+id, e );
			}
			if ( !editAble ){
				check = false;
				result.error( new Exception("用户操作权限不足[分类编辑权限]。") );
				result.setUserMessage( "用户操作权限不足[分类编辑权限]。" );
			}
		}
		if( check ){
			try {
				catagoryInfoServiceAdv.delete( id, currentPerson );
				wrap = new WrapOutId( catagoryInfo.getId() );
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在删除分类信息时发生异常。" );
				logger.error( "system delete catagory info got an exception. id:"+id, e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新AppInfo应用信息对象.", request = WrapInCatagoryInfo.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, WrapInCatagoryInfo wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson( request );
		WrapOutId wrap = null;
		CatagoryInfo catagoryInfo = null;
		String identityName = null;
		String departmentName = null;
		String companyName = null;
		Boolean check = true;
		
		if( wrapIn == null ){
			check = false;
			result.error( new Exception("系统未获取到需要保存的数据.") );
			result.setUserMessage( "系统未获取到需要保存的数据." );
		}
		if( id == null || id.isEmpty() ){
			check = false;
			result.error( new Exception("系统获取到需要更新的数据ID") );
			result.setUserMessage( "系统获取到需要更新的数据ID" );
		}
		if( check ){
			if ( !"xadmin".equalsIgnoreCase(currentPerson.getName()) ){
				identityName = wrapIn.getIdentity();
				if( identityName == null || identityName.isEmpty() ){
					try {
						identityName = userManagerService.getFistIdentityNameByPerson( currentPerson.getName() );
					} catch (Exception e) {
						check = false;
						result.error( e );
						result.setUserMessage( "系统在根据人员姓名查询第一个身份信息时发生异常，请检查是否为人员分配了部门。" );
						logger.error( "system query first identity name by person got an exception.name:" + currentPerson.getName() , e );
					}
				}
			}else{
				identityName = "xadmin";
				departmentName = "xadmin";
				companyName = "xadmin";
			}
		}
		if( check && !"xadmin".equals( identityName ) ){
			try {
				departmentName = userManagerService.getDepartmentNameByIdentity( identityName );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据人员身份查询部门名称时发生异常。" );
				logger.error( "system query department name by identity name got an exception.identityName:" + identityName , e );
			}
		}
		if( check && !"xadmin".equals( identityName ) ){
			try {
				companyName = userManagerService.getCompanyNameByIdentity( identityName );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据人员身份查询公司名称时发生异常。" );
				logger.error( "system query company name by identity name got an exception.identityName:" + identityName , e );
			}
		}
		if( check ){
			try {
				catagoryInfo = catagoryInfoServiceAdv.get( id );
				if( catagoryInfo == null ){
					check = false;
					result.error( new Exception("catagory is not exists!") );
					result.setUserMessage( "需要更新的分类数据不存在。" );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据ID获取分类信息时发生异常。" );
				logger.error( "system get catagory info with id got an exception. id:"+id, e );
			}
		}
		if( check ){
			try {
				catagoryInfo = wrapin_copier.copy( wrapIn );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统将输入的参数转换为可保存对象时发生异常。" );
				logger.error( "system copy wrapIn to catagoryInfo object got an exception." , e );
			}
		}
		if( check ){
			if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
				catagoryInfo.setId( wrapIn.getId() );
			}
			if( catagoryInfo.getCreatorDepartment() == null || catagoryInfo.getCreatorDepartment().isEmpty() ){
				catagoryInfo.setCreatorIdentity( identityName );
				catagoryInfo.setCreatorPerson( currentPerson.getName() );
				catagoryInfo.setCreatorDepartment( departmentName );
				catagoryInfo.setCreatorCompany( companyName );
			}
			try {
				catagoryInfo = catagoryInfoServiceAdv.saveBaseInfo( catagoryInfo, currentPerson );
				wrap = new WrapOutId( catagoryInfo.getId() );
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在保存栏目信息对象时发生异常。" );
				logger.error( "system save catagoryInfo got an exception." , e );
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的CatagoryInfo,下一页.", response = WrapOutCatagoryInfo.class, request = WrapInFilter.class)
	@POST
	@Path("filter/list/{id}/next/{count}/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, @PathParam("appId") String appId, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutCatagoryInfo>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();
		
		equals.put("appId", appId);
		if ((null != wrapIn.getCatagoryIdList()) && ( !wrapIn.getCatagoryIdList().isEmpty())) {
			equals.put("catagoryId", wrapIn.getCatagoryIdList().get(0).getValue());
		}
		if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
			equals.put("creatorUid", wrapIn.getCreatorList().get(0).getValue());
		}
		if ((null != wrapIn.getStatusList()) && (!wrapIn.getStatusList().isEmpty())) {
			equals.put("docStatus", wrapIn.getStatusList().get(0).getValue());
		}
		if ( StringUtils.isNotEmpty(wrapIn.getKey()) ) {
			String key = StringUtils.trim(StringUtils.replace( wrapIn.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				likes.put("title", key);
			}
		}		
		try {
			result = this.standardListNext( copier, id, count, "sequence", equals, null, likes, null, null, null, null, true, DESC);
		} catch ( Exception e ) {
			result.error( e );
			result.setUserMessage( "系统在查询数据时发生异常." );
			logger.error( "系统在查询数据时发生异常", e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的CatagoryInfo,上一页.", response = WrapOutCatagoryInfo.class, request = WrapInFilter.class)
	@POST
	@Path("filter/list/{id}/prev/{count}/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter( @Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, @PathParam("appId") Integer appId, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutCatagoryInfo>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();

		equals.put("appId", appId);
		if ((null != wrapIn.getCatagoryIdList()) && (!wrapIn.getCatagoryIdList().isEmpty())) {
			equals.put("catagoryId", wrapIn.getCatagoryIdList().get(0).getValue());
		}
		if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
			equals.put("creatorUid", wrapIn.getCreatorList().get(0).getValue());
		}
		if ((null != wrapIn.getStatusList()) && (!wrapIn.getStatusList().isEmpty())) {
			equals.put("docStatus", wrapIn.getStatusList().get(0).getValue());
		}
		if (StringUtils.isNotEmpty(wrapIn.getKey())) {
			String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				likes.put("title", key);
			}
		}		
		try{
			result = this.standardListPrev( copier, id, count, "sequence", equals, null, likes, null, null, null, null, true, DESC);
		} catch ( Exception e ) {
			result.error( e );
			result.setUserMessage( "系统在查询数据时发生异常." );
			logger.error( "系统在查询数据时发生异常", e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
}