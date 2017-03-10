package com.x.cms.assemble.control.jaxrs.form;

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
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.core.entity.element.Form;

@Path("form")
public class FormAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( FormAction.class );

	@HttpMethodDescribe(value = "获取全部的表单模板列表", response = WrapOutForm.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllForm(@Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutSimpleForm>> result = new ActionResult<>();
		try {
			result = new ExcuteListAll().execute( request, effectivePerson );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ServiceLogicException( e, "系统在查询所有CMS表单时发生异常。" );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定应用的全部表单模板信息列表", response = WrapOutForm.class)
	@GET
	@Path("list/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listFormByAppId( @Context HttpServletRequest request, @PathParam("appId") String appId ) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<List<WrapOutSimpleForm>> result = new ActionResult<>();
		try {
			result = new ExcuteListByApp().execute( request, effectivePerson, appId );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ServiceLogicException( e, "系统在根据栏目ID查询表单时发生异常。" );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取form对象.", response = WrapOutForm.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		ActionResult<WrapOutForm> result = new ActionResult<>();
		try {
			result = new ExcuteGet().execute( request, effectivePerson, id );
		} catch (Exception e) {
			result = new ActionResult<>();
			Exception exception = new ServiceLogicException( e, "系统在根据ID查询表单时发生异常。" );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "创建Form应用信息对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInForm wrapIn = null;
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInForm.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		if( check ){
			try {
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新Form信息对象.", request = JsonElement.class, response = WrapOutId.class)
	@PUT
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(@Context HttpServletRequest request, @PathParam("id") String id, JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson( request );
		WrapInForm wrapIn = null;
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInForm.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		
		if( check ){
			try {
				wrapIn.setId(id);
				result = new ExcuteSave().execute( request, effectivePerson, wrapIn );
			} catch (Exception e) {
				result = new ActionResult<>();
				result.error( e );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除Form应用信息对象.", response = WrapOutId.class)
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
			Exception exception = new ServiceLogicException( e, "系统在根据ID删除表单时发生异常。" );
			result.error( exception );
			logger.error( exception, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Form,下一页.", response = WrapOutForm.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/{id}/next/{count}/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("count") Integer count, @PathParam("appId") Integer appId, JsonElement jsonElement) {
		ActionResult<List<WrapOutForm>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();
		WrapInFilter wrapIn = null;
		Boolean check = true;
		BeanCopyTools<Form, WrapOutForm> copier = BeanCopyToolsBuilder.create(Form.class, WrapOutForm.class, null, WrapOutForm.Excludes);

		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		
		if( check ){
			try {
				// equals = new ListOrderedMap<>();
				equals.put("appId", appId);
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
				logger.debug("call method [standardListNext] to get resultList[]");
				result = this.standardListNext(copier, id, count, "sequence", equals, null, likes, null, null, null, null, true, DESC);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Form,上一页.", response = WrapOutForm.class, request = JsonElement.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter( @Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, @PathParam("appId") Integer appId, JsonElement jsonElement) {
		ActionResult<List<WrapOutForm>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();
		WrapInFilter wrapIn = null;
		Boolean check = true;
		BeanCopyTools<Form, WrapOutForm> copier = BeanCopyToolsBuilder.create(Form.class, WrapOutForm.class, null, WrapOutForm.Excludes);
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilter.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( exception, currentPerson, request, null);
		}
		if( check ){
			try {
				equals.put("appId", appId);
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
				logger.debug("call method [standardListPrev] to get resultList[]");
				result = this.standardListPrev(copier, id, count, "sequence", equals, null, likes, null, null, null, null, true, DESC);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}