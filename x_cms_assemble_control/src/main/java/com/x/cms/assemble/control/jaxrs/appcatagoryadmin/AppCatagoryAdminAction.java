package com.x.cms.assemble.control.jaxrs.appcatagoryadmin;

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
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.assemble.control.service.AppCatagoryAdminServiceAdv;
import com.x.cms.core.entity.AppCatagoryAdmin;

@Path("appcatagoryadmin")
public class AppCatagoryAdminAction extends StandardJaxrsAction {

	private AppCatagoryAdminServiceAdv appCatagoryAdminServiceAdv = new AppCatagoryAdminServiceAdv();
	private BeanCopyTools<AppCatagoryAdmin, WrapOutAppCatagoryAdmin> wrapout_copier = BeanCopyToolsBuilder.create(AppCatagoryAdmin.class, WrapOutAppCatagoryAdmin.class, null, WrapOutAppCatagoryAdmin.Excludes);
	private BeanCopyTools<WrapInAppCatagoryAdmin, AppCatagoryAdmin> wrapin_copier = BeanCopyToolsBuilder.create(WrapInAppCatagoryAdmin.class, AppCatagoryAdmin.class, null, WrapInAppCatagoryAdmin.Excludes);
	private Logger logger = LoggerFactory.getLogger( AppCatagoryAdminAction.class );

