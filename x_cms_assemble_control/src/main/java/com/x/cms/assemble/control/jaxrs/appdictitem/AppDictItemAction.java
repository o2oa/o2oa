package com.x.cms.assemble.control.jaxrs.appdictitem;

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
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.entity.item.ItemType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("appdictitem")
public class AppDictItemAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( AppDictItemAction.class );
	private static String cacheNamePrefix = ".appDictId.";
	private Ehcache cache = ApplicationCache.instance().getCache( AppDictItem.class );

	@HttpMethodDescribe(value = "根据路径获取App数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItem(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId, @PathParam("appId") String appId) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result.setData( this.getWithAppDictId( appId, appDictId ));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath0(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result.setData( this.getWithAppDictId(appId, appDictId, path0) );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath1(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result.setData(this.getWithAppDictId(appId, appDictId, path0, path1));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath2(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result.setData(this.getWithAppDictId(appId, appDictId, path0, path1, path2));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath3(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result.setData(this.getWithAppDictId(appId, appDictId, path0, path1, path2, path3));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath4(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result.setData(this.getWithAppDictId(appId, appDictId, path0, path1, path2, path3, path4));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath5(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result.setData(this.getWithAppDictId(appId, appDictId, path0, path1, path2, path3, path4, path5));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath6(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result.setData(this.getWithAppDictId(appId, appDictId, path0, path1, path2, path3, path4, path5, path6));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据路径获取App 数据字典数据.", response = JsonElement.class)
	@GET
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getWithAppDictItemWithPath7(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			@PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		try {
			result.setData(this.getWithAppDictId(appId, appDictId, path0, path1, path2, path3, path4, path5, path6, path7));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private JsonElement getWithAppDictId(String appId, String uniqueName, String... paths) throws Exception {
		JsonElement jsonElement = null;
		try {
			AppDict dict = getAppDict(appId, uniqueName);
			if (null == dict) {
				throw new Exception("appDictId unique name :" + uniqueName + " not existed with appId{id:" + appId + "}.");
			}
			String cacheKey = appId + "." + uniqueName + ".path." + StringUtils.join(paths, ".");
			Element element = cache.get( cacheKey);
			if (null != element) {
				jsonElement = (JsonElement) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					List<AppDictItem> list = business.getAppDictItemFactory().listWithAppDictWithPath(dict.getId(), paths);
					ItemConverter<AppDictItem> converter = new ItemConverter<>(AppDictItem.class);
					jsonElement = converter.assemble(list, paths.length);
					cache.put(new Element(cacheKey, jsonElement));
				}
			}
			return jsonElement;
		} catch (Exception e) {
			throw new Exception("getWithAppDictItem error.", e);
		}
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath0(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.putWithAppDictItem(appId, appDictId, jsonElement, path0);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath1(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.putWithAppDictItem(appId, appDictId, jsonElement, path0, path1);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath2(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.putWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath3(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.putWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath4(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.putWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3, path4);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath5(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.putWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath6(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.putWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5, path6);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径更新数据字典局部数据.", response = WrapOutId.class)
	@PUT
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putWithAppDictItemWithPath7(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			@PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.putWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5, path6, path7);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private void putWithAppDictItem(String appId, String uniqueName, JsonElement jsonElement, String... paths) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppDict dict = getAppDict(appId, uniqueName);
			if (null == dict) {
				throw new Exception("appDictId unique name :" + uniqueName + " not existed with appId{id:" + appId + "}.");
			}
			Business business = new Business(emc);
			ItemConverter<AppDictItem> converter = new ItemConverter<>(AppDictItem.class);
			List<AppDictItem> exists = business.getAppDictItemFactory().listWithAppDictWithPath(dict.getId(), paths);
			if (exists.isEmpty()) {
				throw new Exception("appDict{'uniqueName':" + uniqueName + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
			}
			emc.beginTransaction(AppDictItem.class);
			List<AppDictItem> currents = converter.disassemble(jsonElement, paths);
			List<AppDictItem> removes = converter.subtract(exists, currents);
			List<AppDictItem> adds = converter.subtract(currents, exists);
			for (AppDictItem o : removes) {
				emc.remove(o);
			}
			for (AppDictItem o : adds) {
				o.setAppDictId(dict.getId());
				emc.persist(o);
			}
			emc.commit();
			ApplicationCache.notify( AppDict.class );
			ApplicationCache.notify( AppDictItem.class );
		} catch (Exception e) {
			throw new Exception("putWithAppDictItemWithPath error.", e);
		}
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath0(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.postWithAppDictItem(appId, appDictId, jsonElement, path0);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath1(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.postWithAppDictItem(appId, appDictId, jsonElement, path0, path1);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath2(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.postWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath3(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.postWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath4(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.postWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3, path4);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath5(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.postWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath6(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.postWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5, path6);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径添加新的局部数据.", response = WrapOutId.class)
	@POST
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postWithAppDictItemWithPath7(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			@PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.postWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5, path6, path7);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private void postWithAppDictItem(String appId, String uniqueName, JsonElement jsonElement, String... paths) throws Exception {
		try {
			AppDict dict = getAppDict( appId, uniqueName );
			if (null == dict) {
				throw new Exception("appDictId unique name :" + uniqueName + " not existed with appId{id:" + appId + "}.");
			}
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				String[] parentPaths = new String[] { "", "", "", "", "", "", "", "" };
				String[] cursorPaths = new String[] { "", "", "", "", "", "", "", "" };
				for (int i = 0; i < paths.length - 1; i++) {
					parentPaths[i] = paths[i];
					cursorPaths[i] = paths[i];
				}
				cursorPaths[paths.length - 1] = paths[paths.length - 1];
				AppDictItem parent = business.getAppDictItemFactory().getWithAppDictWithPath(dict.getId(), parentPaths[0], parentPaths[1], parentPaths[2],
						parentPaths[3], parentPaths[4], parentPaths[5], parentPaths[6], parentPaths[7]);
				if (null == parent) {
					throw new Exception("parent not existed.");
				}
				AppDictItem cursor = business.getAppDictItemFactory().getWithAppDictWithPath(dict.getId(), cursorPaths[0], cursorPaths[1], cursorPaths[2],
						cursorPaths[3], cursorPaths[4], cursorPaths[5], cursorPaths[6], cursorPaths[7]);
				ItemConverter<AppDictItem> converter = new ItemConverter<>(AppDictItem.class);
				emc.beginTransaction(AppDictItem.class);
				if ((null != cursor) && cursor.getItemType().equals(ItemType.a)) {
					/* 向数组里面添加一个成员对象 */
					Integer index = business.getAppDictItemFactory().getArrayLastIndexWithAppDictWithPath(dict.getId(), paths);
					/* 新的路径开始 */
					String[] ps = new String[paths.length + 1];
					for (int i = 0; i < paths.length; i++) {
						ps[i] = paths[i];
					}
					ps[paths.length] = Integer.toString(index + 1);
					List<AppDictItem> adds = converter.disassemble(jsonElement, ps);
					for (AppDictItem o : adds) {
						o.setAppDictId(dict.getId());
						emc.persist(o);
					}
				} else if ((cursor == null) && parent.getItemType().equals(ItemType.o)) {
					/* 向parent对象添加一个属性值 */
					List<AppDictItem> adds = converter.disassemble(jsonElement, paths);
					for (AppDictItem o : adds) {
						o.setAppDictId(dict.getId());
						emc.persist(o);
					}
				} else {
					throw new Exception("unexpected post with uniqueName{'uniqueName':" + uniqueName + "} path:" + StringUtils.join(paths, ".") + "json:" + jsonElement);
				}
				emc.commit();
				ApplicationCache.notify( AppDict.class );
				ApplicationCache.notify( AppDictItem.class );
			}
		} catch (Exception e) {
			throw new Exception("postWithAppDictItem error.", e);
		}
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath0(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.deleteWithAppDictItem(appId, appDictId, jsonElement, path0);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath1(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.deleteWithAppDictItem(appId, appDictId, jsonElement, path0, path1);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath2(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.deleteWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath3(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.deleteWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath4(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.deleteWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3, path4);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath5(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.deleteWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath6(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.deleteWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5, path6);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据字典ID和路径删除数据字典局部数据.", response = WrapOutId.class)
	@DELETE
	@Path("{appDictId}/app/{appId}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteWithAppDictItemWithPath7(@Context HttpServletRequest request, @PathParam("appDictId") String appDictId,
			@PathParam("appId") String appId, @PathParam("path0") String path0, @PathParam("path1") String path1, @PathParam("path2") String path2,
			@PathParam("path3") String path3, @PathParam("path4") String path4, @PathParam("path5") String path5, @PathParam("path6") String path6,
			@PathParam("path7") String path7, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		try {
			this.deleteWithAppDictItem(appId, appDictId, jsonElement, path0, path1, path2, path3, path4, path5, path6, path7);
			result.setData(new WrapOutId(appDictId));
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private void deleteWithAppDictItem(String appId, String appDictId, JsonElement jsonElement, String... paths) throws Exception {
		try {
			AppDict dict = getAppDict(appId, appDictId);
			if (null == dict) {
				throw new Exception("appDictId unique name :" + appDictId + " not existed with appId{id:" + appId + "}.");
			}
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<AppDictItem> exists = business.getAppDictItemFactory().listWithAppDictWithPath(dict.getId(), paths);
				if (exists.isEmpty()) {
					throw new Exception("appDictId{id:" + appDictId + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
				}
				emc.beginTransaction(AppDictItem.class);
				for (AppDictItem o : exists) {
					emc.remove(o);
				}
				if (NumberUtils.isNumber(paths[paths.length - 1])) {
					int position = paths.length - 1;
					for (AppDictItem o : business.getAppDictItemFactory().listWithAppDictWithPathWithAfterLocation(dict.getId(), NumberUtils.toInt(paths[position]),
							paths)) {
						o.path(Integer.toString(o.pathLocation(position) - 1), position);
					}
				}
				emc.commit();
				ApplicationCache.notify( AppDict.class );
				ApplicationCache.notify( AppDictItem.class );
			}
		} catch (Exception e) {
			throw new Exception("deleteWithAppDictItem error.", e);
		}
	}

	private AppDict getAppDict(String appId, String uniqueName) throws Exception {
		try {
			String cacheKey = appId + "." + uniqueName;
			Element element = cache.get( cacheKey );
			if (element != null) {
				return (AppDict) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					//获取到AppDict的ID
					String id = business.getAppDictFactory().getWithAppWithUniqueName( appId, uniqueName );
					if (null != id) {
						AppDict dict = business.entityManagerContainer().find(id, AppDict.class);
						if (null != dict) {
							cache.put(new Element(cacheKey, dict));
							return dict;
						}
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("get appDictId unique name:" + uniqueName + " with appId{id:" + appId + "} error.");
		}
		return null;
	}
}