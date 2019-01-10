package com.x.report.assemble.control.jaxrs.profile;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.http.WrapOutMap;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

@Path("profiles")
@JaxrsDescribe("汇报概要文件信息管理服务")
public class ReportProfileAction extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ReportProfileAction.class);

	@JaxrsMethodDescribe(value = "列出某一个年份的所有汇报启动概要文件列表", action = ActionListByYear.class)
	@GET
	@Path("list/{year}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void listByYear(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("年份") @PathParam("year") String year) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<List<ActionListByYear.Wo>> result = new ActionResult<>();
		
		try {
			result = new ActionListByYear().execute(request, effectivePerson, year);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "概要汇报概要文件ID删除已经生成的所有文件", action = ActionDeleteInfoWithProfileId.class)
	@DELETE
	@Path("info/{profileId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteInfoWithProfileId(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("汇报概要文件ID") @PathParam("profileId") String profileId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		
		try {
			result = new ActionDeleteInfoWithProfileId().execute(request, effectivePerson, profileId);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据ID删除汇报概要文件以及所有已经生成的汇报相关文件", action = ActionDeleteProfile.class)
	@DELETE
	@Path("{profileId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void delete(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("汇报概要文件ID") @PathParam("profileId") String profileId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		
		try {
			result = new ActionDeleteProfile().execute(request, effectivePerson, profileId);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据汇报概要文件ID补充漏启的汇报，已经启动成功的组织跳过", action = ActionSupplementalWithProfile.class)
	@GET
	@Path("{profileId}/supplemental")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void supplemental (@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("汇报概要文件ID") @PathParam("profileId") String profileId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		
		try {
			result = new ActionSupplementalWithProfile().execute(request, effectivePerson, profileId);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据汇报概要文件ID检查汇报生成情况", action = ActionCheckWithProfile.class)
	@GET
	@Path("{profileId}/check")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void checkReport(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("汇报概要文件ID") @PathParam("profileId") String profileId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutMap> result = new ActionResult<>();
		
		try {
			result = new ActionCheckWithProfile().execute(request, effectivePerson, profileId);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
	
	@JaxrsMethodDescribe(value = "根据汇报概要文件ID更新详细内容", action = ActionUpdateProfileDetail.class)
	@GET
	@Path("update/{profileId}/detail")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateDetail(@Suspended final AsyncResponse asyncResponse, @Context HttpServletRequest request,
			@JaxrsParameterDescribe("汇报概要文件ID") @PathParam("profileId") String profileId) {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		ActionResult<WrapOutMap> result = new ActionResult<>(); 
		
		try {
			result = new ActionUpdateProfileDetail().execute(request, effectivePerson, profileId);
		} catch (Exception e) {
			result = new ActionResult<>();
			logger.error(e, effectivePerson, request, null);
		}
		asyncResponse.resume(ResponseFactory.getDefaultActionResultResponse(result));
	}
}