package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.InTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.application.jaxrs.MemberTerms;
import com.x.base.core.application.jaxrs.NotEqualsTerms;
import com.x.base.core.application.jaxrs.NotInTerms;
import com.x.base.core.application.jaxrs.NotMemberTerms;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.CenterWorkDeleteException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.CenterWorkIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.CenterWorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.CenterWorkQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.InsufficientPermissionsException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.OkrOperationDynamicSaveException;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception.OkrSystemAdminCheckException;
import com.x.okr.assemble.control.service.OkrCenterWorkOperationService;
import com.x.okr.assemble.control.service.OkrCenterWorkQueryService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.organization.core.express.Organization;

@Path("admin/okrcenterworkinfo")
public class OkrCenterWorkInfoAdminAction extends StandardJaxrsAction {

	private Logger logger = LoggerFactory.getLogger(OkrCenterWorkInfoAdminAction.class);
	private BeanCopyTools<OkrCenterWorkInfo, WrapOutOkrCenterWorkInfo> wrapout_copier = BeanCopyToolsBuilder
			.create(OkrCenterWorkInfo.class, WrapOutOkrCenterWorkInfo.class, null, WrapOutOkrCenterWorkInfo.Excludes);
	private OkrCenterWorkQueryService okrCenterWorkInfoService = new OkrCenterWorkQueryService();
	private OkrCenterWorkOperationService okrCenterWorkOperationService = new OkrCenterWorkOperationService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();

	@HttpMethodDescribe(value = "根据ID删除OkrCenterWorkInfo数据对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrCenterWorkInfo okrCenterWorkInfo = null;
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Organization organization = new Organization(ThisApplication.context());
		Boolean hasPermission = false;
		try {
			hasPermission = organization.role().hasAny(currentPerson.getName(), "OkrSystemAdmin");
			if (!hasPermission) {
				check = false;
				Exception exception = new InsufficientPermissionsException(currentPerson.getName(), "OkrSystemAdmin");
				result.error(exception);
				// logger.error( e, currentPerson, request, null);
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new OkrSystemAdminCheckException(e, currentPerson.getName());
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new CenterWorkIdEmptyException();
				result.error(exception);
				// logger.error( e, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				okrCenterWorkInfo = okrCenterWorkInfoService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new CenterWorkQueryByIdException(e, id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			if (okrCenterWorkInfo == null) {
				check = false;
				Exception exception = new CenterWorkNotExistsException(id);
				result.error(exception);
				// logger.error( e, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				okrCenterWorkOperationService.delete(id);
				result.setData(new WrapOutId(id));
			} catch (Exception e) {
				check = false;
				Exception exception = new CenterWorkDeleteException(e, id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				okrWorkDynamicsService.workDynamic(okrCenterWorkInfo.getId(), null, okrCenterWorkInfo.getTitle(),
						"保存中心工作", currentPerson.getName(), currentPerson.getName(), currentPerson.getName(),
						"删除中心工作：" + okrCenterWorkInfo.getTitle(), "中心工作删除成功！");
			} catch (Exception e) {
				Exception exception = new OkrOperationDynamicSaveException(e, id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrCenterWorkInfo对象.", response = WrapOutOkrCenterWorkInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<>();
		WrapOutOkrCenterWorkInfo wrap = null;
		OkrCenterWorkInfo OkrCenterWorkInfo = null;
		Boolean check = true;

		EffectivePerson currentPerson = this.effectivePerson(request);
		Organization organization = new Organization(ThisApplication.context());
		Boolean hasPermission = false;
		try {
			hasPermission = organization.role().hasAny(currentPerson.getName(), "OkrSystemAdmin");
			if (!hasPermission) {
				check = false;
				Exception exception = new InsufficientPermissionsException(currentPerson.getName(), "OkrSystemAdmin");
				result.error(exception);
				// logger.error( e, currentPerson, request, null);
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new OkrSystemAdminCheckException(e, currentPerson.getName());
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new CenterWorkIdEmptyException();
				result.error(exception);
				// logger.error( e, currentPerson, request, null);
			}
		}
		try {
			OkrCenterWorkInfo = okrCenterWorkInfoService.get(id);
			if (OkrCenterWorkInfo != null) {
				wrap = wrapout_copier.copy(OkrCenterWorkInfo);
				result.setData(wrap);
			} else {
				Exception exception = new CenterWorkNotExistsException(id);
				result.error(exception);
				// logger.error( e, currentPerson, request, null);
			}
		} catch (Exception e) {
			Exception exception = new CenterWorkQueryByIdException(e, id);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo,下一页.", response = WrapOutOkrCenterWorkInfo.class, request = WrapInFilterAdminCenterWorkInfo.class)
	@PUT
	@Path("filter/list/{id}/next/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListNextWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, WrapInFilterAdminCenterWorkInfo wrapIn) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Organization organization = new Organization(ThisApplication.context());
		Boolean hasPermission = false;
		try {
			hasPermission = organization.role().hasAny(currentPerson.getName(), "OkrSystemAdmin");
			if (!hasPermission) {
				check = false;
				Exception exception = new InsufficientPermissionsException(currentPerson.getName(), "OkrSystemAdmin");
				result.error(exception);
				// logger.error( e, currentPerson, request, null);
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new OkrSystemAdminCheckException(e, currentPerson.getName());
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		if (check) {
			if (wrapIn == null) {
				wrapIn = new WrapInFilterAdminCenterWorkInfo();
			}
		}
		if (check) {
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("title", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("defaultWorkType", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("description", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("processStatus", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("deployerName", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("deployerOrganizationName", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("deployerCompanyName", wrapIn.getFilterLikeContent());
			}
		}
		if (check) {
			sequenceField = wrapIn.getSequenceField();
			try {
				result = this.standardListNext(wrapout_copier, id, count, sequenceField, equalsMap, notEqualsMap,
						likesMap, insMap, notInsMap, membersMap, notMembersMap, false, wrapIn.getOrder());
			} catch (Exception e) {
				result.error(e);
				logger.error(e, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrCenterWorkInfo,下一页.", response = WrapOutOkrCenterWorkInfo.class, request = WrapInFilterAdminCenterWorkInfo.class)
	@PUT
	@Path("filter/list/{id}/prev/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListPrevWithFilter(@Context HttpServletRequest request, @PathParam("id") String id,
			@PathParam("count") Integer count, WrapInFilterAdminCenterWorkInfo wrapIn) {
		ActionResult<List<WrapOutOkrCenterWorkInfo>> result = new ActionResult<>();
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson(request);
		if (check) {
			if (wrapIn == null) {
				wrapIn = new WrapInFilterAdminCenterWorkInfo();
			}
		}
		if (check) {
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("title", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("defaultWorkType", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("description", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("processStatus", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("deployerName", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("deployerOrganizationName", wrapIn.getFilterLikeContent());
			}
			if (wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty()) {
				likesMap.put("deployerCompanyName", wrapIn.getFilterLikeContent());
			}
		}
		if (check) {
			sequenceField = wrapIn.getSequenceField();
			try {
				result = this.standardListPrev(wrapout_copier, id, count, sequenceField, equalsMap, notEqualsMap,
						likesMap, insMap, notInsMap, membersMap, notMembersMap, false, wrapIn.getOrder());
			} catch (Exception e) {
				result.error(e);
				logger.error(e, currentPerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}