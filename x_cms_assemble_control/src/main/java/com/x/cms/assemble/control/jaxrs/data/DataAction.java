package com.x.cms.assemble.control.jaxrs.data;

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

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.content.DataItem;

@Path("data")
public class DataAction extends AbstractJaxrsAction {
	
	private Logger logger = LoggerFactory.getLogger( DataAction.class );

	@HttpMethodDescribe(value = "根据ID获取文档数据.", response = JsonElement.class)
	@GET
	@Path("document/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithDocument(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowRead( business, effectivePerson, id );
			if( document == null ){
				result.setData(null);
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteGet().execute( effectivePerson, id );
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据路径获取指定document的data数据.", response = JsonElement.class)
	@GET
	@Path("document/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithDocumentWithPath0(@Context HttpServletRequest request, @PathParam("id") String id, 
			@PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowRead( business, effectivePerson, id );
			if( document == null ){
				result.setData(null);
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteGet().execute( effectivePerson, id, path0);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定document的data数据.", response = JsonElement.class)
	@GET
	@Path("document/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithDocumentWithPath1(@Context HttpServletRequest request, @PathParam("id") String id, 
			@PathParam("path0") String path0, @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowRead( business, effectivePerson, id );
			if( document == null ){
				result.setData(null);
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteGet().execute( effectivePerson, id, path0, path1);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定document的data数据.", response = JsonElement.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithDocumentWithPath2(@Context HttpServletRequest request, @PathParam("id") String id, 
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowRead( business, effectivePerson, id );
			if( document == null ){
				result.setData(null);
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteGet().execute( effectivePerson, id, path0, path1, path2);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定document的data数据.", response = JsonElement.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithDocumentWithPath3(@Context HttpServletRequest request, @PathParam("id") String id, 
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2, 
			@PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowRead( business, effectivePerson, id );
			if( document == null ){
				result.setData(null);
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteGet().execute( effectivePerson, id, path0, path1, path2, path3);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定document的data数据.", response = JsonElement.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithDocumentWithPath4(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowRead( business, effectivePerson, id );
			if( document == null ){
				result.setData(null);
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteGet().execute( effectivePerson, id, path0, path1, path2, path3, path4);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定document的data数据.", response = JsonElement.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithDocumentWithPath5(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowRead( business, effectivePerson, id );
			if( document == null ){
				result.setData(null);
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteGet().execute( effectivePerson, id, path0, path1, path2, path3, path4, path5);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定document的data数据.", response = JsonElement.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithDocumentWithPath6(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowRead( business, effectivePerson, id );
			if( document == null ){
				result.setData(null);
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteGet().execute( effectivePerson, id, path0, path1, path2, path3, path4, path5, path6);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定document的data数据.", response = JsonElement.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithDocumentWithPath7(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, @PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowRead( business, effectivePerson, id );
			if( document == null ){
				result.setData(null);
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteGet().execute( effectivePerson, id, path0, path1, path2, path3, path4, path5, path6, path7);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定document的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("document/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithDocument(@Context HttpServletRequest request, @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteUpdate().execute( effectivePerson, document, jsonElement );
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定document的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("document/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithDocumentWithPath0(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteUpdate().execute( effectivePerson, document, jsonElement, path0 );
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定document的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithDocumentWithPath1(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteUpdate().execute( effectivePerson, document, jsonElement, path0, path1);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定document的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithDocumentWithPath2(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteUpdate().execute( effectivePerson, document, jsonElement, path0, path1, path2);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定document的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithDocumentWithPath3(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteUpdate().execute( effectivePerson, document, jsonElement, path0, path1, path2, path3);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定document的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithDocumentWithPath4(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteUpdate().execute( effectivePerson, document, jsonElement, path0, path1, path2, path3, path4);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定document的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithDocumentWithPath5(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteUpdate().execute( effectivePerson, document, jsonElement, path0, path1, path2, path3, path4, path5);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定document的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithDocumentWithPath6(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteUpdate().execute( effectivePerson, document, jsonElement, path0, path1, path2, path3, path4, path5, path6);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "更新指定document的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithDocumentWithPath7(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteUpdate().execute( effectivePerson, document, jsonElement, path0, path1, path2, path3, path4, path5, path6, path7);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemUpdateException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("document/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithDocument( @Context HttpServletRequest request, @PathParam("id") String id, JsonElement jsonElement ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("[postWithDocument]document{'id':'"+id+"'} is null");
			}
			//第一次录入数据，没有path,并且dataitem表中无数据，所以记录方法特殊
			ItemConverter<DataItem> converter = new ItemConverter<>(DataItem.class);
			List<DataItem> adds = converter.disassemble( jsonElement );
			emc.beginTransaction(DataItem.class);
			for ( DataItem o : adds  ) {
				o.setAppId( document.getAppId() );
				o.setCategoryId( document.getCategoryId() );
				o.setDocId( document.getId() );
				o.setDocStatus( document.getDocStatus() );
				emc.persist( o, CheckPersistType.all );
			}
			emc.commit();
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("document/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithDocumentWithPath0(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			result = new ExcuteSave().execute(effectivePerson, business, document, jsonElement, path0);
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemSaveException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("document/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithDocumentWithPath1(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			result = new ExcuteSave().execute(effectivePerson, business, document, jsonElement, path0, path1);
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemSaveException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithDocumentWithPath2(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			result = new ExcuteSave().execute(effectivePerson, business, document, jsonElement, path0, path1, path2);
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemSaveException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithDocumentWithPath3(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			result = new ExcuteSave().execute(effectivePerson, business, document, jsonElement, path0, path1, path2, path3);
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemSaveException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithDocumentWithPath4(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			result = new ExcuteSave().execute(effectivePerson, business, document, jsonElement, path0, path1, path2, path3, path4);
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemSaveException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithDocumentWithPath5(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			result = new ExcuteSave().execute(effectivePerson, business, document, jsonElement, path0, path1, path2, path3, path4, path5);
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemSaveException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithDocumentWithPath6(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			result = new ExcuteSave().execute(effectivePerson, business, document, jsonElement, path0, path1, path2, path3, path4, path5, path6);
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemSaveException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithDocumentWithPath7(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, @PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			result = new ExcuteSave().execute(effectivePerson, business, document, jsonElement, path0, path1, path2, path3, path4, path5, path6, path7);
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemSaveException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("document/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithDocument(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteDelete().execute( effectivePerson, id );
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("document/{id}/{path0}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithDocumentWithPath0(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteDelete().execute( effectivePerson, id, path0);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithDocumentWithPath1(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteDelete().execute( effectivePerson, id, path0, path1);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}/{path2}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithDocumentWithPath2(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteDelete().execute( effectivePerson, id, path0, path1, path2);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithDocumentWithPath3(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteDelete().execute( effectivePerson, id, path0, path1, path2, path3);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithDocumentWithPath4(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteDelete().execute( effectivePerson, id, path0, path1, path2, path3, path4);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithDocumentWithPath5(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteDelete().execute( effectivePerson, id, path0, path1, path2, path3, path4, path5);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithDocumentWithPath6(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteDelete().execute( effectivePerson, id, path0, path1, path2, path3, path4, path5, path6);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "对指定的document删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithDocumentWithPath7(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, @PathParam("path7") String path7) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		Document document = null;
		Boolean check = true;		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			document = this.getDocumentCheckAllowSave( business, effectivePerson, id );
			if( document == null ){
				result = new ActionResult<>();
				Exception exception = new DocumentNotExistsException( id );
				result.error( exception );
			}
		} catch ( Exception e ) {
			result = new ActionResult<>();
			Exception exception = new DataItemPermissionException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			try{
				result = new ExcuteDelete().execute( effectivePerson, id, path0, path1, path2, path3, path4, path5, path6, path7);
			}catch( Exception e ){
				result = new ActionResult<>();
				Exception exception = new DataItemDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 判断用户是否有查询文件内容的权限，如果有，则返回文件数据对象
	 * @param business
	 * @param person
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	private Document getDocumentCheckAllowRead( Business business, EffectivePerson person, String docId ) throws Exception {
		Document document = null;
		//判断文档信息是否存在
		try{
			document = business.getDocumentFactory().get( docId );
		}catch(Exception e){
			
		}
		return document;
	}
	
	/**
	 * 判断用户是否有更新文档数据的权限，如果有，则返回文档对象
	 * @param business
	 * @param person
	 * @param docId
	 * @return
	 * @throws Exception 
	 */
	private Document getDocumentCheckAllowSave(Business business, EffectivePerson person, String docId ) throws Exception {
		Document document = null;
		//判断文档信息是否存在
		document = business.getDocumentFactory().get( docId );
		if( document != null ){
			//判断用户对文档的更新权限
			
		}
		return document;
	}
}