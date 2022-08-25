package com.x.program.center.jaxrs.config;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.JaxrsDescribe;
import com.x.base.core.project.annotation.JaxrsMethodDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpMediaType;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ConfigAction", description = "配置接口.")
@JaxrsDescribe("配置接口.")
@Path("config")
public class ConfigAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigAction.class);
	private static final String OPERATIONID_PREFIX = "ConfigAction::";

	@JaxrsMethodDescribe(value = "获取设置.", action = ActionGet.class)
	@GET
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void get(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionGet.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGet().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "获取人员设置.", action = ActionGetPerson.class)
	@GET
	@Path("person")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionGetPerson.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetPerson().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新人员设置.", action = ActionSetPerson.class)
	@PUT
	@Path("person")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setPerson(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSetPerson.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetPerson().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "获取密钥设置.", action = ActionGetToken.class)
	@GET
	@Path("token")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionGetToken.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetToken().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新密钥设置.", action = ActionSetToken.class)
	@PUT
	@Path("token")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setToken(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSetToken.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetToken().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "获取云服务器设置.", action = ActionGetCollect.class)
	@GET
	@Path("collect")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getCollect(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionGetCollect.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetCollect().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新云服务器设置.", action = ActionSetCollect.class)
	@PUT
	@Path("collect")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setCollect(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSetCollect.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetCollect().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "获取中心服务器设置.", action = ActionGetCenterServer.class)
	@GET
	@Path("centerserver")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getCenterServer(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionGetCenterServer.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetCenterServer().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新中心服务器设置.", action = ActionSetCenterServer.class)
	@PUT
	@Path("centerserver")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setCenterServer(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSetCenterServer.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetCenterServer().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "获取代理配置.", action = ActionGetProxy.class)
	@GET
	@Path("proxy")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getProxy(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionGetProxy.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetProxy().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新代理配置.", action = ActionSetProxy.class)
	@PUT
	@Path("proxy")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setProxy(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSetProxy.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetProxy().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "获取门户配置.", action = ActionGetPortal.class)
	@GET
	@Path("portal")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getPortal(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<ActionGetPortal.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetPortal().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新门户配置.", action = ActionSetPortal.class)
	@PUT
	@Path("portal")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setPortal(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSetPortal.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetPortal().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "获取所有配置文件信息", action = ActionList.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getList(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionList.Wo> result = new ActionResult<>();
		try {
			result = new ActionList().execute(request, effectivePerson);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "打开config文件", action = ActionOpen.class)
	@POST
	@Path("open")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void open(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionOpen.Wo> result = new ActionResult<>();
		try {
			result = new ActionOpen().execute(request, effectivePerson, jsonElement);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "打开运行环境的config文件", action = ActionOpenRuntimeConfig.class)
	@POST
	@Path("open/run/time/config")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void openRuntimeConfig(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<ActionOpenRuntimeConfig.Wo> result = new ActionResult<>();
		try {
			result = new ActionOpenRuntimeConfig().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "保存config文件", action = ActionSave.class)
	@POST
	@Path("save")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	public void save(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSave.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSave().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "获取三元管理设置.", action = ActionGetTernaryManagement.class)
	@GET
	@Path("ternary/management")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void getTernaryManagement(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		ActionResult<ActionGetTernaryManagement.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionGetTernaryManagement().execute(effectivePerson);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@JaxrsMethodDescribe(value = "更新三元管理设置.", action = ActionSetTernaryManagement.class)
	@POST
	@Path("ternary/management")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void setTernaryManagement(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionSetTernaryManagement.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionSetTernaryManagement().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@JaxrsMethodDescribe(value = "更改管理员密码.", action = ActionChangePassword.class)
	@POST
	@Path("change/password")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void changePassword(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			JsonElement jsonElement) {
		ActionResult<ActionChangePassword.Wo> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionChangePassword().execute(effectivePerson, jsonElement);
		} catch (Exception e) {
			LOGGER.error(e, effectivePerson, request, jsonElement);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result, jsonElement));
	}

	@Operation(summary = "列示所有可配置实体类名称.", operationId = OPERATIONID_PREFIX + "listEntity", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListEntity.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "列示所有可配置实体类名称.", action = ActionListEntity.class)
	@GET
	@Path("list/entity")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listEntity(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListEntity.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListEntity().execute(effectivePerson);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "列示所有可配置应用名称.", operationId = OPERATIONID_PREFIX + "listApplication", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListApplication.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "列示所有可配置应用名称.", action = ActionListApplication.class)
	@GET
	@Path("list/application")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listApplication(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListApplication.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListApplication().execute(effectivePerson);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "列示所有节点数据备份.", operationId = OPERATIONID_PREFIX + "listDumpData", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListDumpData.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "列示所有节点数据备份.", action = ActionListDumpData.class)
	@GET
	@Path("list/dump/data")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDumpData(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListDumpData.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListDumpData().execute(effectivePerson);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "列示本节点数据备份.", operationId = OPERATIONID_PREFIX + "listDumpDataCurrentNode", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListDumpDataCurrentNode.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "列示本节点数据备份.", action = ActionListDumpDataCurrentNode.class)
	@GET
	@Path("list/dump/data/current/node")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDumpDataCurrentNode(@Suspended final AsyncResponse asyncResponse,
			@Context HttpServletRequest request) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListDumpDataCurrentNode.Wo>> result = new ActionResult<>();
		try {
			result = new ActionListDumpDataCurrentNode().execute(effectivePerson);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}
