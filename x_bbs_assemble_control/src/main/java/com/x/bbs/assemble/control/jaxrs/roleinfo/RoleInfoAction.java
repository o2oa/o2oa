package com.x.bbs.assemble.control.jaxrs.roleinfo;

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

import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.gson.Gson;
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.WrapOutPermissionInfo;
import com.x.bbs.assemble.control.jaxrs.roleinfo.bean.BindObject;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSUserInfoService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSUserRole;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapPerson;

@Path("user/role")
public class RoleInfoAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( RoleInfoAction.class );
	private BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
	private BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	private BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	private BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	private UserManagerService userManagerService = new UserManagerService();
	private BBSUserInfoService userInfoService = new BBSUserInfoService();	
	private BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
	private BeanCopyTools< BBSPermissionInfo, WrapOutPermissionInfo > permission_wrapout_copier = BeanCopyToolsBuilder.create( BBSPermissionInfo.class, WrapOutPermissionInfo.class, null, WrapOutPermissionInfo.Excludes);
	private BeanCopyTools< BBSRoleInfo, WrapOutRoleInfo > wrapout_copier = BeanCopyToolsBuilder.create( BBSRoleInfo.class, WrapOutRoleInfo.class, null, WrapOutRoleInfo.Excludes);
	private BeanCopyTools<WrapInRoleInfo, BBSRoleInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInRoleInfo.class, BBSRoleInfo.class, null, WrapInRoleInfo.Excludes );
	private BeanCopyTools< BBSUserRole, WrapOutUserRole > userRole_wrapout_copier = BeanCopyToolsBuilder.create( BBSUserRole.class, WrapOutUserRole.class, null, WrapOutUserRole.Excludes);
	
	@HttpMethodDescribe( value = "获取所有RoleInfo的信息列表.", response = WrapOutRoleInfo.class )
	@GET
	@Path("all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAll( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		List<WrapOutRoleInfo> wraps = new ArrayList<>();
		List<BBSRoleInfo> roleInfoList = null;
		Boolean check = true;
		
		if( check ){
			try {
				roleInfoList = roleInfoService.listAllRoleInfo();
				if( roleInfoList == null ){
					roleInfoList = new ArrayList<BBSRoleInfo>();
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在查询所有版块信息时发生异常" );
				logger.error( "system query all section info got an exception!", e );
			}		
		}
		
		if( check ){
			if( roleInfoList != null && roleInfoList.size() > 0 ){
				try {
					wraps = wrapout_copier.copy( roleInfoList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在将版块信息列表转换为输出格式时发生异常" );
					logger.error( "system copy forum list to wraps got an exception!", e );
				}
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe( value = "根据论坛ID查询论坛的角色列表.", response = WrapOutRoleInfo.class )
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutRoleInfo> result = new ActionResult<>();
		WrapOutRoleInfo wrap = null;
		List<WrapOutPermissionInfo> permissionWraps = null;
		BBSRoleInfo roleInfo = null;
		List<BBSPermissionInfo> permissionList = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数id为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数id为空，无法继续进行查询" );
			}
		}
		if( check ){
			try {
				roleInfo = roleInfoService.get( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据对象ID，对象类别查询角色信息列表时发生异常" );
				logger.error( "system list role info with objectId and objectType got an exception!", e );
			}
		}
		
		if( check ){
			//如果角色信息存在，然后再查询该角色所拥有的所有权限信息
			try {
				permissionList = permissionInfoService.listPermissionByRoleCode( roleInfo.getRoleCode() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据角色编码查询权限信息列表时发生异常" );
				logger.error( "system list permission info with role code got an exception!", e );
			}
		}
		
		if( check ){
			if( permissionList != null && permissionList.size() > 0 ){
				try {
					permissionWraps = permission_wrapout_copier.copy( permissionList );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在将权限信息列表转换为输出格式时发生异常" );
					logger.error( "system copy permissoin to wrap got an exception!", e );
				}
			}
		}
		
		if( check ){
			if( roleInfo != null ){
				try {
					wrap = wrapout_copier.copy( roleInfo );
					wrap.setPermissions( permissionWraps );
					result.setData( wrap );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在将角色信息列表转换为输出格式时发生异常" );
					logger.error( "system copy role info to wrap got an exception!", e );
				}
			}else{
				result.error( new Exception("角色信息不存在！") );
				result.setUserMessage( "角色信息不存在！" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据论坛ID查询论坛的角色列表.", request = WrapInFilter.class, response = WrapOutId.class)
	@PUT
	@Path("forum/{forumId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByForum( @Context HttpServletRequest request, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		List<WrapOutRoleInfo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		BBSForumInfo forumInfo = null;
		Boolean check = true;
		
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("传入的参数为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数为空，无法继续进行查询" );
			}
		}
		if( check ){
			if( wrapIn.getForumId() == null || wrapIn.getForumId().isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数forumId为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数forumId为空，无法继续进行查询" );
			}
		}
		if( check ){
			try {
				forumInfo = forumInfoServiceAdv.get( wrapIn.getForumId() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据Id查询论坛信息时发生异常" );
				logger.error( "system query forum with id got an exception!", e );
			}
		}
		if( check ){
			if( forumInfo == null ){
				check = false;
				result.error( new Exception( "系统未能根据传入的ID查询到任何论坛信息！" ) );
				result.setUserMessage( "系统未能根据传入的ID查询到任何论坛信息！" );
			}
		}
		if( check ){
			try {
				roleInfoList = roleInfoService.listRoleByForumId( wrapIn.getForumId() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据对象ID，对象类别查询角色信息列表时发生异常" );
				logger.error( "system list role info with objectId and objectType got an exception!", e );
			}
		}
		if( check ){
			if( roleInfoList != null ){
				try {
					wraps = wrapout_copier.copy( roleInfoList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在将角色信息列表转换为输出格式时发生异常" );
					logger.error( "system copy role to wrap got an exception!", e );
				}
			}else{
				result.error( new Exception("角色信息不存在！") );
				result.setUserMessage( "角色信息不存在！" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "把一个或者多个角色CODE绑定到一个对象（对象名称#对象类型）上.", request = WrapInFilter.class, response = WrapOutId.class)
	@PUT
	@Path("bind/object")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response bindRoleToUser( @Context HttpServletRequest request, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		BindObject bindObject = null;
		Object object = null;
		Boolean check = true;
		
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("传入的参数为空，无法继续进行权限绑定!") );
				result.setUserMessage( "传入的参数为空，无法继续进行权限绑定!" );
			}
		}		
		if( check ){
			if( wrapIn.getBindObject() == null ){
				check = false;
				result.error( new Exception("传入的参数bindObject为空，无法继续进行权限绑定!") );
				result.setUserMessage( "传入的参数bindObject为空，无法继续进行权限绑定!" );
			}
		}
		if( check ){
			if( wrapIn.getBindObject() == null || wrapIn.getBindObject().getObjectName() == null || wrapIn.getBindObject().getObjectName().isEmpty() ){
				check = false;
				result.error( new Exception("传入的需要绑定在角色信息上的对象为空，无法继续进行权限绑定!") );
				result.setUserMessage( "传入的需要绑定在角色信息上的对象为空，无法继续进行权限绑定!" );
			}else{
				bindObject = wrapIn.getBindObject();
			}
		}
		if( check ){
			//遍历所有的对象，检查对象是否真实存在
			if( "人员".equals( wrapIn.getBindObject().getObjectType() )){
				try {
					object = userManagerService.getPersonByName( bindObject.getObjectName() );
					if( object == null ){
						check = false;
						result.error( new Exception("需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() ) );
						result.setUserMessage( "需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "根据传入的绑定对象查询信息时发生异常，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
				}
			}else if( "部门".equals( bindObject.getObjectType() )){
				try {
					object = userManagerService.getDepartmentByName( bindObject.getObjectName() );
					if( object == null ){
						check = false;
						result.error( new Exception("需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() ) );
						result.setUserMessage( "需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "根据传入的绑定对象查询信息时发生异常，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
				}
			}else if( "公司".equals( bindObject.getObjectType() )){
				try {
					object = userManagerService.getCompanyByName( bindObject.getObjectName() );
					if( object == null ){
						check = false;
						result.error( new Exception("需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() ) );
						result.setUserMessage( "需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "根据传入的绑定对象查询信息时发生异常，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
				}
			}else if( "群组".equals( bindObject.getObjectType() )){
				try {
					object = userManagerService.getGroupByName( bindObject.getObjectName() );
					if( object == null ){
						check = false;
						result.error( new Exception("需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() ) );
						result.setUserMessage( "需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "根据传入的绑定对象查询信息时发生异常，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
				}
			}else{
				check = false;
				result.error( new Exception("传入的对象类别不正确[" + bindObject.getObjectType() + "]，必须是人员、部门、公司或者群组中的一种。") );
				result.setUserMessage( "传入的对象类别不正确，必须是人员、部门、公司或者群组中的一种。type:" + bindObject.getObjectType() );
			}
		}
		if( check ){
			try {
				roleInfoService.bindRoleToUser( wrapIn.getBindObject(), wrapIn.getBindRoleCodes() );
				result.setUserMessage( "用户角色绑定成功！" );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据人员姓名以及角色编码列表进行角色绑定时发生异常！" );
				logger.error( "system band roles to user got an exception!", e );
			}
		}
		if( check ){
			try {
				checkUserPermission( wrapIn.getBindObject() );
			} catch (Exception e) {
				logger.error( "system check user permission got an exception!", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	public void checkUserPermission( BindObject  bindObject ) throws Exception{
		//把wrapIn.getBindObject()解析成人员列表，对每一个人员进行权限分析
		List<String> userNames = new ArrayList<>();
		if( bindObject != null ){
			if( "组织".equals( bindObject.getObjectType() )){
				try {
					userNames = userManagerService.listUserNamesByOrganization( bindObject.getObjectName() );
				} catch (Exception e) {
					throw e;
				}
			}else if( "群组".equals( bindObject.getObjectType() )){
				try {
					userNames = userManagerService.listUserNamesByGroupName( bindObject.getObjectName() );
				} catch (Exception e) {
					throw e;
				}
			}else{
				//当它是人员
				userNames.add( bindObject.getObjectName() );
			}
		}
		if( userNames != null && !userNames.isEmpty() ){
			for( String name : userNames ){
				checkUserPermission( name );
			}
		}
	}
	
	public void checkUserPermission( String userName ){
		Gson gson = null;
		List<String> roleCodes = null;
		List<String> permissionCodes = null;
		RoleAndPermission roleAndPermission = null;
		String permissionContent = null;
		
		roleAndPermission = new RoleAndPermission();
		roleAndPermission.setPerson( userName );
		//检查该员的角色和权限信息
		try{
			roleCodes = roleInfoService.listAllRoleCodesForUser( userName );
			roleAndPermission.setRoleInfoList(roleCodes);
		}catch( Exception e ){
			logger.error( "system list all role for user got an exception.", e );
		}
		if( roleCodes != null && !roleCodes.isEmpty() ){
			try{
				permissionCodes = permissionInfoService.listPermissionCodesByRoleCodes( roleCodes );
				roleAndPermission.setPermissionInfoList(permissionCodes);
			}catch( Exception e ){
				logger.error( "system list all permission for user got an exception.", e );
			}
		}
		try{
			gson = XGsonBuilder.pureGsonDateFormated();
			permissionContent = gson.toJson( roleAndPermission );
		}catch( Exception e ){
			logger.error( "system translate object to json got an exception.", e );
		}
		try {//从数据库中查询出人员信息，进行信息更新
			userInfoService.updatePermission( userName, permissionContent );
		} catch (Exception e) {
			logger.error( "system save user info got an exception. username:" + userName , e );
		}
	}
	
	@HttpMethodDescribe(value = "把一个或者多个对象（对象名称#对象类型）绑定到一个角色CODE上.", request = WrapInFilter.class, response = WrapOutId.class)
	@PUT
	@Path("bind/role")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response bindUserToRole( @Context HttpServletRequest request, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		Object object = null;
		Boolean check = true;
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("传入的参数为空，无法继续进行权限绑定!") );
				result.setUserMessage( "传入的参数为空，无法继续进行权限绑定!" );
			}
		}
		if( check ){
			if( wrapIn.getBindRoleCode() == null || wrapIn.getBindRoleCode().isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数bindRoleCode为空，无法继续进行权限绑定!") );
				result.setUserMessage( "传入的参数bindRoleCode为空，无法继续进行权限绑定!" );
			}
		}
		if( check ){
			if( wrapIn.getBindObjectArray() != null && !wrapIn.getBindObjectArray().isEmpty() ){
				//遍历所有的对象，检查对象是否真实存在
				for( BindObject bindObject : wrapIn.getBindObjectArray() ){
					if( "人员".equals( bindObject.getObjectType() )){
						try {
							object = userManagerService.getPersonByName( bindObject.getObjectName() );
							if( object == null ){
								check = false;
								result.error( new Exception("需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() ) );
								result.setUserMessage( "需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
							}
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "根据传入的绑定对象查询信息时发生异常，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
						}
					}else if( "部门".equals( bindObject.getObjectType() )){
						try {
							object = userManagerService.getDepartmentByName( bindObject.getObjectName() );
							if( object == null ){
								check = false;
								result.error( new Exception("需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() ) );
								result.setUserMessage( "需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
							}
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "根据传入的绑定对象查询信息时发生异常，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
						}
					}else if( "公司".equals( bindObject.getObjectType() )){
						try {
							object = userManagerService.getCompanyByName( bindObject.getObjectName() );
							if( object == null ){
								check = false;
								result.error( new Exception("需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() ) );
								result.setUserMessage( "需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
							}
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "根据传入的绑定对象查询信息时发生异常，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
						}
					}else if( "群组".equals( bindObject.getObjectType() )){
						try {
							object = userManagerService.getGroupByName( bindObject.getObjectName() );
							if( object == null ){
								check = false;
								result.error( new Exception("需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() ) );
								result.setUserMessage( "需要绑定到角色上的对象信息不存在，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
							}
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "根据传入的绑定对象查询信息时发生异常，name：" + bindObject.getObjectName() + ", type:" + bindObject.getObjectType() );
						}
					}else{
						check = false;
						result.error( new Exception("传入的对象类别不正确[" + bindObject.getObjectType() + "]，必须是人员、部门、公司或者群组中的一种。") );
						result.setUserMessage( "传入的对象类别不正确，必须是人员、部门、公司或者群组中的一种。type:" + bindObject.getObjectType() );
					}
				}
			}
		}
		
		//校验人员和角色编码的合法性
		if( check ){
			try {
				roleInfoService.bindUserToRole( wrapIn.getBindRoleCode(), wrapIn.getBindObjectArray() );
				result.setUserMessage( "用户角色绑定成功！" );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据人员姓名列表以及角色编码进行角色绑定时发生异常！" );
				logger.error( "system band users to role got an exception!", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据版块ID查询版块的角色列表.", request = WrapInFilter.class, response = WrapOutId.class)
	@PUT
	@Path("section/{sectionId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listBySection( @Context HttpServletRequest request, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		List<WrapOutRoleInfo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		BBSSectionInfo sectionInfo = null;
		Boolean check = true;
		
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("传入的参数为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数为空，无法继续进行查询" );
			}
		}
		if( check ){
			if( wrapIn.getSectionId() == null || wrapIn.getSectionId().isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数sectionId为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数sectionId为空，无法继续进行查询" );
			}
		}
		if( check ){
			try {
				sectionInfo = sectionInfoServiceAdv.get( wrapIn.getSectionId() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据Id查询论坛信息时发生异常" );
				logger.error( "system query section with section id got an exception!", e );
			}
		}
		if( check ){
			if( sectionInfo == null ){
				check = false;
				result.error( new Exception( "系统未能根据传入的ID查询到任何版块信息！" ) );
				result.setUserMessage( "系统未能根据传入的ID查询到任何版块信息！" );
			}
		}
		if( check ){
			try {
				roleInfoList = roleInfoService.listRoleBySectionId( wrapIn.getSectionId() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据对象ID，对象类别查询角色信息列表时发生异常" );
				logger.error( "system list role info with objectId and objectType got an exception!", e );
			}
		}
		if( check ){
			if( roleInfoList != null ){
				try {
					wraps = wrapout_copier.copy( roleInfoList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在将角色信息列表转换为输出格式时发生异常" );
					logger.error( "system copy role to wrap got an exception!", e );
				}
			}else{
				result.error( new Exception("角色信息不存在！") );
				result.setUserMessage( "角色信息不存在！" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据组织名称查询角色列表.", request = WrapInFilter.class, response = WrapOutId.class)
	@PUT
	@Path("organization/selected")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSelectedRoleByOrganization( @Context HttpServletRequest request, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		List<WrapOutRoleInfo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		BBSSectionInfo sectionInfo = null;
		WrapCompany wrapCompany  = null;
		WrapDepartment wrapDepartment  = null;
		Boolean check = true;
		
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("传入的参数为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数为空，无法继续进行查询" );
			}
		}
		if( check ){
			if( wrapIn.getOrganizationName() == null || wrapIn.getOrganizationName().isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数OrganizationName为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数OrganizationName为空，无法继续进行查询" );
			}
		}
		if( check ){
			try {
				wrapCompany = userManagerService.getCompanyByName( wrapIn.getOrganizationName() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据公司名称查询公司信息时发生异常" );
				logger.error( "system query company info with company name got an exception!", e );
			}
		}
		
		if( check ){
			if( wrapCompany == null ){
				try {
					wrapDepartment = userManagerService.getDepartmentByName( wrapIn.getOrganizationName() );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在根据部门名称查询部门信息时发生异常" );
					logger.error( "system query department info with department name got an exception!", e );
				}
			}
		}
		
		if( check ){
			if( wrapCompany == null || wrapDepartment == null ){
				check = false;
				result.error( new Exception("根据传入的组织名称未能查询到任何公司或者部门信息！") );
				result.setUserMessage( "根据传入的组织名称未能查询到任何公司或者部门信息！" );
			}
		}
		
		if( check ){
			if( sectionInfo == null ){
				check = false;
				result.error( new Exception( "系统未能根据传入的ID查询到任何版块信息！" ) );
				result.setUserMessage( "系统未能根据传入的ID查询到任何版块信息！" );
			}
		}
		if( check ){
			try {
				roleInfoList = roleInfoService.listRoleByObjectUniqueId( wrapIn.getOrganizationName(), "组织" );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据对象ID，对象类别查询角色信息列表时发生异常" );
				logger.error( "system list role info with objectId and objectType got an exception!", e );
			}
		}
		if( check ){
			if( roleInfoList != null ){
				try {
					wraps = wrapout_copier.copy( roleInfoList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在将角色信息列表转换为输出格式时发生异常" );
					logger.error( "system copy role to wrap got an exception!", e );
				}
			}else{
				result.error( new Exception("角色信息不存在！") );
				result.setUserMessage( "角色信息不存在！" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据用户姓名查询角色列表.", request = WrapInFilter.class, response = WrapOutId.class)
	@PUT
	@Path("user/selected")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listSelectedRoleByUser( @Context HttpServletRequest request, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		List<WrapOutRoleInfo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		WrapPerson wrapPerson  = null;
		Boolean check = true;
		
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("传入的参数为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数为空，无法继续进行查询" );
			}
		}
		if( check ){
			if( wrapIn.getUserName() == null || wrapIn.getUserName().isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数userName为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数userName为空，无法继续进行查询" );
			}
		}
		if( check ){
			try {
				wrapPerson = userManagerService.getPersonByName( wrapIn.getUserName() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据人员姓名查询人员信息时发生异常" );
				logger.error( "system query person info with person name got an exception!", e );
			}
		}		
		if( check ){
			if( wrapPerson == null ){
				check = false;
				result.error( new Exception("根据传入的人员名称未能查询到任何人员信息！") );
				result.setUserMessage( "根据传入的人员名称未能查询到任何人员信息！" );
			}
		}		
		if( check ){
			try {
				roleInfoList = roleInfoService.listRoleByObjectUniqueId( wrapIn.getUserName(), "人员" );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据对象ID，对象类别查询角色信息列表时发生异常" );
				logger.error( "system list role info with objectId and objectType got an exception!", e );
			}
		}
		if( check ){
			if( roleInfoList != null ){
				try {
					wraps = wrapout_copier.copy( roleInfoList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在将角色信息列表转换为输出格式时发生异常" );
					logger.error( "system copy role to wrap got an exception!", e );
				}
			}else{
				result.error( new Exception("角色信息不存在！") );
				result.setUserMessage( "角色信息不存在！" );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据角色编码查询绑定的对象列表.", request = WrapInFilter.class, response = WrapOutId.class)
	@PUT
	@Path("rolecode/selected")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listBindObjectByRoleCode( @Context HttpServletRequest request, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutUserRole>> result = new ActionResult<>();
		List<WrapOutUserRole> wraps = null;
		List<BBSUserRole> userRoleList = null;
		BBSRoleInfo roleInfo = null;
		Boolean check = true;
		
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception("传入的参数为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数为空，无法继续进行查询" );
			}
		}
		if( check ){
			if( wrapIn.getBindRoleCode() == null || wrapIn.getBindRoleCode().isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数bindRoleCode为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数bindRoleCode为空，无法继续进行查询" );
			}
		}
		if( check ){
			try {
				roleInfo = roleInfoService.getByRoleCode( wrapIn.getBindRoleCode() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据角色编码查询角色信息时发生异常" );
				logger.error( "system query role info with role code got an exception!", e );
			}
		}		
		if( check ){
			if( roleInfo == null ){
				check = false;
				result.error( new Exception("根据传入的角色编码未能查询到任何角色信息！") );
				result.setUserMessage( "根据传入的角色编码未能查询到任何角色信息！" );
			}
		}		
		if( check ){
			try {
				userRoleList = roleInfoService.listUserRoleByRoleCode( roleInfo.getRoleCode() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据对象ID，对象类别查询角色信息列表时发生异常" );
				logger.error( "system list userRole info with roleCode got an exception!roleCode:" + roleInfo.getRoleCode() , e );
			}
		}
		if( check ){
			if( userRoleList != null ){
				try {
					wraps = userRole_wrapout_copier.copy( userRoleList );
					result.setData( wraps );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在转换对象为输出对象格式时发生异常" );
					logger.error( "system copy user role list to wraps got an exception!", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建新的角色信息或者更新角色信息.", request = WrapInRoleInfo.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post( @Context HttpServletRequest request, WrapInRoleInfo wrapIn ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSRoleInfo roleInfo_old = null;
		BBSRoleInfo roleInfo = new BBSRoleInfo();
		List<String> permissionCodes = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		String uniCode = null;
		Boolean check = true;
		if( wrapIn == null ){
			check = false;
			result.error( new Exception("系统传入的JSON对象为空，无法进行数据保存！") );
			result.setUserMessage( "系统传入的JSON对象为空，无法进行数据保存！" );
		}				
		if( check ){
			if( wrapIn.getRoleName() == null || wrapIn.getRoleName().isEmpty() ){
				check = false;
				result.error( new Exception("系统传入的[角色名称]为空，无法进行数据保存！") );
				result.setUserMessage( "系统传入的[角色名称]为空，无法进行数据保存！" );
			}
		}		
		//校验角色类别
		if( check ){
			if( wrapIn.getRoleType() == null || wrapIn.getRoleType().isEmpty() ){
				check = false;
				result.error( new Exception("系统传入的[角色类别]为空，无法进行数据保存！") );
				result.setUserMessage( "系统传入的[角色类别]为空，无法进行数据保存！" );
			}
		}
		//检验信息，如果是论坛角色，那么论坛ID为必须填写，如果是版块角色那么版块ID必须填写
		if( check ){
			if( "论坛角色".equals( wrapIn.getRoleType() ) ){
				if( wrapIn.getForumId() == null || wrapIn.getForumId().isEmpty() ){
					check = false;
					result.error( new Exception("系统传入的[论坛ID]为空，无法进行数据保存！") );
					result.setUserMessage( "系统传入的[论坛ID]为空，无法进行数据保存！" );
				}
			}else if( "版块角色".equals( wrapIn.getRoleType() ) ){
				if( wrapIn.getSectionId() == null || wrapIn.getSectionId().isEmpty() ){
					check = false;
					result.error( new Exception("系统传入的[版块ID]为空，无法进行数据保存！") );
					result.setUserMessage( "系统传入的[版块ID]为空，无法进行数据保存！" );
				}
			}else{
				check = false;
				result.error( new Exception("角色类别不可以为空，无法进行数据保存！") );
				result.setUserMessage( "角色类别不可以为空，无法进行数据保存！" );
			}
		}
		//检验传入的版块ID和论坛ID是否合法
		if( check ){
			if( "论坛角色".equals( wrapIn.getRoleType() ) ){
				try {
					forumInfo = forumInfoServiceAdv.get( wrapIn.getForumId() );
					if( forumInfo == null ){
						check = false;
						result.error( new Exception("系统根据论坛ID未能查询任何论坛信息，无法继续保存角色信息！") );
						result.setUserMessage( "系统根据论坛ID未能查询任何论坛信息，无法继续保存角色信息！" );
					}else{
						uniCode = forumInfo.getId();
						wrapIn.setForumId( forumInfo.getId() );
						wrapIn.setForumName( forumInfo.getForumName() );
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统根据论坛ID查询论坛信息时发生异常" );
					logger.error( "system query forum info by forum id got an exception!", e );
				}
			}else if( "版块角色".equals( wrapIn.getRoleType() ) ){
				try {
					sectionInfo = sectionInfoServiceAdv.get( wrapIn.getSectionId() );
					if( sectionInfo == null ){
						check = false;
						result.error( new Exception("系统根据版块ID未能查询任何版块信息，无法继续保存角色信息！") );
						result.setUserMessage( "系统根据版块ID未能查询任何版块信息，无法继续保存角色信息！" );
					}else{
						uniCode = sectionInfo.getId();
						wrapIn.setSectionId( sectionInfo.getId() );
						wrapIn.setSectionName( sectionInfo.getSectionName() );
						wrapIn.setForumId( sectionInfo.getForumId() );
						wrapIn.setForumName( sectionInfo.getForumName() );
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统根据版块ID查询版块信息时发生异常" );
					logger.error( "system query section info by section id got an exception!", e );
				}
			}
		}		
		if( check ){
			permissionCodes = wrapIn.getPermissionCodes();
			wrapIn.setCreatorName( currentPerson.getName() );
		}
		if( check ){
			if( wrapIn.getRoleCode() == null || wrapIn.getRoleCode().isEmpty() ){
				try{
					//自动组织一个角色编码：BBS + 角色名称的拼音 + ID
					wrapIn.setRoleCode( "BBS_" + new PinyinHelper().getShortPinyin( wrapIn.getRoleName() ).toUpperCase() + "_" + uniCode );
				}catch( Exception e){
					check = false;
					result.error( e );
					result.setUserMessage( "系统根据角色名称组织角色编码时发生异常！" );
					logger.error( "system get role code with role name got an exception!", e );
				}
			}
		}
		if( check ){
			try {
				wrapin_copier.copy( wrapIn, roleInfo );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在COPY传入的对象时发生异常！" );
				logger.error( "system copy wrapIn to roleInfo got an exception!", e );
			}
		}		
		if( check ){
			if( roleInfo.getRoleCode() != null && !roleInfo.getRoleCode().isEmpty() ){
				try {
					roleInfo_old = roleInfoService.getByRoleCode( roleInfo.getRoleCode() );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在根据ID查询角色信息时发生异常！" );
					logger.error( "system query role info with id got an exception!id:" + roleInfo.getId(), e );
				}
			}
		}		
		if( check ){
			try {
				logger.info( ">>>>>>>>系统开始尝试保存角色信息......" );
				roleInfo = roleInfoService.save( roleInfo, permissionCodes );
				wrap = new WrapOutId( roleInfo.getId() );
				result.setData( wrap );
				result.setUserMessage( "角色信息保存成功！" );
				if( roleInfo_old != null ){
					operationRecordService.roleOperation( currentPerson.getName(), roleInfo, "MODIFY", hostIp, hostName );
				}else{
					operationRecordService.roleOperation( currentPerson.getName(), roleInfo, "CREATE", hostIp, hostName );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在保存角色信息时发生异常！" );
				logger.error( "system save role info got an exception!", e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除指定的角色信息.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		BBSRoleInfo roleInfo = null;
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson( request );
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception( "传入的参数ID为空，无法继续进行查询！" ) );
				result.setUserMessage( "传入的参数ID为空，无法继续进行查询" );
			}
		}
		if( check ){
			//查询角色信息是否存在
			try{
				roleInfo = roleInfoService.get( id );
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询角色信息时发生异常！" );
				logger.error( "system query role info with id got an exception!id:" + id, e );
			}
		}
		
		if( check ){
			if( roleInfo == null ){
				check = false;
				result.error( new Exception("角色信息不存在，无法继续进行删除操作！ID=" + id ) );
				result.setUserMessage( "角色信息不存在，无法继续进行删除操作！" );
			}
		}
		
		if( check ){
			try {
				roleInfoService.delete( id );				
				wrap = new WrapOutId( id );
				result.setData( wrap );
				result.setUserMessage( "成功删除角色信息！" );
				operationRecordService.roleOperation( currentPerson.getName(), roleInfo, "DELETE", hostIp, hostName );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在删除角色信息时发生异常" );
				logger.error( "system delete role info got an exception!", e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
}