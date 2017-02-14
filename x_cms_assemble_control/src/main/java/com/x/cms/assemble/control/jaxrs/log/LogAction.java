package com.x.cms.assemble.control.jaxrs.log;

import java.util.Collections;
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

import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.LogFactory;
import com.x.cms.core.entity.Log;

@Path("log")
public class LogAction extends StandardJaxrsAction {

	BeanCopyTools<Log, WrapOutLog> copier = BeanCopyToolsBuilder.create(Log.class, WrapOutLog.class, null,
			WrapOutLog.Excludes);

	@HttpMethodDescribe(value = "获取全部的应用日志列表", response = WrapOutLog.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllLog(@Context HttpServletRequest request) {
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		List<WrapOutLog> wraps = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			LogFactory logFactory = business.getLogFactory();
			List<String> ids = logFactory.listAll();// 获取所有应用日志列表
			List<Log> logList = logFactory.list(ids);// 查询ID IN ids 的所有应用日志信息列表
			wraps = copier.copy(logList);// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort(wraps);// 对查询的列表进行排序
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定操作对象级别的操作日志信息列表", response = WrapOutLog.class)
	@GET
	@Path("list/level/{operationLevel}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLogByOperationLevel(@Context HttpServletRequest request,
			@PathParam("operationLevel") String operationLevel) {
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		List<WrapOutLog> wraps = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			LogFactory logFactory = business.getLogFactory();
			List<String> ids = logFactory.listByOperationLevel(operationLevel);// 获取指定文档的操作日志列表
			List<Log> logList = logFactory.list(ids);// 查询ID IN ids 的所有应用日志信息列表
			wraps = copier.copy(logList);// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort(wraps);// 对查询的列表进行排序
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定应用的操作日志信息列表", response = WrapOutLog.class)
	@GET
	@Path("list/app/{appId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLogByAppId(@Context HttpServletRequest request, @PathParam("appId") String appId) {
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		List<WrapOutLog> wraps = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			LogFactory logFactory = business.getLogFactory();
			List<String> ids = logFactory.listByObject(appId, null, null, null);// 获取指定应用的操作日志列表
			List<Log> logList = logFactory.list(ids);// 查询ID IN ids 的所有应用日志信息列表
			wraps = copier.copy(logList);// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort(wraps);// 对查询的列表进行排序
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定分类的操作日志信息列表", response = WrapOutLog.class)
	@GET
	@Path("list/catagory/{catagoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLogByCatagoryId(@Context HttpServletRequest request,
			@PathParam("catagoryId") String catagoryId) {
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		List<WrapOutLog> wraps = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			LogFactory logFactory = business.getLogFactory();
			List<String> ids = logFactory.listByObject(null, catagoryId, null, null);// 获取指定应用的操作日志列表
			List<Log> logList = logFactory.list(ids);// 查询ID IN ids 的所有应用日志信息列表
			wraps = copier.copy(logList);// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort(wraps);// 对查询的列表进行排序
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "获取指定文档的操作日志信息列表", response = WrapOutLog.class)
	@GET
	@Path("list/document/{documentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listLogByDocumentId(@Context HttpServletRequest request,
			@PathParam("documentId") String documentId) {
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		List<WrapOutLog> wraps = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			LogFactory logFactory = business.getLogFactory();
			List<String> ids = logFactory.listByObject(null, null, documentId, null);// 获取指定应用的操作日志列表
			List<Log> logList = logFactory.list(ids);// 查询ID IN ids 的所有应用日志信息列表
			wraps = copier.copy(logList);// 将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort(wraps);// 对查询的列表进行排序
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取log对象.", response = WrapOutLog.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutLog> result = new ActionResult<>();
		WrapOutLog wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Log log = business.getLogFactory().get(id);
			if (null == log) {
				throw new Exception("log{id:" + id + "} 信息不存在.");
			}
			// 如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
			BeanCopyTools<Log, WrapOutLog> copier = BeanCopyToolsBuilder.create(Log.class, WrapOutLog.class, null,
					WrapOutLog.Excludes);
			wrap = new WrapOutLog();
			copier.copy(log, wrap);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Log,下一页.", response = WrapOutLog.class, request = WrapInFilter.class)
	@POST
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();
		try {
			// equals = new ListOrderedMap<>();
			if ((null != wrapIn.getCatagoryIdList()) && (!wrapIn.getCatagoryIdList().isEmpty())) {
				equals.put("catagoryId", wrapIn.getCatagoryIdList().get(0).getValue());
			}
			if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
				equals.put("creatorUid", wrapIn.getCreatorList().get(0).getValue());
			}
			if ((null != wrapIn.getStatusList()) && (!wrapIn.getStatusList().isEmpty())) {
				equals.put("docStatus", wrapIn.getStatusList().get(0).getValue());
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
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件的Log,上一页.", response = WrapOutLog.class, request = WrapInFilter.class)
	@POST
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutLog>> result = new ActionResult<>();
		EqualsTerms equals = new EqualsTerms();
		LikeTerms likes = new LikeTerms();
		try {
			// equals = new ListOrderedMap<>();
			if ((null != wrapIn.getCatagoryIdList()) && (!wrapIn.getCatagoryIdList().isEmpty())) {
				equals.put("catagoryId", wrapIn.getCatagoryIdList().get(0).getValue());
			}
			if ((null != wrapIn.getCreatorList()) && (!wrapIn.getCreatorList().isEmpty())) {
				equals.put("creatorUid", wrapIn.getCreatorList().get(0).getValue());
			}
			if ((null != wrapIn.getStatusList()) && (!wrapIn.getStatusList().isEmpty())) {
				equals.put("docStatus", wrapIn.getStatusList().get(0).getValue());
			}
			if (StringUtils.isNotEmpty(wrapIn.getKey())) {
				String key = StringUtils.trim(StringUtils.replace(wrapIn.getKey(), "\u3000", " "));
				if (StringUtils.isNotEmpty(key)) {
					// likes = new ListOrderedMap<>();
					likes.put("title", key);
					// likes.put("opinion", key);
				}
			}
			result = this.standardListPrev(copier, id, count, "sequence", equals, null, likes, null, null, null, null,
					true, DESC);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}