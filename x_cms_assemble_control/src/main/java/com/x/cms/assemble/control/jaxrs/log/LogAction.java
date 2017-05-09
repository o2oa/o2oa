package com.x.cms.assemble.control.jaxrs.log;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.jaxrs.log.exception.WrapInConvertException;
import com.x.cms.core.entity.Log;

@Path("log")
public class LogAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( ExcuteListByLevel.class );

	@HttpMethodDescribe(value = "获取指定操作对象级别的操作日志信息列表", response = WrapOutLog.class)
	@GET
	@Path("list/level/{operationLevel}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLogByOperationLevel(@Context HttpServletRequest request, @PathParam("operationLevel") String operationLevel) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		try {
			result = new ExcuteListByLevel().execute( request, effectivePerson, operationLevel );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定应用的操作日志信息列表", response = WrapOutLog.class)
	@GET
	@Path("list/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLogByAppId(@Context HttpServletRequest request, @PathParam("appId") String appId) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		try {
			result = new ExcuteListByAppId().execute( request, effectivePerson, appId );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定分类的操作日志信息列表", response = WrapOutLog.class)
	@GET
	@Path("list/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLogByCategoryId(@Context HttpServletRequest request, @PathParam("categoryId") String categoryId) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		try {
			result = new ExcuteListByCategory().execute( request, effectivePerson, categoryId );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定文档的操作日志信息列表", response = WrapOutLog.class)
	@GET
	@Path("list/document/{documentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLogByDocumentId(@Context HttpServletRequest request, @PathParam("documentId") String documentId) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		try {
			result = new ExcuteListByDocument().execute( request, effectivePerson, documentId );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取log对象.", response = WrapOutLog.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutLog> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			result.error( e );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Log,下一页.", response = WrapOutLog.class, request = JsonElement.class)
	@POST
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();
		WrapInFilter wrapIn = null;
		Boolean check = true;
		BeanCopyTools<Log, WrapOutLog> copier = BeanCopyToolsBuilder.create(Log.class, WrapOutLog.class, null,
				WrapOutLog.Excludes);
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			e.printStackTrace();
		}
		
		if( check ){
			try {
				// equals = new ListOrderedMap<>();
				if ((null != wrapIn.getCategoryIdList()) && (!wrapIn.getCategoryIdList().isEmpty())) {
					equals.put("categoryId", wrapIn.getCategoryIdList().get(0));
				}
				if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
					equals.put("creatorUid", wrapIn.getCreatorList().get(0));
				}
				if ((null != wrapIn.getStatusList()) && (!wrapIn.getStatusList().isEmpty())) {
					equals.put("docStatus", wrapIn.getStatusList().get(0));
				}
				if (StringUtils.isNotEmpty(wrapIn.getKey())) {
					String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
					if (StringUtils.isNotEmpty(key)) {
						// likes = new ListOrderedMap<>();
						likes.put("title", key);
					}
				}
				result = this.standardListNext(copier, id, count, "sequence", equals, null, likes, null, null, null, null,
						true, DESC);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Log,上一页.", response = WrapOutLog.class, request = JsonElement.class)
	@POST
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, JsonElement jsonElement) {
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();
		WrapInFilter wrapIn = null;
		Boolean check = true;
		BeanCopyTools<Log, WrapOutLog> copier = BeanCopyToolsBuilder.create(Log.class, WrapOutLog.class, null,
				WrapOutLog.Excludes);
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			e.printStackTrace();
		}
		if( check ){
			try {
				// equals = new ListOrderedMap<>();
				if ((null != wrapIn.getCategoryIdList()) && (!wrapIn.getCategoryIdList().isEmpty())) {
					equals.put("categoryId", wrapIn.getCategoryIdList().get(0));
				}
				if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
					equals.put("creatorUid", wrapIn.getCreatorList().get(0));
				}
				if ((null != wrapIn.getStatusList()) && (!wrapIn.getStatusList().isEmpty())) {
					equals.put("docStatus", wrapIn.getStatusList().get(0));
				}
				if (StringUtils.isNotEmpty(wrapIn.getKey())) {
					String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
					if (StringUtils.isNotEmpty(key)) {
						likes.put("title", key);
					}
				}
				result = this.standardListPrev(copier, id, count, "sequence", equals, null, likes, null, null, null, null,
						true, DESC);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}