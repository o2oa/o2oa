package com.x.general.assemble.control.jaxrs.area;

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
import com.x.general.assemble.control.jaxrs.qrcode.ActionGetCreate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AreaAction", description = "行政区域.")
@Path("area")
@JaxrsDescribe("行政区域.")
public class AreaAction extends StandardJaxrsAction {

	private static final Logger logger = LoggerFactory.getLogger(AreaAction.class);
	private static final String OPERATIONID_PREFIX = "AreaAction::";

	@Operation(summary = "列示省级行政区域信息.", operationId = OPERATIONID_PREFIX + "listProvince", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListProvince.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "列示省级行政区域信息.", action = ActionListProvince.class)
	@GET
	@Path("list")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listProvince(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request) {
		ActionResult<List<ActionListProvince.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListProvince().execute(effectivePerson);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "列示指定省所属市级行政区域信息.", operationId = OPERATIONID_PREFIX + "listCity", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListCity.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "列示指定省所属市级行政区域信息.", action = ActionListCity.class)
	@GET
	@Path("list/province/{province}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listCity(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("省") @PathParam("province") String province) {
		ActionResult<List<ActionListCity.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListCity().execute(effectivePerson, province);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "列示指定省市所属区行政区域信息.", operationId = OPERATIONID_PREFIX + "listDistrict", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListDistrict.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "列示指定省市所属区行政区域信息.", action = ActionListDistrict.class)
	@GET
	@Path("list/province/{province}/city/{city}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listDistrict(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("省") @PathParam("province") String province,
			@JaxrsParameterDescribe("市") @PathParam("city") String city) {
		ActionResult<List<ActionListDistrict.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListDistrict().execute(effectivePerson, province, city);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}

	@Operation(summary = "列示指定省市区所属街道行政区域信息.", operationId = OPERATIONID_PREFIX + "listStreet", responses = {
			@ApiResponse(content = {
					@Content(array = @ArraySchema(schema = @Schema(implementation = ActionListStreet.Wo.class))) }) })
	@JaxrsMethodDescribe(value = "列示指定省市区所属街道行政区域信息.", action = ActionListStreet.class)
	@GET
	@Path("list/province/{province}/city/{city}/district/{district}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listStreet(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("省") @PathParam("province") String province,
			@JaxrsParameterDescribe("市") @PathParam("city") String city,
			@JaxrsParameterDescribe("区") @PathParam("district") String district) {
		ActionResult<List<ActionListStreet.Wo>> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			result = new ActionListStreet().execute(effectivePerson, province, city, district);
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
			result.error(e);
		}
		asyncResponse.resume(ResponseFactory.getEntityTagActionResultResponse(request, result));
	}
}