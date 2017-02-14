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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.entity.item.ItemType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.content.DataItem;

@Path("data")
public class DataAction extends AbstractJaxrsAction {
	
	private Logger logger = LoggerFactory.getLogger( DataAction.class );
	
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
	private Document getDocumentCheckAllowSave(Business business, EffectivePerson person, String docId) throws Exception {
		Document document = null;
		//判断文档信息是否存在
		document = business.getDocumentFactory().get( docId );
		if( document != null ){
			//判断用户对文档的更新权限
			
		}
		return document;
	}
	
	/**
	 * 从数据库中根据docId和路径来获取文档的内容
	 * @param business
	 * @param docId
	 * @param paths
	 * @return
	 * @throws Exception
	 */
	private JsonElement getData( Business business, String docId, String... paths ) throws Exception {
		JsonElement jsonElement = null;
		try {
			List<DataItem> list = business.getDataItemFactory().listWithDocIdWithPath( docId, paths );
			ItemConverter<DataItem> converter = new ItemConverter<>( DataItem.class );
			logger.debug( "list.size = " + list.size());
			if( paths != null ){
				jsonElement = converter.assemble( list );
			}else{
				jsonElement = converter.assemble( list );
			}
			return jsonElement;
		} catch (Exception e) {
			throw new Exception( "getData error.", e );
		}
	}

