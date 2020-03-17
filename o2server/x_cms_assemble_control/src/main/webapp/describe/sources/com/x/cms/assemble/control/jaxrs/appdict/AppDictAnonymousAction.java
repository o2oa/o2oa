package com.x.cms.assemble.control.jaxrs.appdict;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.annotation.JaxrsParameterDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@JaxrsDescribe("数据字典操作")
@Path("anonymous/surface/appdict")
public class AppDictAnonymousAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(AppDictAnonymousAction.class);

	@JaxrsMethodDescribe(value = "获取单个数据字典以及数据字典数据.", action = ActionGet.class)
	@GET
	@Path("{appDictFlag}/appInfo/{appInfoFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("appDictFlag") String appDictFlag,
			@JaxrsParameterDescribe("栏目标识") @PathParam("appInfoFlag") String appInfoFlag) {
		ActionResult<WrapOutAppDict> result = new ActionResult<>();
		logger.debug("run get appDictFlag:{}, appInfoFlag:{}.", appDictFlag, appInfoFlag);
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson, appDictFlag, appInfoFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取AppInfo的数据字典列表.", action = ActionListWithAppInfo.class)
	@GET
	@Path("list/appInfo/{appInfoFlag}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listWithAppInfo(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request,
			@JaxrsParameterDescribe("栏目标识") @PathParam("appInfoFlag") String appInfoFlag) {
		ActionResult<List<WrapOutAppDict>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListWithAppInfo().execute(appInfoFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取AppInfo下的数据字典数据.", action = ActionGetData.class)
	@GET
	@Path("{appDictFlag}/appInfo/{appInfoFlag}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getData(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("appDictFlag") String appDictFlag,
			@JaxrsParameterDescribe("栏目标识") @PathParam("appInfoFlag") String appInfoFlag) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetData().execute(appDictFlag, appInfoFlag);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取AppInfo下的数据字典数据.", action = ActionGetDataPath0.class)
	@GET
	@Path("{appDictFlag}/appInfo/{appInfoFlag}/{path0}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath0(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("appDictFlag") String appDictFlag,
			@JaxrsParameterDescribe("栏目标识") @PathParam("appInfoFlag") String appInfoFlag,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath0().execute(appDictFlag, appInfoFlag, path0);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取AppInfo下的数据字典数据.", action = ActionGetDataPath1.class)
	@GET
	@Path("{appDictFlag}/appInfo/{appInfoFlag}/{path0}/{path1}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath1(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("appDictFlag") String appDictFlag,
			@JaxrsParameterDescribe("栏目标识") @PathParam("appInfoFlag") String appInfoFlag,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath1().execute(appDictFlag, appInfoFlag, path0, path1);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取AppInfo下的数据字典数据.", action = ActionGetDataPath2.class)
	@GET
	@Path("{appDictFlag}/appInfo/{appInfoFlag}/{path0}/{path1}/{path2}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath2(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("appDictFlag") String appDictFlag,
			@JaxrsParameterDescribe("栏目标识") @PathParam("appInfoFlag") String appInfoFlag,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath2().execute(appDictFlag, appInfoFlag, path0, path1, path2);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取AppInfo下的数据字典数据.", action = ActionGetDataPath3.class)
	@GET
	@Path("{appDictFlag}/appInfo/{appInfoFlag}/{path0}/{path1}/{path2}/{path3}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath3(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("appDictFlag") String appDictFlag,
			@JaxrsParameterDescribe("栏目标识") @PathParam("appInfoFlag") String appInfoFlag,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath3().execute(appDictFlag, appInfoFlag, path0, path1, path2, path3);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取AppInfo下的数据字典数据.", action = ActionGetDataPath4.class)
	@GET
	@Path("{appDictFlag}/appInfo/{appInfoFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath4(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("appDictFlag") String appDictFlag,
			@JaxrsParameterDescribe("栏目标识") @PathParam("appInfoFlag") String appInfoFlag,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath4().execute(appDictFlag, appInfoFlag, path0, path1, path2, path3,
					path4);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取AppInfo下的数据字典数据.", action = ActionGetDataPath5.class)
	@GET
	@Path("{appDictFlag}/appInfo/{appInfoFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath5(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("appDictFlag") String appDictFlag,
			@JaxrsParameterDescribe("栏目标识") @PathParam("appInfoFlag") String appInfoFlag,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath5().execute(appDictFlag, appInfoFlag, path0, path1, path2, path3,
					path4, path5);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取AppInfo下的数据字典数据.", action = ActionGetDataPath6.class)
	@GET
	@Path("{appDictFlag}/appInfo/{appInfoFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath6(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("appDictFlag") String appDictFlag,
			@JaxrsParameterDescribe("栏目标识") @PathParam("appInfoFlag") String appInfoFlag,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath6().execute(appDictFlag, appInfoFlag, path0, path1, path2, path3,
					path4, path5, path6);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "根据路径获取AppInfo下的数据字典数据.", action = ActionGetDataPath7.class)
	@GET
	@Path("{appDictFlag}/appInfo/{appInfoFlag}/{path0}/{path1}/{path2}/{path3}/{path4}/{path5}/{path6}/{path7}/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getDataPath7(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("数据字典标识") @PathParam("appDictFlag") String appDictFlag,
			@JaxrsParameterDescribe("栏目标识") @PathParam("appInfoFlag") String appInfoFlag,
			@JaxrsParameterDescribe("0级路径") @PathParam("path0") String path0,
			@JaxrsParameterDescribe("1级路径") @PathParam("path1") String path1,
			@JaxrsParameterDescribe("2级路径") @PathParam("path2") String path2,
			@JaxrsParameterDescribe("3级路径") @PathParam("path3") String path3,
			@JaxrsParameterDescribe("4级路径") @PathParam("path4") String path4,
			@JaxrsParameterDescribe("5级路径") @PathParam("path5") String path5,
			@JaxrsParameterDescribe("6级路径") @PathParam("path6") String path6,
			@JaxrsParameterDescribe("7级路径") @PathParam("path7") String path7) {
		ActionResult<JsonElement> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetDataPath7().execute(appDictFlag, appInfoFlag, path0, path1, path2, path3,
					path4, path5, path6, path7);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}