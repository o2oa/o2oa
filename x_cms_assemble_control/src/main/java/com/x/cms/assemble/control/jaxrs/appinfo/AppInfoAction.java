package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.Collections;
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
import com.x.cms.assemble.control.jaxrs.catagoryinfo.WrapOutCatagoryInfo;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CatagoryInfoServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CatagoryInfo;

@Path("appinfo")
public class AppInfoAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( AppInfoAction.class );
	private AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	private CatagoryInfoServiceAdv catagoryInfoServiceAdv = new CatagoryInfoServiceAdv();
	private UserManagerService userManagerService = new UserManagerService();
	private BeanCopyTools<WrapInAppInfo, AppInfo> wrapin_copier = BeanCopyToolsBuilder.create(WrapInAppInfo.class, AppInfo.class, null, WrapInAppInfo.Excludes);
	private BeanCopyTools<AppInfo, WrapOutAppInfo> wrapout_copier = BeanCopyToolsBuilder.create(AppInfo.class, WrapOutAppInfo.class, null, WrapOutAppInfo.Excludes);
	private BeanCopyTools<CatagoryInfo, WrapOutCatagoryInfo> catagory_copier = BeanCopyToolsBuilder.create(CatagoryInfo.class, WrapOutCatagoryInfo.class, null, WrapOutCatagoryInfo.Excludes);
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有应用列表", response = WrapOutAppInfo.class)
	@GET
	@Path("list/user")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listMyAppInfo(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		List<WrapOutAppInfo> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<AppInfo> appInfoList = null;
		List<CatagoryInfo> catacoryList = null;
		List<WrapOutCatagoryInfo> wrapOutCatacoryList = null;
		List<String> app_ids = null;
		Boolean isXAdmin = false;
		Boolean check = true;
		
		try {
			isXAdmin = userManagerService.isXAdmin(request, currentPerson);
		} catch (Exception e) {
			check = false;
			result.error( e );
			result.setUserMessage( "系统在检查用户是否是平台管理员时发生异常。" );
			logger.error( "system check user is xadmin got an exception.", e );
		}
		if( check ){
			if ( isXAdmin ) {
				try {
					appInfoList = appInfoServiceAdv.listAll();
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在查询所有的栏目信息时发生异常。" );
					logger.error( "system query all appinfo list got an exception.", e );
				}
			}else{
				try {
					app_ids = appInfoServiceAdv.listAppInfoByUserPermission( currentPerson.getName() );
					if( app_ids != null && !app_ids.isEmpty() ){
						try {
							appInfoList = appInfoServiceAdv.list( app_ids );
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "系统在根据ID列表查询栏目信息时发生异常。" );
							logger.error( "system query appinfo list with ids got an exception.", e );
						}
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在根据用户权限查询所有可见的栏目信息时发生异常。" );
					logger.error( "system query appinfo list for user by permission got an exception.", e );
				}
			}
		}
		if( check ){
			if( appInfoList != null && !appInfoList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appInfoList );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在转换数据对象为输出格式时发生异常。" );
					logger.error( "system copy appinfo list to wrap out got an exception.", e );
				}
			}
		}
		if( check ){
			if (wraps != null && wraps.size() > 0) {
				for ( WrapOutAppInfo appInfo : wraps ) {
					try {
						catacoryList = catagoryInfoServiceAdv.list( appInfo.getCatagoryList() );
					} catch (Exception e ) {
						check = false;
						result.error( e );
						result.setUserMessage( "系统在根据ID列表查询分类信息列表时发生异常。" );
						logger.error( "system query catagory list with ids got an exception.", e );
					}
					if( catacoryList != null && !catacoryList.isEmpty() ){
						try {
							wrapOutCatacoryList = catagory_copier.copy( catacoryList );
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "系统在转换数据对象为输出格式时发生异常。" );
							logger.error( "system copy catagory list to wrap out got an exception.", e );
						}
						appInfo.setWrapOutCatagoryList( wrapOutCatacoryList );
					}
				}
				Collections.sort(wraps);
				result.setData(wraps);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取用户有权限管理的所有应用列表", response = WrapOutAppInfo.class)
	@GET
	@Path("list/admin")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAdminPermissionAppInfoByUser(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		List<WrapOutAppInfo> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<AppInfo> appInfoList = null;
		List<CatagoryInfo> catacoryList = null;
		List<WrapOutCatagoryInfo> wrapOutCatacoryList = null;
		List<String> app_ids = null;
		
		Boolean isXAdmin = false;
		Boolean check = true;
		
		try {
			isXAdmin = userManagerService.isXAdmin(request, currentPerson);
		} catch (Exception e) {
			check = false;
			result.error( e );
			result.setUserMessage( "系统在检查用户是否是平台管理员时发生异常。" );
			logger.error( "system check user is xadmin got an exception.", e );
		}
		if( check ){
			if ( isXAdmin ) {
				try {
					appInfoList = appInfoServiceAdv.listAll();
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在查询所有的栏目信息时发生异常。" );
					logger.error( "system query all appinfo list got an exception.", e );
				}
			}else{
				try {
					app_ids = appInfoServiceAdv.listAdminPermissionAppInfoByUser( currentPerson.getName() );
					if( app_ids != null && !app_ids.isEmpty() ){
						try {
							appInfoList = appInfoServiceAdv.list( app_ids );
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "系统在根据ID列表查询栏目信息时发生异常。" );
							logger.error( "system query appinfo list with ids got an exception.", e );
						}
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在根据用户权限查询所有可见的栏目信息时发生异常。" );
					logger.error( "system query appinfo list for user by permission got an exception.", e );
				}
			}
		}
		if( check ){
			if( appInfoList != null && !appInfoList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appInfoList );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在转换数据对象为输出格式时发生异常。" );
					logger.error( "system copy appinfo list to wrap out got an exception.", e );
				}
			}
		}
		if( check ){
			if (wraps != null && wraps.size() > 0) {
				for ( WrapOutAppInfo appInfo : wraps ) {
					try {
						catacoryList = catagoryInfoServiceAdv.list( appInfo.getCatagoryList() );
					} catch (Exception e ) {
						check = false;
						result.error( e );
						result.setUserMessage( "系统在根据ID列表查询分类信息列表时发生异常。" );
						logger.error( "system query catagory list with ids got an exception.", e );
					}
					if( catacoryList != null && !catacoryList.isEmpty() ){
						try {
							wrapOutCatacoryList = catagory_copier.copy( catacoryList );
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "系统在转换数据对象为输出格式时发生异常。" );
							logger.error( "system copy catagory list to wrap out got an exception.", e );
						}
						appInfo.setWrapOutCatagoryList( wrapOutCatacoryList );
					}
				}
				Collections.sort(wraps);
				result.setData(wraps);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取用户有权限访问的所有应用列表", response = WrapOutAppInfo.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAppInfo(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		List<WrapOutAppInfo> wraps = null;
		List<AppInfo> appInfoList = null;
		Boolean check = true;
		try {
			appInfoList = appInfoServiceAdv.listAll();
		} catch (Exception e) {
			check = false;
			result.error( e );
			result.setUserMessage( "系统在查询所有的栏目信息时发生异常。" );
			logger.error( "system query all appinfo list got an exception.", e );
		}
		if( check ){
			if( appInfoList != null && !appInfoList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appInfoList );
					Collections.sort(wraps);
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在转换数据对象为输出格式时发生异常。" );
					logger.error( "system copy appinfo list to wrap out got an exception.", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取appInfo对象.", response = WrapOutAppInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutAppInfo> result = new ActionResult<>();
		WrapOutAppInfo wrap = null;
		AppInfo appInfo = null;
		Boolean check = true;		
		if( id == null || id.isEmpty() ){
			check = false;
			result.error( new Exception("系统未获取到需要查询的数据ID.") );
			result.setUserMessage( "系统未获取到需要查询的数据ID." );
		}
		if( check ){
			try {
				appInfo = appInfoServiceAdv.get(id);
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID列表查询栏目信息时发生异常。" );
				logger.error( "system query appinfo with id got an exception.", e );
			}
		}
		if( check ){
			if( appInfo == null ){
				check = false;
				result.error( new Exception("需要查询的数据信息不存在.ID:" + id ) );
				result.setUserMessage( "需要查询的数据信息不存在." );
			}else{
				try {
					wrap = wrapout_copier.copy( appInfo );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在转换数据对象为输出格式时发生异常。" );
					logger.error( "system copy appinfo to wrap out got an exception.", e );
				}
				result.setData( wrap );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "创建AppInfo应用信息对象.", request = WrapInAppInfo.class, response = WrapOutId.class)
	@POST
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response post( @Context HttpServletRequest request, WrapInAppInfo wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		AppInfo appInfo = null;
		String identityName = null;
		String departmentName = null;
		String companyName = null;
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
				appInfo = wrapin_copier.copy( wrapIn );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统将输入的参数转换为可保存对象时发生异常。" );
				logger.error( "system copy wrapIn to appinfo object got an exception." , e );
			}
		}
		if( check ){
			if( wrapIn.getId() != null && !wrapIn.getId().isEmpty() ){
				appInfo.setId( wrapIn.getId() );
			}
			appInfo.setCreatorIdentity( identityName );
			appInfo.setCreatorPerson( currentPerson.getName() );
			appInfo.setCreatorDepartment( departmentName );
			appInfo.setCreatorCompany( companyName );
			try {
				appInfo = appInfoServiceAdv.save( appInfo, currentPerson );
				wrap = new WrapOutId(appInfo.getId());
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在保存栏目信息对象时发生异常。" );
				logger.error( "system save appinfo got an exception." , e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除AppInfo应用信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		EffectivePerson currentPerson = this.effectivePerson(request);
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		AppInfo appInfo = null;
		Boolean check = true;
		if( id == null || id.isEmpty() ){
			check = false;
			result.error( new Exception("系统获取到需要删除的数据ID") );
			result.setUserMessage( "系统获取到需要删除的数据ID" );
		}
		if( check ){
			try {
				appInfo = appInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据ID获取栏目信息时发生异常。" );
				logger.error( "system get app info with id got an exception. id:"+id, e );
			}
		}
		if( check ){
			if( appInfo == null ){
				check = false;
				result.error( new Exception("需要删除的数据不存在。") );
				result.setUserMessage( "需要删除的数据不存在。" );
			}
		}
		if( check ){
			Boolean editAble = false;
			try {
				editAble = appInfoServiceAdv.appInfoEditAvailable( request, currentPerson, id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在检查用户是否有权限操作栏目信息时发生异常。" );
				logger.error( "system check app info edit available got an exception. id:"+id, e );
			}
			if ( !editAble ){
				check = false;
				result.error( new Exception("用户操作权限不足[栏目编辑权限]。") );
				result.setUserMessage( "用户操作权限不足[栏目编辑权限]。" );
			}
		}
		if( check ){
			Boolean deleteAble = false;
			try {
				deleteAble = appInfoServiceAdv.appInfoDeleteAvailable( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在检查栏目信息是否允许被删除时发生异常。" );
				logger.error( "system check app info delete available got an exception. id:"+id, e );
			}
			if ( !deleteAble ){
				check = false;
				result.error( new Exception("栏目内仍有分类信息，无法被删除，请先删除分类信息后再删除栏目信息。") );
				result.setUserMessage( "栏目内仍有分类信息，无法被删除，请先删除分类信息后再删除栏目信息。" );
			}
		}
		if( check ){
			try {
				appInfoServiceAdv.delete( id, currentPerson );
				wrap = new WrapOutId(appInfo.getId());
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在删除栏目信息时发生异常。" );
				logger.error( "system delete app info got an exception. id:"+id, e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的AppInfo,下一页.", response = WrapOutAppInfo.class, request = WrapInFilter.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();
		if( wrapIn == null ){
			wrapIn = new WrapInFilter();
		}
		if( id == null ){
			id = "(0)";
		}
		if( count == null ){
			count = 20;
		}
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
		try {
			result = this.standardListNext( wrapout_copier, id, count, "sequence", equals, null, likes, null, null, null, null, true, DESC);
		} catch ( Exception e ) {
			result.error( e );
			result.setUserMessage( "系统在分页查询栏目信息列表时发生异常。" );
			logger.error( "system delete app info got an exception. id:", e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的AppInfo,下一页.", response = WrapOutAppInfo.class, request = WrapInFilter.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();
		if( wrapIn == null ){
			wrapIn = new WrapInFilter();
		}
		if( id == null ){
			id = "(0)";
		}
		if( count == null ){
			count = 20;
		}
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
		try {
			result = this.standardListPrev( wrapout_copier, id, count, "sequence", equals, null, likes, null, null, null, null, true, DESC );
		} catch ( Exception e ) {
			result.error( e );
			result.setUserMessage( "系统在分页查询栏目信息列表时发生异常。" );
			logger.error( "system delete app info got an exception. id:", e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}