	@HttpMethodDescribe(value = "根据ID获取文档数据.", response = JsonElement.class)
	@GET
	@Path("document/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithDocument(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowRead( business, currentPerson, id );
			if( document != null ){				
				result.setData( this.getData( business, document.getId() ) );
			}else{
				logger.debug( "[getWithDocument]文档{'id':'"+id+"'}不存在......" );
				result.setData(null);
			}
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowRead( business, currentPerson, id );
			result.setData( this.getData(business, document.getId(), path0) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowRead( business, currentPerson, id );
			result.setData( this.getData(business, document.getId(), path0, path1) );
		} catch ( Throwable th ) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowRead( business, currentPerson, id );
			result.setData( this.getData(business, document.getId(), path0, path1, path2) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowRead( business, currentPerson, id );
			result.setData( this.getData( business, document.getId(), path0, path1, path2, path3 ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowRead( business, currentPerson, id );
			result.setData( this.getData( business, document.getId(), path0, path1, path2, path3, path4 ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowRead( business, currentPerson, id );
			result.setData(this.getData(business, document.getId(), path0, path1, path2, path3, path4, path5));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowRead( business, currentPerson, id );
			result.setData( this.getData(business, document.getId(), path0, path1, path2, path3, path4, path5, path6) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取指定document的data数据.", response = JsonElement.class)
	@GET
	@Path("document/{id}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithDocumentWithPath7(@Context HttpServletRequest request, @PathParam("docId") String docId,
			@PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5,
			@PathParam("path6") String path6, @PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowRead( business, currentPerson, docId );
			result.setData( this.getData( business, document.getId(), path0, path1, path2, path3, path4, path5, path6, path7) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	/**
	 * 更新文档数据的操作
	 * @param document
	 * @param jsonElement
	 * @param paths
	 * @throws Exception
	 */
	private void putData( Document document, JsonElement jsonElement, String... paths) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ItemConverter<DataItem> converter = new ItemConverter<>(DataItem.class);
			List<DataItem> exists = business.getDataItemFactory().listWithDocIdWithPath( document.getId(), paths );
			if (exists.isEmpty()) {
				throw new Exception( "data{document:" + document.getId() + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
			}
			List<DataItem> currents = converter.disassemble( jsonElement, paths );
			List<DataItem> removes = converter.subtract( exists, currents );
			List<DataItem> adds = converter.subtract( currents, exists );
			emc.beginTransaction(DataItem.class);
			for (DataItem o : removes) {
				emc.remove(o);
			}
			for ( DataItem o : adds ) {
				o.setDocId(document.getId());
				o.setAppId(document.getAppId());
				o.setCatagoryId( document.getCatagoryId() );
				o.setDocStatus( document.getDocStatus() );
				emc.persist( o, CheckPersistType.all );
			}
			emc.commit();
		} catch (Exception e) {
			throw new Exception("putData error.", e);
		}
	}

	@HttpMethodDescribe(value = "更新指定document的Data数据.", response = WrapOutId.class)
	@PUT
	@Path("document/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithDocument(@Context HttpServletRequest request, @PathParam("id") String id,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.putData( document, jsonElement );
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.putData( document, jsonElement );
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.putData( document, jsonElement, path0, path1);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.putData( document, jsonElement, path0, path1, path2);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.putData( document, jsonElement, path0, path1, path2, path3);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.putData( document, jsonElement, path0, path1, path2, path3, path4);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.putData( document, jsonElement, path0, path1, path2, path3, path4, path5);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.putData( document, jsonElement, path0, path1, path2, path3, path4, path5, path6);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.putData( document, jsonElement, path0, path1, path2, path3, path4, path5, path6, path7);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	/**
	 * 新增，保存文档数据信息
	 * @param business
	 * @param document
	 * @param jsonElement
	 * @param paths
	 * @throws Exception
	 */
	private void postData( Business business, Document document, JsonElement jsonElement, String... paths) throws Exception {
		try {
			String[] parentPaths = new String[] { "", "", "", "", "", "", "", "" };
			String[] cursorPaths = new String[] { "", "", "", "", "", "", "", "" };
			for (int i = 0; paths != null && i < paths.length - 1; i++) {
				parentPaths[i] = paths[i];
				cursorPaths[i] = paths[i];
			}
			cursorPaths[paths.length - 1] = paths[paths.length - 1];		
			DataItem parent = business.getDataItemFactory().getWithDocIdWithPath( document, parentPaths[0], parentPaths[1], parentPaths[2], parentPaths[3], parentPaths[4], parentPaths[5], parentPaths[6], parentPaths[7]);
			if (null == parent) {
				throw new Exception("parent not existed.");
			}
			DataItem cursor = business.getDataItemFactory().getWithDocIdWithPath( document, cursorPaths[0], cursorPaths[1], cursorPaths[2], cursorPaths[3], cursorPaths[4], cursorPaths[5], cursorPaths[6], cursorPaths[7]);
			ItemConverter<DataItem> converter = new ItemConverter<>(DataItem.class);
			business.entityManagerContainer().beginTransaction(DataItem.class);
			if ((null != cursor) && cursor.getItemType().equals(ItemType.a)) {
				/* 向数组里面添加一个成员对象 */
				Integer index = business.getDataItemFactory().getArrayLastIndexWithDocIdWithPath( document.getId(), paths );
				/* 新的路径开始 */
				String[] ps = new String[paths.length + 1];
				for (int i = 0; i < paths.length; i++) {
					ps[i] = paths[i];
				}
				ps[paths.length] = Integer.toString(index + 1);
				List<DataItem> adds = converter.disassemble(jsonElement, ps);
				for (DataItem o : adds) {
					o.setAppId(document.getAppId());
					o.setCatagoryId(document.getCatagoryId());
					o.setDocStatus( document.getDocStatus() );
					business.entityManagerContainer().persist(o);
				}
			} else if ((cursor == null) && parent.getItemType().equals(ItemType.o)) {
				/* 向parent对象添加一个属性值 */
				List<DataItem> adds = converter.disassemble(jsonElement, paths);
				for (DataItem o : adds) {
					o.setAppId( document.getAppId() );
					o.setCatagoryId( document.getCatagoryId() );
					o.setDocStatus( document.getDocStatus() );
					business.entityManagerContainer().persist(o);
				}
			} else {
				throw new Exception("unexpected post data with document" + document + ".path:" + StringUtils.join(paths, ".") + "json:" + jsonElement);
			}
			business.entityManagerContainer().commit();
		} catch (Exception e) {
			throw new Exception("postWithApplicationDict error.", e);
		}
	}

	@HttpMethodDescribe(value = "对指定的document添加局部data数据.", response = WrapOutId.class)
	@POST
	@Path("document/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithDocument(@Context HttpServletRequest request, @PathParam("id") String id, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug( "[postWithDocument]用户[" + currentPerson.getName() + "]尝试向数据库保存新的文档数据，documentId=" + id );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("[postWithDocument]document{'id':'"+id+"'} is null");
			}
			//this.postData(business, document, jsonElement);
			
			//第一次录入数据，没有path,并且dataitem表中无数据，所以记录方法特殊
			ItemConverter<DataItem> converter = new ItemConverter<>(DataItem.class);
			List<DataItem> adds = converter.disassemble( jsonElement );
			emc.beginTransaction(DataItem.class);
			for ( DataItem o : adds  ) {
				o.setAppId( document.getAppId() );
				o.setCatagoryId( document.getCatagoryId() );
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.postData(business, document, jsonElement, path0);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.postData(business, document, jsonElement, path0, path1);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.postData(business, document, jsonElement, path0, path1, path2);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.postData(business, document, jsonElement, path0, path1, path2, path3);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.postData(business, document, jsonElement, path0, path1, path2, path3, path4);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.postData(business, document, jsonElement, path0, path1, path2, path3, path4, path5);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.postData(business, document, jsonElement, path0, path1, path2, path3, path4, path5, path6);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.postData(business, document, jsonElement, path0, path1, path2, path3, path4, path5, path6, path7);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private void deleteData(Business business, String job, String... paths) throws Exception {
		try {
			List<DataItem> exists = business.getDataItemFactory().listWithDocIdWithPath(job, paths);
			if (exists.isEmpty()) {
				throw new Exception(
						"data{job:" + job + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
			}
			business.entityManagerContainer().beginTransaction( DataItem.class);
			for (DataItem o : exists) {
				business.entityManagerContainer().remove(o);
			}
			if (NumberUtils.isNumber(paths[paths.length - 1])) {
				int position = paths.length - 1;
				for (DataItem o : business.getDataItemFactory().listWithDocIdWithPathWithAfterLocation(job, NumberUtils.toInt(paths[position]), paths)) {
					o.path(Integer.toString(o.pathLocation(position) - 1), position);
				}
			}
			business.entityManagerContainer().commit();
		} catch (Exception e) {
			throw new Exception("deleteWithApplicationDict error.", e);
		}
	}

	@HttpMethodDescribe(value = "对指定的document删除局部data数据.", response = WrapOutId.class)
	@DELETE
	@Path("document/{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithDocument(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.deleteData(business, document.getId());
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.deleteData(business, document.getId(), path0);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.deleteData(business, document.getId(), path0, path1);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.deleteData(business, document.getId(), path0, path1, path2);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.deleteData(business, document.getId(), path0, path1, path2, path3);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.deleteData(business, document.getId(), path0, path1, path2, path3, path4);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.deleteData(business, document.getId(), path0, path1, path2, path3, path4, path5);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.deleteData(business, document.getId(), path0, path1, path2, path3, path4, path5, path6);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			//判断用户是否有更新文档数据的权限，如果有，则返回文档对象
			Document document = this.getDocumentCheckAllowSave( business, currentPerson, id );
			if( document == null ){
				throw new Exception("document{'id':'"+id+"'} is null");
			}
			this.deleteData(business, document.getId(), path0, path1, path2, path3, path4, path5, path6, path7);
			result.setData( new WrapOutId( id ) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}