	@HttpMethodDescribe(value = "获取指定分类管理员配置列表", response = WrapOutAppCatagoryAdmin.class)
	@GET
	@Path("list/catagory/{catagoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listCatagoryAdmin( @Context HttpServletRequest request, @PathParam("catagoryId") String catagoryId ) {
		ActionResult<List<WrapOutAppCatagoryAdmin>> result = new ActionResult<>();
		List<WrapOutAppCatagoryAdmin> wraps = null;
		List<String> ids = null;
		List<AppCatagoryAdmin> appCatagoryAdminList = null;
		Boolean check = true;
		if( check ){
			if( catagoryId == null || catagoryId.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到传入的参数catagoryId。" ) );
				result.setUserMessage( "系统未获取到传入的参数catagoryId。" );
			}
		}
		if( check ){
			try {
				ids = appCatagoryAdminServiceAdv.listAppCatagoryIdByCatagoryId( catagoryId );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统根据catagoryId获取分类管理员配置列表发生异常。" ) );
				result.setUserMessage( "系统根据catagoryId获取分类管理员配置列表发生异常。" );
				logger.error( "system query catagory admin id list with catagoryid got an exception. catagoryId:" + catagoryId, e );
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					appCatagoryAdminList = appCatagoryAdminServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统根据分类管理员配置信息ID列表获取分类管理员配置信息列表发生异常。" ) );
					result.setUserMessage( "系统根据分类管理员配置信息ID列表获取分类管理员配置信息列表发生异常。" );
					logger.error( "system query catagory admin list with ids got an exception. ", e );
				}
			}
		}
		if( check ){
			if( appCatagoryAdminList != null && !appCatagoryAdminList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appCatagoryAdminList );
					Collections.sort(wraps);
					result.setData(wraps);
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统在将分类管理员配置信息列表转换为输出格式时发生异常" );
					logger.error( "system copy appCatagoryAdmin list to wraps got an exception!", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定应用栏目管理员配置列表", response = WrapOutAppCatagoryAdmin.class)
	@GET
	@Path("list/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAppInfoAdmin(@Context HttpServletRequest request, @PathParam( "appId") String appId ) {
		ActionResult<List<WrapOutAppCatagoryAdmin>> result = new ActionResult<>();
		List<WrapOutAppCatagoryAdmin> wraps = null;
		List<String> ids = null;
		List<AppCatagoryAdmin> appCatagoryAdminList = null;
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
				ids = appCatagoryAdminServiceAdv.listAppCatagoryIdByAppId( appId );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统根据appId获取分类管理员配置列表发生异常。" ) );
				result.setUserMessage( "系统根据appId获取分类管理员配置列表发生异常。" );
				logger.error( "system query catagory admin id list with appId got an exception. appId:" + appId, e );
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					appCatagoryAdminList = appCatagoryAdminServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统根据分类管理员配置信息ID列表获取分类管理员配置信息列表发生异常。" ) );
					result.setUserMessage( "系统根据分类管理员配置信息ID列表获取分类管理员配置信息列表发生异常。" );
					logger.error( "system query catagory admin list with ids got an exception. ", e );
				}
			}
		}
		if( check ){
			if( appCatagoryAdminList != null && !appCatagoryAdminList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appCatagoryAdminList );
					Collections.sort( wraps );
					result.setData( wraps );
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统在将分类管理员配置信息列表转换为输出格式时发生异常" );
					logger.error( "system copy appCatagoryAdmin list to wraps got an exception!", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "获取指定用户的管理员相关配置列表", response = WrapOutAppCatagoryAdmin.class)
	@GET
	@Path("list/person/{person}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPersonAdminConfig(@Context HttpServletRequest request, @PathParam("person") String person) {
		ActionResult<List<WrapOutAppCatagoryAdmin>> result = new ActionResult<>();
		List<WrapOutAppCatagoryAdmin> wraps = null;
		List<String> ids = null;
		List<AppCatagoryAdmin> appCatagoryAdminList = null;
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
				ids = appCatagoryAdminServiceAdv.listAppCatagoryIdByUser( person );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统根据person获取分类管理员配置列表发生异常。" ) );
				result.setUserMessage( "系统根据person获取分类管理员配置列表发生异常。" );
				logger.error( "system query catagory admin id list with person got an exception. person:" + person, e );
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					appCatagoryAdminList = appCatagoryAdminServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统根据分类管理员配置信息ID列表获取分类管理员配置信息列表发生异常。" ) );
					result.setUserMessage( "系统根据分类管理员配置信息ID列表获取分类管理员配置信息列表发生异常。" );
					logger.error( "system query catagory admin list with ids got an exception. ", e );
				}
			}
		}
		if( check ){
			if( appCatagoryAdminList != null && !appCatagoryAdminList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appCatagoryAdminList );
					Collections.sort(wraps);
					result.setData(wraps);
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统在将分类管理员配置信息列表转换为输出格式时发生异常" );
					logger.error( "system copy appCatagoryAdmin list to wraps got an exception!", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取用户有权限访问的权限配置信息列表", response = WrapOutAppCatagoryAdmin.class)
	@GET
	@Path("list/{person}/type/{objectType}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAppCatagoryObjectIdByUser( @Context HttpServletRequest request,
			@PathParam("person") String person, @PathParam("objectType") String objectType ) {
		ActionResult<List<WrapOutAppCatagoryAdmin>> result = new ActionResult<>();
		List<WrapOutAppCatagoryAdmin> wraps = null;
		List<String> ids = null;
		List<AppCatagoryAdmin> appCatagoryAdminList = null;
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
				ids = appCatagoryAdminServiceAdv.listAppCatagoryObjectIdByUser( person, objectType );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统根据person, objectType获取分类管理员配置列表发生异常。" ) );
				result.setUserMessage( "系统根据person, objectType获取分类管理员配置列表发生异常。" );
				logger.error( "system query catagory admin id list with person got an exception. person:" + person + ", objectType:" + objectType, e );
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					appCatagoryAdminList = appCatagoryAdminServiceAdv.list( ids );
				} catch (Exception e) {
					check = false;
					result.error( new Exception( "系统根据分类管理员配置信息ID列表获取分类管理员配置信息列表发生异常。" ) );
					result.setUserMessage( "系统根据分类管理员配置信息ID列表获取分类管理员配置信息列表发生异常。" );
					logger.error( "system query catagory admin list with ids got an exception. ", e );
				}
			}
		}
		if( check ){
			if( appCatagoryAdminList != null && !appCatagoryAdminList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appCatagoryAdminList );
					Collections.sort(wraps);
					result.setData(wraps);
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统在将分类管理员配置信息列表转换为输出格式时发生异常" );
					logger.error( "system copy appCatagoryAdmin list to wraps got an exception!", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取用户有权限访问的所有权限配置信息列表", response = WrapOutAppCatagoryAdmin.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAppCatagoryAdmin( @Context HttpServletRequest request ) {
		ActionResult<List<WrapOutAppCatagoryAdmin>> result = new ActionResult<>();
		List<AppCatagoryAdmin> appCatagoryAdminList = null;
		List<WrapOutAppCatagoryAdmin> wraps = null;
		Boolean check = true;
		if( check ){
			try {
				appCatagoryAdminList = appCatagoryAdminServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统查询所有应用分类管理员配置信息列表发生异常。" ) );
				result.setUserMessage( "系统查询所有应用分类管理员配置信息列表发生异常。" );
				logger.error( "system list all catagory admin got an exception. ", e );
			}
		}
		if( check ){
			if( appCatagoryAdminList != null && !appCatagoryAdminList.isEmpty() ){
				try {
					wraps = wrapout_copier.copy( appCatagoryAdminList );
					Collections.sort(wraps);
					result.setData(wraps);
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统在将分类管理员配置信息列表转换为输出格式时发生异常" );
					logger.error( "system copy appCatagoryAdmin list to wraps got an exception!", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取appCatagoryAdmin对象.", response = WrapOutAppCatagoryAdmin.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutAppCatagoryAdmin> result = new ActionResult<>();
		AppCatagoryAdmin appCatagoryAdmin = null;
		WrapOutAppCatagoryAdmin wrap = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到传入的参数id。" ) );
				result.setUserMessage( "系统未获取到传入的参数id。" );
			}
		}
		if( check ){
			try {
				appCatagoryAdmin = appCatagoryAdminServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统根据指定的ID查询应用分类管理员配置信息列表发生异常。" ) );
				result.setUserMessage( "系统根据指定的ID查询有应用分类管理员配置信息列表发生异常。" );
				logger.error( "system get catagory admin with id got an exception.id:" + id, e );
			}
		}
		if( check ){
			if( appCatagoryAdmin != null ){
				try {
					wrap = wrapout_copier.copy( appCatagoryAdmin );
					result.setData( wrap );
				} catch (Exception e) {
					result.error( e );
					result.setUserMessage( "系统在将分类管理员配置信息列表转换为输出格式时发生异常" );
					logger.error( "system copy appCatagoryAdmin list to wraps got an exception!", e );
				}
			}else{
				result.error( new Exception( "AppCatagoryAdmin{id:" + id + "} 信息不存在." ) );
				result.setUserMessage( "用户查看所指定的信息不存在." );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建AppCatagoryAdmin权限配置信息对象.", request = WrapInAppCatagoryAdmin.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post( @Context HttpServletRequest request, WrapInAppCatagoryAdmin wrapIn ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		AppCatagoryAdmin appCatagoryAdmin = null;
		WrapOutId wrap = null;
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
			if( wrapIn.getAdminName() == null || wrapIn.getAdminName().isEmpty() ){
				check = false;
				result.error( new Exception( "保存数据中管理员姓名不允许为空。" ) );
				result.setUserMessage( "保存数据中管理员姓名不允许为空。" );
			}
		}
		if( check ){
			if( wrapIn.getAdminLevel() == null || wrapIn.getAdminLevel().isEmpty() ){
				check = false;
				result.error( new Exception( "保存数据中管理级别不允许为空。" ) );
				result.setUserMessage( "保存数据中管理级别不允许为空。" );
			}
		}		
		if( check ){
			appCatagoryAdmin = new AppCatagoryAdmin();
			try {
				appCatagoryAdmin = wrapin_copier.copy( wrapIn, appCatagoryAdmin );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在将传入的数据转换为可保存对象时发生异常。" );
				logger.error( "system copy wrap in app catagory admin config got an exception." );
			}
		}
		if( check ){
			try {
				appCatagoryAdmin = appCatagoryAdminServiceAdv.save( appCatagoryAdmin, currentPerson );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在保存应用分类管理员信息数据时发生异常。" );
				logger.error( "system save app catagory admin config got an exception." );
			}
		}
		if( check ){
			if( appCatagoryAdmin != null ){
				try {
					wrap = new WrapOutId( appCatagoryAdmin.getId() );
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

	@HttpMethodDescribe(value = "根据ID删除AppCatagoryAdmin权限配置信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		AppCatagoryAdmin appCatagoryAdmin = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到传入的参数id。" ) );
				result.setUserMessage( "系统未获取到传入的参数id。" );
			}
		}
		if( check ){
			try {
				appCatagoryAdmin = appCatagoryAdminServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				result.error( new Exception( "系统根据指定的ID查询应用分类管理员配置信息列表发生异常。" ) );
				result.setUserMessage( "系统根据指定的ID查询有应用分类管理员配置信息列表发生异常。" );
				logger.error( "system get catagory admin with id got an exception.id:" + id, e );
			}
		}
		if( check ){
			if( appCatagoryAdmin == null ){
				check = false;
				result.error( new Exception( "需要删除的数据不存在。" ) );
				result.setUserMessage( "需要删除的数据不存在。" );
			}
		}
		if( check ){
			try {
				appCatagoryAdminServiceAdv.delete( appCatagoryAdmin, currentPerson );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在删除应用分类管理员信息数据时发生异常。" );
				logger.error( "system delete app catagory admin config got an exception.id:" + id );
			}
		}
		if( check ){
			if( appCatagoryAdmin != null ){
				try {
					wrap = new WrapOutId( id );
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
}