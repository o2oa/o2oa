package com.x.cms.assemble.control.jaxrs.appinfo;

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

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.InTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
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
import com.x.cms.assemble.control.jaxrs.appinfo.exception.AppInfoProcessException;
import com.x.cms.assemble.control.jaxrs.search.exception.AppInfoListViewableInPermissionException;
import com.x.cms.core.entity.AppInfo;

@Path("appinfo")
public class AppInfoAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( AppInfoAction.class );
	
	@HttpMethodDescribe(value = "创建或者更新CMS应用栏目信息对象.", request = JsonElement.class, response = WrapOutId.class )
	@POST
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response save( @Context HttpServletRequest request, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInAppInfo wrapIn = null;
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInAppInfo.class );
		} catch ( Exception e ) {
			check = false;
			Exception exception = new AppInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new AppInfoProcessException( e, "应用栏目信息保存时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除CMS应用信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppInfoProcessException( e, "根据ID删除CMS应用信息对象发生未知异常，ID:"+ id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取appInfo对象.", response = WrapOutAppInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutAppInfo> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppInfoProcessException( e, "根据指定ID查询应用栏目信息对象时发生异常。ID:" + id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取appInfo对象.", response = WrapOutAppInfo.class)
	@GET
	@Path("alias/{alias}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getByAlias( @Context HttpServletRequest request, @PathParam("alias") String alias ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutAppInfo> result = new ActionResult<>();
		try {
			result = new ExcuteGetByAlias().execute( request, effectivePerson, alias );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppInfoProcessException( e, "根据指定应用唯一标识查询应用栏目信息对象时发生异常。ALIAS:" + alias );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有应用列表", response = WrapOutAppInfo.class)
	@GET
	@Path("list/user/view")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWhatICanView( @Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListWhatICanView().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppInfoListViewableInPermissionException( e, effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有应用列表", response = WrapOutAppInfo.class)
	@GET
	@Path("list/user/publish")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWhatICanPublish( @Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListWhatICanPublish().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppInfoProcessException( e, "系统在根据用户权限查询所有可见的栏目信息时发生异常。Name:" + effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "获取用户有权限管理的所有CMS应用列表", response = WrapOutAppInfo.class)
	@GET
	@Path("list/manage")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWhatICanManage( @Context HttpServletRequest request ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListWhatICanManage().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppInfoProcessException( e, "系统在根据用户权限查询所有管理的栏目信息时发生异常。Name:" + effectivePerson.getName() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "获取用户有权限访问的所有CMS应用列表", response = WrapOutAppInfo.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllAppInfo(@Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListAll().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppInfoProcessException( e, "查询所有应用栏目信息对象时发生异常" );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的AppInfo,下一页.", response = WrapOutAppInfo.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		EqualsTerms equals = new EqualsTerms();
		InTerms ins = new InTerms();
		LikeTerms likes = new LikeTerms();
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new AppInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}

		if( check ){
			if( wrapIn == null ){
				result.setCount( 0L );
				result.setData( new ArrayList<WrapOutAppInfo>() );
				return ResponseFactory.getDefaultActionResultResponse(result);
			}
			if( id == null ){
				id = "(0)";
			}
			if( count == null ){
				count = 20;
			}		
			if ((null != wrapIn.getAppIdList()) && (!wrapIn.getAppIdList().isEmpty())) {
				ins.put("id", wrapIn.getAppIdList());
			}
			if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
				ins.put("creatorUid", wrapIn.getCreatorList() );
			}
			if ( StringUtils.isNotEmpty( wrapIn.getKey() ) ) {
				String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					likes.put("appName", key);
				}
			}
			BeanCopyTools<AppInfo, WrapOutAppInfo> wrapout_copier = BeanCopyToolsBuilder.create(AppInfo.class, WrapOutAppInfo.class, null, WrapOutAppInfo.Excludes);
			try {
				result = this.standardListNext( wrapout_copier, id, count, "sequence", equals, null, likes, ins, null, null, null, true, DESC);
			} catch ( Exception e ) {
				result.error( e );
				logger.warn( "系统在分页查询栏目信息列表时发生异常。" );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的AppInfo,下一页.", response = WrapOutAppInfo.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutAppInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		EqualsTerms equals = new EqualsTerms();
		InTerms ins = new InTerms();
		LikeTerms likes = new LikeTerms();
		WrapInFilter wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new AppInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if(check ){
			if( wrapIn == null ){
				wrapIn = new WrapInFilter();
			}
			if( id == null ){
				id = "(0)";
			}
			if( count == null ){
				count = 20;
			}
			if ((null != wrapIn.getAppIdList()) && (!wrapIn.getAppIdList().isEmpty())) {
				ins.put("id", wrapIn.getAppIdList());
			}
			if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
				ins.put("creatorUid", wrapIn.getCreatorList() );
			}
			if ( StringUtils.isNotEmpty( wrapIn.getKey() ) ) {
				String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					likes.put("appName", key);
				}
			}
			BeanCopyTools<AppInfo, WrapOutAppInfo> wrapout_copier = BeanCopyToolsBuilder.create(AppInfo.class, WrapOutAppInfo.class, null, WrapOutAppInfo.Excludes);
			try {
				result = this.standardListPrev( wrapout_copier, id, count, "sequence", equals, null, likes, ins, null, null, null, true, DESC );
			} catch ( Exception e ) {
				result.error( e );
				logger.warn( "系统在分页查询栏目信息列表时发生异常。" );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}