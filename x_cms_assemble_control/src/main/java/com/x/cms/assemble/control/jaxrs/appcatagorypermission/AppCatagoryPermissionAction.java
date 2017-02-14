package com.x.cms.assemble.control.jaxrs.appcatagorypermission;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.service.AppCatagoryPermissionServiceAdv;
import com.x.cms.assemble.control.service.CatagoryInfoServiceAdv;
import com.x.cms.core.entity.AppCatagoryPermission;


@Path("appcatagorypermission")
public class AppCatagoryPermissionAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( AppCatagoryPermissionAction.class );
	private CatagoryInfoServiceAdv catagoryInfoServiceAdv = new CatagoryInfoServiceAdv();
	private AppCatagoryPermissionServiceAdv appCatagoryPermissionServiceAdv = new AppCatagoryPermissionServiceAdv();
	private BeanCopyTools<AppCatagoryPermission, WrapOutAppCatagoryPermission> wrapout_copier = BeanCopyToolsBuilder.create( AppCatagoryPermission.class, WrapOutAppCatagoryPermission.class, null, WrapOutAppCatagoryPermission.Excludes);
	private BeanCopyTools<WrapInAppCatagoryPermission, AppCatagoryPermission> wrapoin_copier = BeanCopyToolsBuilder.create( WrapInAppCatagoryPermission.class, AppCatagoryPermission.class, null, WrapInAppCatagoryPermission.Excludes );

	@HttpMethodDescribe(value = "获取用户有权限访问的权限配置信息列表", response = WrapOutAppCatagoryPermission.class)
	@GET
	@Path("list/{person}/type/{objectType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listUserPermission( @Context HttpServletRequest request, @PathParam("person")String person, @PathParam("objectType")String objectType ) {
		ActionResult<List<WrapOutAppCatagoryPermission>> result = new ActionResult<>();
		List<WrapOutAppCatagoryPermission> wraps = null;
		List<String> ids = null;
		List<AppCatagoryPermission> appCatagoryPermissionList = null;
		Boolean check = true;
		
		if( check ){
			if( person == null || person.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到传入的参数person。" ) );
				result.setUserMessage( "系统未获取到传入的参数person。" );
			}
		}
		if( check ){
			if( objectType == null || objectType.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到传入的参数objectType。" ) );
				result.setUserMessage( "系统未获取到传入的参数objectType。" );
			}
		}
		if( check ){
			try {
				ids = appCatagoryPermissionServiceAdv.listAppCatagoryPermissionByUser( person, objectType );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统根据person, objectType获取应用分类权限配置列表发生异常。" ) );
				result.setUserMessage( "系统根据person, objectType获取应用分类权限配置列表发生异常。" );
				logger.error( "system query catagory permission id list with person and objectType got an exception. person:" + person + ", objectType:" + objectType, e );
			}
		}
		if( check ){
			if( ids == null || ids.isEmpty() ){
				try {
					appCatagoryPermissionList = appCatagoryPermissionServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统根据信息ID列表获取应用分类权限信息列表发生异常。" ) );
					result.setUserMessage( "系统根据信息ID列表获取应用分类权限信息列表发生异常。" );
					logger.error( "system query catagory permission list with ids got an exception. ", e );
				}
			}
		}
		if( check ){
			if( appCatagoryPermissionList != null && !appCatagoryPermissionList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appCatagoryPermissionList );
					Collections.sort(wraps);
					result.setData(wraps);
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统在将应用分类权限信息列表转换为输出格式时发生异常" );
					logger.error( "system copy appCatagoryPermission list to wraps got an exception!", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有应用栏目列表", response = WrapOutAppCatagoryPermission.class)
	@GET
	@Path("list/person/{person}/appInfo")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAppInfoByUserPermission( @Context HttpServletRequest request, @PathParam("person")String person  ) {
		ActionResult<List<WrapOutAppCatagoryPermission>> result = new ActionResult<>();
		List<WrapOutAppCatagoryPermission> wraps = null;
		List<String> ids = null;
		List<AppCatagoryPermission> appCatagoryPermissionList = null;
		Boolean check = true;
		
		if( check ){
			if( person == null || person.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到传入的参数person。" ) );
				result.setUserMessage( "系统未获取到传入的参数person。" );
			}
		}
		if( check ){
			try {
				ids = appCatagoryPermissionServiceAdv.listAppInfoByUserPermission( person );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统根据person获取应用分类权限配置列表发生异常。" ) );
				result.setUserMessage( "系统根据person获取应用分类权限配置列表发生异常。" );
				logger.error( "system query catagory permission id list with person got an exception. person:" + person, e );
			}
		}
		if( check ){
			if( ids == null || ids.isEmpty() ){
				try {
					appCatagoryPermissionList = appCatagoryPermissionServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统根据信息ID列表获取应用分类权限信息列表发生异常。" ) );
					result.setUserMessage( "系统根据信息ID列表获取应用分类权限信息列表发生异常。" );
					logger.error( "system query catagory permission list with ids got an exception. ", e );
				}
			}
		}
		if( check ){
			if( appCatagoryPermissionList != null && !appCatagoryPermissionList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appCatagoryPermissionList );
					Collections.sort(wraps);
					result.setData(wraps);
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统在将应用分类权限信息列表转换为输出格式时发生异常" );
					logger.error( "system copy appCatagoryPermission list to wraps got an exception!", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有分类列表", response = WrapOutAppCatagoryPermission.class)
	@GET
	@Path("list/person/{person}/{appId}/catagoryInfo")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCatagoryInfoByUserPermission( @Context HttpServletRequest request, @PathParam("person")String person, @PathParam("appId")String appId  ) {
		ActionResult<List<WrapOutAppCatagoryPermission>> result = new ActionResult<>();
		List<WrapOutAppCatagoryPermission> wraps = null;
		List<String> appCatagoryIds = null;
		List<String> ids = null;
		List<AppCatagoryPermission> appCatagoryPermissionList = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;		
		if( check ){
			if( person == null || person.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到传入的参数person。" ) );
				result.setUserMessage( "系统未获取到传入的参数person。" );
			}
		}
		if( check ){
			if( appId == null || appId.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到传入的参数appId。" ) );
				result.setUserMessage( "系统未获取到传入的参数objectType。" );
			}
		}
		if( check ){
			try {
				appCatagoryIds = catagoryInfoServiceAdv.listByAppId( appId );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统根据appId获取所有应用分类ID列表发生异常。" ) );
				result.setUserMessage( "系统根据appId获取所有应用分类ID列表发生异常。" );
				logger.error( "system query catagory id list with appId got an exception. appId:" + appId, e );
			}
		}
		if( check ){
			try {
				ids = appCatagoryPermissionServiceAdv.listCatagoryInfoByUserPermission( currentPerson, appCatagoryIds );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统根据person, appCatagoryIds获取应用分类权限配置列表发生异常。" ) );
				result.setUserMessage( "系统根据person, appCatagoryIds获取应用分类权限配置列表发生异常。" );
				logger.error( "system query catagory permission id list with person and objectType got an exception. person:" + person, e );
			}
		}
		if( check ){
			if( ids == null || ids.isEmpty() ){
				try {
					appCatagoryPermissionList = appCatagoryPermissionServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统根据信息ID列表获取应用分类权限信息列表发生异常。" ) );
					result.setUserMessage( "系统根据信息ID列表获取应用分类权限信息列表发生异常。" );
					logger.error( "system query catagory permission list with ids got an exception. ", e );
				}
			}
		}
		if( check ){
			if( appCatagoryPermissionList != null && !appCatagoryPermissionList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appCatagoryPermissionList );
					Collections.sort(wraps);
					result.setData(wraps);
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统在将应用分类权限信息列表转换为输出格式时发生异常" );
					logger.error( "system copy appCatagoryPermission list to wraps got an exception!", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有权限配置信息列表", response = WrapOutAppCatagoryPermission.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAppCatagoryPermission(@Context HttpServletRequest request ) {		
		ActionResult<List<WrapOutAppCatagoryPermission>> result = new ActionResult<>();
		List<WrapOutAppCatagoryPermission> wraps = null;
		List<AppCatagoryPermission> appCatagoryPermissionList = null;
		Boolean check = true;
		
		if( check ){
			try {
				appCatagoryPermissionList = appCatagoryPermissionServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统获取所有应用分类权限信息列表发生异常。" ) );
				result.setUserMessage( "系统获取所有应用分类权限信息列表发生异常。" );
				logger.error( "system query all catagory permission list got an exception. ", e );
			}
		}
		if( check ){
			if( appCatagoryPermissionList != null && !appCatagoryPermissionList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appCatagoryPermissionList );
					Collections.sort(wraps);
					result.setData(wraps);
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统在将应用分类权限信息列表转换为输出格式时发生异常" );
					logger.error( "system copy appCatagoryPermission list to wraps got an exception!", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有权限配置信息列表", response = WrapOutAppCatagoryPermission.class)
	@GET
	@Path("list/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPermissionByApp(@Context HttpServletRequest request, @PathParam("appId")String appId ) {		
		ActionResult<List<WrapOutAppCatagoryPermission>> result = new ActionResult<>();
		List<WrapOutAppCatagoryPermission> wraps = null;
		List<String> ids = null;
		List<AppCatagoryPermission> appCatagoryPermissionList = null;
		Boolean check = true;
		
		if( check ){
			if( appId == null || appId.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到传入的参数appId。" ) );
				result.setUserMessage( "系统未获取到传入的参数appId。" );
			}
		}
		if( check ){
			try {
				ids = appCatagoryPermissionServiceAdv.listPermissionByAppInfo( appId );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统根据appId获取应用分类权限配置列表发生异常。" ) );
				result.setUserMessage( "系统根据appId获取应用分类权限配置列表发生异常。" );
				logger.error( "system query catagory permission id list with appId got an exception. appId:" + appId, e );
			}
		}
		if( check ){
			if( ids == null || ids.isEmpty() ){
				try {
					appCatagoryPermissionList = appCatagoryPermissionServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统根据信息ID列表获取应用分类权限信息列表发生异常。" ) );
					result.setUserMessage( "系统根据信息ID列表获取应用分类权限信息列表发生异常。" );
					logger.error( "system query catagory permission list with ids got an exception. ", e );
				}
			}
		}
		if( check ){
			if( appCatagoryPermissionList != null && !appCatagoryPermissionList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appCatagoryPermissionList );
					Collections.sort(wraps);
					result.setData(wraps);
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统在将应用分类权限信息列表转换为输出格式时发生异常" );
					logger.error( "system copy appCatagoryPermission list to wraps got an exception!", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取appCatagoryPermission对象.", response = WrapOutAppCatagoryPermission.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutAppCatagoryPermission> result = new ActionResult<>();
		WrapOutAppCatagoryPermission wrap = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug("user["+currentPerson.getName()+"] trying to get AppCatagoryPermission{'id':'"+id+"'}......");
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			AppCatagoryPermission AppCatagoryPermission = business.getAppCatagoryPermissionFactory().get(id);
			if ( null == AppCatagoryPermission ) {
				throw new Exception("AppCatagoryPermission{id:" + id + "} 信息不存在.");
			}
			//如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
			wrap = new WrapOutAppCatagoryPermission();
			wrapout_copier.copy(AppCatagoryPermission, wrap);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "创建AppCatagoryPermission权限配置信息对象.", request = WrapInAppCatagoryPermission.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInAppCatagoryPermission wrapIn) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		AppCatagoryPermission appCatagoryPermission = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception( "系统未获取到需要保存的数据, wrapIn为空。" ) );
				result.setUserMessage( "系统未获取到需要保存的数据。" );
			}
		}
		if( check ){
			if( wrapIn.getObjectId() == null || wrapIn.getObjectId().isEmpty() ){
				check = false;
				result.error( new Exception( "保存数据中对象ID不允许为空。" ) );
				result.setUserMessage( "保存数据中对象ID不允许为空。" );
			}
		}
		if( check ){
			if( wrapIn.getObjectType() == null || wrapIn.getObjectType().isEmpty() ){
				check = false;
				result.error( new Exception( "保存数据中对象类别不允许为空。" ) );
				result.setUserMessage( "保存数据中对象类别不允许为空。" );
			}
		}
		if( check ){
			if( wrapIn.getUsedObjectType() == null || wrapIn.getUsedObjectType().isEmpty() ){
				check = false;
				result.error( new Exception( "保存数据中使用者类别不允许为空。" ) );
				result.setUserMessage( "保存数据中使用者类别不允许为空。" );
			}
		}
		if( check ){
			if( wrapIn.getUsedObjectName() == null || wrapIn.getUsedObjectName().isEmpty() ){
				check = false;
				result.error( new Exception( "保存数据中使用者名称不允许为空。" ) );
				result.setUserMessage( "保存数据中使用者名称不允许为空。" );
			}
		}
		if( check ){
			appCatagoryPermission = new AppCatagoryPermission();
			try {
				wrapoin_copier.copy( wrapIn, appCatagoryPermission );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在将传入的数据转换为保存的对象时发生异常。" );
				logger.error( "system copy wrapIn to object got an exception." );
			}
		}
		if( check ){
			try {
				appCatagoryPermission = appCatagoryPermissionServiceAdv.save( appCatagoryPermission, currentPerson );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在保存应用分类权限员信息数据时发生异常。" );
				logger.error( "system save app catagory admin config got an exception." );
			}
		}
		if( check ){
			if( appCatagoryPermission != null ){
				try {
					wrap = new WrapOutId( appCatagoryPermission.getId() );
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在输出已保存对象ID时发生异常。" );
					logger.error( "system wrap out id got an exception." );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除AppCatagoryPermission权限配置信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		AppCatagoryPermission appCatagoryPermission  = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到需要删除的数据id." ) );
				result.setUserMessage( "系统未获取到需要删除的数据id." );
			}
		}
		if( check ){
			try {
				appCatagoryPermission = appCatagoryPermissionServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根据ID获取数据时发生异常." );
				logger.error( "system get appcatagorypermission info with id got an exception.", e );
			}
		}
		if( check ){
			if( appCatagoryPermission == null ){
				check = false;
				result.error( new Exception( "需要删除的数据不存在。" ) );
				result.setUserMessage( "需要删除的数据不存在。" );
			}
		}
		if( check ){
			try {
				appCatagoryPermissionServiceAdv.delete( appCatagoryPermission, currentPerson );
				wrap = new WrapOutId( id );
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统根删除指定的数据时发生异常." );
				logger.error( "system delete appcatagorypermission info got an exception.", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}