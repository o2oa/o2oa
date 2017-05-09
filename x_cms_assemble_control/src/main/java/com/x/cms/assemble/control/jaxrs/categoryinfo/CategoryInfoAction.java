package com.x.cms.assemble.control.jaxrs.categoryinfo;

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
import com.x.cms.assemble.control.jaxrs.categoryinfo.exception.CategoryInfoProcessException;
import com.x.cms.core.entity.CategoryInfo;


@Path("categoryinfo")
public class CategoryInfoAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( CategoryInfoAction.class );
	
	@HttpMethodDescribe(value = "创建或者更新CMS分类信息对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, JsonElement jsonElement ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInCategoryInfo wrapIn = null;
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInCategoryInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new CategoryInfoProcessException( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				Exception exception = new CategoryInfoProcessException( e, "分类信息在保存时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除CategoryInfo应用信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			result = new ExcuteDelete().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new AppInfoProcessException( e, "分类信息在删除时发生异常。ID:" + id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有分类列表", response = WrapOutCategoryInfo.class)
	@GET
	@Path("list/view/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listViewableCategoryInfo( @Context HttpServletRequest request, @PathParam("appId")String appId ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutCategoryInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListWhatICanView().execute( request, appId, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new CategoryInfoProcessException( e, "根据指定应用栏目ID查询分类信息列表时发生异常。ID:" + appId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有分类列表", response = WrapOutCategoryInfo.class)
	@GET
	@Path("list/publish/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPublishableCategoryInfo( @Context HttpServletRequest request, @PathParam("appId")String appId ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutCategoryInfo>> result = new ActionResult<>();
		try {
			result = new ExcuteListWhatICanPublish().execute( request, appId, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new CategoryInfoProcessException( e, "根据应用栏目ID查询分类信息对象时发生异常。AppId:" + appId );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有分类列表", response = WrapOutCategoryInfo.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllCategoryInfo( @Context HttpServletRequest request ) {		
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutCategoryInfo>> result = null;
		try {
			result = new ExcuteListAll().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new CategoryInfoProcessException( e, "查询所有分类信息对象时发生异常。" );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "根据ID获取分类对象.", response = WrapOutCategoryInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutCategoryInfo> result = null;
		try {
			result = new ExcuteGet().execute( request, id, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new CategoryInfoProcessException( e, "根据ID查询分类信息对象时发生异常。ID:" + id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "根据ID获取分类对象.", response = WrapOutCategoryInfo.class)
	@GET
	@Path("alias/{alias}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getByAlias(@Context HttpServletRequest request, @PathParam("alias") String alias ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutCategoryInfo> result = null;
		try {
			result = new ExcuteGetByAlias().execute( request, alias, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new CategoryInfoProcessException( e, "根据标识查询分类信息对象时发生异常。ALIAS:"+ alias );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的CategoryInfo,下一页.", response = WrapOutCategoryInfo.class, request = WrapInFilter.class)
	@POST
	@Path("filter/list/{id}/next/{count}/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, @PathParam("appId") String appId, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutCategoryInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		EqualsTerms equals = new EqualsTerms();
		InTerms ins = new InTerms();
		LikeTerms likes = new LikeTerms();

		if ( null != wrapIn.getAppIdList() && !wrapIn.getAppIdList().isEmpty() ) {
			ins.put("appId", wrapIn.getAppIdList() );
		}
		if ( null != wrapIn.getCategoryIdList() && !wrapIn.getCategoryIdList().isEmpty() ) {
			ins.put("id", wrapIn.getCategoryIdList() );
		}
		if ( null != wrapIn.getCreatorList() && !wrapIn.getCreatorList().isEmpty() ) {
			ins.put("creatorPerson", wrapIn.getCreatorList() );
		}
		if ( StringUtils.isNotEmpty(wrapIn.getKey()) ) {
			String key = StringUtils.trim(StringUtils.replace( wrapIn.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				likes.put("title", key);
			}
		}
		BeanCopyTools<CategoryInfo, WrapOutCategoryInfo> copier = BeanCopyToolsBuilder.create( CategoryInfo.class, WrapOutCategoryInfo.class, null, WrapOutCategoryInfo.Excludes);
		try {
			result = this.standardListNext( copier, id, count, "sequence", equals, null, likes, ins, null, null, null, true, DESC );
		} catch ( Exception e ) {
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的CategoryInfo,上一页.", response = WrapOutCategoryInfo.class, request = WrapInFilter.class)
	@POST
	@Path("filter/list/{id}/prev/{count}/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter( @Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, @PathParam("appId") Integer appId, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutCategoryInfo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		EqualsTerms equals = new EqualsTerms();
		InTerms ins = new InTerms();
		LikeTerms likes = new LikeTerms();
		
		if ( null != wrapIn.getAppIdList() && !wrapIn.getAppIdList().isEmpty() ) {
			ins.put("appId", wrapIn.getAppIdList() );
		}
		if ( null != wrapIn.getCategoryIdList() && !wrapIn.getCategoryIdList().isEmpty() ) {
			ins.put("id", wrapIn.getCategoryIdList() );
		}
		if ( null != wrapIn.getCreatorList() && !wrapIn.getCreatorList().isEmpty() ) {
			ins.put("creatorPerson", wrapIn.getCreatorList() );
		}
		if ( StringUtils.isNotEmpty(wrapIn.getKey()) ) {
			String key = StringUtils.trim(StringUtils.replace( wrapIn.getKey(), "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				likes.put("title", key);
			}
		}
		BeanCopyTools<CategoryInfo, WrapOutCategoryInfo> copier = BeanCopyToolsBuilder.create( CategoryInfo.class, WrapOutCategoryInfo.class, null, WrapOutCategoryInfo.Excludes);
		try{
			result = this.standardListPrev( copier, id, count, "sequence", equals, null, likes, ins, null, null, null, true, DESC );
		} catch ( Exception e ) {
			result.error( e );
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
}